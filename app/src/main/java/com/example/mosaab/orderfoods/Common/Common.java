package com.example.mosaab.orderfoods.Common;

import android.content.Context;
import android.icu.util.Calendar;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.mosaab.orderfoods.Model.User;
import com.example.mosaab.orderfoods.Remote.API_Service;
import com.example.mosaab.orderfoods.Remote.Retrofit_client;

import java.util.Locale;

import retrofit2.Retrofit;

public class Common {
    public static User currntUser;
    public static String PHONE_TEXT ="userPhone";
    public static final String FOOD_ID = "FoodId";
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static API_Service getFCMService()
    {
        return Retrofit_client.getClient(BASE_URL).create(API_Service.class);
    }

    public static final String DELETE = "Delete";

    public static final String USER_KEY ="User";
    public static final String PWD_KEY ="Password";

    public static String convertCodeToStatus(String status) {
        if(status.equals("0")){
            return "placed";}
        else if(status.equals("1")){
            return "On My Way";}
        else {
            return "Shipped";}

    }

    public static boolean  isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

         if(connectivityManager != null)
         {
             NetworkInfo [] info = connectivityManager.getAllNetworkInfo();
             if(info != null)
             {
                 for (int i= 0;i<info.length;i++)
                 {
                     if(info[i].getState() == NetworkInfo.State.CONNECTED)
                         return true;
                 }

             }
         }
         return false;
    }

    public static String getDate(Long time)
    {
        java.util.Calendar calendar = java.util.Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yy HH:mm",calendar).toString());
        return date.toString();
    }

}
