package com.g08.gig.util;

import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.g08.gig.R;

public final class DialogBodyHelper {

    public static final int MAX_LINES_COLLAPSED = 4;

    private DialogBodyHelper() {
    }

   
    public static void setupExpandableDescription(
            TextView bodyView,
            TextView showMoreButton,
            ScrollView scrollView,
            String body) {
        String text = body != null ? body : "";
        bodyView.setText(text);
        bodyView.setMaxLines(MAX_LINES_COLLAPSED);
        bodyView.setEllipsize(TextUtils.TruncateAt.END);

        bodyView.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bodyView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = bodyView.getWidth();
                if (width > 0 && text.length() > 0) {
                    TextPaint paint = bodyView.getPaint();
                    StaticLayout layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            ? StaticLayout.Builder.obtain(text, 0, text.length(), paint, width)
                            .setMaxLines(Integer.MAX_VALUE)
                            .build()
                            : new StaticLayout(text, paint, width, android.text.Layout.Alignment.ALIGN_NORMAL, 1.3f, 0f, true);
                    if (layout.getLineCount() > MAX_LINES_COLLAPSED) {
                        showMoreButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        showMoreButton.setOnClickListener(v -> {
            if (bodyView.getMaxLines() == MAX_LINES_COLLAPSED) {
                bodyView.setMaxLines(Integer.MAX_VALUE);
                bodyView.setEllipsize(null);
                showMoreButton.setText(showMoreButton.getContext().getString(R.string.dialog_show_less));
                bodyView.post(() -> {
                    if (scrollView != null) {
                        int targetScrollY = bodyView.getTop() - bodyView.getPaddingTop();
                        scrollView.smoothScrollTo(0, Math.max(0, targetScrollY));
                    }
                });
            } else {
                bodyView.setMaxLines(MAX_LINES_COLLAPSED);
                bodyView.setEllipsize(TextUtils.TruncateAt.END);
                showMoreButton.setText(showMoreButton.getContext().getString(R.string.dialog_show_more));
            }
        });
    }
}
