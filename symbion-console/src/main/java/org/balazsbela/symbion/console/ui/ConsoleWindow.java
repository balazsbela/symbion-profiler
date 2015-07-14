package org.balazsbela.symbion.console.ui;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.balazsbela.symbion.console.api.Console;
import org.balazsbela.symbion.console.api.MessageHandler;
import org.balazsbela.symbion.console.client.ClientException;
import org.balazsbela.symbion.console.controller.MainController;
import org.balazsbela.symbion.models.ThreadModel;
import org.balazsbela.symbion.visualizer.presentation.Visualizer;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class ConsoleWindow implements MessageHandler, Console {

	private static final Log log = LogFactory.getLog(ConsoleWindow.class);

	public static Shell shlProfilingConsole = new Shell();
	private Table threadTable;
	private Table ruleTable;
	private Button btnConnect;
	private Thread visualizingThread;
	
	public ConsoleWindow() {
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
		shlProfilingConsole.setImage(SWTResourceManager.getImage(ConsoleWindow.class, "/com/sun/java/swing/plaf/windows/icons/JavaCup32.png"));
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

		shlProfilingConsole.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				System.exit(0);
			}
		});
		
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
				stopProfiling();
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
				openSettingsDialog();
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

		TabItem tbtmThreads = new TabItem(tabFolder, SWT.NONE);
		tbtmThreads.setText("Threads");

		threadTable = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmThreads.setControl(threadTable);
		threadTable.setHeaderVisible(true);
		threadTable.setLinesVisible(true);

		Button btnVisualize = new Button(shlProfilingConsole, SWT.NONE);
		btnVisualize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				startVisualizer();
			}
		});
		btnVisualize.setBounds(363, 10, 75, 25);
		btnVisualize.setText("Visualize");

		MainController.getInstance().setMessageHandler(this);
		MainController.getInstance().setConsole(this);

	}

	protected void startVisualizer() {

		if(visualizingThread!=null) {
			visualizingThread.interrupt();			
		}
		
		
		//Start the visualizer on a different thread.
		visualizingThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Visualizer app = new Visualizer();
				app.start();			
				app.reloadData();
			}
		});
				
		visualizingThread.setPriority(Thread.MAX_PRIORITY);
		visualizingThread.setDaemon(false);
		visualizingThread.start();
				
	}

	void startProfiling() throws ClientException {
		MainController.getInstance().startProfiling();
	}

	void connectToRunningInstance() throws ClientException {

		if (!MainController.getInstance().clientIsConnected()) {
			MainController.getInstance().connectToRunningInstance();
			btnConnect.setText("Disconnect");
		} else {
			MainController.getInstance().disconnect();
			btnConnect.setText("Connect");
		}

	}

	void stopProfiling() {
		MainController.getInstance().stopProfiling();
	}

	public void openSettingsDialog() {
		SettingsDialog sd = new SettingsDialog(shlProfilingConsole, SWT.DIALOG_TRIM);
		sd.setParent(this);
		sd.open();
	}

	@Override
	public void handleError(Exception e) {
		final String message = e.getMessage();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(shlProfilingConsole, "Error", message);
			}
		});
	}

	@Override
	public void displayMessage(String message) {
		final String myMessage = message;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(shlProfilingConsole, "Message", myMessage);
				LoadingDialog.getInstance(shlProfilingConsole, SWT.NONE).closeLoader();		
			}
		});
	}
	
	@Override
	public synchronized void openLoadingDialog() {		
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				LoadingDialog.getInstance(shlProfilingConsole, SWT.NONE).open();
				LoadingDialog.getInstance(shlProfilingConsole, SWT.NONE).showLoader();
			}
		});
	}

	@Override
	public void updateListOfMatchedClasses(Set<String> matchedClasses) {
		final Set<String> matched = matchedClasses;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				ruleTable.removeAll();				
				for (String c : matched) {
					TableItem ti = new TableItem(ruleTable, SWT.NONE);
					ti.setText(c);
				}
			}
		});
	}

	@Override
	public void updateThreadList(Set<ThreadModel> threads) {
		final Set<ThreadModel> threadModels = threads;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				threadTable.removeAll();				
				for (ThreadModel c : threadModels) {
					TableItem ti = new TableItem(threadTable, SWT.NONE);
					ti.setText(c.toString());
				}
			}
		});
	}

	@Override
	public void closeLoadingDialog() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {			
				LoadingDialog.getInstance(shlProfilingConsole, SWT.NONE).closeLoader();				
			}
		});
	}

	@Override
	public void toggleDisconnectLabel() {
		System.out.println("Disconnected!");
		Display.getDefault().syncExec(new Runnable() {
			public void run() {			
				btnConnect.setText("Connect");
			}
		});
	}
}
