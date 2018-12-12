package com.example.mosaab.orderfoods.ViewHolder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Database.Database;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.Model.Foods;
import com.example.mosaab.orderfoods.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

   private RecyclerView recyclerView;
   private RecyclerView.LayoutManager layoutManager;

   private FirebaseDatabase database;
   private DatabaseReference foodlist_table;

   private String categoryId = "";

   private FirebaseRecyclerAdapter<Foods,Food_list_ViewHolder> adapter;

   //Serch Funcitonalty
   private FirebaseRecyclerAdapter<Foods,Food_list_ViewHolder> serchAdapter;
   private List<String> suggestList;
   private MaterialSearchBar materialSearchBar;
   private SwipeRefreshLayout swipeRefreshLayout;

   //favorites
   private Database localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        InitUI();
        search_bar();

    }

    private void InitUI() {

        database=FirebaseDatabase.getInstance();
        foodlist_table =database.getReference("Foods");

        //DATABASE LOCAL for saving favorite food
        localDB =new Database(this);

        recyclerView=findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        materialSearchBar = findViewById(R.id.serchBar);
        suggestList = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.food_list_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                //get intent that contain specific category ID
                if(getIntent()!=null)
                {
                    categoryId = getIntent().getStringExtra("CategoryId");
                    check_internet_connection();
                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run()
            {
                //get intent that contain specific category ID
                if(getIntent()!=null)
                {
                    categoryId = getIntent().getStringExtra("CategoryId");
                    check_internet_connection();
                }

            }
        });
    }

    private void check_internet_connection()
    {
        if(!categoryId.isEmpty()&&categoryId!=null)
        {
            if (Common.isConnectedToInternet(getBaseContext())){
                loadListFood(categoryId);}
            else {
                Toast.makeText(FoodList.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void search_bar()
    {
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // when user type their text

                List<String> suggest = new ArrayList<>();

                for (String search:suggestList)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when serch bar is closed
                //restore orignal adapter

                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                //when search finish
                // show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {

        serchAdapter = new FirebaseRecyclerAdapter<Foods, Food_list_ViewHolder>(
                Foods.class,
                R.layout.food_item,
                Food_list_ViewHolder.class,
                foodlist_table.orderByChild("Name").equalTo(text.toString())
        )
            {
                @Override
                protected void populateViewHolder(Food_list_ViewHolder viewHolder, Foods model, final int position)
                {

                viewHolder.food_name.setText(model.getName());

                Picasso.with(getApplicationContext()).load(model.getImage())
                        .into(viewHolder.food_iamge);


                final Foods local =model;
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onclick(View view, int postion, boolean isLongClick)
                    {
                        //intent to specific detail of food
                        Intent detailIntent= new Intent(FoodList.this,FoodDetail.class);
                        detailIntent.putExtra("FoodId",serchAdapter.getRef(position).getKey());
                        startActivity(detailIntent);

                    }
                });
            }
            };
        recyclerView.setAdapter(serchAdapter);
    }

    private void loadSuggest() {

        foodlist_table.orderByChild("MenuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapShot : dataSnapshot.getChildren())
                {
                    Foods item = postSnapShot.getValue(Foods.class);
                    suggestList.add(item.getName()); //add name of food to suggest list
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(FoodList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadListFood(String categoryId) {
         adapter=new FirebaseRecyclerAdapter<Foods, Food_list_ViewHolder>(
                 Foods.class,
                 R.layout.food_item,
                 Food_list_ViewHolder.class,
                 foodlist_table.orderByChild("MenuId").equalTo(categoryId))
         {
             @Override
             protected void populateViewHolder(final Food_list_ViewHolder viewHolder, final Foods model, final int position) {
                 viewHolder.food_name.setText(model.getName());
                 Picasso.with(getApplicationContext()).load(model.getImage())
                         .into(viewHolder.food_iamge);

                 //add favorites
                 add_favorite(viewHolder,model,position);

                 final Foods local =model;
                 viewHolder.setItemClickListner(new ItemClickListner() {
                     @Override
                     public void onclick(View view, int postion, boolean isLongClick) {
                         Intent detailIntent= new Intent(FoodList.this,FoodDetail.class);
                         detailIntent.putExtra("FoodId",adapter.getRef(position).getKey());
                         startActivity(detailIntent);

                     }
                 });
             }
         };
         recyclerView.setAdapter(adapter);

         swipeRefreshLayout.setRefreshing(false);
    }

    //add and delete favorite foods
    private void add_favorite(final Food_list_ViewHolder viewHolder, final Foods model, final int position)
    {
        if (localDB.isFavorites(adapter.getRef(position).getKey()))
        {
            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        //click to change state of favorite
        viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!localDB.isFavorites(adapter.getRef(position).getKey()))
                {
                    localDB.addToFavorites(adapter.getRef(position).getKey());
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                    Toast.makeText(FoodList.this, ""+model.getName()+"Was added to favorites", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    localDB.removeFromFavorites(adapter.getRef(position).getKey());
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    Toast.makeText(FoodList.this, ""+model.getName()+"was removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
