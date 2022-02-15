package com.ncodelab.rewardingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.model.User;

public class UpdateDetailsActivity extends AppCompatActivity {

    TextInputLayout currentPasswordTextLayout,updateNameTextLayout,updatePhoneTextLayout;
    MaterialButton checkCurrentPasswordBtn,updateDetailBtn;
    FirebaseFirestore database;
    User user;

    private final String TAG = "UPDATE_DETAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);


        initializeViews();

        database = FirebaseFirestore.getInstance();

        checkCurrentPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                user = documentSnapshot.toObject(User.class);

                                String userInputPass = currentPasswordTextLayout.getEditText().getText().toString();


                                if (userInputPass.equals(user.getPassword())){

                                    updateNameTextLayout.setEnabled(true);
                                    updatePhoneTextLayout.setEnabled(true);
                                    updateDetailBtn.setEnabled(true);

                                }
                                else {
                                    displayErrorToast("Password not matched");
                                    return;
                                }

                                updateDetail();


                            }
                        });



            }
        });
    }
    private void updateDetail(){


        updateDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = FirebaseFirestore.getInstance();

                String nameToUpdate,phoneToUpdate;

                nameToUpdate = updateNameTextLayout.getEditText().getText().toString();
                phoneToUpdate = updatePhoneTextLayout.getEditText().getText().toString();

                if (phoneToUpdate.equals("")){
                    database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                            .update("name",nameToUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    updatePhoneTextLayout.getEditText().setText("");
                                    updateNameTextLayout.getEditText().setText("");

                                }
                            });
                }
                else if (nameToUpdate.equals("")){
                    database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                            .update("phone",phoneToUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    updatePhoneTextLayout.getEditText().setText("");
                                    updateNameTextLayout.getEditText().setText("");

                                }
                            });
                }
                else {
                    database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                            .update("name",nameToUpdate,"phone",phoneToUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    updatePhoneTextLayout.getEditText().setText("");
                                    updateNameTextLayout.getEditText().setText("");

                                }
                            });
                }


            }
        });



    }


    public void initializeViews(){

        currentPasswordTextLayout = findViewById(R.id.current_password_text_layout);
        updateNameTextLayout = findViewById(R.id.update_name_text_layout);
        updatePhoneTextLayout = findViewById(R.id.update_phone_text_layout);

        checkCurrentPasswordBtn = findViewById(R.id.check_current_password_btn);
        updateDetailBtn = findViewById(R.id.update_details_btn);

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