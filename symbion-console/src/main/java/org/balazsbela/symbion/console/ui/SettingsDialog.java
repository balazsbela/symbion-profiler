package org.balazsbela.symbion.console.ui;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


public class SettingsDialog extends Dialog {

	protected Object result;
	protected Shell shlSettingsDialog;
	private Text txtLocalhost;
	private Text text;
	private Table table;
	private Table table_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SettingsDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
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
		shlSettingsDialog.setSize(628, 476);
		shlSettingsDialog.setText("Settings Dialog");
		
		Group grpHostApplication = new Group(shlSettingsDialog, SWT.NONE);
		grpHostApplication.setText("Host application");
		grpHostApplication.setBounds(10, 10, 602, 93);
		
		Label lblHost = new Label(grpHostApplication, SWT.NONE);
		lblHost.setBounds(10, 28, 55, 15);
		lblHost.setText("Host:");
		
		txtLocalhost = new Text(grpHostApplication, SWT.BORDER);
		txtLocalhost.setText("localhost");
		txtLocalhost.setBounds(71, 25, 76, 21);
		
		text = new Text(grpHostApplication, SWT.BORDER);
		text.setText("7800");
		text.setBounds(71, 52, 76, 21);
		
		Label lblPort = new Label(grpHostApplication, SWT.NONE);
		lblPort.setBounds(10, 55, 55, 15);
		lblPort.setText("Port:");
		
		Group grpMethodRules = new Group(shlSettingsDialog, SWT.NONE);
		grpMethodRules.setText("Method rules");
		grpMethodRules.setBounds(10, 109, 602, 329);
		
		Button btnAddPattern = new Button(grpMethodRules, SWT.NONE);
		btnAddPattern.setBounds(10, 294, 75, 25);
		btnAddPattern.setText("Add Pattern");
		
		Button btnRemovePattern = new Button(grpMethodRules, SWT.NONE);
		btnRemovePattern.setBounds(91, 294, 107, 25);
		btnRemovePattern.setText("Remove Pattern");
		
		TabFolder tabFolder = new TabFolder(grpMethodRules, SWT.NONE);
		tabFolder.setBounds(10, 21, 582, 266);
		
		TabItem tbtmAccept = new TabItem(tabFolder, SWT.NONE);
		tbtmAccept.setText("Accept");
		
		table = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmAccept.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TabItem tbtmReject = new TabItem(tabFolder, SWT.NONE);
		tbtmReject.setText("Reject");
		
		table_1 = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbtmReject.setControl(table_1);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		
		TableItem tableItem = new TableItem(table_1, SWT.NONE);
		tableItem.setText("org.balazsbela.mytestapplication.*(*)");

	}
}
