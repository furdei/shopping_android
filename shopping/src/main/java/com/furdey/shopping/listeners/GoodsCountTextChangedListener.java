package com.furdey.shopping.listeners;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;

/**
 * Used in goods count edit text to prevent wrong format user edition
 * 
 * @author Stepan Furdey
 */
public class GoodsCountTextChangedListener implements TextWatcher {

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		int prec = 3;

		int cursor = Selection.getSelectionStart(s);
		String str = s.toString();
		str = str.replace(",", ".");
		int pointPos = str.indexOf(".");

		if (cursor > pointPos && pointPos >= 0) {
			int afterDecCnt = str.length() - pointPos - 1;

			if (afterDecCnt > prec) {
				if (cursor >= str.length())
					s.delete(str.length() - 1, str.length());
				else
					s.delete(cursor, cursor + 1);
			}
		}

		// Delete zeros at high positions
		str = s.toString();
		int i = 0;
		for (; i < str.length() && str.charAt(i) == '0'; i++)
			;

		if (i > 0 && i < str.length() && str.charAt(i) != '.')
			s.delete(0, i);
	}

}
