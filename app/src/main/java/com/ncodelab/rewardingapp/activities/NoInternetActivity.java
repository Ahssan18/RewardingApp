package com.ncodelab.rewardingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.ncodelab.rewardingapp.R;

public class NoInternetActivity extends AppCompatActivity {

    MaterialButton closeAppBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        closeAppBtn = findViewById(R.id.close_app_btn);

        closeAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}