package com.example.mosaab.orderfoods.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Database.Database;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.Interface.OnItemClickListnrer;
import com.example.mosaab.orderfoods.Model.Favorites;
import com.example.mosaab.orderfoods.Model.Foods;
import com.example.mosaab.orderfoods.Model.Order;
import com.example.mosaab.orderfoods.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Favorties_Adapter extends RecyclerView.Adapter<Favorties_Adapter.FavrotiesViewHolder> {


    private String TAG = "Favorties_Adapter";
    private Context context;
    private List<Favorites> favoritesList;
    private OnItemClickListnrer onItemClickListnrer;


    public Favorties_Adapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    public void setOnItemClickListner(OnItemClickListnrer onitem_clicklistner)
    {
        onItemClickListnrer = onitem_clicklistner;
    }



    @NonNull
    @Override
    public FavrotiesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

       View itemView = LayoutInflater.from(viewGroup.getContext())
               .inflate(R.layout.single_favorties,viewGroup,false);

        return new FavrotiesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavrotiesViewHolder viewHolder, final int i) {


        viewHolder.food_name.setText(favoritesList.get(i).getFoodName());
        viewHolder.price_TV.setText(favoritesList.get(i).getFoodPrice());
        Picasso.get().load(favoritesList.get(i).getFoodIamge())
                .into(viewHolder.food_iamge);

       // boolean isExist = new Database(context).Check_if_Food_exist_inCART(favoritesList.get(i).getFoodId(),Common.currntUser.getPhone());
        final Favorites local = favoritesList.get(i);

        viewHolder.add_ToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListnrer != null) {
                    onItemClickListnrer.onItemClick(view, i);
                }
            }
        });


         viewHolder.food_iamge.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent detailIntent= new Intent(context,FoodDetail.class);
                 detailIntent.putExtra("FoodId",favoritesList.get(i).getFoodId());
                 context.startActivity(detailIntent);
                 Log.d(TAG, "onBindViewHolder: " + favoritesList.get(i).getFoodName() );

             }
         });




    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }
    public void remove_item(int position)
    {
        favoritesList.get(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();

    }

    public void restore_item(Favorites item, int position)
    {
        favoritesList.add(position,item);
        notifyItemInserted(position);

    }

    public Favorites getItem(int Position) {

        return favoritesList.get(Position);
    }

    public class FavrotiesViewHolder extends RecyclerView.ViewHolder   {

        public TextView food_name,price_TV;
        public ImageView food_iamge,fav_image,add_ToCart;
        public RelativeLayout back_ground_View;
        public ConstraintLayout forground_View;


        public FavrotiesViewHolder(@NonNull View itemView)
        {
            super(itemView);

            food_name =itemView.findViewById(R.id.food_name);
            food_iamge =itemView.findViewById(R.id.food_iamge);
            fav_image =itemView.findViewById(R.id.fav);
            price_TV =itemView.findViewById(R.id.price_foodList_TV);
            add_ToCart = itemView.findViewById(R.id.addToCart_FoodItem);

            back_ground_View = itemView.findViewById(R.id.background_view);
            forground_View = itemView.findViewById(R.id.forground_view);
        }

    }
}
