package com.gcc.smartcity.fontui;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFactory {

    public TypeFactory(Context context) {
        String a_BOLD = "Amble-Bold.ttf";
        Typeface ambleBold = Typeface.createFromAsset(context.getAssets(), a_BOLD);
        String a_LIGHT = "Amble-Light.ttf";
        Typeface ambleLight = Typeface.createFromAsset(context.getAssets(), a_LIGHT);
        String a_REGULAR = "Amble-Regular.ttf";
        Typeface ambleRegular = Typeface.createFromAsset(context.getAssets(), a_REGULAR);
        String o_ITALIC = "OpenSans-Italic.ttf";
        Typeface openSansItalic = Typeface.createFromAsset(context.getAssets(), o_ITALIC);
        String o_REGULAR = "OpenSans-Regular.ttf";
        Typeface openSansRegular = Typeface.createFromAsset(context.getAssets(), o_REGULAR);
    }

}