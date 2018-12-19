package com.example.mosaab.orderfoods.ViewHolder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {

    private EditText edtPhone,edtpassword;
    private EditText edtSecureCode,forgetPass_edtPhone;
    private Button   btnSignIn,need_new_account_BU;
    private CheckBox checkBox_remember_me ;
    private TextView textForgetPassword;

    private  FirebaseDatabase firebaseDatabase;
    private  DatabaseReference table_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Apply_New_Font();
        setContentView(R.layout.activity_sign_in);

         InitUI();



        textForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowForgetPasswordDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               SignIn_Validation();
            }
        });

        need_new_account_BU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent SignUp_Intent = new Intent(SignIn.this,SignUP.class);
                startActivity(SignUp_Intent);
                finish();
            }
        });


    }



    private void InitUI() {

        edtpassword=findViewById(R.id.edtPassword);
        edtPhone=findViewById(R.id.edtPhone);
        btnSignIn=findViewById(R.id.btn_SingIn);
        need_new_account_BU = findViewById(R.id.need_new_account);
        checkBox_remember_me = findViewById(R.id.checkbox_remember);
        textForgetPassword = findViewById(R.id.forget_password_TV);

        Paper.init(this);

        firebaseDatabase =FirebaseDatabase.getInstance();
        table_user = firebaseDatabase.getReference("User");

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

    private void SignIn_Validation() {
        if (Common.isConnectedToInternet(getBaseContext())) {

            if (checkBox_remember_me.isChecked())
            {
                Paper.book().write(Common.USER_KEY,edtPhone.getText().toString());
                Paper.book().write(Common.PWD_KEY,edtpassword.getText().toString());
            }
            final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();

            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //check if user not exist in the Database
                    if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                        mDialog.dismiss();
                        //Get user information
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        user.setPhone(edtPhone.getText().toString());

                        if (user.getPassword().equals(edtpassword.getText().toString())) {

                            Intent Home_intent = new Intent(SignIn.this, Home.class);
                            Common.currntUser = user;
                            startActivity(Home_intent);
                            finish();

                            table_user.removeEventListener(this);
                        } else {
                            Toast.makeText(SignIn.this, "Wrong password !", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(SignIn.this, "User not exist", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            Toast.makeText(SignIn.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    private void ShowForgetPasswordDialog() {

        AlertDialog.Builder builder =new AlertDialog.Builder(this,R.style.MyDialogTheme);
        builder.setTitle("Forget password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forget_password_view =inflater.inflate(R.layout.forget_password_layout,null);

        builder.setView(forget_password_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

              forgetPass_edtPhone = forget_password_view.findViewById(R.id.edtPhone);
              edtSecureCode = forget_password_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //check if user available

                Forget_password();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void Forget_password() {
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.child(edtPhone.getText().toString())
                        .getValue(User.class);

                if(user.getSecureCode().equals(edtSecureCode.getText().toString()))
                {
                    Toast.makeText(SignIn.this, "Your password :" +user.getPassword(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SignIn.this, "Wrong secure code !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
