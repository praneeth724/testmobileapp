package lk.rush.homeservicego.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import lk.rush.homeservicego.R;

public class AdminAddServiceActivity extends AppCompatActivity {

    private EditText etServiceName, etDescription, etPrice, etProviderPhone, etLatitude, etLongitude;
    private Spinner spinnerCategory;
    private Button btnSaveService;
    private ProgressBar progressBar;

    private FirebaseFirestore db;

    // Category options matching HomeActivity cards
    private String[] categories = {"Cleaning", "Plumbing", "Electrical", "Appliance Repair"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_service);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Service");
        }

        db = FirebaseFirestore.getInstance();

        etServiceName    = findViewById(R.id.etServiceName);
        etDescription    = findViewById(R.id.etDescription);
        etPrice          = findViewById(R.id.etPrice);
        etProviderPhone  = findViewById(R.id.etProviderPhone);
        etLatitude       = findViewById(R.id.etLatitude);
        etLongitude      = findViewById(R.id.etLongitude);
        spinnerCategory  = findViewById(R.id.spinnerCategory);
        btnSaveService   = findViewById(R.id.btnSaveService);
        progressBar      = findViewById(R.id.progressBar);

        // Populate the category spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        btnSaveService.setOnClickListener(v -> saveService());
    }

    private void saveService() {
        String name     = etServiceName.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String desc     = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String phone    = etProviderPhone.getText().toString().trim();
        String latStr   = etLatitude.getText().toString().trim();
        String lngStr   = etLongitude.getText().toString().trim();

        // Validate required fields
        if (TextUtils.isEmpty(name)) {
            etServiceName.setError("Enter service name");
            etServiceName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            etDescription.setError("Enter description");
            etDescription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(priceStr)) {
            etPrice.setError("Enter price");
            etPrice.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etProviderPhone.setError("Enter provider phone");
            etProviderPhone.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            etPrice.setError("Enter a valid number");
            etPrice.requestFocus();
            return;
        }

        // Latitude and Longitude are optional — default to Colombo if empty
        double latitude  = TextUtils.isEmpty(latStr)  ? 6.9271  : Double.parseDouble(latStr);
        double longitude = TextUtils.isEmpty(lngStr) ? 79.8612 : Double.parseDouble(lngStr);

        progressBar.setVisibility(View.VISIBLE);
        btnSaveService.setEnabled(false);

        // Build service data including map coordinates
        HashMap<String, Object> serviceData = new HashMap<>();
        serviceData.put("name",          name);
        serviceData.put("category",      category);
        serviceData.put("description",   desc);
        serviceData.put("price",         price);
        serviceData.put("providerPhone", phone);
        serviceData.put("latitude",      latitude);
        serviceData.put("longitude",     longitude);
        serviceData.put("imageUrl",      ""); // placeholder

        // Save to Firestore, then store the generated ID inside the document
        db.collection("services")
                .add(serviceData)
                .addOnSuccessListener(documentReference -> {
                    String serviceId = documentReference.getId();
                    documentReference.update("serviceId", serviceId);

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Service added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to Admin screen
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnSaveService.setEnabled(true);
                    Toast.makeText(this, "Failed to add service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
