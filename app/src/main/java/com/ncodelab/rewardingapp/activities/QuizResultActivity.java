package com.ncodelab.rewardingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.model.EarningHistory;

public class QuizResultActivity extends AppCompatActivity {

    TextView userScore,userRightAnswer,userWrongAnswer;
    MaterialButton backToHomeBtn;

    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        userScore = findViewById(R.id.user_score);
        userRightAnswer = findViewById(R.id.userRightAnswers);
        userWrongAnswer = findViewById(R.id.userWrongAnswer);

        database = FirebaseFirestore.getInstance();

        backToHomeBtn = findViewById(R.id.goToHome);

        userScore.setText(getIntent().getStringExtra("quizResult"));
        userRightAnswer.setText(getIntent().getStringExtra("RIGHT"));
        userWrongAnswer.setText(getIntent().getStringExtra("WRONG"));


        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .update("userPoints", FieldValue.increment(Long.parseLong(getIntent().getStringExtra("quizResult"))));

        EarningHistory earningHistory = new EarningHistory("Quiz",Long.parseLong(getIntent().getStringExtra("quizResult")));
        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .collection("UserEarnedPoints")
                .document()
                .set(earningHistory);


        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuizResultActivity.this,MainActivity.class));
                finish();
            }
        });


    }
}