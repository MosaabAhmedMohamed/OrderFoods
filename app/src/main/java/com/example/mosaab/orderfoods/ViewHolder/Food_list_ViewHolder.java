package com.example.mosaab.orderfoods.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mosaab.orderfoods.R;

import com.example.mosaab.orderfoods.Interface.ItemClickListner;

public class Food_list_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name;
    public ImageView food_iamge,fav_image;

    private ItemClickListner itemClickListner;

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    public Food_list_ViewHolder(@NonNull View itemView) {
        super(itemView);

        food_name=itemView.findViewById(R.id.food_name);
        food_iamge=itemView.findViewById(R.id.food_iamge);
        fav_image=itemView.findViewById(R.id.fav);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onclick(v,getAdapterPosition(),false);

    }
}
