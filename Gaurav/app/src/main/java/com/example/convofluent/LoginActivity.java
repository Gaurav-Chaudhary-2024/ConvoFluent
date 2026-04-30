package com.example.convofluent;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText    etEmail, etPassword;
    private ImageButton btnTogglePassword;
    private Button      btnLogin;
    private TextView    tvGoToRegister;

    private boolean passwordVisible = false;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = DatabaseHelper.getInstance(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        btnLogin         = findViewById(R.id.btnLogin);
        tvGoToRegister   = findViewById(R.id.tvGoToRegister);
    }

    private void setupClickListeners() {

        btnTogglePassword.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                etPassword.setInputType(
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnTogglePassword.setImageResource(R.drawable.ic_eye_on);
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Basic validation
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        long userId = db.loginUser(email, password);
        if (userId == -1) {
            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Success — save session and go to Home
        SessionManager.login(this, userId);
        db.updateStreak(userId); // check/update daily streak on login

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}