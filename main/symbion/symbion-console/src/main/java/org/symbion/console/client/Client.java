package org.symbion.console.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.balazsbela.symbion.constants.Constants;

public class Client {
	private static final Log log = LogFactory.getLog(Client.class);
	
	private Socket s;
	private ObjectOutputStream out;
	private ObjectInputStream in;	
	private String ruleString;
	
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
			handleException(e);
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
			out.writeUTF(ruleString);
			out.flush();
			expectOk();
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	public synchronized void stopProfiling() throws ClientException {
		verifyConnection();
		try {
			log.info("Stopping profiling.");				
			sendAndWaitAck(Constants.CMD_STOPPROFILING);
			
			String xml = in.readUTF();
			
			FileWriter file = new FileWriter("execution-timeline.xml");
			file.write(xml);
			file.close();
			
			log.info("Client received response.");
		} catch (Exception e) {
			handleException(e);
		}
	}
}
