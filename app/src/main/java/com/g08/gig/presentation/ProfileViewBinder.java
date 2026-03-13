package com.g08.gig.presentation;

import android.app.Activity;
import android.widget.TextView;

import com.g08.gig.R;
import com.g08.gig.models.User;

public final class ProfileViewBinder {

    private ProfileViewBinder() {
    }

    public static void bind(Activity activity, User user) {
        if (user == null) return;

        TextView first = activity.findViewById(R.id.profileFirstName);
        TextView last = activity.findViewById(R.id.profileLastName);
        TextView email = activity.findViewById(R.id.profileEmail);

        if (first != null) first.setText(user.getFirstName() != null ? user.getFirstName() : "");
        if (last != null) last.setText(user.getLastName() != null ? user.getLastName() : "");
        if (email != null) email.setText(user.getEmail() != null ? user.getEmail() : "");
    }
}
