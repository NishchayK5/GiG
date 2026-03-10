package com.g08.gig.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.g08.gig.business.services.RequestService;
import com.g08.gig.models.Client;
import com.g08.gig.models.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.time.format.DateTimeFormatter;
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
        requestService = ((GiGApp) getApplication()).getRequestService();

        int userId = getIntent().getIntExtra("USER_ID", -1);
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("USER_ID", userId).apply();


        emptyRequestsView = findViewById(R.id.emptyRequestsView);
        requestsList = findViewById(R.id.requestsList);
        requestsContent = findViewById(R.id.requestsContent);
        profileContent = findViewById(R.id.profileContent);

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

    /**
     * Temporary sample data so you can see the request cards in the UI.
     * Remove or replace with repository data when ready.
     */
    private void loadRequests() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int clientId = prefs.getInt("USER_ID", -1);

        List<Request> allRequests = requestService.getRequestsByClient(clientId);

        List<Request> current = new ArrayList<>();
        List<Request> past = new ArrayList<>();

        for (Request r : allRequests) {
            if (r.getRequestStatus()) {
                current.add(r);
            } else {
                past.add(r);
            }
        }

        setRequests(current, past);
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
        requestsContent.setVisibility(View.GONE);
        profileContent.setVisibility(View.VISIBLE);
    }

    public void setRequests(List<Request> current, List<Request> past) {
        currentRequests = current != null ? new ArrayList<>(current) : new ArrayList<>();
        pastRequests = past != null ? new ArrayList<>(past) : new ArrayList<>();
        showListForCurrentTab();
    }

    private void showRequestDetail(Request request) {
        String time = "";
        if (request.getTimeOfRequest() != null) {
            time = request.getTimeOfRequest().format(DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a"));
        }

        RequestDetailDialog dialog = RequestDetailDialog.newInstance(
                request.getId(),
                request.getTitle(),
                request.getBody(),
                time,
                request.getRequestStatus()
        );
        dialog.setActionListener(new RequestDetailDialog.OnRequestActionListener() {
            @Override
            public void onDeleteRequest(int requestId) {
                currentRequests.removeIf(r -> r.getId() == requestId);
                pastRequests.removeIf(r -> r.getId() == requestId);
                showListForCurrentTab();
                Toast.makeText(ClientHome.this, "Request deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditRequest(int requestId) {
                Toast.makeText(ClientHome.this, "Edit request", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "request_detail");
    }
}
