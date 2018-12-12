package com.example.mosaab.orderfoods.ViewHolder;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Database.Database;
import com.example.mosaab.orderfoods.Model.Notification_;
import com.example.mosaab.orderfoods.Model.Order;
import com.example.mosaab.orderfoods.Model.Request;
import com.example.mosaab.orderfoods.Model.Response;
import com.example.mosaab.orderfoods.Model.Sender;
import com.example.mosaab.orderfoods.Model.Token;
import com.example.mosaab.orderfoods.R;
import com.example.mosaab.orderfoods.Remote.API_Service;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class Cart extends AppCompatActivity {

    private TextView txtTotalPrice;
    private Button btnPlace;
    private RecyclerView recyclerView_Cart;
    private RecyclerView.LayoutManager layoutManager;
    private EditText comment_ET,address_ET;
    private View order_address_comment;
    private LayoutInflater inflater;

    private FirebaseDatabase database;
    private DatabaseReference requests;


    private API_Service api_service;


   private List<Order> cart;
   private CartAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        InitUI();
        LoadListFood();

        btnPlace.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(cart.size() >0) {
                    ShowAlertDialog();
                }
                else {
                    Toast.makeText(Cart.this, "your cart is empty !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        

        
    }

    private void InitUI()
    {
        recyclerView_Cart= findViewById(R.id.listCart);
        recyclerView_Cart.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView_Cart.setLayoutManager(layoutManager);


        txtTotalPrice=findViewById(R.id.total);
        btnPlace=findViewById(R.id.btn_PlaceOrder);

        inflater = this.getLayoutInflater();
        order_address_comment = inflater.inflate(R.layout.adress_order_comment,null );
        address_ET = order_address_comment.findViewById(R.id.address_edt);
        comment_ET = order_address_comment.findViewById(R.id.comment_edt);



        database = FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        cart = new ArrayList<>();

        api_service = Common.getFCMService();
    }

    private void ShowAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this,R.style.MyDialogTheme);

        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter your address");




        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                add_order_request();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void add_order_request()
    {
        Request request =new Request(
                Common.currntUser.getPhone(),
                address_ET.getText().toString(),
                txtTotalPrice.getText().toString(),
                Common.currntUser.getName(),
                "0",//status
                comment_ET.getText().toString(),
                cart);

        String order_number =String.valueOf(System.currentTimeMillis());
        requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);



        new Database(getApplicationContext()).CleanCart();

        Send_Notification_Order(order_number);

      //  Toast.makeText(Cart.this, "Thank you, Order placed", Toast.LENGTH_SHORT).show();
       // finish();
    }

    private void Send_Notification_Order(final String order_number) {

        DatabaseReference tokens = database.getReference("Tokens");

        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot post_snapshot : dataSnapshot.getChildren())
                {
                    Token server_token = post_snapshot.getValue(Token.class);

                    Notification_ notification = new Notification_("Order Food","You have new order" + order_number);
                    Sender content = new Sender(server_token.getToken(),notification);

                    api_service.sendNotification(content)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you, Order placed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {

                                            Toast.makeText(Cart.this, "Failed !!!", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                    Log.d("Response", "onFailure: "  + t.getMessage());
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void LoadListFood() {
        cart=new Database(this).getCarts();
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView_Cart.setAdapter(adapter);

        int total =0;

        for(Order order:cart)
        {
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

            Locale locale = new Locale("en","US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txtTotalPrice.setText(fmt.format(total));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.DELETE))
        {
            deleteCart(item.getOrder());
        }

        return true;

    }

    //delete cart from local DB
    private void deleteCart(int order) {

        cart.remove(order);

        new Database(this).CleanCart();

        for (Order item:cart)
        {
            new Database(this).addToCart(item);
        }
        LoadListFood();
    }
}
