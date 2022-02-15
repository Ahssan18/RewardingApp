package com.ncodelab.rewardingapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.adapter.EarningHistoryAdapter;
import com.ncodelab.rewardingapp.model.EarningHistory;
import com.ncodelab.rewardingapp.model.User;
import com.ncodelab.rewardingapp.model.WithdrawRequest;

import java.util.ArrayList;
import java.util.Objects;


public class WalletFragment extends Fragment {

    public WalletFragment() {
        // Required empty public constructor
    }
    Context FragmentContext;
    MaterialButton openWithdrawDialogue;
    TextView userEarnings,pointsConversion,minimumWithdrawLimit;

    long withdrawLimit,pointsValue;

    String userName,userUid;

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final String TAG = "QUIZ_APP_WITHDRAW";

    RecyclerView earningHistoryRecyclerView;
    ArrayList<EarningHistory> earningHistoryList;
    EarningHistoryAdapter earningHistoryAdapter;
    User user;
    long totalWithdrawRequests;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        openWithdrawDialogue = view.findViewById(R.id.open_withdraw_dialogue_btn);
        userEarnings = view.findViewById(R.id.user_points);

        earningHistoryRecyclerView = view.findViewById(R.id.user_earning_history_recyclerview);

        pointsConversion = view.findViewById(R.id.points_conversion);
        minimumWithdrawLimit = view.findViewById(R.id.minimum_withdraw_limit_text);
        getData();
        setEarningRecyclerview();
        DateChangeListener();

        openWithdrawDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdraw();
            }
        });


        return view;

    }

    public void setEarningRecyclerview(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        earningHistoryRecyclerView.setLayoutManager(layoutManager);
        earningHistoryList = new ArrayList<EarningHistory>();

        ViewCompat.setNestedScrollingEnabled(earningHistoryRecyclerView, false);

        earningHistoryAdapter = new EarningHistoryAdapter(getContext(), earningHistoryList);

        earningHistoryRecyclerView.setAdapter(earningHistoryAdapter);
    }

    public void DateChangeListener(){

        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .collection("UserEarnedPoints").orderBy("earningDate", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null){

                            Log.d("Firestore_error",error.getMessage());
                            return;
                        }

                        for (DocumentChange dc: value.getDocumentChanges()){

                            if (dc.getType() == DocumentChange.Type.ADDED){

                                earningHistoryList.add(dc.getDocument().toObject(EarningHistory.class));

                            }
                            earningHistoryAdapter.notifyDataSetChanged();

                        }


                    }
                });


    }

    public void withdraw(){

        Dialog withdrawDialogue = new Dialog(getContextNullSafety());
        withdrawDialogue.setContentView(R.layout.withdraw_dialogue);

        MaterialButton withdrawBtn;


        withdrawBtn = withdrawDialogue.findViewById(R.id.withdraw_btn);

        withdrawDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        if (user.getUserPoints() >= withdrawLimit){

            withdrawDialogue.show();



            withdrawBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String paypalId,amount;

                    TextInputLayout paypalIdTextLayout,amountTextLayout;
                    paypalIdTextLayout = withdrawDialogue.findViewById(R.id.paypal_id_textlayout);
                    amountTextLayout = withdrawDialogue.findViewById(R.id.amount_text_layout);


                    paypalId = paypalIdTextLayout.getEditText().getText().toString();
                    amount = Objects.requireNonNull(amountTextLayout.getEditText()).getText().toString();

                    int finalPointsToWithdraw = Integer.parseInt(amount);

                    //Rechecking
                    if (user.getUserPoints()>=withdrawLimit){

                        if (finalPointsToWithdraw > user.getUserPoints()){
                            displayErrorToast("Not Enough Points");
                            return;
                        }

                        //Decrementing user points
                        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                                .update("userPoints", FieldValue.increment(-finalPointsToWithdraw))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            withdrawDialogue.cancel();
                                        }

                                    }
                                });

                        //Adding to history
                        EarningHistory earningHistory = new EarningHistory("Withdraw", (long) finalPointsToWithdraw);
                        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                                .collection("UserEarnedPoints")
                                .add(earningHistory)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if (task.isSuccessful()){
                                            Log.d(TAG,"Earning History updated");
                                        }
                                        else {
                                            Log.d(TAG,"Earning history not updated REASON = " +task.getException());
                                        }

                                    }
                                });

                        //Sending withdraw request
                        long addWithdrawRequest = ++totalWithdrawRequests;
                        WithdrawRequest withdrawRequest = new WithdrawRequest(userName,userUid,paypalId, finalPointsToWithdraw,"Withdraw"+ addWithdrawRequest+firebaseAuth.getUid().substring(24));
                        database.collection("WithdrawRequests")
                                .document("Withdraw"+addWithdrawRequest+withdrawRequest.getUserId().substring(24))
                                .set(withdrawRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            Log.d(TAG,"Withdraw request sent");
                                            displaySuccessfulToast("Withdraw request sent");

                                            database.collection("Stats").document("stats")
                                                    .update("withdrawRequests", FieldValue.increment(1));

                                            withdrawDialogue.cancel();
                                        }
                                        else {
                                            Log.d(TAG,"Withdraw request not sent REASON = " +task.getException());
                                        }

                                    }
                                });
                    }

                    else {
                        displayErrorToast("Not enough points left");
                    }



                }
            });
        }

        else {
            displayErrorToast("Not enough points to withdraw");
            withdrawDialogue.cancel();
        }


    }

    public void getData(){
        DocumentReference documentReference = database.collection("Settings").document("withdrawInfo");

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                withdrawLimit = documentSnapshot.getLong("minimumWithdrawLimit");

                pointsValue = documentSnapshot.getLong("pointsConversion");

                minimumWithdrawLimit.setText(withdrawLimit+" in minimum withdraw limit");

                pointsConversion.setText(pointsValue +" = 1 $");


            }
        });

        DocumentReference documentReference1 = database.collection("Stats").document("stats");

        documentReference1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                totalWithdrawRequests = documentSnapshot.getLong("withdrawRequests");
            }
        });





        database.collection("Users").document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        user = documentSnapshot.toObject(User.class);

                        userEarnings.setText(String.valueOf(user.getUserPoints()));

                        userName = user.getName();
                        userUid = user.getReferCode();

                    }
                });

    }

    public void displayErrorToast(String msg){
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.toast_layout,getView().findViewById(R.id.customToast));

        MaterialCardView customToast = view.findViewById(R.id.customToast);
        TextView toastMessage = view.findViewById(R.id.toastMessage);
        ImageView icon = view.findViewById(R.id.toastIcon);

        toastMessage.setText(msg);
        icon.setImageDrawable(getContext().getDrawable(R.drawable.ic_wrong));

        customToast.setCardBackgroundColor(getResources().getColor(R.color.red));

        Toast errorToast = new Toast(getContext());
        errorToast.setDuration(Toast.LENGTH_SHORT);


        errorToast.setView(view);

        errorToast.setGravity(Gravity.TOP,0,20);

        errorToast.show();
    }

    public void displaySuccessfulToast(String msg){
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.toast_layout,getView().findViewById(R.id.customToast));

        MaterialCardView customToast = view.findViewById(R.id.customToast);
        TextView toastMessage = view.findViewById(R.id.toastMessage);
        ImageView icon = view.findViewById(R.id.toastIcon);

        toastMessage.setText(msg);
        icon.setImageDrawable(getContext().getDrawable(R.drawable.ic_right));

        customToast.setCardBackgroundColor(getResources().getColor(R.color.green));

        Toast errorToast = new Toast(getContext());
        errorToast.setDuration(Toast.LENGTH_SHORT);


        errorToast.setView(view);

        errorToast.setGravity(Gravity.TOP,0,20);

        errorToast.show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FragmentContext = context;
    }

    /**CALL THIS IF YOU NEED CONTEXT*/
    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (FragmentContext != null) return FragmentContext;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
}