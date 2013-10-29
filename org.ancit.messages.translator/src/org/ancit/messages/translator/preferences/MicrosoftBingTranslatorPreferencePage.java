package org.ancit.messages.translator.preferences;

import org.ancit.messages.translator.Activator;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class MicrosoftBingTranslatorPreferencePage extends
		FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public MicrosoftBingTranslatorPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	public MicrosoftBingTranslatorPreferencePage(int style) {
		super(style);
		// TODO Auto-generated constructor stub
	}

	public MicrosoftBingTranslatorPreferencePage(String title, int style) {
		super(title, style);
		// TODO Auto-generated constructor stub
	}

	public MicrosoftBingTranslatorPreferencePage(String title,
			ImageDescriptor image, int style) {
		super(title, image, style);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		StringFieldEditor editor = new StringFieldEditor("Client_ID", "Client ID", getFieldEditorParent());
		addField(editor);
		editor = new StringFieldEditor("Client_SECRETQUESTION", "Client Secret Question", getFieldEditorParent());
		addField(editor);
	}

}
