package com.g08.gig.presentation;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.g08.gig.R;
import com.g08.gig.models.Bid;

import java.util.ArrayList;
import java.util.List;

public class RequestDetailDialog extends BaseGigDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_BODY = "body";
    private static final String ARG_TIME = "time";
    private static final String ARG_ACTIVE = "active";
    private static final String ARG_REQUEST_ID = "request_id";

    private static final int MAX_LINES_COLLAPSED = 4;
    private static final float DIALOG_MAX_HEIGHT_RATIO = 0.85f;

    public interface OnRequestActionListener {
        void onDeleteRequest(int requestId);
        void onCompleteRequest(int requestId);
        /** Client accepts this bid: create appointment and typically complete the request. */
        void onAcceptBid(Bid bid);
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
        bindHeader(view, args);
        bindDescription(view, args);
        bindStatus(view, args);
        bindBids(view);
        bindListeners(view, requestId);
    }

    private void bindHeader(View view, Bundle args) {
        TextView titleView = view.findViewById(R.id.dialogRequestTitle);
        titleView.setText(args.getString(ARG_TITLE, ""));
    }

    private void bindDescription(View view, Bundle args) {
        TextView bodyView = view.findViewById(R.id.dialogRequestBody);
        TextView timeView = view.findViewById(R.id.dialogRequestTime);
        String body = args.getString(ARG_BODY, "");
        bodyView.setText(body);
        bodyView.setMaxLines(MAX_LINES_COLLAPSED);
        bodyView.setEllipsize(android.text.TextUtils.TruncateAt.END);
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
                bodyView.setEllipsize(android.text.TextUtils.TruncateAt.END);
                showMoreButton.setText("Show more");
            }
        });
    }

    private void bindStatus(View view, Bundle args) {
        TextView statusView = view.findViewById(R.id.dialogRequestStatus);
        Button deleteButton = view.findViewById(R.id.dialogDeleteButton);
        Button completeButton = view.findViewById(R.id.dialogCompleteButton);
        boolean isActive = args.getBoolean(ARG_ACTIVE, true);
        if (isActive) {
            statusView.setText(R.string.request_detail_status_active);
            setStatusBackground(statusView, ContextCompat.getColor(requireContext(), R.color.main_cust_color));
        } else {
            statusView.setText(R.string.request_detail_status_closed);
            setStatusBackground(statusView, ContextCompat.getColor(requireContext(), R.color.grey));
            deleteButton.setVisibility(View.GONE);
            completeButton.setVisibility(View.GONE);
        }
    }

    private void bindBids(View view) {
        Bundle args = getArguments();
        boolean requestIsActive = args == null || args.getBoolean(ARG_ACTIVE, true);

        LinearLayout bidsContainer = view.findViewById(R.id.dialogBidsContainer);
        View bidsEmptyView = view.findViewById(R.id.dialogBidsEmptyView);
        TextView bidsProfileHint = view.findViewById(R.id.dialogBidsProfileHint);
        bidsContainer.removeAllViews();
        for (Bid bid : bids) {
            final Bid bidForRow = bid;
            View bidRow = LayoutInflater.from(requireContext()).inflate(R.layout.bid_row, bidsContainer, false);
            TextView bidderName = bidRow.findViewById(R.id.bidRowName);
            TextView bidCost = bidRow.findViewById(R.id.bidRowCost);
            Button acceptButton = bidRow.findViewById(R.id.bidRowAcceptButton);
            if (bidForRow.getPro() != null) {
                bidderName.setText(bidForRow.getPro().getFirstName() + " " + bidForRow.getPro().getLastName());
                final int proId = bidForRow.getPro().getId();
                bidderName.setOnClickListener(v -> {
                    Intent i = new Intent(requireContext(), ProPublicDetailActivity.class);
                    i.putExtra(ProPublicDetailActivity.EXTRA_PRO_ID, proId);
                    startActivity(i);
                });
            } else {
                bidderName.setText("");
                bidderName.setOnClickListener(null);
                bidderName.setClickable(false);
            }
            bidCost.setText(String.format("$%.2f", bidForRow.getCost()));

            boolean canAccept = requestIsActive
                    && bidForRow.getPro() != null
                    && bidForRow.getRequest() != null;
            acceptButton.setVisibility(canAccept ? View.VISIBLE : View.GONE);
            acceptButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onAcceptBid(bidForRow);
                }
                dismiss();
            });
            bidsContainer.addView(bidRow);
        }
        boolean hasBids = !bids.isEmpty();
        bidsEmptyView.setVisibility(hasBids ? View.GONE : View.VISIBLE);
        bidsContainer.setVisibility(hasBids ? View.VISIBLE : View.GONE);
        if (bidsProfileHint != null) {
            bidsProfileHint.setVisibility(hasBids ? View.VISIBLE : View.GONE);
        }
    }

    private void bindListeners(View view, int requestId) {
        ImageButton closeIcon = view.findViewById(R.id.dialogCloseIcon);
        Button deleteButton = view.findViewById(R.id.dialogDeleteButton);
        Button completeButton = view.findViewById(R.id.dialogCompleteButton);
        closeIcon.setOnClickListener(v -> dismiss());
        deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteRequest(requestId);
            }
            dismiss();
        });
        completeButton.setOnClickListener(v -> {
            dismiss();
            if (actionListener != null) {
                actionListener.onCompleteRequest(requestId);
            }
        });
    }

    private void setStatusBackground(TextView view, int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(16f);
        view.setBackground(bg);
    }
}
