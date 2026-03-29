package com.g08.gig.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.RequestService;

/**
 * Client flow: static review form (title, stars, description) for presentation only;
 * Complete closes the request in the app.
 */
public class CompleteRequestReviewDialog extends BaseGigDialogFragment {

    private static final String ARG_REQUEST_ID = "request_id";
    private static final float DIALOG_MAX_HEIGHT_RATIO = 0.88f;

    public interface OnReviewSubmitListener {
        void onReviewSubmit(String title, String body, int stars);
    }

    private OnReviewSubmitListener reviewListener;

    public void setReviewListener(OnReviewSubmitListener listener) {
        this.reviewListener = listener;
    }

    public static CompleteRequestReviewDialog newInstance(int requestId) {
        CompleteRequestReviewDialog f = new CompleteRequestReviewDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_ID, requestId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * DIALOG_WIDTH_RATIO);
            int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * DIALOG_MAX_HEIGHT_RATIO);
            getDialog().getWindow().setLayout(width, maxHeight);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_complete_request_review, container, false);
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
        if (requestId < 0) {
            dismiss();
            return;
        }

        RequestService requestService = GiGApp.get(requireContext()).getRequestService();

        EditText titleField = view.findViewById(R.id.completeReviewTitleInput);
        EditText bodyField = view.findViewById(R.id.completeReviewBodyInput);
        RatingBar ratingBar = view.findViewById(R.id.completeReviewRating);
        Button cancel = view.findViewById(R.id.completeReviewCancelButton);
        Button submit = view.findViewById(R.id.completeReviewSubmitButton);

        cancel.setOnClickListener(v -> dismiss());

        submit.setOnClickListener(v -> {
            String title = titleField.getText().toString().trim();
            String body = bodyField.getText().toString().trim();
            int stars = (int) ratingBar.getRating();

            if (title.isEmpty()) {
                //TODO: error message here
               // titleField.setError(getString(R.string.error_required));
                return;
            }

            if (reviewListener != null) {
                reviewListener.onReviewSubmit(title, body, stars);
            }

            requestService.completeRequest(requestId);
            getParentFragmentManager().setFragmentResult("complete_review_done", new Bundle());
            Toast.makeText(requireContext(), R.string.request_completed, Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }
}
