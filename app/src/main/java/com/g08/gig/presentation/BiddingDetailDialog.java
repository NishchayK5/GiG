package com.g08.gig.presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.g08.gig.R;
import com.g08.gig.models.Client;
import com.g08.gig.util.DateFormats;
import com.g08.gig.util.DialogBodyHelper;

import java.time.LocalDateTime;

public class BiddingDetailDialog extends BaseGigDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_BODY = "body";
    private static final String ARG_TIME = "time";
    private static final String ARG_ACTIVE = "active";
    private static final String ARG_REQUEST_ID = "request_id";
    private static final String ARG_CLIENT_FIRST = "client_first";
    private static final String ARG_CLIENT_LAST = "client_last";
    private static final String ARG_CLIENT_ADDRESS = "client_address";
    private static final String ARG_HAS_CLIENT = "has_client";

    private LocalDateTime proposedTime = null;

    public interface OnBiddingActionListener {
        void onBid(int requestId, double bidAmount, LocalDateTime proposedTime);
    }

    private OnBiddingActionListener actionListener;

    public void setActionListener(OnBiddingActionListener listener) {
        this.actionListener = listener;
    }

    public static BiddingDetailDialog newInstance(int requestId, String title, String body, String time, boolean isActive,
                                                  Client client) {
        BiddingDetailDialog dialog = new BiddingDetailDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_ID, requestId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_BODY, body);
        args.putString(ARG_TIME, time);
        args.putBoolean(ARG_ACTIVE, isActive);
        boolean hasClient = client != null;
        args.putBoolean(ARG_HAS_CLIENT, hasClient);
        if (hasClient) {
            args.putString(ARG_CLIENT_FIRST, nullToEmpty(client.getFirstName()));
            args.putString(ARG_CLIENT_LAST, nullToEmpty(client.getLastName()));
            args.putString(ARG_CLIENT_ADDRESS, nullToEmpty(client.getAddress()));
        }
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * BaseGigDialogFragment.DIALOG_WIDTH_RATIO);
            int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * BaseGigDialogFragment.DIALOG_MAX_HEIGHT_RATIO);
            getDialog().getWindow().setLayout(width, maxHeight);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bidding_detail, container, false);
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
        View clientSection = view.findViewById(R.id.dialogClientSection);
        TextView clientName = view.findViewById(R.id.dialogClientName);
        TextView clientAddress = view.findViewById(R.id.dialogClientAddress);
        ImageButton closeIcon = view.findViewById(R.id.dialogCloseIcon);
        EditText bidAmountInput = view.findViewById(R.id.dialogBidAmountInput);

        Button bidButton = view.findViewById(R.id.dialogBidButton);
        bidButton.setEnabled(false);
        bidButton.setAlpha(0.5f);
        final Button finalBidButton = bidButton;

        titleView.setText(args.getString(ARG_TITLE, ""));
        String body = args.getString(ARG_BODY, "");
        TextView showMoreButton = view.findViewById(R.id.dialogShowMoreButton);
        ScrollView scrollView = view.findViewById(R.id.dialogScrollView);
        DialogBodyHelper.setupExpandableDescription(bodyView, showMoreButton, scrollView, body);
        timeView.setText(args.getString(ARG_TIME, ""));

        if (args.getBoolean(ARG_HAS_CLIENT, false)) {
            clientSection.setVisibility(View.VISIBLE);
            String first = args.getString(ARG_CLIENT_FIRST, "");
            String last = args.getString(ARG_CLIENT_LAST, "");
            String fullName = (nullToEmpty(first) + " " + nullToEmpty(last)).trim();
            clientName.setText(fullName.isEmpty() ? "\u2014" : fullName);
            clientAddress.setText(args.getString(ARG_CLIENT_ADDRESS, ""));
        } else {
            clientSection.setVisibility(View.GONE);
        }

        closeIcon.setOnClickListener(v -> dismiss());

        Button pickTimeButton = view.findViewById(R.id.dialogPickTimeButton);
        TextView pickedTimeView = view.findViewById(R.id.dialogPickedTimeView);

        pickTimeButton.setOnClickListener(v -> {
            LocalDateTime initial = proposedTime != null ? proposedTime : LocalDateTime.now();
            DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                    (dateView, year, month, day) -> {
                        TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                                (timeView2, hour, minute) -> {
                                    proposedTime = LocalDateTime.of(year, month + 1, day, hour, minute);
                                    pickedTimeView.setText(proposedTime.format(DateFormats.REQUEST_TIME));
                                    pickedTimeView.setVisibility(View.VISIBLE);
                                    finalBidButton.setEnabled(true);
                                    finalBidButton.setAlpha(1.0f);
                                },
                                initial.getHour(), initial.getMinute(), false);
                        timePicker.show();
                    },
                    initial.getYear(), initial.getMonthValue() - 1, initial.getDayOfMonth());

            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            datePicker.show();
        });

        bidButton.setOnClickListener(v -> {
            String amountText = bidAmountInput.getText().toString().trim();
            if (amountText.isEmpty()) {
                bidAmountInput.setError(getString(R.string.bid_amount_required));
                bidAmountInput.requestFocus();
                return;
            }

            if (proposedTime == null) {
                Toast.makeText(requireContext(), "Please select a proposed time", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                bidAmountInput.setError(getString(R.string.bid_amount_required));
                bidAmountInput.requestFocus();
                return;
            }

            if (actionListener != null) {
                actionListener.onBid(requestId, amount, proposedTime);
            }
            dismiss();
        });
    }

    private static String nullToEmpty(String s) {
        return s != null ? s : "";
    }

}
