package org.balazsbela.symbion.profiler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import static org.balazsbela.symbion.profiler.Log.print;
import static org.balazsbela.symbion.profiler.Constants.*;


/**
 * Single-thread daemon that allow remote connections
 * 
 */
class Server extends Thread {

    private Config config;

    public Server(Config config) {
        super("Symbion_SERVER");
        this.config = config;
        setDaemon(true);
        setPriority(MAX_PRIORITY);
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
                    ObjectOutputStream out = new ObjectOutputStream(
                            new BufferedOutputStream(s.getOutputStream()));

                    // The client may give up now if not compatible...                   

                    ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                    serveClient(in, out);
                    Thread.sleep(250);
                } catch (SocketException e) {
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

    private void serveClient(final ObjectInputStream in, final ObjectOutputStream out)
        throws Exception {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
            switch (in.readInt()) {
                case CMD_STARTPROFILING :
                    System.gc();                  
                    break;              
                default :
                    out.writeInt(STATUS_UNKNOWN_CMD);
            }
        }
    }
}
