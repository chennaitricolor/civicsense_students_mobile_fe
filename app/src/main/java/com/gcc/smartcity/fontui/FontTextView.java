package com.gcc.smartcity.fontui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Custom TextView which allows us to apply custom font
 */
public class FontTextView extends AppCompatTextView {

	public FontTextView(Context context) {
		super(context);
		CustomFont.applyFont(context, this, null);
	}
	
	public FontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		CustomFont.applyFont(context, this, attrs);
	}
	
	public FontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		CustomFont.applyFont(context, this, attrs);
	}
	
}
