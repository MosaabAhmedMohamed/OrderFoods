package com.example.mosaab.orderfoods.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Model.Rating;
import com.example.mosaab.orderfoods.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Show_Comments extends AppCompatActivity {

    private static final String TAG = "Show_Comments";
    private RecyclerView recyclerView;

    private FirebaseDatabase db;
    private DatabaseReference rating_Table;

    private SwipeRefreshLayout swipeRefreshLayout;


    //vars
    private String food_id= "";

    private FirebaseRecyclerAdapter<Rating,Show_Comments_Adapter> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Apply_New_Font();
        setContentView(R.layout.activity_show__comments);

        Init_UI();
    }

    private void Init_UI()
    {
        db = FirebaseDatabase.getInstance();
        rating_Table = db.getReference("Rating");


        recyclerView = findViewById(R.id.comment_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_comments);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                check_intrnet();
            }
        });

        //for first load
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run()
            {
                check_intrnet();
            }
        });

    }

    private void check_intrnet()
    {
        if (getIntent()!= null) {

            food_id =getIntent().getStringExtra(Common.FOOD_ID);
            Log.d(TAG, "check_intrnet: "+food_id.toString());
                    if (!food_id.isEmpty() && food_id != null)
                    {
                        if (Common.isConnectedToInternet(Show_Comments.this))
                        {
                            LoadComments_ViewHolder();
                        }
                        else {
                            Toast.makeText(Show_Comments.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
        }
    }

    private void LoadComments_ViewHolder()
    {
        Log.d(TAG, "LoadComments_ViewHolder: ");
        Query query = rating_Table.orderByChild("food_ID").equalTo(food_id);

        FirebaseRecyclerOptions<Rating> options = new FirebaseRecyclerOptions.Builder<Rating>()
                .setQuery(query,Rating.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Rating, Show_Comments_Adapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Show_Comments_Adapter holder, int position, @NonNull Rating model) {

                holder.ratingBar_Comments.setRating(Float.parseFloat(model.getRate_Value()));
                holder.comment_TV.setText("Comment: "+model.getComment());
                holder.user_phone_TV.setText("User :"+ model.getUser_Phone());

            }

            @NonNull
            @Override
            public Show_Comments_Adapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_comment_layout,viewGroup,false);

                return new Show_Comments_Adapter(itemView);
            }
        };
        adapter.startListening();

        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }

    private void Apply_New_Font() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        if (adapter != null)
        {
            adapter.stopListening();
        }
    }
}
