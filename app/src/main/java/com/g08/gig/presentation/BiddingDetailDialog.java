package com.g08.gig.presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
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
import androidx.core.content.ContextCompat;
import com.g08.gig.R;
import com.g08.gig.util.DateFormats;

import java.time.LocalDateTime;

public class BiddingDetailDialog extends BaseGigDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_BODY = "body";
    private static final String ARG_TIME = "time";
    private static final String ARG_ACTIVE = "active";
    private static final String ARG_REQUEST_ID = "request_id";
    private static final int MAX_LINES_COLLAPSED = 4;
    private static final float DIALOG_MAX_HEIGHT_RATIO = 0.85f;

    private LocalDateTime proposedTime = null;

    public interface OnBiddingActionListener {
        void onBid(int requestId, double bidAmount, LocalDateTime proposedTime);
    }

    private OnBiddingActionListener actionListener;

    public void setActionListener(OnBiddingActionListener listener) {
        this.actionListener = listener;
    }

    public static BiddingDetailDialog newInstance(int requestId, String title, String body, String time, boolean isActive) {
        BiddingDetailDialog dialog = new BiddingDetailDialog();
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
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * BaseGigDialogFragment.DIALOG_WIDTH_RATIO);
            int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * DIALOG_MAX_HEIGHT_RATIO);
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
        TextView statusView = view.findViewById(R.id.dialogRequestStatus);
        ImageButton closeIcon = view.findViewById(R.id.dialogCloseIcon);
        EditText bidAmountInput = view.findViewById(R.id.dialogBidAmountInput);

        Button bidButton = view.findViewById(R.id.dialogBidButton);
        bidButton.setEnabled(false);
        bidButton.setAlpha(0.5f);
        final Button finalBidButton = bidButton;

        titleView.setText(args.getString(ARG_TITLE, ""));
        String body = args.getString(ARG_BODY, "");
        bodyView.setText(body);
        bodyView.setMaxLines(MAX_LINES_COLLAPSED);
        bodyView.setEllipsize(TextUtils.TruncateAt.END);
        timeView.setText(args.getString(ARG_TIME, ""));

        TextView showMoreButton = view.findViewById(R.id.dialogShowMoreButton);
        ScrollView scrollView = view.findViewById(R.id.dialogScrollView);

        bodyView.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bodyView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = bodyView.getWidth();
                if (width > 0 && body.length() > 0) {
                    TextPaint paint = bodyView.getPaint();
                    StaticLayout layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            ? StaticLayout.Builder.obtain(body, 0, body.length(), paint, width)
                            .setMaxLines(Integer.MAX_VALUE)
                            .build()
                            : new StaticLayout(body, paint, width, android.text.Layout.Alignment.ALIGN_NORMAL, 1.3f, 0f, true);
                    if (layout.getLineCount() > MAX_LINES_COLLAPSED) {
                        showMoreButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        showMoreButton.setOnClickListener(v -> {
            if (bodyView.getMaxLines() == MAX_LINES_COLLAPSED) {
                bodyView.setMaxLines(Integer.MAX_VALUE);
                bodyView.setEllipsize(null);
                showMoreButton.setText("Show less");
                bodyView.post(() -> {
                    if (scrollView != null) {
                        int targetScrollY = bodyView.getTop() - bodyView.getPaddingTop();
                        scrollView.smoothScrollTo(0, Math.max(0, targetScrollY));
                    }
                });
            } else {
                bodyView.setMaxLines(MAX_LINES_COLLAPSED);
                bodyView.setEllipsize(TextUtils.TruncateAt.END);
                showMoreButton.setText("Show more");
            }
        });

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

    private void setStatusBackground(TextView view, int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(16f);
        view.setBackground(bg);
    }
}
