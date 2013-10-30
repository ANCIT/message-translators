package org.ancit.messages.translator.editors;

import org.eclipse.jface.viewers.LabelProvider;

import com.memetix.mst.language.Language;

public class LanguageLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		if (element instanceof Language) {
			Language language = (Language) element;
			try {
				return language.getName(language);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.getText(element);
	}

}
