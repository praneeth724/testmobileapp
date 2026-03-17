package lk.rush.homeservicego.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import lk.rush.homeservicego.R;
import lk.rush.homeservicego.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    // Firebase Auth for secure login
    private FirebaseAuth mAuth;
    // Firestore to fetch user profile (name, role) after login
    private FirebaseFirestore fs;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        tvRegister  = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        mAuth          = FirebaseAuth.getInstance();
        fs             = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Step 1: Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    // Get the UID of the logged-in user
                    String userId = authResult.getUser().getUid();

                    // Step 2: Fetch user profile from Firestore using the UID
                    fs.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener((DocumentSnapshot document) -> {
                                progressBar.setVisibility(View.GONE);

                                if (document.exists()) {
                                    // Read profile fields
                                    String name = document.getString("name");
                                    String role = document.getString("role");

                                    // Safety: if role is missing in Firestore, default to customer
                                    if (role == null) role = "customer";

                                    // Save session locally
                                    sessionManager.createLoginSession(userId, name, email, role);

                                    Log.d("LOGIN", "Logged in: " + name + " | Role: [" + role + "]");

                                    // Route based on role — using equalsIgnoreCase so
                                    // "Admin", "ADMIN", "admin" all work correctly
                                    Intent intent;
                                    if (role.equalsIgnoreCase("admin")) {
                                        Toast.makeText(this, "Welcome Admin, " + name + "!", Toast.LENGTH_SHORT).show();
                                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                                    } else {
                                        Toast.makeText(this, "Welcome, " + name + "!", Toast.LENGTH_SHORT).show();
                                        intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    }
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Firebase Auth succeeded but Firestore document is missing
                                    // This means the admin profile was not created in Firestore
                                    progressBar.setVisibility(View.GONE);
                                    btnLogin.setEnabled(true);
                                    Toast.makeText(this,
                                            "Profile not found for UID: " + userId + "\nPlease create it in Firestore.",
                                            Toast.LENGTH_LONG).show();
                                    Log.e("LOGIN", "No Firestore doc for userId: " + userId);
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("LOGIN", "Firestore error", e);
                            });
                })
                .addOnFailureListener(e -> {
                    // Wrong email or password
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    Log.e("LOGIN", "Auth error", e);
                });
    }
}