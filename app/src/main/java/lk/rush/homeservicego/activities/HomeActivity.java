package lk.rush.homeservicego.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.utils.SessionManager;

public class HomeActivity extends AppCompatActivity {

    private MaterialCardView cardCleaning, cardPlumbing, cardElectrical, cardAppliance;
    private Button btnLogout, btnViewBookings, btnProfile;
    private TextView tvTitle;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cardCleaning   = findViewById(R.id.cardCleaning);
        cardPlumbing   = findViewById(R.id.cardPlumbing);
        cardElectrical = findViewById(R.id.cardElectrical);
        cardAppliance  = findViewById(R.id.cardAppliance);
        btnLogout      = findViewById(R.id.btnLogout);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnProfile     = findViewById(R.id.btnProfile);
        tvTitle        = findViewById(R.id.tvTitle);

        sessionManager = new SessionManager(this);

        cardCleaning.setOnClickListener(v -> openServiceList("Cleaning"));
        cardPlumbing.setOnClickListener(v -> openServiceList("Plumbing"));
        cardElectrical.setOnClickListener(v -> openServiceList("Electrical"));
        cardAppliance.setOnClickListener(v -> openServiceList("Appliance Repair"));

        btnViewBookings.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, BookingHistoryActivity.class)));

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        updateGreeting();

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            sessionManager.logoutUser();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGreeting();
    }

    private void updateGreeting() {
        String fullName = sessionManager.getUserName();
        if (fullName != null && !fullName.isEmpty()) {
            String firstName = fullName.split(" ")[0];
            tvTitle.setText("Hi, " + firstName + "!");
        }
    }

    private void openServiceList(String category) {
        Intent intent = new Intent(HomeActivity.this, ServiceListActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
