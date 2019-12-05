package com.gcc.smartcity.fontui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;

/**
 * Custom RadioButton which allows us to apply custom font
 */
public class FontRadioButton extends AppCompatRadioButton {

	public FontRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		CustomFont.applyFont(context, this, attrs);
	}

	public FontRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomFont.applyFont(context, this, attrs);
	}

	public FontRadioButton(Context context) {
		super(context);
		CustomFont.applyFont(context, this, null);
	}
	
}
