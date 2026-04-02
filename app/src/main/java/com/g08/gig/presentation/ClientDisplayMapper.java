package com.g08.gig.presentation;

import android.content.Context;

import androidx.annotation.Nullable;

import com.g08.gig.R;
import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.util.Strings;


public final class ClientDisplayMapper {

    private ClientDisplayMapper() {
    }

    @Nullable
    public static Client clientForAppointment(@Nullable Appointment appt) {
        if (appt == null) {
            return null;
        }
        Request request = appt.getRequest();
        if (request != null && request.getClient() != null) {
            return request.getClient();
        }
        return appt.getClient();
    }

    public static String formatFullName(Context context, @Nullable Client client) {
        return formatFullNameFromParts(context,
                client != null ? client.getFirstName() : null,
                client != null ? client.getLastName() : null);
    }

    public static String formatFullNameFromParts(Context context, @Nullable String firstName,
                                                 @Nullable String lastName) {
        String full = (Strings.nullToEmpty(firstName) + " " + Strings.nullToEmpty(lastName)).trim();
        if (full.isEmpty()) {
            return context.getString(R.string.empty_field_placeholder);
        }
        return full;
    }
}
