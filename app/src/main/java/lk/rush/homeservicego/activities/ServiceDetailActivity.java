package lk.rush.homeservicego.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
// Uri still used for the phone call intent below
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.models.Service;

public class ServiceDetailActivity extends AppCompatActivity {

    private TextView tvServiceName, tvCategory, tvPrice, tvDescription, tvProviderPhone;
    private Button btnCall, btnMap, btnBookNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        // Enable back button in title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Service Details");
        }

        // Get the Service object passed from ServiceListActivity
        Service service = (Service) getIntent().getSerializableExtra("service");

        if (service == null) {
            Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        tvServiceName    = findViewById(R.id.tvServiceName);
        tvCategory       = findViewById(R.id.tvCategory);
        tvPrice          = findViewById(R.id.tvPrice);
        tvDescription    = findViewById(R.id.tvDescription);
        tvProviderPhone  = findViewById(R.id.tvProviderPhone);
        btnCall          = findViewById(R.id.btnCall);
        btnMap           = findViewById(R.id.btnMap);
        btnBookNow       = findViewById(R.id.btnBookNow);

        // Fill in service data
        tvServiceName.setText(service.getName());
        tvCategory.setText(service.getCategory());
        tvPrice.setText("Rs. " + service.getPrice());
        tvDescription.setText(service.getDescription());
        tvProviderPhone.setText(service.getProviderPhone());

        // Call provider using phone intent
        btnCall.setOnClickListener(v -> {
            String phone = service.getProviderPhone();
            if (phone != null && !phone.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Open embedded MapActivity with service coordinates
        btnMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(ServiceDetailActivity.this, MapActivity.class);
            mapIntent.putExtra("latitude",    service.getLatitude());
            mapIntent.putExtra("longitude",   service.getLongitude());
            mapIntent.putExtra("serviceName", service.getName());
            startActivity(mapIntent);
        });

        // Go to booking screen, passing service data
        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(ServiceDetailActivity.this, BookingActivity.class);
            intent.putExtra("service", service);
            startActivity(intent);
        });
    }

    // Handle back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
