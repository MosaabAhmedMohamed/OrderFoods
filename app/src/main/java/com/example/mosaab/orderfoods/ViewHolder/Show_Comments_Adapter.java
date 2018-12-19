package com.example.mosaab.orderfoods.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mosaab.orderfoods.R;

public class Show_Comments_Adapter extends RecyclerView.ViewHolder
{
    public TextView user_phone_TV,comment_TV;
    public RatingBar ratingBar_Comments;

    public Show_Comments_Adapter(@NonNull View itemView) {
        super(itemView);
        user_phone_TV = itemView.findViewById(R.id.user_comment_tv);
        comment_TV = itemView.findViewById(R.id.comment_tv);
        ratingBar_Comments = itemView.findViewById(R.id.Rating_Bar_comment);
    }


}
