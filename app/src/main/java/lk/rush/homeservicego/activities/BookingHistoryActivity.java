package lk.rush.homeservicego.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.adapters.BookingAdapter;
import lk.rush.homeservicego.models.Booking;
import lk.rush.homeservicego.utils.SessionManager;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private BookingAdapter adapter;
    private ArrayList<Booking> bookingList;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;

    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db             = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        rvBookings  = findViewById(R.id.rvBookings);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        bookingList = new ArrayList<>();
        adapter     = new BookingAdapter(bookingList);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);

        loadMyBookings();
    }

    private void loadMyBookings() {
        progressBar.setVisibility(View.VISIBLE);
        rvBookings.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        String userId = sessionManager.getUserId();

        db.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    bookingList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Booking booking = doc.toObject(Booking.class);
                        bookingList.add(booking);
                    }

                    adapter.notifyDataSetChanged();

                    if (bookingList.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvBookings.setVisibility(View.GONE);
                    } else {
                        rvBookings.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load bookings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
