package com.gcc.smartcity.fontui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * Custom EditText which allows us to apply custom font
 */
public class FontEditText extends AppCompatEditText {

    EditTextListenerInterface editTextListenerInterface;

    Context mContext;

    public FontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFont.applyFont(context, this, attrs);
        mContext = context;
    }

    public FontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFont.applyFont(context, this, attrs);
        mContext = context;
    }

    public FontEditText(Context context) {
        super(context);
        CustomFont.applyFont(context, this, null);
        mContext = context;
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables(null, null, icon, null);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if ((editTextListenerInterface != null) && (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP))
            editTextListenerInterface.onKeyboardBackButtonPressed();
        return super.onKeyPreIme(keyCode, event);
    }

    public void setEditTextListenerInterface(EditTextListenerInterface editTextListenerInterface) {
        this.editTextListenerInterface = editTextListenerInterface;
    }

    public interface EditTextListenerInterface {
        void onKeyboardBackButtonPressed();
    }
}
