package com.example.mosaab.orderfoods.ViewHolder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Model.User;
import com.example.mosaab.orderfoods.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private Button btnSingUp,btnSingIn;
    private TextView txtSlogan;

    private  FirebaseDatabase firebaseDatabase;
    private  DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Apply_New_Font();
        setContentView(R.layout.activity_main);


       InitUI();
       check_remember_me();

        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent SignIn_intent =new Intent(MainActivity.this,SignIn.class);
               startActivity(SignIn_intent);
            }
        });

        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignUp_intent =new Intent(MainActivity.this,SignUP.class);
                startActivity(SignUp_intent);
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

    private void InitUI() {

        btnSingIn= findViewById(R.id.btn_SingIn);
        btnSingUp= findViewById(R.id.btn_SingUp);

        txtSlogan= findViewById(R.id.txtSlogan);

        Paper.init(this);

        firebaseDatabase =FirebaseDatabase.getInstance();
        table_user = firebaseDatabase.getReference("User");
    }

    //Check Remember me if the user saved his sign in
    private void check_remember_me()
    {
        String user =Paper.book().read(Common.USER_KEY);
        String password =Paper.book().read(Common.PWD_KEY);

        if(user != null && password !=null)
        {
            if(!user.isEmpty()&&!password.isEmpty())
            {
                remember_me_login(user,password);
            }
        }
    }

    private void remember_me_login(final String phone, final String password) {

        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //check if user is exist in the Database
                    if (dataSnapshot.child(phone).exists()) {

                        mDialog.dismiss();

                        //Get user information
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);

                        if (user.getPassword().equals(password)) {


                            Intent Home_intent = new Intent(MainActivity.this, Home.class);
                            Common.currntUser = user;
                            startActivity(Home_intent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Wrong password !", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User not exist", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }



        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
