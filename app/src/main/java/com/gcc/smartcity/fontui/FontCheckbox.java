package com.gcc.smartcity.fontui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * Custom CheckBox which allows us to apply custom font
 */
public class FontCheckbox extends AppCompatCheckBox {

    public FontCheckbox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFont.applyFont(context, this, attrs);
    }

    public FontCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFont.applyFont(context, this, attrs);
    }

    public FontCheckbox(Context context) {
        super(context);
        CustomFont.applyFont(context, this, null);
    }

}
