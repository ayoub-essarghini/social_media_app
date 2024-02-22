package com.blogweb.khawatiri_with_paanel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.blogweb.khawatiri_with_paanel.MainActivity;
import com.blogweb.khawatiri_with_paanel.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
                boolean isLoggedIn = preferences.getBoolean("isLoggedIn",false);
                if (isLoggedIn){
                    startActivity(new Intent(Splash.this, MainActivity.class));
                    finish();
                }else {
                    isFirstTime();
                }

            }
        },3500);
    }


    private void isFirstTime() {
        SharedPreferences preferences = getApplication().getSharedPreferences("onBoard", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean("isFirstTime",true);
        if (isFirstTime){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime",false);
            editor.apply();
            //start auth activity
            startActivity(new Intent(Splash.this,OnBoardActivity.class));
            finish();
        }else{
            //start home activity
            startActivity(new Intent(Splash.this,AuthActivity.class));
            finish();
        }
    }
}