package com.example.convofluent;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Launch RegisterActivity as the first screen
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish(); // close MainActivity so back button doesn't return to it
    }
}