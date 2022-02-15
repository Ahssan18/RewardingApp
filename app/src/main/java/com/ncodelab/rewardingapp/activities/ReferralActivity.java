package com.ncodelab.rewardingapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.adapter.ReferNetworkAdapter;
import com.ncodelab.rewardingapp.model.EarningHistory;
import com.ncodelab.rewardingapp.model.ReferNetwork;
import com.ncodelab.rewardingapp.model.User;

import java.util.ArrayList;

public class ReferralActivity extends AppCompatActivity {

    TextInputLayout inputReferCodeTextLayout;
    MaterialButton getReferralBonusBtn;
    TextView userReferCode,referralDetails;
    ImageView copyBtn;
    String userEmailId;
    long pointsForReferralSender,pointsForReferralGetter;

    FloatingActionButton shareAppBtn;

    ArrayList<ReferNetwork> referNetworkList;
    ReferNetworkAdapter referNetworkAdapter;
    RecyclerView referNetworkRecyclerView;

    private final String TAG = "REFER_FUNCTION";

    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        initializeViews();
        getUserData();
        CheckRefer();
        getReferralData();
        setEarningHistoryRecyclerview();
        DateChangeListener();

        getReferralBonusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referralProgram();
            }
        });

        shareAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });


    }

    public void initializeViews(){

        inputReferCodeTextLayout = findViewById(R.id.input_refer_code_text_layout);
        getReferralBonusBtn = findViewById(R.id.get_referral_bonus_btn);
        userReferCode = findViewById(R.id.user_refer_code);
        copyBtn = findViewById(R.id.copy_refer_code_btn);
        referralDetails = findViewById(R.id.referral_details);

        shareAppBtn = findViewById(R.id.share_app_btn);

        referNetworkRecyclerView = findViewById(R.id.user_referral_recyclerview);

    }

    private void referralProgram(){

        String userInputReferCode = inputReferCodeTextLayout.getEditText().getText().toString();

        database.collection("Users").whereEqualTo("referCode",userInputReferCode)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (userInputReferCode.equals(FirebaseAuth.getInstance().getUid())){
                            displayErrorToast("You cannot enter your own refer code");
                            return;
                        }
                        if (userInputReferCode.length()<28){
                            displayErrorToast("Invalid refer code");
                            return;
                        }

                        else {
                            //Updating referral sender points

                            database.collection("Users").document(userInputReferCode)
                                    .update("userPoints", FieldValue.increment(pointsForReferralSender))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                Log.d(TAG,"Sender points updated");
                                                displaySuccessfulToast("Successful");
                                                CheckRefer();
                                            }
                                            else {
                                                Log.d(TAG,"Sender points not updated = "+task.getException());
                                            }

                                        }
                                    });

                            //Updating Referral Sender Points
                            EarningHistory earningHistory = new EarningHistory("Successful Referral",pointsForReferralSender);

                            database.collection("Users").document(inputReferCodeTextLayout.getEditText().getText().toString())
                                    .collection("UserEarnedPoints")
                                    .document()
                                    .set(earningHistory);


                            ReferNetwork referNetwork = new ReferNetwork(userEmailId);
                            database.collection("Users").document(inputReferCodeTextLayout.getEditText().getText().toString())
                                    .collection("UserNetwork")
                                    .document()
                                    .set(referNetwork)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                Log.d("REFERRAL","USER NETWORK CREATED");
                                            }
                                            else {
                                                Log.d("REFERRAL","ERROR OCCURRED = "+task.getException());
                                            }

                                        }
                                    });

                            //Updating referral getter points

                            database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                                    .update("userPoints",FieldValue.increment(pointsForReferralGetter))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                Log.d(TAG,"User points updated");
                                                getReferralBonusBtn.setVisibility(View.GONE);
                                            }
                                            else {
                                                Log.d(TAG,"User points not updated = "+task.getException());
                                            }

                                        }
                                    });


                            database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                                    .update("referredBy",userInputReferCode)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                Log.d(TAG,"User Referref by updated");
                                            }
                                            else{
                                                Log.d(TAG,"User referred by not updated "+task.getException());
                                            }

                                        }
                                    });

                            //Adding User History
                            EarningHistory earningHistory1 = new EarningHistory("Referral Bonus",pointsForReferralGetter);

                            database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                                    .collection("UserEarnedPoints")
                                    .document()
                                    .set(earningHistory1);



                        }


                    }
                });



    }

    public void CheckRefer(){

        DocumentReference documentReference = database.collection("Users").document(FirebaseAuth.getInstance().getUid());

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String userReferredBy = documentSnapshot.getString("referredBy");

                if (userReferredBy== null){

                    inputReferCodeTextLayout.setVisibility(View.VISIBLE);
                    getReferralBonusBtn.setVisibility(View.VISIBLE);
                }
                else {
                    inputReferCodeTextLayout.setVisibility(View.GONE);
                    getReferralBonusBtn.setVisibility(View.GONE);
                }

            }
        });




    }

    public void getReferralData(){
        DocumentReference dc = database.collection("Settings").document("referralInfo");

        dc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot2) {

                pointsForReferralSender = documentSnapshot2.getLong("referrerBonus");
                pointsForReferralGetter = documentSnapshot2.getLong("referralGetterBonus");

                referralDetails.setText("Refer a friend and get "+pointsForReferralSender+ " for every referral and your friend gets" +pointsForReferralGetter +" earningHistory");

            }
        });
    }

    public void shareApp(){
        final String appPackageName = ReferralActivity.this.getPackageName();
        Intent sendIntent = new Intent();
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName+" Enter my refer code :- "+FirebaseAuth.getInstance().getUid()+" to get 50 points");
        sendIntent.setType("text/plain");
        ReferralActivity.this.startActivity(sendIntent);
    }

    public void setEarningHistoryRecyclerview(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ReferralActivity.this,LinearLayoutManager.VERTICAL,false);
        referNetworkRecyclerView.setLayoutManager(layoutManager);
        referNetworkList = new ArrayList<ReferNetwork>();

        ViewCompat.setNestedScrollingEnabled(referNetworkRecyclerView, false);

        referNetworkAdapter = new ReferNetworkAdapter(ReferralActivity.this,referNetworkList);

        referNetworkRecyclerView.setAdapter(referNetworkAdapter);
    }

    public void DateChangeListener(){

        database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("UserNetwork")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null){

                            Log.d("Firestore_error",error.getMessage());
                            return;
                        }

                        for (DocumentChange dc: value.getDocumentChanges()){

                            if (dc.getType() == DocumentChange.Type.ADDED){

                                referNetworkList.add(dc.getDocument().toObject(ReferNetwork.class));

                            }
                            referNetworkAdapter.notifyDataSetChanged();

                        }


                    }
                });


    }

    public void getUserData(){

        database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);

                        userEmailId = user.getEmail();
                        userReferCode.setText(user.getReferCode());

                        copyBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ClipboardManager clipboard = (ClipboardManager)ReferralActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Code", user.getReferCode());
                                if (clipboard == null || clip == null) return;
                                displaySuccessfulToast("Referral code copied");
                                clipboard.setPrimaryClip(clip);
                            }
                        });


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