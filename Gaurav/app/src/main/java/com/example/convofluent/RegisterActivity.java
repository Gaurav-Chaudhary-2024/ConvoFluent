package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText    etFullName, etUsername, etEmail, etPassword, etConfirmPassword;
    private ImageButton btnTogglePassword, btnToggleConfirmPassword, btnBack;
    private CheckBox    cbTerms;
    private Button      btnCreateAccount;
    private TextView    tvLogin;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = DatabaseHelper.getInstance(this);
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etFullName               = findViewById(R.id.etFullName);
        etUsername               = findViewById(R.id.etUsername);
        etEmail                  = findViewById(R.id.etEmail);
        etPassword               = findViewById(R.id.etPassword);
        etConfirmPassword        = findViewById(R.id.etConfirmPassword);
        btnTogglePassword        = findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword);
        cbTerms                  = findViewById(R.id.cbTerms);
        btnCreateAccount         = findViewById(R.id.btnCreateAccount);
        tvLogin                  = findViewById(R.id.tvLogin);
        btnBack                  = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setTransformationMethod(isPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            btnTogglePassword.setImageResource(
                    isPasswordVisible ? R.drawable.ic_eye_on : R.drawable.ic_eye_off);
            etPassword.setSelection(etPassword.getText().length());
        });

        btnToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            etConfirmPassword.setTransformationMethod(isConfirmPasswordVisible
                    ? HideReturnsTransformationMethod.getInstance()
                    : PasswordTransformationMethod.getInstance());
            btnToggleConfirmPassword.setImageResource(
                    isConfirmPasswordVisible ? R.drawable.ic_eye_on : R.drawable.ic_eye_off);
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnCreateAccount.setOnClickListener(v -> {
            if (validateForm()) attemptRegister();
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Check uniqueness before going to ProfileSetup
        if (db.emailExists(email)) {
            etEmail.setError("This email is already registered");
            etEmail.requestFocus();
            return;
        }
        if (db.usernameExists(username)) {
            etUsername.setError("This username is already taken");
            etUsername.requestFocus();
            return;
        }

        // Pass data to ProfileSetupActivity — final save happens after gender+language chosen
        Intent intent = new Intent(this, ProfileSetupActivity.class);
        intent.putExtra("full_name", fullName);
        intent.putExtra("username",  username);
        intent.putExtra("email",     email);
        intent.putExtra("password",  password);
        startActivity(intent);
    }

    private boolean validateForm() {
        String fullName        = etFullName.getText().toString().trim();
        String username        = etUsername.getText().toString().trim();
        String email           = etEmail.getText().toString().trim();
        String password        = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus(); return false;
        }
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus(); return false;
        }
        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus(); return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus(); return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus(); return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus(); return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus(); return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus(); return false;
        }
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please accept the Terms of Service", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}