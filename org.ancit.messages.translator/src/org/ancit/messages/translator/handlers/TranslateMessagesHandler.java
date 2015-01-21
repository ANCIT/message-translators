package org.ancit.messages.translator.handlers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.ancit.messages.translator.Activator;
import org.ancit.messages.translator.dialog.LanguageSelectionDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class TranslateMessagesHandler extends AbstractHandler {

	private String newFilePropertyFileName;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.getFirstElement() instanceof IFile) {
				IFile file = (IFile) sSelection.getFirstElement();
				translateWords(file);
			}
		}
		return null;
	}
	
	private void translateWords(IFile file) {
		
		BufferedReader br = null;
		String editorText = "";
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(file.getLocation().toOSString()));
 
			while ((sCurrentLine = br.readLine()) != null) {
				editorText += sCurrentLine + "\n";
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		//"PropertiesFileTranslator"
		String clientID = Activator.getDefault().getPreferenceStore().getString("Client_ID");
		//"hZvPOCl/f2TPam9T11fSOXv5q5Gci0LQ+dLtzk+12bY="
	    String secretQuestion = Activator.getDefault().getPreferenceStore().getString("Client_SECRETQUESTION");
	    if(clientID == null || clientID.isEmpty() || secretQuestion == null || secretQuestion.isEmpty()) {
	    	MessageDialog.openError(Display.getDefault().getActiveShell(), "MS Bing Configuration Error", ("Client ID and/or Secret Question required to connect to Microsoft BING is not configured." +
	    			"\nGo to windows>preferences>general>i18n Translator to configure the same." +
	    			"\nPlease visit http://www.microsoft.com/web/post/using-the-free-bing-translation-apis to get details on how to get yourself a Client ID and Client Secret Question."));
	    	return;
	    }
	    Translate.setClientId(clientID);
	    Translate.setClientSecret(secretQuestion);
	    StringBuffer outputText = new StringBuffer();
	    
	    String propertiesFileName = file.getName();
	    propertiesFileName = propertiesFileName.substring(0,propertiesFileName.lastIndexOf("."));
	    Language translateLanguage = Language.ENGLISH;
	    if(propertiesFileName.endsWith("_fr") || propertiesFileName.endsWith("_de") || propertiesFileName.equals("_ja")) {
	    	String language = propertiesFileName.substring(propertiesFileName.lastIndexOf("_")+1);
	    	translateLanguage = getLanguage(language);
	    } else {
//	    	ElementListSelectionDialog dialog = 
//	    		     new ElementListSelectionDialog(Display.getDefault().getActiveShell(), new LanguageLabelProvider());
//	    		dialog.setTitle("Language Selection");
//	    		dialog.setMessage("Select a Language to translate into... (* = any string, ? = any char):");
//	    		dialog.setElements(Language.values());
//	    		int open = dialog.open();
//	    		if(open == IDialogConstants.CANCEL_ID) {
//	    			System.out.println("Error Occurred while Translation of your Properties File." +
//							"\nYou did not select a Language to translate into...");
//	    			return;
//	    		}
//	    		translateLanguage =  (Language)dialog.getFirstResult();
	    	LanguageSelectionDialog selectionDialog = new LanguageSelectionDialog(Display.getDefault().getActiveShell(), file);
	    	int open = selectionDialog.open();
	    	if(open == IDialogConstants.CANCEL_ID) {
    			System.out.println("Error Occurred while Translation of your Properties File." +
						"\nYou did not select a Language to translate into...");
    			return;
    		}
    		translateLanguage =  (Language)selectionDialog.getLanguage();
    		newFilePropertyFileName = selectionDialog.getPropertyFileName();
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
					System.out.println("Error Occurred while Translation of your Properties File." +
							"\nVerify if the ClientID and ClientSecretQuestion required for BING is configued properly.");
					return;
				}
			}
		}
		String translatedText = "#This Property File is translated by ANCIT's Message i18n Translator Plugin \n"+outputText.toString();
		System.out.println("Translated to "+translateLanguage.name());
		System.out.println(translatedText);
		
		try {
			 
			IFile newFile = file.getParent().getFile(new Path(newFilePropertyFileName));
			
			// convert String into InputStream
			InputStream is = new ByteArrayInputStream(translatedText.getBytes());
			newFile.create(is, true, null);
 			System.out.println("Done");
 
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Language getLanguage(String language) {		
		return Language.fromString(language);
	}

}
