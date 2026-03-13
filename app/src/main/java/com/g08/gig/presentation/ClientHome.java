package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import com.g08.gig.util.Pair;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.session.SessionManager;
import com.g08.gig.business.services.BidService;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.RequestService;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import com.g08.gig.util.DateFormats;

import java.util.ArrayList;
import java.util.List;

public class ClientHome extends AppCompatActivity {

    private static final int TAB_CURRENT = 0;
    private static final int TAB_PAST = 1;

    private RequestAdapter requestAdapter;
    private View emptyRequestsView;
    private RecyclerView requestsList;
    private View requestsContent;
    private View profileContent;
    private List<Request> currentRequests = new ArrayList<>();
    private List<Request> pastRequests = new ArrayList<>();
    private RequestService requestService;
    private BidService bidService;
    private ClientUserService clientUserService;

    private final ActivityResultLauncher<Intent> createRequestLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) return;
                loadRequests();
                Toast.makeText(this, R.string.create_request_submitted, Toast.LENGTH_SHORT).show();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_client);

        requestService = GiGApp.get(this).getRequestService();
        bidService = GiGApp.get(this).getBidService();
        clientUserService = GiGApp.get(this).getClientUserService();

        int userId = getIntent().getIntExtra(SessionManager.KEY_USER_ID, -1);
        if (userId != -1) {
            SessionManager.setUserId(this, userId);
        }

        bindViews();
        setupRequestListAndTabs();
        setupBottomNav();
        setupProfileAndLogout(userId);
    }

    private void bindViews() {
        emptyRequestsView = findViewById(R.id.emptyRequestsView);
        requestsList = findViewById(R.id.requestsList);
        requestsContent = findViewById(R.id.requestsContent);
        profileContent = findViewById(R.id.profileContent);
    }

    private void setupRequestListAndTabs() {
        requestAdapter = new RequestAdapter(this::showRequestDetail);
        requestsList.setLayoutManager(new LinearLayoutManager(this));
        requestsList.setAdapter(requestAdapter);

        requestAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateEmptyState();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                updateEmptyState();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                updateEmptyState();
            }
        });

        setupTabs();
        loadRequests();
        showListForCurrentTab();
    }

    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showRequestsView();
                return true;
            }
            if (id == R.id.nav_new_request) {
                createRequestLauncher.launch(new Intent(this, CreateRequest.class));
                bottomNav.setSelectedItemId(R.id.nav_home);
                return true;
            }
            if (id == R.id.nav_profile) {
                showProfileView();
                return true;
            }
            return false;
        });
    }

    private void setupProfileAndLogout(int userId) {
        updateProfileViews(userId);
        LinearLayout profileEditTile = findViewById(R.id.profileEditTile);
        profileEditTile.setOnClickListener(v -> NavigationHelper.startEditProfile(this, SessionManager.getUserId(this), true));
        LinearLayout profileLogoutTile = findViewById(R.id.profileLogoutTile);
        profileLogoutTile.setOnClickListener(v -> NavigationHelper.logout(this));
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.requestsTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_current_requests)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_past_requests)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showListForCurrentTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadRequests() {
        int clientId = SessionManager.getUserId(this);
        Pair<List<Request>, List<Request>> currentAndPast = requestService.getRequestsByClientCurrentAndPast(clientId);
        setRequests(currentAndPast.first, currentAndPast.second);
    }

    private void showListForCurrentTab() {
        TabLayout tabLayout = findViewById(R.id.requestsTabLayout);
        int position = tabLayout.getSelectedTabPosition();
        List<Request> list = (position == TAB_PAST) ? pastRequests : currentRequests;
        requestAdapter.submitList(new ArrayList<>(list));
    }

    private void updateEmptyState() {
        boolean isEmpty = requestAdapter.getItemCount() == 0;
        emptyRequestsView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void showRequestsView() {
        requestsContent.setVisibility(View.VISIBLE);
        profileContent.setVisibility(View.GONE);
    }

    private void showProfileView() {
        int userId = SessionManager.getUserId(this);
        updateProfileViews(userId);
        requestsContent.setVisibility(View.GONE);
        profileContent.setVisibility(View.VISIBLE);
    }

    private void updateProfileViews(int userId) {
        if (userId == -1) return;
        Client client = clientUserService.getClientById(userId);
        ProfileViewBinder.bind(this, client);
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateProfileViews(SessionManager.getUserId(this));
    }

    public void setRequests(List<Request> current, List<Request> past) {
        currentRequests = current != null ? new ArrayList<>(current) : new ArrayList<>();
        pastRequests = past != null ? new ArrayList<>(past) : new ArrayList<>();
        showListForCurrentTab();
    }

    private void showRequestDetail(Request request) {
        String time = "";
        if (request.getTimeOfRequest() != null) {
            time = request.getTimeOfRequest().format(DateFormats.REQUEST_TIME);
        }

        RequestDetailDialog dialog = RequestDetailDialog.newInstance(
                request.getId(),
                request.getTitle(),
                request.getBody(),
                time,
                request.getRequestStatus()
        );
        dialog.setBids(bidService.getAllByRequest(request));
        dialog.setActionListener(new RequestDetailDialog.OnRequestActionListener() {
            @Override
            public void onDeleteRequest(int requestId) {
                requestService.removeRequest(requestId);
                loadRequests();
                Toast.makeText(ClientHome.this, getString(R.string.request_deleted), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleteRequest(int requestId) {
                requestService.completeRequest(requestId);
                loadRequests();
                Toast.makeText(ClientHome.this, getString(R.string.request_completed), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "request_detail");
    }
}
