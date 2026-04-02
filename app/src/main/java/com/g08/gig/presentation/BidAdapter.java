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
import com.g08.gig.models.Bid;
import com.g08.gig.models.Request;
import com.g08.gig.util.DateFormats;

public class BidAdapter extends ListAdapter<Bid, BidAdapter.BidViewHolder> {

    public interface OnBidClickListener {
        void onBidClick(Bid bid);
    }

    @Nullable
    private final OnBidClickListener listener;

    public BidAdapter(@Nullable OnBidClickListener listener) {
        super(new DiffUtil.ItemCallback<Bid>() {
            @Override
            public boolean areItemsTheSame(@NonNull Bid oldItem, @NonNull Bid newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Bid oldItem, @NonNull Bid newItem) {
                Request oldReq = oldItem.getRequest();
                Request newReq = newItem.getRequest();
                return oldReq.getTitle().equals(newReq.getTitle())
                        && oldReq.getBody().equals(newReq.getBody())
                        && oldItem.getCost() == newItem.getCost();
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bidding_card, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        Bid bid = getItem(position);
        Request request = bid.getRequest();

        holder.title.setText(request.getTitle());
        holder.body.setText(request.getBody());

        if (request.getTimeOfRequest() != null) {
            holder.time.setText(request.getTimeOfRequest().format(DateFormats.REQUEST_TIME));
        } else {
            holder.time.setText("");
        }

        if (bid.getCost() > 0) {
            holder.bidPrice.setVisibility(View.VISIBLE);
            holder.bidPrice.setText(String.format("$%.2f", (double) bid.getCost()));
        } else {
            holder.bidPrice.setVisibility(View.GONE);
        }

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onBidClick(bid));
        }
    }

    static class BidViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView body;
        final TextView time;
        final TextView bidPrice;

        BidViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.requestCardTitle);
            body = itemView.findViewById(R.id.requestCardBody);
            time = itemView.findViewById(R.id.requestCardTime);
            bidPrice = itemView.findViewById(R.id.requestCardBidPrice);
        }
    }
}
