package lk.rush.homeservicego.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.models.Service;
import lk.rush.homeservicego.utils.SessionManager;

public class BookingActivity extends AppCompatActivity {

    private TextView tvBookingServiceName;
    private Button btnSelectDate, btnSelectTime, btnConfirmBooking;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private SessionManager sessionManager;

    // Stores selected date and time as strings
    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Book Service");
        }

        // Get the service passed from ServiceDetailActivity
        Service service = (Service) getIntent().getSerializableExtra("service");

        if (service == null) {
            Toast.makeText(this, "Service not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db             = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        tvBookingServiceName = findViewById(R.id.tvBookingServiceName);
        btnSelectDate        = findViewById(R.id.btnSelectDate);
        btnSelectTime        = findViewById(R.id.btnSelectTime);
        btnConfirmBooking    = findViewById(R.id.btnConfirmBooking);
        progressBar          = findViewById(R.id.progressBar);

        tvBookingServiceName.setText(service.getName());

        // Show DatePickerDialog when user taps date button
        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year  = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day   = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, y, m, d) -> {
                        // Format date as YYYY-MM-DD
                        selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                        btnSelectDate.setText("Date: " + selectedDate);
                    }, year, month, day);

            // Don't allow past dates
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            datePicker.show();
        });

        // Show TimePickerDialog when user taps time button
        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour   = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this,
                    (view, h, min) -> {
                        // Format time as HH:MM AM/PM
                        String amPm = h >= 12 ? "PM" : "AM";
                        int h12 = h > 12 ? h - 12 : (h == 0 ? 12 : h);
                        selectedTime = String.format(Locale.getDefault(), "%02d:%02d %s", h12, min, amPm);
                        btnSelectTime.setText("Time: " + selectedTime);
                    }, hour, minute, false).show();
        });

        // Confirm booking button
        btnConfirmBooking.setOnClickListener(v -> confirmBooking(service));
    }

    private void confirmBooking(Service service) {
        // Check both date and time are selected
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnConfirmBooking.setEnabled(false);

        // Get current user info from session
        String userId   = sessionManager.getUserId();
        String userName = sessionManager.getUserName();

        // Build booking data map to save in Firestore
        HashMap<String, Object> bookingData = new HashMap<>();
        bookingData.put("userId",      userId);
        bookingData.put("userName",    userName);
        bookingData.put("serviceId",   service.getServiceId());
        bookingData.put("serviceName", service.getName());
        bookingData.put("bookingDate", selectedDate);
        bookingData.put("bookingTime", selectedTime);
        bookingData.put("status",      "pending"); // always starts as pending

        // Save to Firestore - Firestore auto-generates the booking ID
        db.collection("bookings")
                .add(bookingData)
                .addOnSuccessListener(documentReference -> {
                    // Save the auto-generated ID back into the document
                    String bookingId = documentReference.getId();
                    documentReference.update("bookingId", bookingId);

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_LONG).show();

                    // Go back to home after booking
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnConfirmBooking.setEnabled(true);
                    Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
