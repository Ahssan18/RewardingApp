package com.ncodelab.rewardingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.model.ReferNetwork;

import java.util.ArrayList;

public class ReferNetworkAdapter extends RecyclerView.Adapter<ReferNetworkAdapter.ReferNetworkViewHolder> {

    Context context;
    ArrayList<ReferNetwork> referNetworkArrayList;

    public ReferNetworkAdapter(Context context, ArrayList<ReferNetwork> referNetworkArrayList) {
        this.context = context;
        this.referNetworkArrayList = referNetworkArrayList;
    }

    @NonNull
    @Override
    public ReferNetworkAdapter.ReferNetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.refer_network_layout,parent,false);
        return new ReferNetworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferNetworkAdapter.ReferNetworkViewHolder holder, int position) {

        ReferNetwork referNetwork = referNetworkArrayList.get(position);

        holder.emailId.setText(referNetwork.getEmailId().substring(0,4)+"*****"+ referNetwork.getEmailId().substring(8));
    }

    @Override
    public int getItemCount() {
        return referNetworkArrayList.size();
    }

    public class ReferNetworkViewHolder extends RecyclerView.ViewHolder {

        TextView emailId;

        public ReferNetworkViewHolder(@NonNull View itemView) {
            super(itemView);

            emailId = itemView.findViewById(R.id.email_id);

        }
    }
}
