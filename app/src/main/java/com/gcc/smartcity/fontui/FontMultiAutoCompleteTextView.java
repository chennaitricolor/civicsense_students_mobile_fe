package com.gcc.smartcity.fontui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

/**
 * Custom MultiAutoCompleteTextView which allows us to apply custom font
 */
public class FontMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {

	public FontMultiAutoCompleteTextView(Context context) {
		super(context);
		CustomFont.applyFont(context, this, null);
	}

	public FontMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomFont.applyFont(context, this, attrs);
	}

	public FontMultiAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		CustomFont.applyFont(context, this, attrs);
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {

	}
	
}
