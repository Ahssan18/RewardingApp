package com.ncodelab.rewardingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ncodelab.rewardingapp.R;
import com.ncodelab.rewardingapp.activities.WebActivity;
import com.ncodelab.rewardingapp.model.Article;
import com.ncodelab.rewardingapp.model.EarningHistory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    Context context;
    ArrayList<Article> articleArrayList;

    public ArticleAdapter(Context context, ArrayList<Article> articleArrayList) {
        this.context = context;
        this.articleArrayList = articleArrayList;
    }

    @NonNull
    @Override
    public ArticleAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_card_layout,parent,false);

        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleAdapter.ArticleViewHolder holder, int position) {

        Article article = articleArrayList.get(position);

        if (article.getArticleDescription() == null){
            holder.articleDescription.setVisibility(View.GONE);
        }
        if (article.getArticleImageUrl() == null){
            holder.articleImage.setVisibility(View.GONE);
        }

        holder.articleTitle.setText(article.getArticleTitle());
        holder.articleDescription.setText(article.getArticleDescription());
        Picasso.get().load(article.getArticleImageUrl()).into(holder.articleImage);


        holder.articleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                v.getContext().startActivity(browserIntent);
                
                long articleReward = 1;

                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                        .update("userPoints", FieldValue.increment(articleReward));

                EarningHistory earningHistory = new EarningHistory("Reading Article",articleReward);
                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getUid())
                        .collection("UserEarnedPoints")
                        .document()
                        .set(earningHistory);
            }
        });



    }

    @Override
    public int getItemCount() {
        return articleArrayList.size();
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView articleCard;
        TextView articleTitle,articleDescription;
        ImageView articleImage;


        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);

            articleCard = itemView.findViewById(R.id.article_card);
            articleTitle = itemView.findViewById(R.id.article_title);
            articleDescription = itemView.findViewById(R.id.article_description);

            articleImage = itemView.findViewById(R.id.article_image);

            articleCard = itemView.findViewById(R.id.article_card);


        }
    }
}
