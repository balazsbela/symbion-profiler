package org.balazsbela.symbion.profiler;

import static org.balazsbela.symbion.profiler.Log.print;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import org.balazsbela.symbion.config.Config;
import org.balazsbela.symbion.config.Rule;
import org.balazsbela.symbion.constants.Constants;
import org.balazsbela.symbion.models.ExecutionTimeline;
import org.balazsbela.symbion.utils.TimelineMarshaller;
import org.balazsbela.symbion.utils.Utils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * Single-thread daemon that allow remote connections
 * 
 */
class Server extends Thread {

	private Config config;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private DataInputStream dataInStream;
	private DataOutputStream dataOutputStream;
	private CharsetEncoder encoder;
	private CharsetDecoder decoder;
	private TimelineMarshaller marshaller;
	private ThreadProfiler.IPCServices ipcService = null;

	public Server(Config config) {
		super("Symbion_SERVER");
		this.config = config;
		setDaemon(true);
		setPriority(MAX_PRIORITY);

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
		marshaller = (TimelineMarshaller) context.getBean("timeLineMarshaller");

	}

	public String marshallTimeline() {
		synchronized (ThreadProfiler.globalLock) {
			print(0, "Sending timeline");
			print(0, "Number of registered function calls:"+getTimeline().getCalledMethodsWrapper().getCalledMethods().size());
			return marshaller.encode(getTimeline().getCalledMethodsWrapper());
		}
		// ByteArrayOutputStream bstream = new ByteArrayOutputStream();
		// XMLEncoder encoder = new XMLEncoder(new
		// BufferedOutputStream(bstream));
		// encoder.writeObject(ExecutionTimeline.calledMethods);

		// encoder.close();

		// print(0,"Gathered data:");
		// for(FunctionCall fc:ExecutionTimeline.calledMethods) {
		// print(0,fc.getParent().getContainingClassName()+"."+fc.getParent().getMethodName()+"->"+fc.getTarget().getContainingClassName()+"."+fc.getTarget().getMethodName()+fc.getTarget().getMethodSignature());
		// print(0,fc.getParent().getLineNumber()+"->"+fc.getTarget().getLineNumber());
		// }

		// return bstream.toString();
	}

	public String marshallMatchedClasses() {

		ByteArrayOutputStream bstream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(bstream));

		System.out.println(getTimeline());
		System.out.println("Matched class count:" + getTimeline().getMatchedclasses().size());
		encoder.writeObject(getTimeline().getMatchedclasses());
		encoder.close();

