package com.ncodelab.rewardingapp.activities;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.model.User;

public class CreateAccountActivity extends AppCompatActivity {

    TextInputLayout userNameInput, userEmailInput, userPhoneInput, userPasswordInput;
    MaterialButton createAccountBtn, login;
    ImageView backBtn;
    String mbl;

    public static final String TAG = "AUTHENTICATION";

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initializeViews();

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this, WelcomeActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
            }
        });


    }

    public void initializeViews() {

        userNameInput = findViewById(R.id.user_name_input);
        userEmailInput = findViewById(R.id.user_email_input);
        userPhoneInput = findViewById(R.id.user_phone_input);
        mbl = getIntent().getStringExtra("phone");
        Log.e(TAG, "phone => " + mbl);
        try {
            userPhoneInput.getEditText().setText(mbl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        userPasswordInput = findViewById(R.id.user_password_input);

        createAccountBtn = findViewById(R.id.create_account_btn);

        login = findViewById(R.id.login);

        backBtn = findViewById(R.id.back_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

    }

    private void createAccount() {

        if (userNameInput.getEditText().getText().toString().equals("")) {
            displayErrorToast("Please enter your name");
            return;
        }
        if (userEmailInput.getEditText().getText().toString().equals("")) {
            displayErrorToast("Please enter your email");
            return;
        }
        userPhoneInput.getEditText().setText(mbl);
        userPhoneInput.getEditText().setEnabled(false);
        if (userPhoneInput.getEditText().getText().toString().equals("")) {
            displayErrorToast("Please enter your email");
            return;
        }
       /* if (userPasswordInput.getEditText().getText().toString().equals("")){
            displayErrorToast("Please create your password");
            return;
        }*/

        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        createAccountBtn.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        String name, email, phone, password;

        name = userNameInput.getEditText().getText().toString();
        email = userEmailInput.getEditText().getText().toString();
        phone = userPhoneInput.getEditText().getText().toString();
        password = userPasswordInput.getEditText().getText().toString();

        database.collection("Stats").document("stats")
                .update("totalUsers", FieldValue.increment(1));

        Log.d(TAG, "User Account Created");

        String referralCode = firebaseAuth.getUid();

        User user = new User(name, email, phone, null, firebaseAuth.getUid(), null, "Phone");

        database.collection("Users")
                .document(firebaseAuth.getUid())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            displaySuccessfulToast("Account Created");
                            startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                            finish();
                            Log.d(TAG, "User Data Saved");
                            progressBar.setVisibility(View.VISIBLE);
                        } else {
                            displayErrorToast("Wrong Credentials or Account already exist with these credentials");
                            Log.d(TAG, "Error occurred in firestore = " + task.getException().toString());
                            createAccountBtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                });
      /*  firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {




                        } else {
                            createAccountBtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            displayErrorToast("Some Error occurred");
                            Log.d(TAG, "Firebase Auth Error = " + task.getException());
                        }


                    }
                });*/


    }

    public void displayErrorToast(String msg) {
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.toast_layout, findViewById(R.id.customToast));

        MaterialCardView customToast = view.findViewById(R.id.customToast);
        TextView toastMessage = view.findViewById(R.id.toastMessage);
        ImageView icon = view.findViewById(R.id.toastIcon);

        toastMessage.setText(msg);
        icon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_wrong));

        customToast.setCardBackgroundColor(getResources().getColor(R.color.red));

        Toast errorToast = new Toast(getApplicationContext());
        errorToast.setDuration(Toast.LENGTH_SHORT);


        errorToast.setView(view);

        errorToast.setGravity(Gravity.TOP, 0, 20);

        errorToast.show();
    }

    public void displaySuccessfulToast(String msg) {
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.toast_layout, findViewById(R.id.customToast));

        MaterialCardView customToast = view.findViewById(R.id.customToast);
        TextView toastMessage = view.findViewById(R.id.toastMessage);
        ImageView icon = view.findViewById(R.id.toastIcon);

        toastMessage.setText(msg);
        icon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_right));

        customToast.setCardBackgroundColor(getResources().getColor(R.color.green));

        Toast errorToast = new Toast(getApplicationContext());
        errorToast.setDuration(Toast.LENGTH_SHORT);


        errorToast.setView(view);

        errorToast.setGravity(Gravity.TOP, 0, 20);

        errorToast.show();
    }
}