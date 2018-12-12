package com.example.mosaab.orderfoods.Services;

import com.example.mosaab.orderfoods.Common.Common;
import com.example.mosaab.orderfoods.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class Firebase_Id_Service extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token_refreshed = FirebaseInstanceId.getInstance().getToken();
        if(Common.currntUser != null)
        {
            Update_token_ToFirebase(token_refreshed);
        }

    }


    private void Update_token_ToFirebase(String token_refreshed) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token =new Token(token_refreshed,false);//false becuse this token send from client app
        tokens.child(Common.currntUser.getPhone()).setValue(token);
    }
}
