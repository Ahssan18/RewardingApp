package com.ncodelab.rewardingapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.activities.AboutUsActivity;
import com.ncodelab.rewardingapp.activities.PrivacyPolicyActivity;
import com.ncodelab.rewardingapp.activities.SupportActivity;
import com.ncodelab.rewardingapp.activities.UpdateDetailsActivity;
import com.ncodelab.rewardingapp.activities.WelcomeActivity;
import com.ncodelab.rewardingapp.model.User;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    TextView userName,userEmail;
    FirebaseFirestore database;

    Context contextFragment;

    MaterialCardView updateDetailsCard,logoutCard,aboutUsCard,privacyPolicyCard,supportCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseFirestore.getInstance();

        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        updateDetailsCard = view.findViewById(R.id.update_details_card);
        logoutCard = view.findViewById(R.id.logout_card);
        aboutUsCard = view.findViewById(R.id.about_us_card);
        privacyPolicyCard = view.findViewById(R.id.privacy_policy_card);
        supportCard = view.findViewById(R.id.support_card);

        updateDetailsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateDetailsActivity.class));
            }
        });

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog logoutDialog = new Dialog(getContextNullSafety());

                logoutDialog.setContentView(R.layout.logout_dialogue);

                logoutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

                MaterialButton cancelLogoutBtn,logoutBtn;

                cancelLogoutBtn = logoutDialog.findViewById(R.id.cancel_logout_btn);
                logoutBtn = logoutDialog.findViewById(R.id.logout_btn);

                cancelLogoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logoutDialog.cancel();
                    }
                });

                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContextNullSafety(), WelcomeActivity.class));
                        getActivity().finish();
                    }
                });

                logoutDialog.show();
            }
        });

        aboutUsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContextNullSafety(), AboutUsActivity.class));
            }
        });

        privacyPolicyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContextNullSafety(), PrivacyPolicyActivity.class));
            }
        });

        supportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContextNullSafety(), SupportActivity.class));
            }
        });

        getUserData();
        checkLogin();


        return view;
    }
    private void getUserData(){

        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                userName.setText(user.getName());
                userEmail.setText(user.getEmail());

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
    private void checkLogin(){

        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        User user = documentSnapshot.toObject(User.class);

                        if (user.getSignInBy().equals("Google")){

                            updateDetailsCard.setEnabled(false);
                            updateDetailsCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContextNullSafety(), "You are sign in by google", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                });



    }
}