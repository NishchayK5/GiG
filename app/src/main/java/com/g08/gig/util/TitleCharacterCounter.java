package com.g08.gig.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.g08.gig.R;

public final class TitleCharacterCounter {

    private TitleCharacterCounter() {
    }

    public static void attach(@NonNull EditText editText, @NonNull TextView counterLabel, int maxLength) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateLabel(editText, counterLabel, maxLength);
            }
        };
        editText.addTextChangedListener(watcher);
        updateLabel(editText, counterLabel, maxLength);
    }

    private static void updateLabel(EditText editText, TextView counterLabel, int maxLength) {
        int len = editText.getText() != null ? editText.getText().length() : 0;
        int remaining = Math.max(0, maxLength - len);
        counterLabel.setText(editText.getContext().getResources().getQuantityString(
                R.plurals.title_field_characters_remaining,
                remaining,
                remaining));
    }
}
