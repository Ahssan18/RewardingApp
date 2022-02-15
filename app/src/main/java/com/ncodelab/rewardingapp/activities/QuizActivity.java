package com.ncodelab.rewardingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.model.Questions;

import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {


    TextView questionTv,option1,option2,option3,option4,countdownTime,questionsAttempted;
    long userScore = 0;
    int index = 0;
    int wrongAnswers = 0;
    int rightAnswers = 0;
    MaterialButton nextBtn;

    Context context;

    ArrayList<Questions> questionsArrayList;

    Questions questions;

    CountDownTimer countDownTimer;

    FirebaseFirestore database;

    RelativeLayout adLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initializeViews();

        questionsArrayList = new ArrayList<>();
        database = FirebaseFirestore.getInstance();

        showBannerAd();

        nullSafety();

        Random random = new Random();

        //change the bound value according to your total questions
        int questionRandom = random.nextInt(5);


        database.collection("QuizQuestions")
                .whereGreaterThanOrEqualTo("index",questionRandom)
                .orderBy("index")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if (queryDocumentSnapshots.getDocuments().size()<5){
                    database.collection("QuizQuestions")
                            .whereLessThanOrEqualTo("index",questionRandom)
                            .orderBy("index")
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                                Questions quizQuestions1 = snapshot.toObject(Questions.class);
                                questionsArrayList.add(quizQuestions1);
                            }
                            goToNextQuestion();

                        }
                    });
                }
                else {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots){
                        Questions quizQuestion2 = snapshot.toObject(Questions.class);
                        questionsArrayList.add(quizQuestion2);
                    }
                    goToNextQuestion();

                }

            }
        });



        timeReset();
        goToNextQuestion();




    }
    public void answerChecker(TextView textView){

        if (textView.getText().toString().equals(questions.getAnswer())){
            userScore++;
            rightAnswers++;
            textView.setBackground(getResources().getDrawable(R.drawable.right_option_bg));
            option1.setEnabled(false);
            option2.setEnabled(false);
            option3.setEnabled(false);
            option4.setEnabled(false);


        }
        else
        {
            textView.setBackground(getResources().getDrawable(R.drawable.wrong_option_bg));
            userScore--;
            wrongAnswers++;
            option1.setEnabled(false);
            option2.setEnabled(false);
            option3.setEnabled(false);
            option4.setEnabled(false);
        }

    }

    public void timeReset(){

        //you can change quiz time by changing the first value
        countDownTimer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                countdownTime.setText(String.valueOf(millisUntilFinished/1000));

            }

            @Override
            public void onFinish() {

                Dialog dialog = new Dialog(QuizActivity.this);

                dialog.setContentView(R.layout.time_out_dialogue_layout);

                dialog.setCancelable(false);

                MaterialButton btnRetry = dialog.findViewById(R.id.btn_retry);

                btnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(QuizActivity.this,MainActivity.class));
                        finish();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                if (!isFinishing()){
                    dialog.show();
                }
                else {
                    Log.d("ERROR","error is coming");
                }



            }
        };

    }

    public void resetAnswers(){

        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);

        option1.setBackground(getResources().getDrawable(R.drawable.option_bg));
        option2.setBackground(getResources().getDrawable(R.drawable.option_bg));
        option3.setBackground(getResources().getDrawable(R.drawable.option_bg));
        option4.setBackground(getResources().getDrawable(R.drawable.option_bg));

    }

    public void goToNextQuestion(){

        if (countDownTimer != null){
            countDownTimer.cancel();
        }

        Log.d("USER_QUIZ","SCORE = "+userScore);
        timeReset();

        countDownTimer.start();

        if (index < questionsArrayList.size()){

            questionsAttempted.setText(String.format("%d/%d",(index+1), questionsArrayList.size()));

            questions = questionsArrayList.get(index);

            questionTv.setText(questions.getQuestion());
            option1.setText(questions.getOptionA());
            option2.setText(questions.getOptionB());
            option3.setText(questions.getOptionC());
            option4.setText(questions.getOptionD());

            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

            nextBtn.setEnabled(true);

        }

    }

    public void onClick(View view) {

        switch (view.getId()){

            case R.id.option_one:
            case R.id.option_two:
            case R.id.option_three:
            case R.id.option_four:


                if (countDownTimer != null){
                    countDownTimer.cancel();
                }

                TextView userSelected = (TextView) view;

                answerChecker(userSelected);

                break;




            case R.id.next_btn:
                resetAnswers();
                if (index< questionsArrayList.size()-1){
                    index++;
                    goToNextQuestion();
                }
                else
                {
                    String result = String.valueOf(userScore);
                    displayCongratsToast("You earned "+result+" Points");
                    Intent intent = new Intent(QuizActivity.this,QuizResultActivity.class);
                    intent.putExtra("quizResult",result);
                    intent.putExtra("RIGHT",String.valueOf(rightAnswers));
                    intent.putExtra("WRONG",String.valueOf(wrongAnswers));
                    startActivity(intent);
                    finish();
                }
                break;

        }

    }

    public void initializeViews(){

        questionTv = findViewById(R.id.question);
        option1 = findViewById(R.id.option_one);
        option2 = findViewById(R.id.option_two);
        option3 = findViewById(R.id.option_three);
        option4 = findViewById(R.id.option_four);
        countdownTime = findViewById(R.id.timer);
        questionsAttempted = findViewById(R.id.questionsAttempted);

        nextBtn = findViewById(R.id.next_btn);

        adLayout = findViewById(R.id.ad_layout_quiz);



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

                    AdView adView = new AdView(QuizActivity.this);
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

    public void nullSafety(){


        if (questionTv.getText().toString().equals("Loading") && option1.getText().toString().equals("Loading")
            && option2.getText().toString().equals("Loading") && option3.getText().toString().equals("Loading")
            && option4.getText().toString().equals("Loading"))
        {
            option1.setEnabled(false);
            option2.setEnabled(false);
            option3.setEnabled(false);
            option4.setEnabled(false);

            nextBtn.setEnabled(false);

        }
        else {
            option1.setEnabled(true);
            option2.setEnabled(true);
            option3.setEnabled(true);
            option4.setEnabled(true);

            nextBtn.setEnabled(true);
        }
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