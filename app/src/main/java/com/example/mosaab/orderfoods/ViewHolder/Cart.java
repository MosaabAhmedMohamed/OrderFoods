package com.example.mosaab.orderfoods.ViewHolder;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Database.Database;
import com.example.mosaab.orderfoods.Helper.Recycler_item_touch_helper;
import com.example.mosaab.orderfoods.Interface.Recycler_item_touch_helper_listner;
import com.example.mosaab.orderfoods.Model.Notification_;
import com.example.mosaab.orderfoods.Model.Order;
import com.example.mosaab.orderfoods.Model.PlaceAutocompleteAdapter;
import com.example.mosaab.orderfoods.Model.Request;
import com.example.mosaab.orderfoods.Model.Response;
import com.example.mosaab.orderfoods.Model.Sender;
import com.example.mosaab.orderfoods.Model.Token;
import com.example.mosaab.orderfoods.R;
import com.example.mosaab.orderfoods.Remote.API_Service;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, Recycler_item_touch_helper_listner {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 10;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final int Error_Dialog_request = 9001;

    private Boolean mLocationPermissionsGranted = false;



    private static final String TAG ="CART" ;

    protected TextView txtTotalPrice;
    private Button btnPlace;
    private RecyclerView recyclerView_Cart;
    private RecyclerView.LayoutManager layoutManager;
    private EditText comment_ET;
    private AutoCompleteTextView address_TV;
    private View order_address_comment;
    private LayoutInflater inflater;
    private RelativeLayout base_layout;
    private RadioButton my_location_btn,my_home_btn,radio_COD_btn,radio_Dept_btn;

    private FirebaseDatabase database;
    private DatabaseReference requests;

    //places for getting suggestion
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds latLngBounds;

    //location
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location device_location;
    //for getting address from latlng
    Geocoder geocoder;
    List<Address> addresses ;

     //vars
    private static final int UPDATE_INTERVAL = 5000;
    private static final int FATEST_INTERVAL = 3000;
    private static final int DISPLACMENT = 10;
    private String payment_method = null;
    private String payment_state = null;

    private API_Service api_service;


    private List<Order> cart;
    private CartAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Apply_New_Font();
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

    private void InitUI()
    {
        recyclerView_Cart= findViewById(R.id.listCart);
        recyclerView_Cart.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView_Cart.setLayoutManager(layoutManager);

        base_layout = findViewById(R.id.base_layout);
        txtTotalPrice=findViewById(R.id.total);
        btnPlace=findViewById(R.id.btn_PlaceOrder);

        google_API_Client();
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,latLngBounds,null);
        //for getting address from latlng
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = null;

        //alert dialog
        inflater = this.getLayoutInflater();
        order_address_comment = inflater.inflate(R.layout.adress_order_comment,null );
        comment_ET = order_address_comment.findViewById(R.id.comment_edt);
        address_TV = order_address_comment.findViewById(R.id.address_edt);
        my_location_btn = order_address_comment.findViewById(R.id.my_location_btn);
        my_home_btn = order_address_comment.findViewById(R.id.my_home_btn);
        radio_COD_btn = order_address_comment.findViewById(R.id.radio_COD_btn);
        radio_Dept_btn = order_address_comment.findViewById(R.id.radio_DEBT_btn);

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new Recycler_item_touch_helper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView_Cart);

        database = FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");
        cart = new ArrayList<>();

        api_service = Common.getFCMService();
    }

    private void google_API_Client() {

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        latLngBounds = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));

    }

    private void ShowAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this,R.style.MyDialogTheme);

        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter your address");


        if(order_address_comment.getParent()!=null)
            ((ViewGroup)order_address_comment.getParent()).removeView(order_address_comment); // <- fix
        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        address_TV.setAdapter(placeAutocompleteAdapter);

        if (isServiceSDk())
        {
            getLocationPermission();
            if (mLocationPermissionsGranted)
            {
                set_myCurrnt_Address();
                set_myHome_Address();
            }
        }



        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               if (!TextUtils.isEmpty(address_TV.getText().toString()) ) {
                  // add_order_request(payment_method, payment_state);
                  if(check_payment())
                  {
                      add_order_request();
                  }
               }
               else {
                   Toast.makeText(Cart.this, "pleas enter your address", Toast.LENGTH_SHORT).show();
               }
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

    private boolean check_payment() {

        //check payment
        if(!radio_COD_btn.isChecked() && !radio_Dept_btn.isChecked())
        {
            Toast.makeText(this, "Please enter payment method", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (radio_COD_btn.isChecked())
        {
            payment_method = "Cash on delivery";
            payment_state = "unpaid";
            return true;
        }
        else if (radio_Dept_btn.isChecked())
        {
             payment_method = "Dept";
             payment_state = "unpaid";
            return true;
        }

        return false;
    }


    private void add_order_request()
    {
        Request request =new Request(
                Common.currntUser.getPhone(),
                address_TV.getText().toString(),
                txtTotalPrice.getText().toString(),
                Common.currntUser.getName(),
                "0",//status
                comment_ET.getText().toString(),
                payment_method,
                payment_state,
                String.format("%s,%s",device_location.getLatitude(),device_location.getLongitude()),
                cart);

        String order_number =String.valueOf(System.currentTimeMillis());
        requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);



        new Database(getApplicationContext()).CleanCart(Common.currntUser.getPhone());

        Send_Notification_Order(order_number);

      //  Toast.makeText(Cart.this, "Thank you, Order placed", Toast.LENGTH_SHORT).show();
       // finish();
    }

    private void set_myCurrnt_Address()
    {
        my_location_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    try {
                        if (device_location != null)
                        {
                            addresses = geocoder.getFromLocation(device_location.getLatitude(), device_location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String address = addresses.get(0).getAddressLine(0);

                                address_TV.setText(address);
                        }
                        else
                            {
                            Toast.makeText(Cart.this, "enable GPS", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }




    private void set_myHome_Address() {

        my_home_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {

                    address_TV.setText(Common.currntUser.getHomeAddress());
                }
            }
        });
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
        cart=new Database(this).getCarts(Common.currntUser.getPhone());
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView_Cart.setAdapter(adapter);

        int total =0;

        for(Order order:cart)
        {
            total+= (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

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

        new Database(this).CleanCart(Common.currntUser.getPhone());

        for (Order item:cart)
        {
            new Database(this).addToCart(item);
        }
        LoadListFood();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: "+connectionResult.getErrorMessage());
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
              //  initMap();
                getDeviceLocation();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                  //  initMap();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if (mLocationPermissionsGranted) {
                  //  getDeviceLocation();
                } else {
                    getLocationPermission();
                }
            }
        }

    }

    //to know if this device is allowed to access map or not
    public boolean isServiceSDk()
    {
        Log.d(TAG, "isServiceOk:checking google services version ");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Cart.this);

        if (available == ConnectionResult.SUCCESS)
        {
            //everything is fine and the user can make map requests_table

            Log.d(TAG, "isServiceOk: Google play serviecs is working ");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            //an error occurred but we can fix it

            Log.d(TAG, "isServiceOk: an error occurred but we can fix it");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Cart.this,available,Error_Dialog_request);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, " this device is not supported ", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {

                final Task divece_location = mFusedLocationProviderClient.getLastLocation();

                divece_location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        device_location = (Location) task.getResult();

                        if (task.isSuccessful() && device_location != null) {
                            Log.d(TAG, "onComplete: found location!");


                             //device_location.getLatitude() device_location.getLongitude();
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(Cart.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof CartAdapter.CartViewHolder)
        {
            String name = ((CartAdapter)recyclerView_Cart.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem =  ((CartAdapter)recyclerView_Cart.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            new Database(getBaseContext()).removeFromCart(deleteItem.getProudctId(),Common.currntUser.getPhone());
            adapter.remove_item(deleteIndex);

            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currntUser.getPhone());

            for (Order item : orders)
            {
                total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

             Locale locale = new Locale("en","US");
             NumberFormat format = NumberFormat.getCurrencyInstance(locale);

             txtTotalPrice.setText(format.format(total));

                Snackbar snackbar = Snackbar.make(base_layout,name+" removed from cart",Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        new Database(getBaseContext()).addToCart(deleteItem);
                        adapter.restore_item(deleteItem,deleteIndex);

                        int total = 0;
                        List<Order> orders = new Database(getBaseContext()).getCarts(Common.currntUser.getPhone());

                        for (Order item : orders)
                        {
                            total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

                            Locale locale = new Locale("en","US");
                            NumberFormat format = NumberFormat.getCurrencyInstance(locale);

                            txtTotalPrice.setText(format.format(total));
                    }
                }


            });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
        }
      }

    }
}
