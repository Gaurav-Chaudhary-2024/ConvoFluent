package com.example.convofluent;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class RegisterActivity extends AppCompatActivity {

    // UI Elements
    private EditText etFullName, etUsername, etEmail, etPassword, etConfirmPassword;
    private ImageButton btnTogglePassword, btnToggleConfirmPassword;
    private CheckBox cbTerms;
    private Button btnCreateAccount;
    private TextView tvLogin;
    private ImageButton btnBack;

    // Track visibility state
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etFullName           = findViewById(R.id.etFullName);
        etUsername           = findViewById(R.id.etUsername);
        etEmail              = findViewById(R.id.etEmail);
        etPassword           = findViewById(R.id.etPassword);
        etConfirmPassword    = findViewById(R.id.etConfirmPassword);
        btnTogglePassword        = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        cbTerms              = findViewById(R.id.cbTerms);
        btnCreateAccount     = findViewById(R.id.btnCreateAccount);
        tvLogin              = findViewById(R.id.tvLogin);
        btnBack              = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Toggle Password Visibility
        btnTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.ic_eye_on);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
            }
            // Keep cursor at end
            etPassword.setSelection(etPassword.getText().length());
        });

        // Toggle Confirm Password Visibility
        btnToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_on);
            } else {
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        // Create Account Button
        btnCreateAccount.setOnClickListener(v -> {
            if (validateForm()) {
                Intent intent = new Intent(this, ProfileSetupActivity.class);
                startActivity(intent);
            }
        });

        // Login link
        tvLogin.setOnClickListener(v -> {
            // TODO: Navigate to Login screen
            Toast.makeText(this, "Navigate to Login", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateForm() {
        String fullName       = etFullName.getText().toString().trim();
        String username       = etUsername.getText().toString().trim();
        String email          = etEmail.getText().toString().trim();
        String password       = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Full Name check
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        // Username check
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }
        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            return false;
        }

        // Email check
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        // Password check
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        // Confirm Password check
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Please confirm your password");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        // Terms checkbox check
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept the Terms of Service to continue", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // All good!
    }
}