package com.blogweb.khawatiri_with_paanel.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.blogweb.khawatiri_with_paanel.Fragments.SigninFragement;
import com.blogweb.khawatiri_with_paanel.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameAthContainer,new SigninFragement()).commit();

    }
}