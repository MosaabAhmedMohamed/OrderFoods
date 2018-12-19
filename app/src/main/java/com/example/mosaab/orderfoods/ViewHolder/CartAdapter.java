package com.example.mosaab.orderfoods.ViewHolder;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Database.Database;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.Model.Order;
import com.example.mosaab.orderfoods.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
    public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

        private List<Order> ListData;
        private Cart Cart;
        private List<Order> cart;
        private CartViewHolder cartView_Holder;


        public CartAdapter(List<Order> listData, Cart cart) {
            ListData = listData;
            this.Cart = cart;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View itemView = inflater.inflate(R.layout.cart_layout, viewGroup, false);

            return new CartViewHolder(itemView);
        }




        @Override
        public void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, final int i)
        {

            Picasso.get()
                    .load(ListData.get(i).getImage())
                    .into(cartViewHolder.cart_food_iamge);

            cartViewHolder.elegantNumberButton.setNumber(ListData.get(i).getQuantity());
            cartViewHolder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener()
            {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                    Order order = ListData.get(i);
                    order.setQuantity(String.valueOf(newValue));
                    new Database(Cart.getApplicationContext()).updateCart(order);
                    cartView_Holder = cartViewHolder;
                    Total_Price(cartViewHolder,i);
                }
            });



            cartViewHolder.text_cart_name.setText(ListData.get(i).getProductName());

        }

        private void Total_Price(CartViewHolder cartViewHolder, int i) {
            Locale locale = new Locale("en", "US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            int price = (Integer.parseInt(ListData.get(i).getPrice())) * (Integer.parseInt(ListData.get(i).getQuantity()));
            cartViewHolder.text_price.setText(fmt.format(price));

            cart=new Database(Cart.getApplicationContext()).getCarts(Common.currntUser.getPhone());
            int total =0;

            for(Order order : cart)
            {
                total+= (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));


                Cart.txtTotalPrice.setText(fmt.format(total));
            }
        }

        @Override
        public int getItemCount() {
            return ListData.size();
        }

        public Order getItem(int position)
        {
            return ListData.get(position);
        }
        public void remove_item(int position)
        {
            ListData.get(position);
           notifyItemRemoved(position);
           notifyDataSetChanged();

        }

        public void restore_item(Order item, int position)
        {
            ListData.add(position,item);
            notifyItemInserted(position);

        }

      public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
                View.OnCreateContextMenuListener {

            public TextView text_cart_name, text_price;
            public ImageView cart_food_iamge;
            public ElegantNumberButton elegantNumberButton;

            public RelativeLayout back_ground_View;
            public LinearLayout forground_View;

            private ItemClickListner itemClickListner;

            public void setText_cart_name(TextView text_cart_name)
            {
                this.text_cart_name = text_cart_name;
            }

            public CartViewHolder(@NonNull View itemView) {
                super(itemView);
                text_cart_name = itemView.findViewById(R.id.cart_item_name);
                text_price = itemView.findViewById(R.id.cart_item_ptice);

                cart_food_iamge = itemView.findViewById(R.id.food_image_cart);
                elegantNumberButton = itemView.findViewById(R.id.number_button_cart);

                back_ground_View = itemView.findViewById(R.id.background_view);
                forground_View = itemView.findViewById(R.id.forground_view);

                itemView.setOnCreateContextMenuListener(this);

            }

            @Override
            public void onClick(View v) {

            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Select action");
                menu.add(0, 0, getAdapterPosition(), Common.DELETE);
            }
        }
    }