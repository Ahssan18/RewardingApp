package com.ncodelab.rewardingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.activities.QuizActivity;
import com.ncodelab.rewardingapp.activities.ReferralActivity;
import com.ncodelab.rewardingapp.activities.SpinWheelActivity;
import com.ncodelab.rewardingapp.adapter.ArticleAdapter;
import com.ncodelab.rewardingapp.model.Article;
import com.ncodelab.rewardingapp.model.User;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    RecyclerView articleRecyclerview;
    ArrayList<Article> articles;

    ArticleAdapter articleAdapter;

    FirebaseFirestore database;

    MaterialCardView spinWheelCard,quizCard,referActivityCard;

    TextView userName,userPoints;

    InterstitialAd mInterstitialAd;

    Context contextFragment;

    public final String TAG = "ADMOB";

    MaterialCardView adCard1,adCard2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        database = FirebaseFirestore.getInstance();

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        articles = new ArrayList<Article>();

        articleRecyclerview = view.findViewById(R.id.article_recycler_view);

        spinWheelCard = view.findViewById(R.id.spin_wheel_card);
        quizCard = view.findViewById(R.id.quiz_card);
        referActivityCard = view.findViewById(R.id.refer_earn_card);

        userName = view.findViewById(R.id.user_name);
        userPoints = view.findViewById(R.id.user_points);

        adCard1 = view.findViewById(R.id.ad_card_1);
        adCard2 = view.findViewById(R.id.ad_card_2);

        showInterstitialAds();

        getUserData();

        spinWheelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            startActivity(new Intent(getContext(),SpinWheelActivity.class));
                        }
                    });

                } else {
                    startActivity(new Intent(getContextNullSafety(), SpinWheelActivity.class));
                }
            }
        });

        quizCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            startActivity(new Intent(getContext(),QuizActivity.class));
                        }
                    });

                } else {
                    startActivity(new Intent(getContextNullSafety(), QuizActivity.class));
                }
            }
        });

        referActivityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            startActivity(new Intent(getContext(),ReferralActivity.class));
                        }
                    });

                } else {
                    startActivity(new Intent(getContextNullSafety(), ReferralActivity.class));
                }
            }
        });

        showBannerAd();

        DataChangeListener();

        setArticleRecyclerview();

        return view;
    }

    public void setArticleRecyclerview(){

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        articleRecyclerview.setLayoutManager(layoutManager);

        ArrayList<Article> articleArrayList = new ArrayList<Article>();

        ViewCompat.setNestedScrollingEnabled(articleRecyclerview, false);

        articleAdapter = new ArticleAdapter(getContext(), articles);

        articleRecyclerview.setAdapter(articleAdapter);

    }

    public void DataChangeListener(){

        database.collection("Articles")
                .orderBy("uploadTime", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null){
                            Log.d("firestoreError",error.getMessage());
                        }
                        else {
                            for (DocumentChange dc: value.getDocumentChanges()){

                                if (dc.getType() == DocumentChange.Type.ADDED){

                                    articles.add(dc.getDocument().toObject(Article.class));
                                }
                                if (dc.getType() == DocumentChange.Type.REMOVED){

                                    articles.remove(dc.getDocument().toObject(Article.class));
                                }
                                articleAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                });

    }

    public void getUserData(){

        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        User user = documentSnapshot.toObject(User.class);

                        userName.setText(user.getName());
                        userPoints.setText(String.valueOf(user.getUserPoints()));

                    }
                });

    }

    public void showBannerAd(){


        DocumentReference documentReference = database.collection("Settings").document("Ads");

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String bannerId = documentSnapshot.getString("BannerAdId");

                if (bannerId == null){
                    adCard1.setVisibility(View.GONE);
                    adCard2.setVisibility(View.GONE);
                }
                else {

                    AdView adView = new AdView(getContextNullSafety());
                    adView.setAdSize(AdSize.BANNER);
                    adView.setAdUnitId(bannerId);
                    ((MaterialCardView)adCard1).addView(adView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);

                    AdView adView1 = new AdView(getContextNullSafety());
                    adView1.setAdSize(AdSize.BANNER);
                    adView1.setAdUnitId(bannerId);
                    ((MaterialCardView)adCard2).addView(adView1);
                    AdRequest adRequest1 = new AdRequest.Builder().build();
                    adView1.loadAd(adRequest1);

                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.d("ADMOB","ERROR =" +loadAdError.toString());
                            adCard1.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            Log.d("ADMOB","Ad Loaded");
                        }
                    });

                    adView1.setAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.d("ADMOB","ERROR =" +loadAdError.toString());
                            adCard2.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
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

                    InterstitialAd.load(getContextNullSafety(),interstitialId, adRequest,
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextFragment = context;
    }

    /**CALL THIS IF YOU NEED CONTEXT*/
    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextFragment != null) return contextFragment;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
}