package org.ancit.messages.translator.dialog;

import org.ancit.messages.translator.editors.LanguageLabelProvider;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.deferred.SetModel;

import com.memetix.mst.language.Language;

public class LanguageSelectionDialog extends Dialog {
	private Text txtFilenametotranslate;
	private Text txtWritetofilename;
	private ComboViewer comboViewer;
	private Language language;
	private IFile fileToTranslate;
	private String writeToFileName;

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @param fileToTranslate 
	 */
	public LanguageSelectionDialog(Shell parentShell, IFile fileToTranslate) {
		super(parentShell);
		this.fileToTranslate = fileToTranslate;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		Label lblFileName = new Label(container, SWT.NONE);
		lblFileName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFileName.setText("Read From");
		
		txtFilenametotranslate = new Text(container, SWT.BORDER);
		txtFilenametotranslate.setText("fileNameToTranslate");
		txtFilenametotranslate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtFilenametotranslate.setText(fileToTranslate.getFullPath().toOSString());
		txtFilenametotranslate.setEnabled(false);
		
		Label lblLanguage = new Label(container, SWT.NONE);
		lblLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLanguage.setText("Language");
		
		comboViewer = new ComboViewer(container, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new LanguageLabelProvider());
		comboViewer.setInput(Language.values());
		
		Label lblWriteTo = new Label(container, SWT.NONE);
		lblWriteTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWriteTo.setText("Write To");
		
		txtWritetofilename = new Text(container, SWT.BORDER);
		txtWritetofilename.setText("writeToFileName");
		txtWritetofilename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtWritetofilename.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String text = txtWritetofilename.getText();
				if(!text.endsWith(".properties")) {
					text += ".properties";
				}
				IContainer parentFolder = fileToTranslate.getParent();
				IFile file = parentFolder.getFile(new Path(text));
				if(file.exists()) {
					getButton(OK).setEnabled(false);
					txtWritetofilename.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				} else {
					getButton(OK).setEnabled(true);
					txtWritetofilename.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
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
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	protected void okPressed() {
		IStructuredSelection sSelection = (IStructuredSelection)comboViewer.getSelection();
		if(!sSelection.isEmpty()) {
			language = (Language)sSelection.getFirstElement();
		}
		
		writeToFileName = txtWritetofilename.getText();
		if(!writeToFileName.endsWith(".properties")) {
			writeToFileName += ".properties";
		}
		
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 208);
	}

	public Language getLanguage() {
		// TODO Auto-generated method stub
		return language;
	}
	
	public String getPropertyFileName() {
		
		return writeToFileName;
	}

}
