package lk.rush.homeservicego.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.adapters.ServiceAdapter;
import lk.rush.homeservicego.models.Service;

public class ServiceListActivity extends AppCompatActivity {

    private TextView tvCategoryTitle;
    private RecyclerView rvServices;
    private ServiceAdapter adapter;
    private ArrayList<Service> serviceList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        // Initialize UI components
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvServices = findViewById(R.id.rvServices);
        
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Setup RecyclerView with click listener — opens ServiceDetailActivity
        serviceList = new ArrayList<>();
        adapter = new ServiceAdapter(serviceList, service -> {
            Intent intent = new Intent(ServiceListActivity.this, ServiceDetailActivity.class);
            intent.putExtra("service", service); // Service is Serializable so can be passed directly
            startActivity(intent);
        });
        rvServices.setLayoutManager(new LinearLayoutManager(this));
        rvServices.setAdapter(adapter);

        // Get category from intent
        String category = getIntent().getStringExtra("category");

        if (category != null) {
            tvCategoryTitle.setText(category + " Services");
            loadServices(category);
        } else {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadServices(String category) {
        db.collection("services")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    serviceList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Service service = document.toObject(Service.class);
                        serviceList.add(service);
                    }
                    adapter.notifyDataSetChanged();
                    
                    if (serviceList.isEmpty()) {
                        Toast.makeText(this, "No services available in this category", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading services: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
