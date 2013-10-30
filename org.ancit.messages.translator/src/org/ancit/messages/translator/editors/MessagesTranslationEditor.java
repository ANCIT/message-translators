package org.ancit.messages.translator.editors;


import org.ancit.messages.translator.Activator;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;


/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MessagesTranslationEditor extends MultiPageEditorPart implements IResourceChangeListener{

	/** The text editor used in page 0. */
	private TextEditor editor;

	/** The text widget used in page 2. */
	private StyledText text;

	/**
	 * Creates a multi-page editor example.
	 */
	public MessagesTranslationEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage0() {
		try {
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
	}

	/**
	 * Creates page 2 of the multi-page editor,
	 * which shows the sorted text.
	 */
	void createPage2() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);

		int index = addPage(composite);
		setPageText(index, "Preview");
	}
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage2();
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 1) {
			translateWords();
		}
	}
	private void translateWords() {
		String editorText =
				editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
		//"PropertiesFileTranslator"
		String clientID = Activator.getDefault().getPreferenceStore().getString("Client_ID");
		//"hZvPOCl/f2TPam9T11fSOXv5q5Gci0LQ+dLtzk+12bY="
	    String secretQuestion = Activator.getDefault().getPreferenceStore().getString("Client_SECRETQUESTION");
	    if(clientID == null || clientID.isEmpty() || secretQuestion == null || secretQuestion.isEmpty()) {
	    	text.setText("Client ID and/or Secret Question required to connect to Microsoft BING is not configured." +
	    			"\nGo to windows>preferences>general>i18n Translator to configure the same." +
	    			"\nPlease visit http://www.microsoft.com/web/post/using-the-free-bing-translation-apis to get details on how to get yourself a Client ID and Client Secret Question.");
	    	return;
	    }
	    Translate.setClientId(clientID);
	    Translate.setClientSecret(secretQuestion);
	    StringBuffer outputText = new StringBuffer();
	    
	    String propertiesFileName = ((IFileEditorInput)editor.getEditorInput()).getFile().getName();
	    propertiesFileName = propertiesFileName.substring(0,propertiesFileName.lastIndexOf("."));
	    Language translateLanguage = Language.ENGLISH;
	    if(propertiesFileName.endsWith("_fr") || propertiesFileName.endsWith("_de") || propertiesFileName.equals("_ja")) {
	    	String language = propertiesFileName.substring(propertiesFileName.lastIndexOf("_")+1);
	    	translateLanguage = getLanguage(language);
	    } else {
	    	ElementListSelectionDialog dialog = 
	    		     new ElementListSelectionDialog(Display.getDefault().getActiveShell(), new LanguageLabelProvider());
	    		dialog.setTitle("Language Selection");
	    		dialog.setMessage("Select a Language to translate into... (* = any string, ? = any char):");
	    		dialog.setElements(Language.values());
	    		int open = dialog.open();
	    		if(open == IDialogConstants.CANCEL_ID) {
	    			text.setText("Error Occurred while Translation of your Properties File." +
							"\nYou did not select a Language to translate into...");
	    			return;
	    		}
	    		translateLanguage =  (Language)dialog.getFirstResult();
	    }
	    
		String[] propertyRecords = editorText.split("\n");
		for (String propertyRecord : propertyRecords) {
			String[] propertyEntry = propertyRecord.split("=");
			if(propertyEntry.length == 2) {
				String key = propertyEntry[0];
				String value = propertyEntry[1];

				try {
					value = Translate.execute(value, translateLanguage);
					outputText.append(key+"="+value+"\n");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					text.setText("Error Occurred while Translation of your Properties File." +
							"\nVerify if the ClientID and ClientSecretQuestion required for BING is configued properly.");
					return;
				}
			}
		}
		text.setText("#This Property File is translated by ANCIT's Message i18n Translator Plugin \n"+outputText.toString());
	}
	private Language getLanguage(String language) {		
		return Language.fromString(language);
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
	
	

}
