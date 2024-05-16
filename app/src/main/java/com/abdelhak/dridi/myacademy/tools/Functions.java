package com.abdelhak.dridi.myacademy.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.abdelhak.dridi.myacademy.activities.academy.AcademyHomeActivity;

import java.text.DecimalFormat;
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

    public static String formatPhoneNumber(String phoneNumber) {
        String digits = phoneNumber.replaceAll("[^\\d]", "");
        StringBuilder formattedNumber = new StringBuilder();
        for (int i = 0; i < digits.length(); i += 2) {
            formattedNumber.append(digits.substring(i, Math.min(i + 2, digits.length())));
            if (i + 2 < digits.length()) {
                formattedNumber.append("-");
            }
        }

        return formattedNumber.toString();
    }

    public static String formatPrice(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(price);
    }

    public static String[] wilayas = {
            "Adrar",
            "Chlef",
            "Laghouat",
            "Oum El Bouaghi",
            "Batna",
            "Bejaia",
            "Biskra",
            "Bechar",
            "Blida",
            "Bouira",
            "Tamanghasset",
            "Tebessa",
            "Tlemcen",
            "Tiaret",
            "Tizi Ouzou",
            "Algiers",
            "Djelfa",
            "Jijel",
            "Setif",
            "Saida",
            "Skikda",
            "Sidi Bel Abbes",
            "Annaba",
            "Guelma",
            "Constantine",
            "Medea",
            "Mostaganem",
            "M'Sila",
            "Mascara",
            "Ouargla",
            "Oran",
            "El Bayadh",
            "Illizi",
            "Bordj Bou Arreridj",
            "Boumerdes",
            "El Tarf",
            "Tindouf",
            "Tissemsilt",
            "El Oued",
            "Khenchela",
            "Souk Ahras",
            "Tipaza",
            "Mila",
            "Ain Defla",
            "Naama",
            "Ain Temouchent",
            "Ghardaia",
            "Relizane"
    };

    public static void showToastError(Context context) {
        Toast.makeText(context, "There is a problem, check your internet and retry", Toast.LENGTH_SHORT).show();
    }
}
