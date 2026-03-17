package lk.rush.homeservicego.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.utils.SessionManager;

public class HomeActivity extends AppCompatActivity {

    private MaterialCardView cardCleaning, cardPlumbing, cardElectrical, cardAppliance;
    private Button btnLogout, btnViewBookings, btnProfile;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cardCleaning = findViewById(R.id.cardCleaning);
        cardPlumbing = findViewById(R.id.cardPlumbing);
        cardElectrical = findViewById(R.id.cardElectrical);
        cardAppliance = findViewById(R.id.cardAppliance);
        btnLogout       = findViewById(R.id.btnLogout);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnProfile      = findViewById(R.id.btnProfile);

        sessionManager = new SessionManager(this);

        cardCleaning.setOnClickListener(v -> openServiceList("Cleaning"));
        cardPlumbing.setOnClickListener(v -> openServiceList("Plumbing"));
        cardElectrical.setOnClickListener(v -> openServiceList("Electrical"));
        cardAppliance.setOnClickListener(v -> openServiceList("Appliance Repair"));

        // Open booking history screen
        btnViewBookings.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, BookingHistoryActivity.class));
        });

        // Open profile screen
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase Auth and clear local session
            FirebaseAuth.getInstance().signOut();
            sessionManager.logoutUser();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openServiceList(String category) {
        Intent intent = new Intent(HomeActivity.this, ServiceListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}