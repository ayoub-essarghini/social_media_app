package com.blogweb.khawatiri_with_paanel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogweb.khawatiri_with_paanel.Fragments.HomeFragment;
import com.blogweb.khawatiri_with_paanel.Fragments.LikeFragment;
import com.blogweb.khawatiri_with_paanel.Fragments.NotificationFragment;
import com.blogweb.khawatiri_with_paanel.Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {


    private int selectedTab = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout home_layout = findViewById(R.id.home_layout);
        final LinearLayout like_layout = findViewById(R.id.like_layout);
        final LinearLayout notif_layout = findViewById(R.id.notifications_layout);
        final LinearLayout profile_layout = findViewById(R.id.profile_layout);

        final ImageView imgHome = findViewById(R.id.homeImg);
        final ImageView imgLike = findViewById(R.id.likeImg);
        final ImageView imgNotif = findViewById(R.id.notifImg);
        final ImageView imgProfile = findViewById(R.id.profileImg);

        final TextView homeTxt = findViewById(R.id.homeTxt);
        final TextView likeTxt = findViewById(R.id.likeTxt);
        final TextView notifTxt = findViewById(R.id.notifTxt);
        final TextView profileTxt = findViewById(R.id.profileTxt);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, HomeFragment.class,null).commit();

        home_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedTab != 1){
                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, HomeFragment.class,null).commit();
                    likeTxt.setVisibility(View.GONE);
                    notifTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);

                    imgLike.setImageResource(R.drawable.like_icon);
                    imgNotif.setImageResource(R.drawable.notif__icon);
                    imgProfile.setImageResource(R.drawable.profile_icon);

                    like_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notif_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profile_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    homeTxt.setVisibility(View.VISIBLE);
                    imgHome.setImageResource(R.drawable.home_selected_icon);
                    home_layout.setBackgroundResource(R.drawable.round_back_home_100);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);

                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    home_layout.startAnimation(scaleAnimation);
                    selectedTab =1;


                }

            }
        });

        like_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedTab != 2){
                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, LikeFragment.class,null).commit();
                    homeTxt.setVisibility(View.GONE);
                    notifTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);

                    imgHome.setImageResource(R.drawable.home_icon);
                    imgNotif.setImageResource(R.drawable.notif__icon);
                    imgProfile.setImageResource(R.drawable.profile_icon);

                    home_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notif_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profile_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    likeTxt.setVisibility(View.VISIBLE);
                    imgLike.setImageResource(R.drawable.like_selected_icon);
                    like_layout.setBackgroundResource(R.drawable.round_back_like_100);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0.0f);

                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    like_layout.startAnimation(scaleAnimation);
                    selectedTab =2;


                }
            }
        });
        notif_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 3){
                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, NotificationFragment.class,null).commit();
                    homeTxt.setVisibility(View.GONE);
                    likeTxt.setVisibility(View.GONE);
                    profileTxt.setVisibility(View.GONE);

                    imgHome.setImageResource(R.drawable.home_icon);
                    imgLike.setImageResource(R.drawable.like_icon);
                    imgProfile.setImageResource(R.drawable.profile_icon);

                    home_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    like_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profile_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    notifTxt.setVisibility(View.VISIBLE);
                    imgNotif.setImageResource(R.drawable.notif_selected_icon);
                    notif_layout.setBackgroundResource(R.drawable.round_back_notif_100);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);

                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    notif_layout.startAnimation(scaleAnimation);
                    selectedTab =3;


                }
            }
        });
        profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab != 4){
                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, ProfileFragment.class,null).commit();
                    homeTxt.setVisibility(View.GONE);
                    likeTxt.setVisibility(View.GONE);
                    notifTxt.setVisibility(View.GONE);

                    imgHome.setImageResource(R.drawable.home_icon);
                    imgLike.setImageResource(R.drawable.like_icon);
                    imgNotif.setImageResource(R.drawable.notif__icon);

                    home_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    like_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    notif_layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

                    profileTxt.setVisibility(View.VISIBLE);
                    imgProfile.setImageResource(R.drawable.profile_selected_icon);
                    profile_layout.setBackgroundResource(R.drawable.round_back_profile_100);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);

                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);
                    profile_layout.startAnimation(scaleAnimation);
                    selectedTab =4;


                }
            }
        });
    }
}