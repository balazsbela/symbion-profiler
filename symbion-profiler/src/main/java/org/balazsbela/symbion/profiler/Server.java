package org.balazsbela.symbion.profiler;

import static org.balazsbela.symbion.profiler.Constants.STATUS_UNKNOWN_CMD;
import static org.balazsbela.symbion.profiler.Log.print;
import org.balazsbela.symbion.profiler.Utils;
import org.balazsbela.symbion.profiler.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import generated.ExecutionTimeLineContainer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.xml.transform.stream.StreamResult;

/**
 * Single-thread daemon that allow remote connections
 * 
 */
class Server extends Thread {

	Jaxb2Marshaller marshaller;

	private Config config;

	public Server(Config config) {
		super("Symbion_SERVER");
		this.config = config;
		setDaemon(true);
		setPriority(MAX_PRIORITY);
		
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
		marshaller = (Jaxb2Marshaller) context.getBean("marshaller");
	}

	@Override
	public void run() {
		try {
			while (true) {
				print(0, "Listening on port " + config.getPort() + "...");
				ServerSocket srv = new ServerSocket(config.getPort());
				Socket s = srv.accept();
				print(0, "Serving connection from " + s.getRemoteSocketAddress());
				try {

					ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));

					// The client may give up now if not compatible...
					out.writeUTF(Constants.VERSION_STRING);
					out.flush();

					ObjectInputStream in = new ObjectInputStream(s.getInputStream());

					serveClient(in, out);
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

	private void serveClient(final ObjectInputStream in, final ObjectOutputStream out) throws Exception {

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
				print(0, "Profiling requested");

				if (config.isWaitConnection()) {
					synchronized (Agent.waitConnectionLock) {
						Agent.waitConnectionLock.notifyAll();
					}
				}

				break;
			case Constants.CMD_DISCONNECT:
				out.writeInt(Constants.CMD_ACK);
				out.flush();
				return;
			case Constants.CMD_RCV_CFG:
				// Receive rules
				String rules = in.readUTF();
				print(0, rules);
				config.setRules(Utils.parseRules(rules));

				for (Rule r : config.getRules()) {
					print(0, r.getPattern());
				}

				out.writeInt(Constants.CMD_ACK);
				out.flush();
				break;
			case Constants.CMD_STOPPROFILING:
				out.writeInt(Constants.CMD_ACK);
				out.flush();
				
				ExecutionTimeLineContainer timeline = new ExecutionTimeLineContainer();
				for (String s : ExecutionTimeline.calledMethods) {
					timeline.getMethodCall().add(s);
				}
				java.io.StringWriter sw = new StringWriter();
				marshaller.marshal(timeline, new StreamResult(sw));
				out.writeUTF(sw.toString());
				out.flush();
				break;
			default:
				out.writeInt(STATUS_UNKNOWN_CMD);
			}
		}
	}
}
