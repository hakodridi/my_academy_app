package com.abdelhak.dridi.myacademy.tools;

import android.app.ProgressDialog;
import android.content.Context;

import java.util.regex.Pattern;

public class Functions {

    public static final String TAG = "OKAH_LOUBNA";
    public static ProgressDialog progress;


    public static void showProgressDialog(Context context) {
        if (progress == null) {
            progress = new ProgressDialog(context);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }
    }
    public static void dismissProgressDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
            progress = null;
        }
    }

    private static final String EMAIL_PATTERN = "^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }
}
