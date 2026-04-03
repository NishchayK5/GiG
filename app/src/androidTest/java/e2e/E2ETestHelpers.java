package e2e;

import android.content.Context;
import android.view.View;
import android.widget.RatingBar;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.BidService;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.business.services.RequestService;
import com.g08.gig.models.Bid;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.Request;
import com.g08.gig.persistence.real.AppDbHelper;
import com.g08.gig.session.SessionManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.hamcrest.Matcher;

import java.time.LocalDateTime;

public final class E2ETestHelpers {
    private E2ETestHelpers() {}

    public static void clearDb() {
        Context context = ApplicationProvider.getApplicationContext();
        GiGApp app = (GiGApp) context.getApplicationContext();
        // Close the existing helper, delete the file, then re-init so the app gets a fresh connection
        app.reinitialize(); // closes dbHelper
        context.deleteDatabase(AppDbHelper.DB_NAME); // safe now — connection is closed
        app.reinitialize(); // re-opens and recreates tables
        SessionManager.clearSession(context);
    }

    public static GiGApp app() {
        Context context = ApplicationProvider.getApplicationContext();
        return (GiGApp) context.getApplicationContext();
    }

    public static Client seedClient(String first, String last, String email) {
        ClientUserService service = app().getClientUserService();
        return service.addClient(first, last, email, "Password1!", "Password1!", "2045551234", "123 Main St");
    }

    public static Pro seedPro(String first, String last, String email) {
        ProUserService service = app().getProUserService();
        return service.addPro(first, last, email, "2045555678", "Password1!", "Password1!",
                new String[]{"Brakes", "Oil"}, "Winnipeg");
    }

    public static Request seedRequest(Client client, String title, String body) {
        RequestService service = app().getRequestService();
        return service.addRequest(client, title, body);
    }

    public static Bid seedBid(Pro pro, Request request, double cost, LocalDateTime time) {
        BidService service = app().getBidService();
        return service.addBid(pro, request, cost, time);
    }

    public static ViewAction setRating(final float rating) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(RatingBar.class);
            }

            @Override
            public String getDescription() {
                return "Set rating on a RatingBar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RatingBar ratingBar = (RatingBar) view;
                ratingBar.setRating(rating);
                uiController.loopMainThreadUntilIdle();
            }
        };
    }
    public static void resetAppState(Context context) {
        SessionManager.clearSession(context);

        AppDbHelper dbHelper = new AppDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            db.execSQL("PRAGMA foreign_keys = OFF");

            db.execSQL("DELETE FROM skills");
            db.execSQL("DELETE FROM reviews");
            db.execSQL("DELETE FROM appointments");
            db.execSQL("DELETE FROM bid");
            db.execSQL("DELETE FROM requests");
            db.execSQL("DELETE FROM pros");
            db.execSQL("DELETE FROM clients");
            db.execSQL("DELETE FROM users");

            db.execSQL("DELETE FROM sqlite_sequence");

            db.execSQL("PRAGMA foreign_keys = ON");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}

