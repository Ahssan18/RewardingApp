package com.ncodelab.rewardingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.model.EarningHistory;

import java.util.Calendar;
import java.util.Random;

public class SpinWheelActivity extends AppCompatActivity {

    public static final int [] points = {20,10,100,5,50,2,200,0};
    public static final int [] pointsDegrees = new int[points.length];
    public static final Random random = new Random();

    private int degrees = 0;
    private boolean isSpinning = false;
    private ImageView spinWheel;
    MaterialButton spinWheelBtn;

    FirebaseFirestore database;

    RelativeLayout adLayout;

    InterstitialAd mInterstitialAd;


    int spins = 5;

    public final String TAG = "SPIN_WHEEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_wheel);


        database = FirebaseFirestore.getInstance();

        spinWheel = findViewById(R.id.spin_wheel);
        spinWheelBtn = findViewById(R.id.spin_wheel_btn);
        adLayout = findViewById(R.id.ad_layout);

        getDegreeForPoints();
        showBannerAd();

        spinWheelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();


                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                String todayDate = year+""+month+""+day;

                SharedPreferences sharedPreferences = getSharedPreferences("PREFS",0);
                boolean currentDay = sharedPreferences.getBoolean(todayDate,false);

                if(!currentDay){
                    if (!isSpinning)
                    {
                        spin();
                        isSpinning = true;
                    }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(todayDate,true);
                        editor.apply();

                }
                
                else {
                    displayErrorToast("No Spins left today");
                }


            }
        });



    }
    private void spin(){

        degrees = random.nextInt(points.length-1);

        RotateAnimation rotateAnimation = new RotateAnimation(0,(360*points.length)+pointsDegrees[degrees],
                RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);

        rotateAnimation.setDuration(2600);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                int spinPoints = points[points.length - (degrees + 1)];

                displayCongratsToast("You Earned "+spinPoints);

                long pointsToAdd = spinPoints * 1;

                String uId = FirebaseAuth.getInstance().getUid();

                database.collection("Users")
                        .document(uId)
                        .update("userPoints", FieldValue.increment(pointsToAdd))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    Log.d(TAG,"Points added to wallet");

                                    EarningHistory earningHistory = new EarningHistory("Spin Wheel",pointsToAdd);

                                    database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                                            .collection("UserEarnedPoints")
                                            .document()
                                            .set(earningHistory);
                                }
                                else {
                                    Log.d(TAG,"Firestore error = "+task.getException());
                                }

                            }
                        });

                showAd();
                isSpinning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        spinWheel.startAnimation(rotateAnimation);

    }

    private void getDegreeForPoints(){

        int pointDegree = 360/points.length;

        for (int i = 0; i <points.length ; i++){
            pointsDegrees[i] = (i+1)*pointDegree;
        }

    }
    public void showBannerAd(){


        DocumentReference documentReference = database.collection("Settings").document("Ads");

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String bannerId = documentSnapshot.getString("BannerAdId");

                if (bannerId == null){
                    adLayout.setVisibility(View.GONE);
                }
                else {

                    AdView adView = new AdView(SpinWheelActivity.this);
                    adView.setAdSize(AdSize.BANNER);
                    adView.setAdUnitId(bannerId);
                    ((RelativeLayout)adLayout).addView(adView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);


                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.d("ADMOB","ERROR =" +loadAdError.toString());
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            Log.d("ADMOB","Ad Loaded");
                        }
                    });

                }
                Log.d("ADMOB",""+bannerId);
            }
        });



    }

    public void showInterstitialAds(){

        DocumentReference documentReference = database.collection("Settings").document("Ads");

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String interstitialId = documentSnapshot.getString("InterstitialAdId");
                Log.d(TAG,"AD ID = "+interstitialId);

                if (interstitialId != null){
                    AdRequest adRequest = new AdRequest.Builder().build();

                    InterstitialAd.load(SpinWheelActivity.this,interstitialId, adRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    // The mInterstitialAd reference will be null until
                                    // an ad is loaded.
                                    mInterstitialAd = interstitialAd;
                                    Log.i(TAG, "onAdLoaded");
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Handle the error
                                    Log.i("HELLO", loadAdError.getMessage());
                                    mInterstitialAd = null;
                                }
                            });
                }

                else {
                    Log.d(TAG,"AD ID IS NULL");
                }

            }
        });


    }

    public void showAd(){

        showInterstitialAds();

        if (mInterstitialAd != null){
            mInterstitialAd.show(SpinWheelActivity.this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                }
            });
        }
        else {
            Log.d("TESTING","Ad Load Failed");
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

    public void displayCongratsToast(String msg){
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.toast_layout,findViewById(R.id.customToast));

        MaterialCardView customToast = view.findViewById(R.id.customToast);
        TextView toastMessage = view.findViewById(R.id.toastMessage);
        ImageView icon = view.findViewById(R.id.toastIcon);

        toastMessage.setText(msg);
        icon.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_wallet));

        customToast.setCardBackgroundColor(getResources().getColor(R.color.yellow));

        Toast errorToast = new Toast(getApplicationContext());
        errorToast.setDuration(Toast.LENGTH_SHORT);


        errorToast.setView(view);

        errorToast.setGravity(Gravity.TOP,0,20);

        errorToast.show();
    }
}