package com.g08.gig.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.g08.gig.R;
import com.g08.gig.models.Appointment;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.g08.gig.util.DateFormats;

import java.util.Objects;

public class AppointmentAdapter extends ListAdapter<Appointment, AppointmentAdapter.ViewHolder> {

    public interface OnAppointmentClickListener {
        void onAppointmentClick(@NonNull Appointment appointment);
    }

    @Nullable
    private final OnAppointmentClickListener clickListener;

    public AppointmentAdapter(@Nullable OnAppointmentClickListener clickListener) {
        super(new DiffUtil.ItemCallback<Appointment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
                if (oldItem.getId() != newItem.getId()) {
                    return false;
                }
                if (!Objects.equals(oldItem.getAppointmentStart(), newItem.getAppointmentStart())) {
                    return false;
                }
                if (!Objects.equals(oldItem.getAppointmentEnd(), newItem.getAppointmentEnd())) {
                    return false;
                }
                Request oldReq = oldItem.getRequest();
                Request newReq = newItem.getRequest();
                if (!Objects.equals(
                        oldReq != null ? oldReq.getTitle() : null,
                        newReq != null ? newReq.getTitle() : null)) {
                    return false;
                }
                return sameResolvedClient(
                        ClientDisplayMapper.clientForAppointment(oldItem),
                        ClientDisplayMapper.clientForAppointment(newItem));
            }
        });
        this.clickListener = clickListener;
    }

    private static boolean sameResolvedClient(@Nullable Client a, @Nullable Client b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.getId() == b.getId()
                && Objects.equals(a.getFirstName(), b.getFirstName())
                && Objects.equals(a.getLastName(), b.getLastName());
    }

    public AppointmentAdapter() {
        this(null);
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
        Request request = appt.getRequest();

        if (request != null && request.getTitle() != null) {
            holder.title.setText(request.getTitle());
        } else {
            holder.title.setText("");
        }

        Client client = ClientDisplayMapper.clientForAppointment(appt);
        holder.client.setText(ClientDisplayMapper.formatFullName(holder.itemView.getContext(), client));

        if (appt.getAppointmentStart() != null) {
            holder.start.setText(appt.getAppointmentStart().format(DateFormats.REQUEST_TIME));
        } else {
            holder.start.setText("");
        }
        if (appt.getAppointmentEnd() != null) {
            holder.end.setText(appt.getAppointmentEnd().format(DateFormats.REQUEST_TIME));
        } else {
            holder.end.setText("");
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAppointmentClick(appt);
            }
        });
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
