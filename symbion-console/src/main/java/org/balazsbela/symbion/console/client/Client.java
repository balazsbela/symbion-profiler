package org.balazsbela.symbion.console.client;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.balazsbela.symbion.console.controller.MainController;
import org.balazsbela.symbion.constants.Constants;
import org.balazsbela.symbion.models.FunctionCallListWrapper;
import org.balazsbela.symbion.models.ThreadModel;
import org.balazsbela.symbion.utils.TimelineMarshaller;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {
	public static final String DATA_FILE = "execution-timeline.xml";
	
	private static final Log log = LogFactory.getLog(Client.class);
	private Socket s;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String ruleString;
	private boolean profiling = false;
	private Timer timer = new Timer();
	private DataInputStream dataInStream;
	private DataOutputStream dataOutputStream;
	private CharsetEncoder encoder;
	private CharsetDecoder decoder;
	private TimelineMarshaller marshaller;

	public Client() {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
		marshaller = (TimelineMarshaller) context.getBean("timeLineMarshaller");
		registerPollingTimer();
	}

	public String getRuleString() {
		return ruleString;
	}

	public void setRuleString(String ruleString) {
		this.ruleString = ruleString;
	}

	public synchronized void connect(String host, int port) throws ClientException {
		try {
			if (isConnected()) {
				throw new ClientException("Client already connected");
			}
			s = new Socket(host, port);
			out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
			in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
			Charset charset = Charset.forName("UTF-8");

			decoder = charset.newDecoder();
			encoder = charset.newEncoder();
			dataOutputStream = new DataOutputStream(s.getOutputStream());
			dataOutputStream.flush();
			dataInStream = new DataInputStream(s.getInputStream());

			String serverVersion = in.readUTF();
			if (!serverVersion.equals(Constants.VERSION_STRING)) {
				s.close();
				s = null;
				out = null;
				in = null;
				throw new ClientException("Incompatible versions!");
			}
			log.info("Client connected to " + host + ":" + port);
		} catch (Exception e) {
			handleException(new ClientException("Could not connect!"));
		}
	}

	public synchronized boolean isConnected() {
		return s != null && s.isConnected() && !s.isInputShutdown();
	}

	public synchronized void disconnect() throws ClientException {
		verifyConnection();
		try {
			log.info("Client disconnecting");
			sendAndWaitAck(Constants.CMD_DISCONNECT);
			close(null);
			profiling = false;
		} catch (Exception e) {
			handleException(e);
		}
	}

	private void verifyConnection() throws ClientException {
		if (!isConnected()) {
			log.error("Client not connected!");
			throw new ClientException("Client not connected");
		}
	}

	private void sendAndWaitAck(int cmdId) throws ClientException {
		try {
			out.writeInt(cmdId);
			out.flush();
			expectOk();
		} catch (Exception e) {			
			MainController.getInstance().disconnected();
			handleException(e);
		}
	}

	private void expectOk() throws ClientException {
		try {
			int status = in.readInt();
			if (status != 0) {
				throw new ClientException("Command Error: code=" + status);
			}
		} catch (Exception e) {
			handleException(e);
		}
	}

	private void handleException(Exception e) throws ClientException {
		close(e);
		if (e instanceof ClientException) {
			throw (ClientException) e;
		}
		if (e instanceof IOException) {
			e.printStackTrace();
			throw new ClientException("I/O Error", e);
		}
		throw new ClientException("Unexpeced Client Error", e);
	}

	private void close(Throwable t) {
		if (s != null) {
			try {
				s.close();
			} catch (Exception any) {
				// ignore
			}
			s = null;
			in = null;
			out = null;
			profiling = false;
			if (t != null) {
				log.error("Client being disconnected due to error", t);
			} else {
				log.info("Client being disconnected normally");
			}
		}
	}

	public synchronized void startProfiling() throws ClientException {
		verifyConnection();
		try {
			log.info("Sending rules.");
			sendRules();
			log.info("Client sending start profiling command.");
			sendAndWaitAck(Constants.CMD_STARTPROFILING);
			log.info("Client received response.");
			profiling = true;
		} catch (Exception e) {
			handleException(e);
		}
	}

	public synchronized void sendRules() throws ClientException {
		verifyConnection();
		try {
			log.info("Client sending profiling rules.");
			out.writeInt(Constants.CMD_RCV_CFG);
			out.flush();
			int filter = MainController.getInstance().getSettings().isFilterParents() ? 1 : 0;
			out.writeInt(filter);
			out.flush();

			out.writeUTF(ruleString);
			out.flush();
			expectOk();
		} catch (Exception e) {
			handleException(e);
		}
	}

	private String readXML() throws IOException {
		int length = dataInStream.readInt();
		
		byte[] stringBuffer = new byte[length];
		int received = 0;
		String response = "";
		StringBuffer buffer = new StringBuffer();
		dataInStream.readFully(stringBuffer);
//		while (received < length) {
//			received += dataInStream.read(stringBuffer);
//			buffer.append(decoder.decode(ByteBuffer.wrap(stringBuffer)).toString());
//			System.out.println("Received " + received + " out of " + length);
//		}

		response = decoder.decode(ByteBuffer.wrap(stringBuffer)).toString().trim();

		return response;
	}

	public synchronized void stopProfiling() throws ClientException {
		verifyConnection();
		try {
			log.info("Stopping profiling.");
			profiling = false;

			sendAndWaitAck(Constants.CMD_STOPPROFILING);
			
			MainController.getInstance().openLoadingDialog();
			
			String xml = readXML();
			FunctionCallListWrapper callsWrapper = marshaller.decode(xml);				
			
			
			System.out.println("Saving timeline to:"+MainController.getInstance().getSettings().getOutputFolder()+"/"+DATA_FILE);
			File f1 = new File(MainController.getInstance().getSettings().getOutputFolder()+"/"+DATA_FILE);
			if(f1.exists()) {
				f1.delete();
			}
			
			BufferedWriter fstream = new BufferedWriter(new FileWriter(MainController.getInstance().getSettings().getOutputFolder()+"/"+DATA_FILE));			
			fstream.write(xml.trim());
			fstream.close();			
			
			MainController.getInstance().closeLoadingDialog();
			MainController.getInstance().notifyUI("Profiling data saved! You can now visualize.");
			log.info("Client received response.");
		} catch (Exception e) {
			handleException(e);
			e.printStackTrace();
		}
	}

	public synchronized void pollForUpdates() throws ClientException {
		if (profiling) {
			verifyConnection();
			try {
				log.info("Polling for class and thread data");
				sendAndWaitAck(Constants.CMD_POLLDATA);

				String xml = readXML();
				XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(
						xml.getBytes("UTF8"))));
				Set<String> matchedClasses = (Set<String>) decoder.readObject();
				MainController.getInstance().updateMatchedClasses(matchedClasses);
				log.info("Received class data:" + xml);

				xml = readXML();
				decoder = new XMLDecoder(new BufferedInputStream(new ByteArrayInputStream(xml.getBytes("UTF8"))));
				Set<ThreadModel> threads = (Set<ThreadModel>) decoder.readObject();
				log.info("Received threadlist:");
				for (ThreadModel t : threads) {
					log.info(t.getName());
				}
				MainController.getInstance().updateThreadList(threads);

			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	public void registerPollingTimer() {
		int delay = 5000; // delay for 5 sec.
		int period = 10000; // repeat every 10 sec.

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					pollForUpdates();
				} catch (ClientException e) {
					e.printStackTrace();
				}
			}
		}, delay, period);

	}
}
