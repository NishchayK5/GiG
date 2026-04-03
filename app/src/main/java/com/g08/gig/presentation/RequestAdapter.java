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
import com.g08.gig.models.Request;
import com.g08.gig.util.DateFormats;

public class RequestAdapter extends ListAdapter<Request, RequestAdapter.RequestViewHolder> {

    public interface OnRequestClickListener {
        void onRequestClick(Request request);
    }

    private OnRequestClickListener clickListener;

    public RequestAdapter() {
        super(new DiffUtil.ItemCallback<Request>() {
            @Override
            public boolean areItemsTheSame(@NonNull Request oldItem, @NonNull Request newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Request oldItem, @NonNull Request newItem) {
                return oldItem.getTitle().equals(newItem.getTitle())
                        && oldItem.getBody().equals(newItem.getBody());
            }
        });
    }

    public RequestAdapter(OnRequestClickListener listener) {
        this();
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request_card, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = getItem(position);
        if (request == null) return;

        holder.title.setText(request.getTitle());
        holder.body.setText(request.getBody());
        if (request.getTimeOfRequest() != null) {
            holder.time.setText(request.getTimeOfRequest().format(DateFormats.REQUEST_TIME));
        } else {
            holder.time.setText("");
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onRequestClick(request);
            }
        });
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView body;
        final TextView time;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.requestCardTitle);
            body = itemView.findViewById(R.id.requestCardBody);
            time = itemView.findViewById(R.id.requestCardTime);
        }
    }
}
