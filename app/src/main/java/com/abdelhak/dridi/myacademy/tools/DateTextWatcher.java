package com.abdelhak.dridi.myacademy.tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
public class DateTextWatcher implements TextWatcher {
    private EditText editText;
    private boolean isDeleting = false;

    public DateTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Check if text is being deleted
        isDeleting = count > after;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Not used
    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = s.toString();
        if (!isDeleting && (input.length() == 2 || input.length() == 5)) {
            // Add hyphens when user types the 3rd and 6th characters
            input += "-";
            editText.setText(input);
            editText.setSelection(input.length());
        } else if (input.length() > 10) {
            // Limit input length to 10 characters (DD-MM-YYYY)
            editText.setText(input.substring(0, 10));
            editText.setSelection(10);
        }
    }
}

