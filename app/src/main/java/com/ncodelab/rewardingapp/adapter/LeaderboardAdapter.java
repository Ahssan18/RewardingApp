package com.ncodelab.rewardingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.model.User;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    Context context;
    ArrayList<User> leaderboardArraylist;

    public LeaderboardAdapter(Context context, ArrayList<User> leaderboardArraylist) {
        this.context = context;
        this.leaderboardArraylist = leaderboardArraylist;
    }

    @NonNull
    @Override
    public LeaderboardAdapter.LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_board_layout,parent,false);

        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.LeaderboardViewHolder holder, int position) {

        User leaderboard = leaderboardArraylist.get(position);

        holder.userName.setText(leaderboard.getName());
        holder.userRank.setText(""+ ++position);
        holder.userPoints.setText(String.valueOf(leaderboard.getUserPoints()));

        if (FirebaseAuth.getInstance().getUid().equals(leaderboard.getReferCode())){

            holder.leaderBardLayout.setBackgroundColor(context.getResources().getColor(R.color.primary_light));
            holder.userName.setTextColor(context.getResources().getColor(R.color.white));
            holder.userRank.setTextColor(context.getResources().getColor(R.color.white));
            holder.userPoints.setTextColor(context.getResources().getColor(R.color.white));

        }


    }

    @Override
    public int getItemCount() {
        return leaderboardArraylist.size();
    }

    public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

        TextView userName,userPoints,userRank;
        RelativeLayout leaderBardLayout;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userPoints = itemView.findViewById(R.id.user_points);
            userRank = itemView.findViewById(R.id.user_rank);
            leaderBardLayout = itemView.findViewById(R.id.user_leaderboard_layout);


        }
    }
}
