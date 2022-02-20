package com.ncodelab.rewardingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.ncodelab.rewardingapp.R;

public class PhoneAuthActivity extends AppCompatActivity {

    private TextInputLayout etPhone;
    private Button btnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        initViews();
        clickListeners();
    }

    private void clickListeners() {
        btnSend.setOnClickListener(v -> {
        String mobile=etPhone.getEditText().getText().toString();
            if(mobile.isEmpty() || mobile.length() < 10){
                etPhone.setError("Enter a valid mobile");
                etPhone.requestFocus();
                return;
            }

            Intent intent = new Intent(PhoneAuthActivity.this, CodeVerificationActivity.class);
            intent.putExtra("mobile", mobile);
            startActivity(intent);
        });
    }

    public void initViews()
    {
        etPhone=findViewById(R.id.user_phone);
        btnSend=findViewById(R.id.send_code);
    }
}