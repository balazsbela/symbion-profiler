package org.balazsbela.symbion.console.ui;
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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;


public class Profiler {

	protected Shell shlProfilingConsole;
	private Table table;
	private Table table_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Profiler window = new Profiler();
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
		
		Button btnConnect = new Button(shlProfilingConsole, SWT.NONE);
		btnConnect.setBounds(10, 10, 75, 25);
		btnConnect.setText("Connect");
		
		Button btnStartProfiling = new Button(shlProfilingConsole, SWT.NONE);
		btnStartProfiling.setBounds(91, 10, 84, 25);
		btnStartProfiling.setText("Start profiling");
		
		Button btnNewButton = new Button(shlProfilingConsole, SWT.NONE);
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
				SettingsDialog sd = new SettingsDialog(shlProfilingConsole,SWT.DIALOG_TRIM);
				sd.open();
			}
		});
		btnSettings.setBounds(282, 10, 75, 25);
		btnSettings.setText("Settings");
		
		TabFolder tabFolder = new TabFolder(shlProfilingConsole, SWT.NONE);
		tabFolder.setBounds(10, 41, 591, 327);
		
		TabItem tbtmMatchedClasses_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmMatchedClasses_1.setText("Matched classes");
		
		table_1 = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmMatchedClasses_1.setControl(table_1);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		
		TabItem tbtmThreads = new TabItem(tabFolder, SWT.NONE);
		tbtmThreads.setText("Threads");
		
		table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmThreads.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

	}
}
