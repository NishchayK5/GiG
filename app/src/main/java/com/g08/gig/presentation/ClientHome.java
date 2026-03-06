package com.g08.gig.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import java.time.LocalDateTime;
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
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                String title = result.getData().getStringExtra(CreateRequest.EXTRA_REQUEST_TITLE);
                String body = result.getData().getStringExtra(CreateRequest.EXTRA_REQUEST_BODY);
                if (title == null || body == null) return;
                int newId = 100 + currentRequests.size() + pastRequests.size();
                Client placeholderClient = new Client(1, "client@email.com", "Sarah", "Martinez",
                        "2045552233", "452 Oak Street, Winnipeg", null);
                currentRequests.add(0, new Request(newId, placeholderClient, title, body, LocalDateTime.now()));
                showListForCurrentTab();
                Toast.makeText(this, R.string.create_request_submitted, Toast.LENGTH_SHORT).show();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_client);
        requestService = ((GiGApp) getApplication()).getRequestService();
        int clientId = getIntent().getIntExtra("USER_ID", -1);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("USER_ID", clientId).apply();


        emptyRequestsView = findViewById(R.id.emptyRequestsView);
        requestsList = findViewById(R.id.requestsList);
        requestsContent = findViewById(R.id.requestsContent);
        profileContent = findViewById(R.id.profileContent);

        requestAdapter = new RequestAdapter();
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

        Client placeholderClient = new Client(1, "client@email.com", "Sarah", "Martinez",
                "2045552233", "452 Oak Street, Winnipeg", null);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int clientId = prefs.getInt("USER_ID", -1);
        currentRequests = requestService.getRequestsByClient(clientId);
        LocalDateTime now = LocalDateTime.now();

        pastRequests = new ArrayList<>();
        pastRequests.add(new Request(4, placeholderClient, "Tire rotation",
                "Had my tires rotated last month. Service was quick and professional.", now.minusDays(14)));
        pastRequests.add(new Request(5, placeholderClient, "Battery replacement",
                "Old battery was replaced. All good now.", now.minusDays(30)));
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

    /**
     * Call this when you have requests from a repository.
     * Split into current (active) and past (completed/cancelled) as needed.
     */
    public void setRequests(List<Request> current, List<Request> past) {
        currentRequests = current != null ? new ArrayList<>(current) : new ArrayList<>();
        pastRequests = past != null ? new ArrayList<>(past) : new ArrayList<>();
        showListForCurrentTab();
    }

    /**
     * Single list: all treated as current. Use setRequests(current, past) when you have split data.
     */
    public void setRequests(List<Request> requests) {
        currentRequests = requests != null ? new ArrayList<>(requests) : new ArrayList<>();
        pastRequests = new ArrayList<>();
        showListForCurrentTab();
    }
}
