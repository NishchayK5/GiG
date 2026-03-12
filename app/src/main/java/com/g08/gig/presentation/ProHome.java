package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.session.SessionManager;
import com.g08.gig.business.services.BidService;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.RequestService;
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
    private View emptyBidsView;
    private View homeContent;
    private View biddingsViewContent;
    private View appointmentViewContent;
    private View profileViewContent;

    private RequestService requestService;
    private BidService bidService;

    private ProUserService proUserService;
    private List<Request> availableRequests = new ArrayList<>();
    private List<Request> bidRequests = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_pro);

        requestService = ((GiGApp) getApplication()).getRequestService();
        bidService = ((GiGApp) getApplication()).getBidService();
        proUserService = ((GiGApp) getApplication()).getProUserService();

        int userId = getIntent().getIntExtra(SessionManager.KEY_USER_ID, -1);
        if (userId != -1) {
            SessionManager.setUserId(this, userId);
        }

        bindViews();
        setupBiddingsList();
        setupBidsList();
        setupBottomNav();
        setupProfileAndLogout(userId);
    }

    private void bindViews() {
        emptyBiddingsView = findViewById(R.id.emptyBiddingsView);
        emptyBidsView = findViewById(R.id.emptyBidsView);
        homeContent = findViewById(R.id.homeContent);
        biddingsViewContent = findViewById(R.id.biddingsViewContent);
        appointmentViewContent = findViewById(R.id.appointmentViewContent);
        profileViewContent = findViewById(R.id.profileViewContent);
    }

    private void setupBiddingsList() {
        RecyclerView biddingsList = findViewById(R.id.biddingsList);
        biddingAdapter = new BiddingAdapter(request -> showBiddingDetail(request, SessionManager.getUserId(this)));
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
        loadRequests();
        updateEmptyState();
    }

    private void setupBidsList() {
        RecyclerView bidsList = findViewById(R.id.bidsList);
        bidsAdapter = new BiddingAdapter(request -> {});
        bidsList.setLayoutManager(new LinearLayoutManager(this));
        bidsList.setAdapter(bidsAdapter);
        bidsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateEmptyBidsState();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateEmptyBidsState();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateEmptyBidsState();
            }
        });
        loadBids();
        updateEmptyBidsState();
    }

    private void setupBottomNav() {
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
                updateProfileViews(SessionManager.getUserId(this));
                return true;
            }
            return false;
        });
    }

    private void setupProfileAndLogout(int userId) {
        updateProfileViews(userId);
        LinearLayout profileEditTile = findViewById(R.id.profileEditTile);
        profileEditTile.setOnClickListener(v -> NavigationHelper.startEditProfile(this, SessionManager.getUserId(this), false));
        LinearLayout profileLogoutTile = findViewById(R.id.profileLogoutTile);
        profileLogoutTile.setOnClickListener(v -> NavigationHelper.logout(this));
    }

    private void showView(View visibleView) {
        homeContent.setVisibility(visibleView == homeContent ? View.VISIBLE : View.GONE);
        biddingsViewContent.setVisibility(visibleView == biddingsViewContent ? View.VISIBLE : View.GONE);
        appointmentViewContent.setVisibility(visibleView == appointmentViewContent ? View.VISIBLE : View.GONE);
        profileViewContent.setVisibility(visibleView == profileViewContent ? View.VISIBLE : View.GONE);
    }
    private void loadBids() {
        int userId = SessionManager.getUserId(this);
        Pro pro = proUserService.getProById(userId);
        List<Request> requestsWithCosts = bidService.getRequestsWithBidCostsByPro(pro);
        bidsAdapter.submitList(new ArrayList<>(requestsWithCosts));
    }

    private void updateProfileViews(int userId) {
        if (userId == -1) return;
        Pro pro = proUserService.getProById(userId);
        ProfileViewBinder.bind(this, pro);
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

    private void updateEmptyBidsState() {
        boolean isEmpty = bidsAdapter.getItemCount() == 0;
        emptyBidsView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
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
