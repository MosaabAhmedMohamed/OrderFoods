package com.example.mosaab.orderfoods.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtOrderId, txtOrderStauts, txtOrderPhone, txtOrderAddress;
    public ImageView delete_order;

    private ItemClickListner itemClickListner;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = itemView.findViewById(R.id.Order_address);
        txtOrderId = itemView.findViewById(R.id.order_name);
        txtOrderStauts = itemView.findViewById(R.id.Order_status);
        txtOrderPhone = itemView.findViewById(R.id.Order_Phone);
        delete_order = itemView.findViewById(R.id.order_item_delete);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onclick(v,getAdapterPosition(),false);
    }
}

