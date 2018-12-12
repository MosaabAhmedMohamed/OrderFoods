package com.example.mosaab.orderfoods.ViewHolder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Interface.ItemClickListner;
import com.example.mosaab.orderfoods.Model.Category;
import com.example.mosaab.orderfoods.Model.Token;
import com.example.mosaab.orderfoods.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textFullName;
    private RecyclerView recycler_menu;
    private RecyclerView.LayoutManager layoutManager;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View Nav_headrView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseDatabase database;
    private DatabaseReference Category_table;


    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitUI();



        Update_Token(FirebaseInstanceId.getInstance().getToken());

        //Register Services



    }


    private void InitUI() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        Paper.init(this);

        //init Firebase
        database=FirebaseDatabase.getInstance();
        Category_table =database.getReference("Category");

        fab = (FloatingActionButton) findViewById(R.id.fab);

        swipeRefreshLayout = findViewById(R.id.content_home_layout);
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

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run()
            {
                check_intrnet();
            }
        });

        //Init Recycler View
        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Nav_headrView = navigationView.getHeaderView(0);
        textFullName  = Nav_headrView.findViewById(R.id.txtFullName);
        if (Common.currntUser!=null)
        {
            textFullName.setText(Common.currntUser.getName());
        }
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent CartIntent=new Intent(Home.this,Cart.class);
                startActivity(CartIntent);
            }
        });


    }

    private void check_intrnet()
    {
        if(Common.isConnectedToInternet(Home.this))
        {
            LoadMenu();
        }
        else {
            Toast.makeText(Home.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void LoadMenu() {
        adapter =new FirebaseRecyclerAdapter<Category,MenuViewHolder>(
                Category.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                Category_table)
        {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, final int position)
            {
              viewHolder.txtMenuName.setText(model.getName());

              Picasso.with(getApplicationContext()).load(model.getLink())
                      .into(viewHolder.imageView);

              // clicked type of food in the menu
              final Category clickedItem = model;
              viewHolder.setItemClickListner(new ItemClickListner() {
                  @Override
                  public void onclick(View view, int postion, boolean isLongClick) {

                      Intent food_list_intent = new Intent(Home.this,FoodList.class);
                      food_list_intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                      startActivity(food_list_intent);
                                        }
              });
            }
        };
        recycler_menu.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);
    }

    private void Update_Token(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data =new Token(token,false);//false becuse this token send from client app
        if(Common.currntUser!=null)
           tokens.child(Common.currntUser.getPhone()).setValue(data);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        if (item.getItemId() ==R.id.refresh)
            LoadMenu();
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_carts) {
            Intent CartIntent =new Intent(Home.this,Cart.class);
            startActivity(CartIntent);

        }
        else if (id == R.id.nav_orders) {
            Intent OrdersIntent=new Intent(Home.this,OrderStatus.class);
            startActivity(OrdersIntent);
        }
        else if (id == R.id.nav_change_password) {
           Show_Change_Password_Dialog();
        }
        else if(id == R.id.nav_contact_us)
        {
           Contact_Us();
        }
        else if (id == R.id.nav_logout) {
           Paper.book().destroy();

            Intent SignIn = new Intent(Home.this,com.example.mosaab.orderfoods.ViewHolder.SignIn.class);
            SignIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(SignIn);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Contact_Us()
    {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"mosabahmeddev@gmail.com"});
        try {
            startActivity(Intent.createChooser(intent, "E-mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            //do something else
        }
    }

    private void Show_Change_Password_Dialog() {

        AlertDialog.Builder alertDialog =new AlertDialog.Builder(this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please fill all information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_password = inflater.inflate(R.layout.change_password_layout,null);

        final EditText Password_EDT = layout_password.findViewById(R.id.password_ET);
        final EditText new_Password_EDT = layout_password.findViewById(R.id.new_password_ET);
        final EditText repeat_Password_EDT = layout_password.findViewById(R.id.repeat_new_password_ET);

        alertDialog.setView(layout_password);

        alertDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               if(Password_EDT.getText().toString().equals(Common.currntUser.getPassword()))
               {

                   if(new_Password_EDT.getText().toString().equals(repeat_Password_EDT.getText().toString()))
                   {
                       Map<String,Object> passwordUpdate = new HashMap<>();
                       passwordUpdate.put("Password",new_Password_EDT.getText().toString());

                       //make update
                       DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
                       user.child(Common.currntUser.getPhone())
                               .updateChildren(passwordUpdate)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {

                                       Toast.makeText(Home.this, "password was updated successfully", Toast.LENGTH_SHORT).show();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
                   else {
                       Toast.makeText(Home.this, "new password doesn't match !", Toast.LENGTH_SHORT).show();
                   }
               }
               else
               {
                   Toast.makeText(Home.this, "wrong old password !", Toast.LENGTH_SHORT).show();
               }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();

    }
}