		return bstream.toString();
	}

	public String marshallThreadList() {
		ByteArrayOutputStream bstream = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(bstream));
		encoder.writeObject(getTimeline().getThreads());
		encoder.close();

		return bstream.toString();
	}

	@Override
	public void run() {
		try {
			while (true) {
				print(0, "Server started!");
				print(0, "Listening on port " + config.getPort() + "...");

				ServerSocket srv = new ServerSocket(config.getPort());
				Socket s = srv.accept();
				print(0, "Serving connection from " + s.getRemoteSocketAddress());
				try {

					Charset charset = Charset.forName("UTF-8");
					decoder = charset.newDecoder();
					encoder = charset.newEncoder();
					dataOutputStream = new DataOutputStream(s.getOutputStream());
					dataOutputStream.flush();
					dataInStream = new DataInputStream(s.getInputStream());

					out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
					// The client may give up now if not compatible...
					out.writeUTF(Constants.VERSION_STRING);
					out.flush();
					in = new ObjectInputStream(s.getInputStream());
					serveClient(in, out, s);
					Thread.sleep(250);
				} catch (SocketException e) {
					print(0, "ERROR:" + e);
					if (e.getMessage().equals("Connection reset")) {
						print(0, "Connection closed by client");
					} else {
						print(0, "Socket I/O error", e);
					}
				} catch (Exception any) {
					print(0, "Error during request processing. Closing connection", any);
				}
				try {
					if (s != null) {
						s.close();
					}
					if (srv != null) {
						srv.close();
					}
				} catch (IOException nnn) {
					// ignore
				}
			}
		} catch (Throwable e) {
			print(0, "Server exception", e);
			if (config.isExitVmOnFailure()) {
				print(0, "Aborting JVM...");
				System.exit(3);
			}
		} finally {
			print(0, "Server exiting");
		}
	}

	/**
	 * XML is big,and writeUTF can't send more than 64K
	 * 
	 * @param xml
	 * @param s
	 * @throws IOException
	 */
	private void sendXML(String xml, Socket s) {
		try {
			CharBuffer reqBuffer = CharBuffer.wrap(xml.toCharArray());
			ByteBuffer nbBuffer = encoder.encode(reqBuffer);

			dataOutputStream.writeInt(nbBuffer.array().length);
			dataOutputStream.flush();
			dataOutputStream.write(nbBuffer.array());
			dataOutputStream.flush();
		} catch (IOException e) {
			print(0, e.getStackTrace().toString());
		}

	}

	private void serveClient(final ObjectInputStream in, final ObjectOutputStream out, Socket s) throws Exception {

		print(0, "Now serving clients!");

		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}

			int command = in.readInt();
			print(0, "Received command:" + command);
			switch (command) {
			case Constants.CMD_STARTPROFILING:
				out.writeInt(Constants.CMD_ACK);
				out.flush();

				print(0, "Profiling requested!");
				BytecodeTransformer.setEnabled(true);
				
				if (config.isWaitConnection()) {
					synchronized (Agent.waitConnectionLock) {
						Agent.waitConnectionLock.notifyAll();
					}
				}
				if(ipcService!=null) {
					ipcService.startProfiling();
				}

				break;
			case Constants.CMD_DISCONNECT:
				out.writeInt(Constants.CMD_ACK);
				out.flush();
				s.close();
				return;
			case Constants.CMD_RCV_CFG:

				synchronized (ThreadProfiler.globalLock) {
					int filterParents = in.readInt();
					print(0, "Filter parents of function calls:" + filterParents);

					boolean filter = filterParents == 1 ? true : false;
					config.setFilterParents(filter);

					// Receive rules
					String rules = in.readUTF();
					print(0, rules);

					List<Rule> ruleList = Utils.parseRules(rules);
					config.setRules(ruleList);

					for (Rule r : config.getRules()) {
						print(0, r.getPattern());
					}

					out.writeInt(Constants.CMD_ACK);
					out.flush();
				}
				break;
			case Constants.CMD_STOPPROFILING:
				out.writeInt(Constants.CMD_ACK);
				out.flush();

				print(0, "Stopping profiling and sending data:");

				BytecodeTransformer.setEnabled(false);
				// while (!BytecodeTransformer.isSuccessfullyStopped()) {
				// }

				print(0, "Marshalling...");

				String xml = marshallTimeline();
				print(0, "Data has " + xml.length() + " bytes!");

				sendXML(xml, s);
				ipcService.stopProfiling();
				BytecodeTransformer.ipcStarted = false;
				
				break;
			case Constants.CMD_POLLDATA:
				out.writeInt(Constants.CMD_ACK);
				out.flush();

				System.out.println("Polling for data!");
				// synchronized (ThreadProfiler.globalLock) {
				sendXML(marshallMatchedClasses(), s);
				sendXML(marshallThreadList(), s);
				// }

				break;
			default:
				out.writeInt(Constants.STATUS_UNKNOWN_CMD);
				out.flush();
			}
		}
	}

	public void setMarshaller(TimelineMarshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void update(ExecutionTimeline t) {

	}

	public ExecutionTimeline getTimeline() {
		if (ipcService == null) {
			setUpRMIPRoxy();
			System.out.println("Connecting to RMI!");
		}
		return ipcService.retrieveTimeline();
	}

	public void setUpRMIPRoxy() {

		try {			
			
			RmiProxyFactoryBean rmiProxy = new RmiProxyFactoryBean();
			rmiProxy.setServiceInterface(ThreadProfiler.IPCServices.class);
			rmiProxy.setServiceUrl("rmi://127.0.0.1:1099/SymbionIPC");
			rmiProxy.setLookupStubOnStartup(false);
			ipcService = (ThreadProfiler.IPCServices) ProxyFactory.getProxy(rmiProxy.getServiceInterface(), rmiProxy);
			rmiProxy.prepare();
			System.out.println("Testing RMI connection " + ipcService.retrieveTimeline().getCalledMethods().size());
		}
		catch (RemoteLookupFailureException e) {
			print(0, "Tried to connect but server not started yet. Rescheduling.");
			synchronized (Agent.rmiLock) {
				try {
					Agent.rmiLock.wait();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			setUpRMIPRoxy();
		}
	}

}
