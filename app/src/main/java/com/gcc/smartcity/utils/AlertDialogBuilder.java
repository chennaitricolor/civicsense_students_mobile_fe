package com.gcc.smartcity.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

public class AlertDialogBuilder {
    private static AlertDialogBuilder sInstance;
    private AlertDialog mAlertDialog;

    private AlertDialogBuilder() {
    }

    public static synchronized AlertDialogBuilder getInstance() {

        if (sInstance == null) {
            sInstance = new AlertDialogBuilder();
        }
        return sInstance;
    }

    public void showErrorDialog(String title, String message, String buttonText, Context context) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setMessage(message);

        if (title != null && title.length() > 0) {
            // We can show an alert dialog with an empty title. Hence this check.
            builder.setTitle(title);
        }

        // Generic error dialogs are used for information only. User only clicks on OK to dismiss it.
        builder.setNegativeButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public void showErrorDialog(String title, String message, String buttonText, Context context, final OnSingleBtnDialogListener onSingleBtnDialogListener) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setMessage(message);

        if (title != null && title.length() > 0) {
            // We can show an alert dialog with an empty title. Hence this check.
            builder.setTitle(title);
        }

        // Generic error dialogs are used for information only. User only clicks on OK to dismiss it.
        builder.setNegativeButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (onSingleBtnDialogListener != null)
                    onSingleBtnDialogListener.onSingleButtonClicked();
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    public void showErrorDialog(String title, String message, String negativeButtonText, String positiveButtonText, final String whichDialog, Context context, final OnDialogListener mDialogListener) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setMessage(message);

        if (title != null && title.length() > 0) {
            // We can show an alert dialog with an empty title. Hence this check.
            builder.setTitle(title);
        }

        // Generic error dialogs are used for information only. User only clicks on OK to dismiss it.
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialogListener.onNegativeButtonClick(whichDialog);
            }
        });

        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialogListener.onPositiveButtonClick(whichDialog);

            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();
        mAlertDialog.setCancelable(false);
    }
}
