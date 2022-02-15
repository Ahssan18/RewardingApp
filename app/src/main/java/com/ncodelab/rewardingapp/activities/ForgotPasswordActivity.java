package com.ncodelab.rewardingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.ncodelab.rewardingapp.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputLayout emailInput;
    MaterialButton resetPassword;
    ProgressBar progressBar;
    ImageView backBtn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                finish();
            }
        });



    }
    public void initializeViews(){

        emailInput = findViewById(R.id.user_email_input);

        resetPassword = findViewById(R.id.send_password_reset_email);

        progressBar = findViewById(R.id.progress_bar);

        backBtn = findViewById(R.id.back_btn);

    }

    private void resetPassword(){

        if (emailInput.getEditText().getText().toString().isEmpty()){
            displayErrorToast("Enter your email");
            return;
        }
        else {
            firebaseAuth.sendPasswordResetEmail(emailInput.getEditText().getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            displaySuccessfulToast("Password reset link send");

                            ForgotPasswordActivity.this.startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            ForgotPasswordActivity.this.finish();
                        }
                        else {
                            displayErrorToast("Entered email is not registered with any account");
                        }
                    });
        }
    }
    public void displayErrorToast(String msg){
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.toast_layout,findViewById(R.id.customToast));

        MaterialCardView customToast = view.findViewById(R.id.customToast);
        TextView toastMessage = view.findViewById(R.id.toastMessage);
        ImageView icon = view.findViewById(R.id.toastIcon);

        toastMessage.setText(msg);
        icon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_wrong));

        customToast.setCardBackgroundColor(getResources().getColor(R.color.red));

        Toast errorToast = new Toast(getApplicationContext());
        errorToast.setDuration(Toast.LENGTH_SHORT);


        errorToast.setView(view);

        errorToast.setGravity(Gravity.TOP,0,20);

        errorToast.show();
    }
    public void displaySuccessfulToast(String msg){
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.toast_layout,findViewById(R.id.customToast));

        MaterialCardView customToast = view.findViewById(R.id.customToast);
        TextView toastMessage = view.findViewById(R.id.toastMessage);
        ImageView icon = view.findViewById(R.id.toastIcon);

        toastMessage.setText(msg);
        icon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_right));

        customToast.setCardBackgroundColor(getResources().getColor(R.color.green));

        Toast errorToast = new Toast(getApplicationContext());
        errorToast.setDuration(Toast.LENGTH_SHORT);


        errorToast.setView(view);

        errorToast.setGravity(Gravity.TOP,0,20);

        errorToast.show();
    }

}