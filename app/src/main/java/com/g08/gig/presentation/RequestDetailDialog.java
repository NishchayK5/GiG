package com.g08.gig.presentation;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.g08.gig.models.Bid;
import com.g08.gig.util.DateFormats;
import com.g08.gig.util.DialogBodyHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RequestDetailDialog extends BaseGigDialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_BODY = "body";
    private static final String ARG_TIME = "time";
    private static final String ARG_ACTIVE = "active";
    private static final String ARG_REQUEST_ID = "request_id";

    public interface OnRequestActionListener {
        void onDeleteRequest(int requestId);
        void onCompleteRequest(int requestId);
        /** Client accepts this bid: create appointment and typically complete the request. */
        void onAcceptBid(Bid bid);
    }

    private OnRequestActionListener actionListener;
    private List<Bid> bids = new ArrayList<>();
    private Bid selectedBid = null;

    public void setActionListener(OnRequestActionListener listener) {
        this.actionListener = listener;
    }
    public void setBids(List<Bid> bids) {
        this.bids = bids != null ? bids : new ArrayList<>();
    }

    private void setCompleteButtonState(Button completeButton) {
        completeButton.setEnabled(selectedBid != null);
        completeButton.setAlpha(selectedBid != null ? 1.0f : 0.5f);
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
            int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * BaseGigDialogFragment.DIALOG_MAX_HEIGHT_RATIO);
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
        TextView showMoreButton = view.findViewById(R.id.dialogShowMoreButton);
        ScrollView scrollView = view.findViewById(R.id.dialogScrollView);
        DialogBodyHelper.setupExpandableDescription(bodyView, showMoreButton, scrollView, body);
        timeView.setText(args.getString(ARG_TIME, ""));
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

            TextView bidProposedTime = bidRow.findViewById(R.id.bidRowProposedTime);
            if (bidForRow.getTimeOfBid() != null) {
                LocalDateTime start = bidForRow.getTimeOfBid();
                LocalDateTime end = start.plusHours(1);
                bidProposedTime.setText(
                        start.format(DateFormats.REQUEST_TIME) + " to " + end.format(DateFormats.REQUEST_TIME)
                );
                bidProposedTime.setVisibility(View.VISIBLE);
            } else {
                bidProposedTime.setVisibility(View.GONE);
            }

            boolean canAccept = requestIsActive
                    && bidForRow.getPro() != null
                    && bidForRow.getRequest() != null;
            acceptButton.setVisibility(canAccept ? View.VISIBLE : View.GONE);
            acceptButton.setOnClickListener(v -> {
                selectedBid = bidForRow;

                for (int i = 0; i < bidsContainer.getChildCount(); i++) {
                    View row = bidsContainer.getChildAt(i);
                    row.setAlpha(i == bidsContainer.indexOfChild(bidRow) ? 1.0f : 0.5f);
                    Button btn = row.findViewById(R.id.bidRowAcceptButton);
                    if (btn != null) {
                        btn.setEnabled(false);
                    }
                }

                if (requestIsActive) {
                    TextView statusView = view.findViewById(R.id.dialogRequestStatus);
                    if (statusView != null) {
                        statusView.setText("Ongoing");
                        setStatusBackground(statusView, ContextCompat.getColor(requireContext(), R.color.main_cust_color_legacy));
                    }
                }
                acceptButton.setText("Accepted");

                if (actionListener != null) {
                    actionListener.onAcceptBid(bidForRow);
                }

                Button completeButton = getView() == null ? null : getView().findViewById(R.id.dialogCompleteButton);
                if (completeButton != null) setCompleteButtonState(completeButton);
            });
            if (bidForRow.getBidStatus()) {
                selectedBid = bidForRow;
                acceptButton.setText("Accepted");
                acceptButton.setEnabled(false);
            }
            bidsContainer.addView(bidRow);
        }

        if (selectedBid != null) {
            for (int i = 0; i < bidsContainer.getChildCount(); i++) {
                View row = bidsContainer.getChildAt(i);
                Bid rowBid = bids.get(i);
                row.setAlpha(rowBid.getId() == selectedBid.getId() ? 1.0f : 0.5f);
                Button btn = row.findViewById(R.id.bidRowAcceptButton);
                if (btn != null) btn.setEnabled(false);
            }
            TextView statusView = view.findViewById(R.id.dialogRequestStatus);
            if (statusView != null && requestIsActive) {
                statusView.setText("Ongoing");
                setStatusBackground(statusView, ContextCompat.getColor(requireContext(), R.color.main_cust_color_legacy));
            }
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

        setCompleteButtonState(completeButton);

        closeIcon.setOnClickListener(v -> dismiss());

        deleteButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.request_confirm_delete_title)
                .setMessage(R.string.request_confirm_delete_message)
                .setNegativeButton(R.string.complete_review_cancel, null)
                .setPositiveButton(R.string.request_detail_delete, (d, which) -> {
                    if (actionListener != null) {
                        actionListener.onDeleteRequest(requestId);
                    }
                    dismiss();
                })
                .show());

        completeButton.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.request_confirm_complete_title)
                .setMessage(R.string.request_confirm_complete_message)
                .setNegativeButton(R.string.complete_review_cancel, null)
                .setPositiveButton(R.string.request_confirm_complete_positive, (d, which) -> {
                    dismiss();
                    if (actionListener != null) {
                        actionListener.onCompleteRequest(requestId);
                    }
                })
                .show());
    }

    private void setStatusBackground(TextView view, int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(16f);
        view.setBackground(bg);
    }
}
