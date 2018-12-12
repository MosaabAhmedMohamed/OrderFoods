
package com.example.mosaab.orderfoods.ViewHolder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mosaab.orderfoods.R;

public class splach_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach_screen);

        Thread Splash = new Thread(){
            @Override
            public void run(){

                try {
                    sleep(2000); // the time of holding the splash
                    Intent splash = new Intent(splach_screen.this,MainActivity.class);
                    startActivity(splash);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Splash.start();

    }
}
