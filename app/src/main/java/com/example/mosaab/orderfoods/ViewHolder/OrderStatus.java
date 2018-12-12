package com.example.mosaab.orderfoods.ViewHolder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewStubCompat;
import android.view.View;

import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.Model.Request;
import com.example.mosaab.orderfoods.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    
    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    
    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        
        recyclerView=findViewById(R.id.ListOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if(getIntent() !=null) {
            LoadOrder(Common.currntUser.getPhone());
        }
        else {
            LoadOrder(getIntent().getStringExtra("userPhone"));
        }
    }

    private void LoadOrder(String phone) {
        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_status_item,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {

                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStauts.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAdress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onclick(View view, int postion, boolean isLongClick) {

                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);

    }

}
