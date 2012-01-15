package org.balazsbela.symbion.console.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.symbion.console.client.Client;
import org.symbion.console.client.ClientException;
import org.eclipse.swt.widgets.TableColumn;

public class ConsoleWindow {

	private static final Log log = LogFactory.getLog(ConsoleWindow.class);

	protected Shell shlProfilingConsole;
	private Table table;
	private Table ruleTable;
	private Button btnConnect;

	Client client;

	public ConsoleWindow() {
		client = new Client();
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ConsoleWindow window = new ConsoleWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlProfilingConsole.open();
		shlProfilingConsole.layout();
		while (!shlProfilingConsole.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlProfilingConsole = new Shell();
		shlProfilingConsole.setSize(617, 416);
		shlProfilingConsole.setText("Profiling console");

		btnConnect = new Button(shlProfilingConsole, SWT.NONE);
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				try {
					connectToRunningInstance();
				} catch (ClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnConnect.setBounds(10, 10, 75, 25);
		btnConnect.setText("Connect");

		Button btnStartProfiling = new Button(shlProfilingConsole, SWT.NONE);
		btnStartProfiling.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				try {
					startProfiling();
				} catch (ClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnStartProfiling.setBounds(91, 10, 84, 25);
		btnStartProfiling.setText("Start profiling");

		Button btnNewButton = new Button(shlProfilingConsole, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				try {
					stopProfiling();
				} catch (ClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		btnNewButton.setBounds(181, 10, 84, 25);
		btnNewButton.setText("Stop profiling");

		Button btnSettings = new Button(shlProfilingConsole, SWT.NONE);
		btnSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				SettingsDialog sd = new SettingsDialog(shlProfilingConsole, SWT.DIALOG_TRIM);
				sd.open();
			}
		});
		btnSettings.setBounds(282, 10, 75, 25);
		btnSettings.setText("Settings");

		TabFolder tabFolder = new TabFolder(shlProfilingConsole, SWT.NONE);
		tabFolder.setBounds(10, 41, 591, 327);

		TabItem tbtmMatchedClasses_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmMatchedClasses_1.setText("Matched classes");

		ruleTable = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmMatchedClasses_1.setControl(ruleTable);
		ruleTable.setHeaderVisible(true);
		ruleTable.setLinesVisible(true);

		TableColumn tblclmnPattern = new TableColumn(ruleTable, SWT.NONE);
		tblclmnPattern.setWidth(569);
		tblclmnPattern.setText("Pattern");

		TableItem tableItem = new TableItem(ruleTable, SWT.NONE);
		tableItem.setText("org.balazsbela.FirmManagement.*(*)");

		TableItem tableItem_1 = new TableItem(ruleTable, SWT.NONE);
		tableItem_1.setText("org.balazsbela.AnotherPackage.*(*)");

		TabItem tbtmThreads = new TabItem(tabFolder, SWT.NONE);
		tbtmThreads.setText("Threads");

		table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmThreads.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

	}

	void startProfiling() throws ClientException {

		String ruleString = "";
		for (TableItem ti : ruleTable.getItems()) {
			ruleString += ti.getText() + ":accept" + ";";
		}
		client.setRuleString(ruleString);

		Thread t = new Thread("SymbionStartProfilingThread") {
			public void run() {
				try {
					client.startProfiling();
				} catch (ClientException e) {
					MessageDialog.openError(shlProfilingConsole, "Error", e.toString());
					log.error(e);
					e.printStackTrace();
				}
			};
		};
		t.setName("StartProfiling");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(false);
		t.start();
	}

	void connectToRunningInstance() throws ClientException {

		if (btnConnect.getText().equals("Connect")) {
			btnConnect.setText("Disconnect");
		} else {
			btnConnect.setText("Connect");
		}

		Thread t = new Thread("SymbionConnectThread") {
			public void run() {
				try {
					if (!client.isConnected()) {
						client.connect("localhost", 31337);
					} else {
						client.disconnect();
					}
				} catch (ClientException e) {
					MessageDialog.openError(null, "Error", e.toString());

					log.error(e);
					e.printStackTrace();
				}
			};
		};
		t.setName("Connect");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(false);
		t.start();

	}

	void stopProfiling() throws ClientException {

	
		Thread t = new Thread("SymbionStopProfilingThread") {
			public void run() {
				try {
					client.stopProfiling();
				} catch (ClientException e) {
					MessageDialog.openError(shlProfilingConsole, "Error", e.toString());
					log.error(e);
					e.printStackTrace();
				}
			};
		};
		t.setName("StopProfiling");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(false);
		t.start();
	}
}
