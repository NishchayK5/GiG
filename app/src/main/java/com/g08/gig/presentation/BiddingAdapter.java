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

import java.time.format.DateTimeFormatter;

public class BiddingAdapter extends ListAdapter<Request, BiddingAdapter.BiddingViewHolder> {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a");

    public interface OnBiddingClickListener {
        void onRequestClick(Request request);
    }

    private final OnBiddingClickListener listener;

    public BiddingAdapter(OnBiddingClickListener listener) {
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
        this.listener = listener;
    }

    @NonNull
    @Override
    public BiddingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bidding_card, parent, false);
        return new BiddingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BiddingViewHolder holder, int position) {
        Request request = getItem(position);
        holder.title.setText(request.getTitle());
        holder.body.setText(request.getBody());
        if (request.getTimeOfRequest() != null) {
            holder.time.setText(request.getTimeOfRequest().format(TIME_FORMAT));
        } else {
            holder.time.setText("");
        }
        if (request.getBidCost() > 0) {
            holder.bidPrice.setVisibility(View.VISIBLE);
            holder.bidPrice.setText(String.format("$%.2f", request.getBidCost()));

        } else {
            holder.bidPrice.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onRequestClick(request));
    }

    static class BiddingViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView body;
        final TextView time;
        final TextView bidPrice;


        BiddingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.requestCardTitle);
            body = itemView.findViewById(R.id.requestCardBody);
            time = itemView.findViewById(R.id.requestCardTime);
            bidPrice = itemView.findViewById(R.id.requestCardBidPrice);

        }
    }
}
