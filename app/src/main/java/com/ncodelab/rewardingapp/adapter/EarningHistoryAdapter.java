package com.ncodelab.rewardingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.model.EarningHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarningHistoryAdapter extends RecyclerView.Adapter<EarningHistoryAdapter.EarningHistoryViewHolder> {

    Context context;
    ArrayList<EarningHistory> earningHistoryArrayList;

    public EarningHistoryAdapter(Context context, ArrayList<EarningHistory> earningHistoryArrayList) {
        this.context = context;
        this.earningHistoryArrayList = earningHistoryArrayList;
    }

    @NonNull
    @Override
    public EarningHistoryAdapter.EarningHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.earning_history_layout,parent,false);

        return new EarningHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningHistoryAdapter.EarningHistoryViewHolder holder, int position) {

        EarningHistory earningHistory = earningHistoryArrayList.get(position);

        if (earningHistory.getEarnedBy().equals("Withdraw")){
            holder.pointsEarned.setTextColor(context.getResources().getColor(R.color.red));
            holder.earnedBy.setTextColor(context.getResources().getColor(R.color.red));
        }
        else if (earningHistory.getPointsEarned().toString().startsWith("-")){
            holder.pointsEarned.setTextColor(context.getResources().getColor(R.color.red));
            holder.earnedBy.setTextColor(context.getResources().getColor(R.color.red));
        }
        else {
            holder.pointsEarned.setTextColor(context.getResources().getColor(R.color.green));
            holder.earnedBy.setTextColor(context.getResources().getColor(R.color.green));

        }
        holder.earnedBy.setText(earningHistory.getEarnedBy());
        holder.pointsEarned.setText(String.valueOf(earningHistory.getPointsEarned()));

        Date date = earningHistory.getEarningDate();

        if (date == null){

            holder.dateEarned.setVisibility(View.INVISIBLE);

        }
        else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm");
            holder.dateEarned.setText(simpleDateFormat.format(date));
        }




    }

    @Override
    public int getItemCount() {
        return earningHistoryArrayList.size();
    }

    public class EarningHistoryViewHolder extends RecyclerView.ViewHolder {

        TextView pointsEarned,earnedBy,dateEarned;

        public EarningHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            pointsEarned = itemView.findViewById(R.id.earningHistory);
            earnedBy = itemView.findViewById(R.id.earned_by);
            dateEarned = itemView.findViewById(R.id.date);

        }
    }
}
