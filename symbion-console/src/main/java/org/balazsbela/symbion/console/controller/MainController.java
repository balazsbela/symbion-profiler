package org.balazsbela.symbion.console.controller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.balazsbela.symbion.config.Config;
import org.balazsbela.symbion.console.api.Console;
import org.balazsbela.symbion.console.api.MessageHandler;
import org.balazsbela.symbion.console.client.Client;
import org.balazsbela.symbion.console.client.ClientException;
import org.balazsbela.symbion.constants.Constants;
import org.balazsbela.symbion.models.SettingsModel;
import org.balazsbela.symbion.models.ThreadModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class MainController {
	public static final String SETTINGS_XML = "settings.xml";
	public static final int DEFAULTPORT = 31337;

	private static MainController instance;
	// Config holds the settings transmitted to the server.
	private Config config = new Config();
	SettingsModel settings;
	Client client = new Client();
	String ruleString = new String();
	private MessageHandler messageHandler;
	private Console console;
	
	private MainController() {
		loadSettings();
		File f1 = new File(".");
		Constants.SETTINGS_XML_PATH = f1+"/settings.xml";
	}

	public static MainController getInstance() {
		if (instance == null) {
			instance = new MainController();
		}
		return instance;
	}

	public void updateRules(SettingsModel model) {
		String ruleString = "";
		for (String r : model.getRules()) {
			ruleString += r + ":accept" + ";";
		}
		
		for (String r : model.getRejectRules()) {
			ruleString += r + ":reject" + ";";
		}
		
		config.parseRules(ruleString);
		this.ruleString = ruleString;
	}

	public void startProfiling() throws ClientException {
		client.setRuleString(ruleString);

		Thread t = new Thread("SymbionStartProfilingThread") {
			public void run() {
				try {
					client.startProfiling();
				} catch (ClientException e) {
					e.printStackTrace();
				}
			};
		};
		t.setName("StartProfiling");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(false);
		t.start();
	}

	public void connectToRunningInstance() {
		Thread t = new Thread("SymbionConnectThread") {
			public void run() {
				try {
					if (!client.isConnected()) {
						client.connect("localhost", 31337);
					} else {
						client.disconnect();
					}
				} catch (ClientException e) {
					messageHandler.handleError(e);
				}
			};
		};
		t.setName("Connect");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(false);
		t.start();

	}

	public void stopProfiling() {
		Thread t = new Thread("SymbionStopProfilingThread") {
			public void run() {
				try {
					client.stopProfiling();
				} catch (ClientException e) {
					messageHandler.handleError(e);
				}
			};
		};
		t.setName("StopProfiling");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(false);
		t.start();
	}

	public boolean clientIsConnected() {
		return client.isConnected();
	}

	public String getRuleString() {
		return ruleString;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public void disconnect() {
		Thread t = new Thread("SymbionStopProfilingThread") {
			public void run() {
				try {
					client.disconnect();
				} catch (ClientException e) {
					messageHandler.handleError(e);
				}
			};
		};
		t.setName("StopProfiling");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(false);
		t.start();
	}

	public void saveSettings(SettingsModel model) throws FileNotFoundException {
		settings = model;
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(SETTINGS_XML)));
		encoder.writeObject(model);
		encoder.close();

		updateRules(model);
		config.setPort(settings.getPort());
	}

	public void loadSettings() {
		XMLEncoder encoder;
		try {
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(SETTINGS_XML)));
			settings = (SettingsModel) decoder.readObject();
			decoder.close();

		} catch (FileNotFoundException e) {
			settings = new SettingsModel();
			settings.setSourcePath("");
			File dir1 = new File(".");
			try {
				settings.setOutputFolder(dir1.getCanonicalPath());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			settings.setHost("localhost");
			settings.setRules(new HashSet<String>());
			settings.setRejectRules(new HashSet<String>());			
			settings.setPort(DEFAULTPORT);			
		}
		
		updateRules(settings);
		config.setPort(settings.getPort());
	}

	public SettingsModel getSettings() {
		return settings;
	}

	public void notifyUI(String message) {
		messageHandler.displayMessage(message);
	}

	public void openLoadingDialog() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				messageHandler.openLoadingDialog();
			}
		}).start();		
		
	}
	
	public void updateMatchedClasses(Set<String> matchedClasses) {
		console.updateListOfMatchedClasses(matchedClasses);
	}

	public void setConsole(Console console) {
		this.console = console;
	}

	public void updateThreadList(Set<ThreadModel> threads) {
		console.updateThreadList(threads);
	}

	public void closeLoadingDialog() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				messageHandler.closeLoadingDialog();
			}
		}).start();		
	}

	public void disconnected() {
		messageHandler.toggleDisconnectLabel();
	}

}
