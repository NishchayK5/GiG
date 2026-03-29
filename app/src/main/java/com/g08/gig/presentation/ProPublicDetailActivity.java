package com.g08.gig.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.ReviewService;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProPublicDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRO_ID = "pro_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_public_detail);

        int proId = getIntent().getIntExtra(EXTRA_PRO_ID, -1);
        if (proId <= 0) {
            Toast.makeText(this, R.string.pro_public_detail_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ProUserService proUserService = GiGApp.get(this).getProUserService();
        ReviewService reviewService = GiGApp.get(this).getReviewService();

        Pro pro = proUserService.getProById(proId);
        if (pro == null) {
            Toast.makeText(this, R.string.pro_public_detail_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageButton back = findViewById(R.id.proDetailBack);
        back.setOnClickListener(v -> finish());

        TextView nameView = findViewById(R.id.proDetailName);
        TextView avgRatingView = findViewById(R.id.proDetailAvgRating);
        TextView avgRatingOutOfView = findViewById(R.id.proDetailAvgRatingOutOf);
        TextView cityView = findViewById(R.id.proDetailCity);
        TextView skillsView = findViewById(R.id.proDetailSkills);

        String first = pro.getFirstName() != null ? pro.getFirstName() : "";
        String last = pro.getLastName() != null ? pro.getLastName() : "";
        nameView.setText((first + " " + last).trim());
        cityView.setText(pro.getCityOfOperation() != null ? pro.getCityOfOperation() : "");
        skillsView.setText(formatSkillSet(pro.getSkillSet()));

        LinearLayout reviewsContainer = findViewById(R.id.proDetailReviewsContainer);
        View emptyReviewsView = findViewById(R.id.proDetailReviewsEmptyView);

        List<Review> reviews = reviewService.getAllReviewsForPro(pro);
        if (reviews == null) {
            reviews = new ArrayList<>();
        }

        reviewsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        if (reviews.isEmpty()) {
            emptyReviewsView.setVisibility(View.VISIBLE);
            bindAverageRating(avgRatingView, avgRatingOutOfView, 0);
        } else {
            emptyReviewsView.setVisibility(View.GONE);
            bindAverageRating(avgRatingView, avgRatingOutOfView, averageStars(reviews));
            for (Review r : reviews) {
                String reviewerName = formatReviewer(r.getReviewer());
                ReviewDisplay display = new ReviewDisplay(
                        r.getTitle() != null ? r.getTitle() : "",
                        r.getBody() != null ? r.getBody() : "",
                        r.getStars(),
                        reviewerName
                );
                addReviewRow(inflater, reviewsContainer, display);
            }
        }
    }

    private void bindAverageRating(TextView avgRatingView, TextView avgOutOfView, double average) {
        if (avgRatingView == null) {
            return;
        }
        if (average <= 0 || Double.isNaN(average)) {
            avgRatingView.setVisibility(View.GONE);
            if (avgOutOfView != null) {
                avgOutOfView.setVisibility(View.GONE);
            }
            return;
        }
        avgRatingView.setVisibility(View.VISIBLE);
        avgRatingView.setText(String.format(Locale.US, "%.1f", average));
        if (avgOutOfView != null) {
            avgOutOfView.setVisibility(View.VISIBLE);
            avgOutOfView.setText(R.string.pro_public_detail_rating_out_of);
        }
    }

    private static double averageStars(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }
        int sum = 0;
        int n = 0;
        for (Review r : reviews) {
            sum += r.getStars();
            n++;
        }
        return n == 0 ? 0 : (double) sum / n;
    }


    private void addReviewRow(LayoutInflater inflater, LinearLayout reviewsContainer, ReviewDisplay row) {
        View item = inflater.inflate(R.layout.item_review_public, reviewsContainer, false);
        TextView title = item.findViewById(R.id.reviewItemTitle);
        TextView stars = item.findViewById(R.id.reviewItemStars);
        TextView reviewer = item.findViewById(R.id.reviewItemReviewer);
        TextView body = item.findViewById(R.id.reviewItemBody);
        title.setText(row.title);
        stars.setText(starLabel(row.stars));
        reviewer.setText(row.reviewerName);
        body.setText(row.body);
        reviewsContainer.addView(item);
    }

    private static final class ReviewDisplay {
        final String title;
        final String body;
        final int stars;
        final String reviewerName;

        ReviewDisplay(String title, String body, int stars, String reviewerName) {
            this.title = title;
            this.body = body;
            this.stars = stars;
            this.reviewerName = reviewerName;
        }
    }

    private static String formatSkillSet(String[] skills) {
        if (skills == null || skills.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < skills.length; i++) {
            String s = skills[i];
            if (s == null || s.trim().isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(s.trim());
        }
        return sb.toString();
    }

    private static String formatReviewer(Client reviewer) {
        if (reviewer == null) {
            return "";
        }
        String fn = reviewer.getFirstName() != null ? reviewer.getFirstName() : "";
        String ln = reviewer.getLastName() != null ? reviewer.getLastName() : "";
        String name = (fn + " " + ln).trim();
        return name.isEmpty() ? "" : name;
    }

    private static String starLabel(int stars) {
        int n = Math.max(0, Math.min(5, stars));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append('\u2605');
        }
        for (int i = n; i < 5; i++) {
            sb.append('\u2606');
        }
        sb.append("  ").append(stars).append("/5");
        return sb.toString();
    }
}
