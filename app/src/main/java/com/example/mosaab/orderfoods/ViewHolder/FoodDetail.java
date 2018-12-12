package com.example.mosaab.orderfoods.ViewHolder;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Database.Database;
import com.example.mosaab.orderfoods.Model.Foods;
import com.example.mosaab.orderfoods.Model.Order;
import com.example.mosaab.orderfoods.Model.Rating;
import com.example.mosaab.orderfoods.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.AppRatingDialogView;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    private TextView food_name,fod_price,food_description;
    private ImageView food_detail_image;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton btnCart,btn_Rating;
    private ElegantNumberButton elegantNumberButton;
    private RatingBar ratingBar;

    private Foods CurrantFoods;

    private String food_id="";

    private FirebaseDatabase database;
    private DatabaseReference food_detail;
    private DatabaseReference rating_Table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        InitUI();
        if(getIntent()!=null)
        {
            food_id=getIntent().getStringExtra("FoodId");
            if(!food_id.isEmpty())
            {
                check_internet_connection();
            }
        }

        btn_Rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_Rating_Dialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get from local database
                add_cart_to_DB();
            }
        });

    }

    private void InitUI()
    {
        elegantNumberButton=findViewById(R.id.number_button);
        btnCart=findViewById(R.id.btnCart);
        btn_Rating = findViewById(R.id.btn_Rating);
        ratingBar = findViewById(R.id.Rating_Bar);

        //init firebase
        database = FirebaseDatabase.getInstance();
        food_detail =database.getReference("Foods");
        rating_Table =database.getReference("Rating");

        food_description=findViewById(R.id.food_decerption);
        fod_price=findViewById(R.id.food_nprice);
        food_name=findViewById(R.id.food_detial_name);
        food_detail_image =findViewById(R.id.img_food);

        collapsingToolbarLayout =findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedToolbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedToolbar);

    }

    //add food detail to local Database
    private void add_cart_to_DB()
    {
        new Database(getApplicationContext()).addToCart(new Order(food_id,
                CurrantFoods.getName(),
                elegantNumberButton.getNumber(),
                CurrantFoods.getPrice(),
                CurrantFoods.getDiscount()
        ));

        Toast.makeText(FoodDetail.this, "Added to cart", Toast.LENGTH_SHORT).show();
    }

    private void check_internet_connection()
    {
        if(Common.isConnectedToInternet(getBaseContext())) {
            getDetailFood(food_id);
            getRatedFood(food_id);
        }
        else {
            Toast.makeText(FoodDetail.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void getRatedFood(String food_id)
    {
        Query foodRating = rating_Table.orderByChild("food_ID").equalTo(food_id);

        foodRating.addValueEventListener(new ValueEventListener()
        {
            int count =0, sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {

                    Rating item = postSnapShot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRate_Value());
                    count++;
                }

                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(FoodDetail.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void Show_Rating_Dialog() {


             new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.text_color_bright)
                .setDescriptionTextColor(R.color.text_color_dark)
                .setHint("Please write your comment here..")
                .setCommentBackgroundColor(R.color.text_color_yeallo)
                .setCommentTextColor(R.color.text_color_bright)
                     .setHintTextColor(R.color.text_color_off_white)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(final String food_id)
    {
        food_detail.child(food_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CurrantFoods =dataSnapshot.getValue(Foods.class);

                Picasso.with(getApplicationContext()).load(CurrantFoods.getImage()).into(food_detail_image);

                collapsingToolbarLayout.setTitle(CurrantFoods.getName());

                fod_price.setText(CurrantFoods.getPrice());

                food_name.setText(CurrantFoods.getName());

                food_description.setText(CurrantFoods.getDescription());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, String Comments) {

        final Rating rating =new Rating(Common.currntUser.getPhone(),
                food_id,
                String.valueOf(value),
                Comments);
        rating_Table.child(Common.currntUser.getPhone()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currntUser.getPhone()).exists())
                {
                    //remove old rate
                    rating_Table.child(Common.currntUser.getPhone()).removeValue();
                    //adding new rate
                    rating_Table.child(Common.currntUser.getPhone()).setValue(rating);

                }
                else
                {
                    rating_Table.child(Common.currntUser.getPhone()).setValue(rating);

                }

                Toast.makeText(FoodDetail.this, "Thank you for rating us", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
