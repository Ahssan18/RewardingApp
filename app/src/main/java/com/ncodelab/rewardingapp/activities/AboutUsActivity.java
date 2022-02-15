package com.ncodelab.rewardingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.ncodelab.rewardingapp.R;

public class AboutUsActivity extends AppCompatActivity {

    TextView flaticonAttribute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        flaticonAttribute = findViewById(R.id.flaticon_credit);

        flaticonAttribute.setMovementMethod(LinkMovementMethod.getInstance());

    }
}