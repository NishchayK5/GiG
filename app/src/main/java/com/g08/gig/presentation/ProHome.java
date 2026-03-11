package com.g08.gig.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.BidService;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.RequestService;
import com.g08.gig.models.Bid;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProHome extends AppCompatActivity {

    private BiddingAdapter biddingAdapter;
    private BiddingAdapter bidsAdapter;
    private View emptyBiddingsView;
    private View homeContent;
    private View biddingsViewContent;
    private View appointmentViewContent;
    private View profileViewContent;

    private RequestService requestService;
    private BidService bidService;

    private ProUserService proUserService;
    private List<Request> availableRequests = new ArrayList<>();
    private List<Request> bidRequests = new ArrayList<>(); // add this


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
        RecyclerView bidsList = findViewById(R.id.bidsList);

        requestService = ((GiGApp) getApplication()).getRequestService();
        bidService = ((GiGApp) getApplication()).getBidService();
        proUserService = ((GiGApp) getApplication()).getProUserService();

        int userId = getIntent().getIntExtra("USER_ID", -1);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("USER_ID", userId).apply();

        biddingAdapter = new BiddingAdapter(request -> showBiddingDetail(request, userId));

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
        bidsAdapter = new BiddingAdapter(request -> {}); // no click action for now
        bidsList.setLayoutManager(new LinearLayoutManager(this));
        bidsList.setAdapter(bidsAdapter);

        loadRequests();
        loadBids();


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

        LinearLayout profileLogoutTile = findViewById(R.id.profileLogoutTile);

        profileLogoutTile.setOnClickListener(v -> {
            SharedPreferences logoutPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            logoutPrefs.edit().clear().apply();

            Intent intent = new Intent(this, StartUp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showView(View visibleView) {
        homeContent.setVisibility(visibleView == homeContent ? View.VISIBLE : View.GONE);
        biddingsViewContent.setVisibility(visibleView == biddingsViewContent ? View.VISIBLE : View.GONE);
        appointmentViewContent.setVisibility(visibleView == appointmentViewContent ? View.VISIBLE : View.GONE);
        profileViewContent.setVisibility(visibleView == profileViewContent ? View.VISIBLE : View.GONE);
    }
    private void loadBids() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);
        List<Bid> myBids = bidService.getAllByPro(proUserService.getProById(userId));
        List<Request> result = new ArrayList<>();
        for (Bid bid : myBids) {
            if (bid != null && bid.getRequest() != null) {
                Request r = bid.getRequest();
                r.setBidCost(bid.getCost());
                result.add(r);
            }
        }
        bidsAdapter.submitList(result);
    }



    private void loadRequests() {
        availableRequests = requestService.getRequestsByStatus(true);
        biddingAdapter.submitList(new ArrayList<>(availableRequests));
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = biddingAdapter.getItemCount() == 0;
        emptyBiddingsView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void showBiddingDetail(Request request, int userId) {
        String time = "";
        if (request.getTimeOfRequest() != null) {
            time = request.getTimeOfRequest().format(DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a"));
        }

        BiddingDetailDialog dialog = BiddingDetailDialog.newInstance(
                request.getId(),
                request.getTitle(),
                request.getBody(),
                time,
                request.getRequestStatus()
        );
        dialog.setActionListener((requestId, bidAmount) -> {
            int cost = (int) bidAmount;
            bidService.addBid(proUserService.getProById(userId), request, cost);
            Toast.makeText(ProHome.this, getString(R.string.bid) + " $" + bidAmount + ": " + request.getTitle(), Toast.LENGTH_SHORT).show();
            loadBids();
            updateEmptyState();
        });
        dialog.show(getSupportFragmentManager(), "bidding_detail");
    }
}
