package org.balazsbela.symbion.console.ui;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.balazsbela.symbion.console.controller.MainController;
import org.balazsbela.symbion.models.SettingsModel;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class SettingsDialog extends Dialog {

	protected Object result;
	protected Shell shlSettingsDialog;
	private Text txtLocalhost;
	private Table ruleTable;
	private SettingsDialog self;
	private ConsoleWindow parent;
	private Text sourceText;
	private Spinner spinner;
	private Button ruleCheckbox;
	private Table rejectTable;
	private TabFolder tabFolder;
	private Text outputPath;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SettingsDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		self=this;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlSettingsDialog.open();
		shlSettingsDialog.layout();
		Display display = getParent().getDisplay();
		while (!shlSettingsDialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlSettingsDialog = new Shell(getParent(), getStyle());
		shlSettingsDialog.setSize(628, 531);
		shlSettingsDialog.setText("Settings Dialog");
		
		shlSettingsDialog.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				try {
					updateSettings();
				} catch (FileNotFoundException e1) {
					MessageDialog.openError(shlSettingsDialog, "Error", "Could not save settings!");
					e1.printStackTrace();
				}
			}
		});
		Group grpHostApplication = new Group(shlSettingsDialog, SWT.NONE);
		grpHostApplication.setText("Host application");
		grpHostApplication.setBounds(10, 44, 197, 90);
		
		Label lblHost = new Label(grpHostApplication, SWT.NONE);
		lblHost.setBounds(10, 28, 55, 15);
		lblHost.setText("Host:");
		
		txtLocalhost = new Text(grpHostApplication, SWT.BORDER);
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(71, 25, 104, 21);
		
		Label lblPort = new Label(grpHostApplication, SWT.NONE);
		lblPort.setBounds(10, 55, 55, 15);
		lblPort.setText("Port:");
		
		spinner = new Spinner(grpHostApplication, SWT.BORDER);
		spinner.setMaximum(100000);
		spinner.setSelection(31337);	
		spinner.setBounds(71, 52, 62, 22);
		
		Group grpMethodRules = new Group(shlSettingsDialog, SWT.NONE);
		grpMethodRules.setText("Method rules");
		grpMethodRules.setBounds(10, 164, 602, 329);
		
		Button btnAddPattern = new Button(grpMethodRules, SWT.NONE);
	
		btnAddPattern.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				openPatternWindow();
			}
		});
		btnAddPattern.setBounds(10, 294, 75, 25);
		btnAddPattern.setText("Add Pattern");
		
		Button btnRemovePattern = new Button(grpMethodRules, SWT.NONE);
		btnRemovePattern.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				if(tabFolder.getSelectionIndex()==0) {
					ruleTable.remove(ruleTable.getSelectionIndex());
				}
				else {
					rejectTable.remove(rejectTable.getSelectionIndex());
				}
			}
		});
		btnRemovePattern.setBounds(91, 294, 107, 25);
		btnRemovePattern.setText("Remove Pattern");
		
		tabFolder = new TabFolder(grpMethodRules, SWT.NONE);		
		tabFolder.setBounds(10, 21, 582, 266);
		
		TabItem tbtmAccept = new TabItem(tabFolder, SWT.NONE);
		tbtmAccept.setText("Accept");
		
		ruleTable = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmAccept.setControl(ruleTable);
		ruleTable.setHeaderVisible(true);
		ruleTable.setLinesVisible(true);
		
		TabItem tbtmReject = new TabItem(tabFolder, SWT.NONE);
		tbtmReject.setText("Reject");
		
		rejectTable = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmReject.setControl(rejectTable);
		rejectTable.setHeaderVisible(true);
		rejectTable.setLinesVisible(true);
		
		ruleCheckbox = new Button(grpMethodRules, SWT.CHECK);
		ruleCheckbox.setBounds(204, 296, 243, 21);
		ruleCheckbox.setText("Apply these rules for calling methods also");
		
		Group grpSourceCodeFolder = new Group(shlSettingsDialog, SWT.NONE);
		grpSourceCodeFolder.setText("Source code folder");
		grpSourceCodeFolder.setBounds(213, 88, 399, 70);
		
		sourceText = new Text(grpSourceCodeFolder, SWT.BORDER);
		sourceText.setBounds(10, 23, 298, 21);
		
		Button btnBrowse = new Button(grpSourceCodeFolder, SWT.NONE);
		
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				DirectoryDialog dlg = new DirectoryDialog(shlSettingsDialog);		     
		        dlg.setFilterPath(sourceText.getText());

		        // Change the title bar text
		        dlg.setText("SWT's DirectoryDialog");

		        // Customizable message displayed in the dialog
		        dlg.setMessage("Select a directory");

		        // Calling open() will open and run the dialog.
		        // It will return the selected directory, or
		        // null if user cancels
		        String dir = dlg.open();
		        if (dir != null) {
		          // Set the text box to the new selection
		        	sourceText.setText(dir);		        	
		        }
			}
		});
		btnBrowse.setBounds(314, 21, 75, 25);
		btnBrowse.setText("Browse");
		
		Group grpOutputFolder = new Group(shlSettingsDialog, SWT.NONE);
		grpOutputFolder.setBounds(213, 10, 399, 72);
		grpOutputFolder.setText("Output folder");
		
		outputPath = new Text(grpOutputFolder, SWT.BORDER);
		outputPath.setBounds(10, 27, 298, 21);
		
		Button btnBrowser = new Button(grpOutputFolder, SWT.NONE);
		
		btnBrowser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				DirectoryDialog dlg = new DirectoryDialog(shlSettingsDialog);		     
		        dlg.setFilterPath(outputPath.getText());

		        // Change the title bar text
		        dlg.setText("SWT's DirectoryDialog");

		        // Customizable message displayed in the dialog
		        dlg.setMessage("Select a directory");

		        // Calling open() will open and run the dialog.
		        // It will return the selected directory, or
		        // null if user cancels
		        String dir = dlg.open();
		        if (dir != null) {
		          // Set the text box to the new selection
		        	outputPath.setText(dir);
		        }
			}
		});
		btnBrowser.setBounds(314, 25, 75, 25);
		btnBrowser.setText("Browse");
		
		loadSettings();

	}

	public Table getRuleTable() {
		return ruleTable;
	}
	
	public void openPatternWindow() {
		PatternWindow pw = new PatternWindow(shlSettingsDialog);
		pw.setParent(self);
		pw.open();
	}

	public void setParent(ConsoleWindow parent) {
		this.parent = parent;
	}
	

	private void updateSettings() throws FileNotFoundException {
		SettingsModel settings = new SettingsModel();
		
		Set<String> rules = new HashSet<String>();
		for(TableItem ti :ruleTable.getItems()) {
			rules.add(ti.getText());
		}		
		
		Set<String> rejectRules = new HashSet<String>();
		for(TableItem ti : rejectTable.getItems() ) {
			rejectRules.add(ti.getText());
		}		
		
		String host = txtLocalhost.getText();
		int port = spinner.getSelection();
		
		String sourcePath = "";
		if(sourceText!=null) {
			sourcePath =sourceText.getText();
		}
		
		String outputPathStr = "";
		if(outputPath!=null) {
			outputPathStr =outputPath.getText();
		}

		settings.setSourcePath(sourcePath);		
		settings.setOutputFolder(outputPathStr);
		settings.setHost(host);
		settings.setPort(port);
		settings.setRules(rules);
		settings.setRejectRules(rejectRules);
		settings.setFilterParents(ruleCheckbox.getSelection());
		MainController.getInstance().saveSettings(settings);
	}		
	
	public void loadSettings() {
		SettingsModel settings = MainController.getInstance().getSettings();
		sourceText.setText(settings.getSourcePath());
		outputPath.setText(settings.getOutputFolder());
		txtLocalhost.setText(settings.getHost());
		spinner.setSelection(settings.getPort());
		ruleCheckbox.setSelection(settings.isFilterParents());
		
		ruleTable.removeAll();
		for(String rule:settings.getRules()) {
			TableItem ti = new TableItem(ruleTable, SWT.NONE);
			ti.setText(rule);
		}
		
		rejectTable.removeAll();
		for(String rule:settings.getRejectRules()) {
			TableItem ti = new TableItem(rejectTable, SWT.NONE);
			ti.setText(rule);
		}
	}

	public TabFolder getTabFolder() {
		return tabFolder;
	}

	public Table getRejectTable() {
		return rejectTable;
	}
}
