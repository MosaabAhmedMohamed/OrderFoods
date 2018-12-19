package com.example.mosaab.orderfoods.ViewHolder;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Database.Database;
import com.example.mosaab.orderfoods.Helper.Recycler_item_touch_helper;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.Interface.OnItemClickListnrer;
import com.example.mosaab.orderfoods.Interface.Recycler_item_touch_helper_listner;
import com.example.mosaab.orderfoods.Model.Favorites;
import com.example.mosaab.orderfoods.Model.Foods;
import com.example.mosaab.orderfoods.Model.Order;
import com.example.mosaab.orderfoods.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavortiesActivity extends AppCompatActivity implements Recycler_item_touch_helper_listner, OnItemClickListnrer {

    private List <Favorites> favoritesList ;
    private Favorties_Adapter favorties_adapter;
    private static final String TAG = "FavortiesActivity";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout baseLayout;
    public ImageView add_ToCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorties);

        Init_UI();
    }

    private void Init_UI() {

        baseLayout = findViewById(R.id.base_layout);

        favoritesList = new ArrayList<>();
        favoritesList = new Database(this).get_all_favorties(Common.currntUser.getPhone());
        favorties_adapter = new Favorties_Adapter(this,favoritesList);

        recyclerView = findViewById(R.id.recycler_favotties);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavortiesActivity.this));


        add_ToCart = findViewById(R.id.addToCart_FoodItem);
        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new Recycler_item_touch_helper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);


      InitSwipeRefresh();

    }

    private void InitSwipeRefresh() {

        baseLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        baseLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                //get intent that contain specific category ID
                if(getIntent()!=null)
                {

                    LoadFavorties();
                }

            }
        });

        baseLayout.post(new Runnable() {
            @Override
            public void run()
            {
                //get intent that contain specific category ID
                if(getIntent()!=null)
                {

                    LoadFavorties();
                }

            }
        });

    }

    private void LoadFavorties()
    {
       recyclerView.setAdapter(favorties_adapter);
       favorties_adapter.setOnItemClickListner(FavortiesActivity.this);
        baseLayout.setRefreshing(false);
    }

    private void add_cart_to_DB(int position)
    {
        boolean isExist = new Database(this).Check_if_Food_exist_inCART(favoritesList.get(position).getFoodId(),Common.currntUser.getPhone());
        Log.d(TAG, "add_cart_to_DB: "+String.valueOf(isExist));
        if(!isExist) {
            new Database(getApplicationContext()).addToCart(new Order(
                    Common.currntUser.getPhone(),
                    favoritesList.get(position).getFoodId(),
                    favoritesList.get(position).getFoodName(),
                    "1",
                    favoritesList.get(position).getFoodPrice(),
                    favoritesList.get(position).getFoodDiscount(),
                    favoritesList.get(position).getFoodIamge()

            ));

        }
        else
        {
            new Database(getBaseContext()).increase_Cart(Common.currntUser.getPhone(),favoritesList.get(position).getFoodIamge());

        }
        Toast.makeText(FavortiesActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int podition) {

        if (viewHolder instanceof Favorties_Adapter.FavrotiesViewHolder)
        {
            String name = ((Favorties_Adapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getFoodName();

            final Favorites deleteItem =  ((Favorties_Adapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            new Database(getBaseContext()).removeFromCart(deleteItem.getFoodId(),Common.currntUser.getPhone());
            favorties_adapter.remove_item(deleteIndex);



                Snackbar snackbar = Snackbar.make(baseLayout,name+" removed from cart ",Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        new Database(getBaseContext()).addToFavorites(deleteItem);
                        favorties_adapter.restore_item(deleteItem,deleteIndex);

                    }


                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        }

    @Override
    public void onItemClick(View view, final int position) {

                add_cart_to_DB(position);


    }
}
