package com.ncodelab.rewardingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.ncodelab.rewardingapp.R;

public class SupportActivity extends AppCompatActivity {

    MaterialButton mailUsBtn;

    String supportEmailAddress = "example@email.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        mailUsBtn = findViewById(R.id.mail_us_btn);

        mailUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail();
            }
        });


    }
    public void composeEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+"youremail@email.com"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL,supportEmailAddress);
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }
}