package com.ncodelab.rewardingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ncodelab.rewardingapp.R;

import java.util.Date;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout emailInput,passwordInput;
    MaterialButton loginBtn,createAccount;
    TextView forgotPassword;

    ImageView backBtn;

    private FirebaseAuth firebaseAuth;

    FirebaseFirestore database;

    private final String TAG = "LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        initializeViews();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,WelcomeActivity.class));
                finish();
            }
        });



    }
    public void initializeViews(){

        emailInput = findViewById(R.id.user_email_input);
        passwordInput = findViewById(R.id.user_password_input);

        loginBtn = findViewById(R.id.login_btn);
        createAccount = findViewById(R.id.create_account);

        backBtn = findViewById(R.id.back_btn);

        forgotPassword = findViewById(R.id.text_forgot_password);

    }

    private void login(){


        if (emailInput.getEditText().getText().toString().isEmpty()){
            displayErrorToast("Enter your email");
            return;
        }
        if (passwordInput.getEditText().getText().toString().isEmpty()){
            displayErrorToast("Enter your password");
            return;
        }

        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.INVISIBLE);

        String email = emailInput.getEditText().getText().toString();
        String password = passwordInput.getEditText().getText().toString();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Log.d(TAG,"Successful");
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
                displaySuccessfulToast("Log In Successful");


                Date loginDate = Timestamp.now().toDate();

                database.collection("Users")
                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .update("password",password,"lastLogin",loginDate);

            }
            else {
                Log.d(TAG,"Failed = "+task.getException());
                displayErrorToast("Wrong Credentials");
                loginBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


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