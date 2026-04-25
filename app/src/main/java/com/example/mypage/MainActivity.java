package com.example.mypage;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mypage.ui.ScriptFeedActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Immediately start the ScriptFeedActivity as the main entry point
        Intent intent = new Intent(this, ScriptFeedActivity.class);
        startActivity(intent);
        finish();
    }
}