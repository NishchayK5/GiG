package com.g08.gig.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g08.gig.R;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProHome extends AppCompatActivity {

    private BiddingAdapter biddingAdapter;
    private View emptyBiddingsView;
    private View homeContent;
    private View biddingsViewContent;
    private View appointmentViewContent;
    private View profileViewContent;
    private List<Request> availableRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_pro);

        emptyBiddingsView = findViewById(R.id.emptyBiddingsView);
        homeContent = findViewById(R.id.homeContent);
        biddingsViewContent = findViewById(R.id.biddingsViewContent);
        appointmentViewContent = findViewById(R.id.appointmentViewContent);
        profileViewContent = findViewById(R.id.profileViewContent);

        RecyclerView biddingsList = findViewById(R.id.biddingsList);

        biddingAdapter = new BiddingAdapter(new BiddingAdapter.OnBiddingActionListener() {
            @Override
            public void onBid(Request request) {
                Toast.makeText(ProHome.this, getString(R.string.bid) + ": " + request.getTitle(), Toast.LENGTH_SHORT).show();
                availableRequests.remove(request);
                biddingAdapter.submitList(new ArrayList<>(availableRequests));
                updateEmptyState();
            }

            @Override
            public void onReject(Request request) {
                Toast.makeText(ProHome.this, getString(R.string.reject) + ": " + request.getTitle(), Toast.LENGTH_SHORT).show();
                availableRequests.remove(request);
                biddingAdapter.submitList(new ArrayList<>(availableRequests));
                updateEmptyState();
            }
        });

        biddingsList.setLayoutManager(new LinearLayoutManager(this));
        biddingsList.setAdapter(biddingAdapter);

        biddingAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateEmptyState();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateEmptyState();
            }
        });

        loadTemporaryRequests();
        biddingAdapter.submitList(new ArrayList<>(availableRequests));
        updateEmptyState();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showView(homeContent);
                return true;
            }
            if (id == R.id.nav_requests) {
                showView(biddingsViewContent);
                return true;
            }
            if (id == R.id.nav_appointment) {
                showView(appointmentViewContent);
                return true;
            }
            if (id == R.id.nav_profile) {
                showView(profileViewContent);
                return true;
            }
            return false;
        });
    }

    private void showView(View visibleView) {
        homeContent.setVisibility(visibleView == homeContent ? View.VISIBLE : View.GONE);
        biddingsViewContent.setVisibility(visibleView == biddingsViewContent ? View.VISIBLE : View.GONE);
        appointmentViewContent.setVisibility(visibleView == appointmentViewContent ? View.VISIBLE : View.GONE);
        profileViewContent.setVisibility(visibleView == profileViewContent ? View.VISIBLE : View.GONE);
    }

    private void loadTemporaryRequests() {
        Client placeholderClient = new Client(1, "client@email.com", "Sarah", "Martinez",
                "2045552233", "452 Oak Street, Winnipeg", null);
        LocalDateTime now = LocalDateTime.now();

        availableRequests = new ArrayList<>();
        availableRequests.add(new Request(1, placeholderClient, "Brake pad replacement",
                "My 2019 Honda Civic needs new front brake pads. There's a grinding noise when I stop.", now.minusDays(1)));
        availableRequests.add(new Request(2, placeholderClient, "Oil change needed",
                "Due for synthetic oil change. Prefer weekend appointment if possible.", now.minusHours(5)));
        availableRequests.add(new Request(3, placeholderClient, "AC not cooling",
                "Car AC blows warm air. 2020 Toyota Corolla. Would like a diagnosis.", now.minusHours(2)));
    }

    private void updateEmptyState() {
        boolean isEmpty = biddingAdapter.getItemCount() == 0;
        emptyBiddingsView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }
}
