package com.ncodelab.rewardingapp.fragments;

import android.content.Context;
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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.adapter.LeaderboardAdapter;
import com.ncodelab.rewardingapp.model.User;

import java.util.ArrayList;


public class LeaderboardFragment extends Fragment {

    public LeaderboardFragment() {
        // Required empty public constructor
    }
    Context fragmentContext;
    RecyclerView leaderboardRecyclerview;
    LeaderboardAdapter leaderboardAdapter;
    ArrayList<User> leaderboardList;
    FirebaseFirestore database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        database = FirebaseFirestore.getInstance();

        leaderboardRecyclerview = view.findViewById(R.id.leaderboard_recyclerview);

        setEarningHistoryRecyclerview();
        DateChangeListener();

        return view;
    }
    public void setEarningHistoryRecyclerview(){
        leaderboardRecyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        leaderboardRecyclerview.setLayoutManager(layoutManager);
        leaderboardList = new ArrayList<User>();

        ViewCompat.setNestedScrollingEnabled(leaderboardRecyclerview, false);

        leaderboardAdapter = new LeaderboardAdapter(getContextNullSafety(),leaderboardList);

        leaderboardRecyclerview.setAdapter(leaderboardAdapter);
    }

    public void DateChangeListener(){

        database.collection("Users").orderBy("userPoints", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null){

                            Log.d("Firestore_error",error.getMessage());
                            return;
                        }

                        for (DocumentChange dc: value.getDocumentChanges()){

                            if (dc.getType() == DocumentChange.Type.ADDED){

                                leaderboardList.add(dc.getDocument().toObject(User.class));

                            }
                            leaderboardAdapter.notifyDataSetChanged();

                        }


                    }
                });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentContext = context;
    }

    /**CALL THIS IF YOU NEED CONTEXT*/
    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (fragmentContext != null) return fragmentContext;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
}