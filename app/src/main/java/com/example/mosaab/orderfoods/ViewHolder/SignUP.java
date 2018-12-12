package com.example.mosaab.orderfoods.ViewHolder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Model.User;
import com.example.mosaab.orderfoods.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUP extends AppCompatActivity {

    private EditText edtName,edtPhone,edtPassword,edtSecureCode;
    private Button btnSignUp,have_account_Bu;

    private FirebaseDatabase database;
    private DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        InitUI();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               SigUp_Validation();
            }
        });

        have_account_Bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent SignUp_Intent = new Intent(SignUP.this,SignIn.class);
                startActivity(SignUp_Intent);
                finish();
            }
        });

    }

    private void InitUI() {

        edtName=findViewById(R.id.edtName);
        edtPhone=findViewById(R.id.edtPhone);
        edtPassword=findViewById(R.id.edtPassword);
        edtSecureCode = findViewById(R.id.edt_Secure_Code);
        btnSignUp=findViewById(R.id.btn_SingUp);
        have_account_Bu = findViewById(R.id.have_account_BU);

        database=FirebaseDatabase.getInstance();
        table_user=database.getReference("User");
    }

    private void SigUp_Validation() {

        if(Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog mDialog = new ProgressDialog(SignUP.this);
            mDialog.setMessage("Please Wait...");
            mDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // if user already exist
                    if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                        mDialog.dismiss();
                        Toast.makeText(SignUP.this, "User is exist", Toast.LENGTH_SHORT).show();
                    } else {
                        mDialog.dismiss();

                        User user = new User(edtName.getText().toString(),
                                edtPassword.getText().toString(),
                                edtSecureCode.getText().toString());
                        table_user.child(edtPhone.getText().toString()).setValue(user);

                        Toast.makeText(SignUP.this, "Sign up successfully", Toast.LENGTH_SHORT).show();

                        finish();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            });
        }
        else {
            Toast.makeText(SignUP.this, "Please check your internet connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }


}
