package com.g08.gig.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.g08.gig.R;
import com.g08.gig.models.Appointment;
import com.g08.gig.util.DateFormats;


public class AppointmentAdapter extends ListAdapter<Appointment, AppointmentAdapter.ViewHolder> {

    protected AppointmentAdapter() {
        super(new DiffUtil.ItemCallback<Appointment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
                return oldItem.getId() == newItem.getId();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appt = getItem(position);

        if (appt.getRequest() != null) {
            holder.title.setText(appt.getRequest().getTitle());
        }
        if (appt.getClient() != null) {
            holder.client.setText(appt.getClient().getFirstName() + " " + appt.getClient().getLastName());
        }
        if (appt.getAppointmentStart() != null) {
            holder.start.setText(appt.getAppointmentStart().format(DateFormats.REQUEST_TIME));
        }
        if (appt.getAppointmentEnd() != null) {
            holder.end.setText(appt.getAppointmentEnd().format(DateFormats.REQUEST_TIME));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, client, start, end;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.appointmentItemTitle);
            client = itemView.findViewById(R.id.appointmentItemClient);
            start = itemView.findViewById(R.id.appointmentItemStart);
            end = itemView.findViewById(R.id.appointmentItemEnd);
        }
    }
}
