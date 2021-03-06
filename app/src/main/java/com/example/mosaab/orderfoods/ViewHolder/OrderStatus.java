package com.example.mosaab.orderfoods.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.Model.Request;
import com.example.mosaab.orderfoods.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Apply_New_Font();
        setContentView(R.layout.activity_order_status);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.ListOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if (getIntent() != null) {
            LoadOrder(Common.currntUser.getPhone());
        } else {
            LoadOrder(getIntent().getStringExtra("userPhone"));
        }
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

    private void LoadOrder(String phone) {

        Query OrderByUser = requests.orderByChild("phone").equalTo(phone);

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(OrderByUser, Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View item_View = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.order_status_item, viewGroup, false);
                return new OrderViewHolder(item_View);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull Request model) {
                viewHolder.txtOrderId.setText("ID : "+adapter.getRef(position).getKey());
                viewHolder.txtOrderStauts.setText("Status : "+Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText("Address : "+model.getAdress());
                viewHolder.txtOrderPhone.setText("Phone : "+model.getPhone());

                viewHolder.delete_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                      if (adapter.getItem(position).getStatus().equals("0"))
                      {
                          remove_order(adapter.getRef(position).getKey());

                      }
                      else {
                          Toast.makeText(OrderStatus.this, "You cannot delete this order ", Toast.LENGTH_SHORT).show();
                      }
                    }
                });
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onclick(View view, int postion, boolean isLongClick) {

                    }
                });



            }


        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void remove_order(String key) {

        requests.child(key)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderStatus.this, "order deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(OrderStatus.this, "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
        {adapter.startListening();}
    }
}