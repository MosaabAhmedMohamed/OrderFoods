package com.example.mosaab.orderfoods.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.Model.Order;
import com.example.mosaab.orderfoods.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,
    View.OnCreateContextMenuListener{

    public TextView text_cart_name,text_price;
    public ImageView image_cart_count;

    private ItemClickListner itemClickListner;

    public void setText_cart_name(TextView text_cart_name)
    {
        this.text_cart_name=text_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        text_cart_name = itemView.findViewById(R.id.cart_item_name);
        text_price = itemView.findViewById(R.id.cart_item_ptice);

        image_cart_count=itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
      menu.setHeaderTitle("Select action");
      menu.add(0,0,getAdapterPosition(),Common.DELETE);
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> ListData;
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        ListData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout,viewGroup,false);

        return new CartViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i) {

        TextDrawable drawable = TextDrawable.builder().buildRound(""+ListData.get(i).getQuantity(),Color.RED);

        cartViewHolder.image_cart_count.setImageDrawable(drawable);

        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(ListData.get(i).getPrice()))*(Integer.parseInt(ListData.get(i).getQuantity()));
        cartViewHolder.text_price.setText(fmt.format(price));

        cartViewHolder.text_cart_name.setText(ListData.get(i).getProductName());

    }

    @Override
    public int getItemCount() {
        return ListData.size();
    }
}
