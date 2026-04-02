package com.g08.gig.presentation;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.AppointmentService;
import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.util.DateFormats;
import com.g08.gig.util.DialogBodyHelper;
import com.g08.gig.util.Strings;

/**
 * Read-only details for a pro's appointment (request, client, schedule).
 */
public class AppointmentDetailDialog extends BaseGigDialogFragment {

    private static final String ARG_APPOINTMENT_ID = "appointment_id";

    public static AppointmentDetailDialog newInstance(int appointmentId) {
        AppointmentDetailDialog f = new AppointmentDetailDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_APPOINTMENT_ID, appointmentId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogWidthAndMaxHeight();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_appointment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            dismiss();
            return;
        }
        int appointmentId = args.getInt(ARG_APPOINTMENT_ID, -1);
        if (appointmentId < 0) {
            dismiss();
            return;
        }

        AppointmentService appointmentService = GiGApp.get(requireContext()).getApptService();
        Appointment appt = appointmentService.getApptById(appointmentId);
        if (appt == null) {
            dismiss();
            return;
        }

        TextView titleView = view.findViewById(R.id.dialogRequestTitle);
        TextView statusView = view.findViewById(R.id.dialogAppointmentStatus);
        View clientSection = view.findViewById(R.id.dialogClientSection);
        TextView clientName = view.findViewById(R.id.dialogClientName);
        TextView clientEmail = view.findViewById(R.id.dialogClientEmail);
        TextView clientPhone = view.findViewById(R.id.dialogClientPhone);
        TextView clientAddress = view.findViewById(R.id.dialogClientAddress);
        TextView bodyView = view.findViewById(R.id.dialogRequestBody);
        TextView showMore = view.findViewById(R.id.dialogShowMoreButton);
        ScrollView scrollView = view.findViewById(R.id.dialogScrollView);
        TextView postedTime = view.findViewById(R.id.dialogRequestTime);
        TextView startView = view.findViewById(R.id.dialogAppointmentStart);
        TextView endView = view.findViewById(R.id.dialogAppointmentEnd);
        ImageButton closeIcon = view.findViewById(R.id.dialogCloseIcon);

        Request request = appt.getRequest();
        if (request != null) {
            titleView.setText(request.getTitle());
            String body = request.getBody() != null ? request.getBody() : "";
            DialogBodyHelper.setupExpandableDescription(bodyView, showMore, scrollView, body);
            if (request.getTimeOfRequest() != null) {
                postedTime.setText(request.getTimeOfRequest().format(DateFormats.REQUEST_TIME));
            } else {
                postedTime.setText("");
            }
            boolean active = request.getRequestStatus();
            statusView.setVisibility(View.VISIBLE);
            if (active) {
                statusView.setText(R.string.request_detail_status_active);
                setStatusBackground(statusView, ContextCompat.getColor(requireContext(), R.color.main_cust_color));
            } else {
                statusView.setText(R.string.request_detail_status_closed);
                setStatusBackground(statusView, ContextCompat.getColor(requireContext(), R.color.grey));
            }
        } else {
            titleView.setText(R.string.appointment_view);
            DialogBodyHelper.setupExpandableDescription(bodyView, showMore, scrollView, "");
            postedTime.setText("");
            statusView.setVisibility(View.GONE);
        }

        Client client = ClientDisplayMapper.clientForAppointment(appt);
        if (client != null) {
            clientSection.setVisibility(View.VISIBLE);
            clientName.setText(ClientDisplayMapper.formatFullName(requireContext(), client));
            clientEmail.setText(Strings.nullToEmpty(client.getEmail()));
            clientPhone.setText(Strings.nullToEmpty(client.getPhoneNumber()));
            clientAddress.setText(Strings.nullToEmpty(client.getAddress()));
        } else {
            clientSection.setVisibility(View.GONE);
        }

        if (appt.getAppointmentStart() != null) {
            startView.setText(appt.getAppointmentStart().format(DateFormats.REQUEST_TIME));
        } else {
            startView.setText("");
        }
        if (appt.getAppointmentEnd() != null) {
            endView.setText(appt.getAppointmentEnd().format(DateFormats.REQUEST_TIME));
        } else {
            endView.setText("");
        }

        closeIcon.setOnClickListener(v -> dismiss());
    }

    private void setStatusBackground(TextView statusView, int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(getResources().getDimension(R.dimen.status_badge_corner_radius));
        statusView.setBackground(bg);
    }
}
