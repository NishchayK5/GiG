package com.g08.gig.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g08.gig.R;
import com.g08.gig.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> users = new ArrayList<>();

    public UserAdapter(List<User> initialUsers) {
        setItems(initialUsers);
    }

    public void setItems(List<User> newUsers) {
        users.clear();
        if (newUsers != null) {
            users.addAll(newUsers);
        }
        notifyDataSetChanged();
    }

    public User getItemAt(int position) {
        return users.get(position);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_row, parent, false);
        return new UserViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.name.setText(user.getFirstName() + " " + user.getLastName());
        holder.email.setText(user.getEmail() == null ? "" : user.getEmail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView email;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userName);
            email = itemView.findViewById(R.id.userEmail);
        }
    }
}
