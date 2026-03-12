package com.g08.gig.presentation;

import android.widget.EditText;

import java.util.Map;

public final class ValidationErrorBinder {

    private ValidationErrorBinder() {
    }

    public static void setValidationError(Map<String, EditText> fieldToView,
                                          String fieldName,
                                          String message,
                                          EditText defaultField) {
        EditText target = getFieldForValidationError(fieldToView, fieldName, defaultField);
        if (target != null) {
            target.setError(message);
            target.requestFocus();
        }
    }

    public static EditText getFieldForValidationError(Map<String, EditText> fieldToView,
                                                     String fieldName,
                                                     EditText defaultField) {
        if (fieldName != null && fieldToView != null) {
            EditText exact = fieldToView.get(fieldName);
            if (exact != null) return exact;
            for (Map.Entry<String, EditText> e : fieldToView.entrySet()) {
                if (fieldName.startsWith(e.getKey())) return e.getValue();
            }
        }
        return defaultField;
    }
}
