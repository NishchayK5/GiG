package com.g08.gig.presentation;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.g08.gig.R;
import com.g08.gig.models.Bid;

import java.util.ArrayList;
import java.util.List;

public class RequestDetailDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_BODY = "body";
    private static final String ARG_TIME = "time";
    private static final String ARG_ACTIVE = "active";
    private static final String ARG_REQUEST_ID = "request_id";

    public interface OnRequestActionListener {
        void onDeleteRequest(int requestId);
        void onCompleteRequest(int requestId);
    }

    private OnRequestActionListener actionListener;
    private List<Bid> bids = new ArrayList<>();

    public void setActionListener(OnRequestActionListener listener) {
        this.actionListener = listener;
    }
    public void setBids(List<Bid> bids) {
        this.bids = bids != null ? bids : new ArrayList<>();
    }

    public static RequestDetailDialog newInstance(int requestId, String title, String body, String time, boolean isActive) {
        RequestDetailDialog dialog = new RequestDetailDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_ID, requestId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_BODY, body);
        args.putString(ARG_TIME, time);
        args.putBoolean(ARG_ACTIVE, isActive);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_request_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            dismiss();
            return;
        }

        int requestId = args.getInt(ARG_REQUEST_ID, -1);

        TextView titleView = view.findViewById(R.id.dialogRequestTitle);
        TextView bodyView = view.findViewById(R.id.dialogRequestBody);
        TextView timeView = view.findViewById(R.id.dialogRequestTime);
        TextView statusView = view.findViewById(R.id.dialogRequestStatus);
        ImageButton closeIcon = view.findViewById(R.id.dialogCloseIcon);
        Button deleteButton = view.findViewById(R.id.dialogDeleteButton);
        Button completeButton = view.findViewById(R.id.dialogCompleteButton);
        LinearLayout bidsContainer = view.findViewById(R.id.dialogBidsContainer);


        titleView.setText(args.getString(ARG_TITLE, ""));
        bodyView.setText(args.getString(ARG_BODY, ""));
        timeView.setText(args.getString(ARG_TIME, ""));

        boolean isActive = args.getBoolean(ARG_ACTIVE, true);
        if (isActive) {
            statusView.setText(R.string.request_detail_status_active);
            setStatusBackground(statusView, ContextCompat.getColor(requireContext(), R.color.main_cust_color));
        } else {
            statusView.setText(R.string.request_detail_status_closed);
            setStatusBackground(statusView, ContextCompat.getColor(requireContext(), R.color.grey));
        }

        bidsContainer.removeAllViews();
        for (Bid bid : bids) {
            View bidRow = LayoutInflater.from(requireContext()).inflate(R.layout.bid_row, bidsContainer, false);
            TextView bidderName = bidRow.findViewById(R.id.bidRowName);
            TextView bidCost = bidRow.findViewById(R.id.bidRowCost);
            bidderName.setText(bid.getPro().getFirstName() + " " + bid.getPro().getLastName());
            bidCost.setText(String.format("$%.2f", bid.getCost()));
            bidsContainer.addView(bidRow);
        }

        closeIcon.setOnClickListener(v -> dismiss());

        deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteRequest(requestId);
            }
            dismiss();
        });

        completeButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onCompleteRequest(requestId);
            }
            dismiss();
        });
    }

    private void setStatusBackground(TextView view, int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(16f);
        view.setBackground(bg);
    }
}
