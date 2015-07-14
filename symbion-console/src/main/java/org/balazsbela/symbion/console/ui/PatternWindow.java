package org.balazsbela.symbion.console.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

public class PatternWindow extends Dialog {
	private Text acceptTextField;
	SettingsDialog parent;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public PatternWindow(Shell parentShell) {		
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 4;
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblAllUnmatchedMethods = new Label(container, SWT.NONE);
		lblAllUnmatchedMethods.setText("All unmatched methods will be rejected by default.");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblYouCanUse = new Label(container, SWT.NONE);
		lblYouCanUse.setText("You can use wildcards.");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblExampleorgmypackagesubpackage = new Label(container, SWT.NONE);
		lblExampleorgmypackagesubpackage.setText("Example:org.mypackage.subpackage.*(*)");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblThisWillMake = new Label(container, SWT.NONE);
		lblThisWillMake.setText("This will make the profiler accept all methods in all classes");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblOfThatPackage = new Label(container, SWT.NONE);
		lblOfThatPackage.setText("of that package, regardless of signature.");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblPatternToAccept = new Label(container, SWT.NONE);
		lblPatternToAccept.setText("Pattern:");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		acceptTextField = new Text(container, SWT.BORDER);
		acceptTextField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		acceptTextField.addTraverseListener(new TraverseListener() {
			
			@Override
			public void keyTraversed(TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_RETURN) {
					String rule = acceptTextField.getText();
					addRule(rule);
				}				
			}
		});
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				String rule = acceptTextField.getText();
				addRule(rule);
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	public void setParent(SettingsDialog parent) {
		this.parent = parent;
	}

	public void addRule(String rule) {
		TableItem ruleItem;

		if(parent.getTabFolder().getSelectionIndex() == 0){
			ruleItem = new TableItem(parent.getRuleTable(),SWT.NONE);
		}
		else {
			ruleItem = new TableItem(parent.getRejectTable(),SWT.NONE);
		}
		ruleItem.setText(rule);
	}
}
