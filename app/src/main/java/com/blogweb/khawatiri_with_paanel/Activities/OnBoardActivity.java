package com.blogweb.khawatiri_with_paanel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogweb.khawatiri_with_paanel.Adapters.ViewPagerAdapter;
import com.blogweb.khawatiri_with_paanel.R;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btn_left,btn_right;
    private ViewPagerAdapter adapter;
    private LinearLayout dotslayout;
    private TextView dots[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        init();
    }


    private void init(){
        viewPager = findViewById(R.id.view_pager);
        btn_left = findViewById(R.id.btn_left);
        btn_right = findViewById(R.id.btn_right);
        dotslayout = findViewById(R.id.dotslayout);
        adapter = new ViewPagerAdapter(this);
        addDots(0);
        viewPager.addOnPageChangeListener(listener);
        viewPager.setAdapter(adapter);
        btn_right.setOnClickListener(view -> {
            if (btn_right.getText().toString().equals("التالي")){
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }else {

                startActivity(new Intent(OnBoardActivity.this,AuthActivity.class));
                finish();
            }
        });

        btn_left.setOnClickListener(view -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+2);

        });
    }
    //methode to create dots from html code
    private void addDots(int position){
        dotslayout.removeAllViews();
        dots = new TextView[3];
        for (int i=0 ; i<dots.length;i++){
            dots[i] = new TextView(this);
            //this code html create dot
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getApplicationContext().getResources().getColor(R.color.grey));
            dotslayout.addView(dots[i]);
        }
        if (dots.length>0){
            dots[position].setTextColor(getApplicationContext().getResources().getColor(R.color.gradien1_color));
        }
    }
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDots(position);
            //check if we reached to page 3 and change next button text to finish
            //and hide skip button
            if (position==0){
                btn_left.setVisibility(View.VISIBLE);
                btn_left.setEnabled(true);
                btn_right.setText("التالي");
            }else if (position==1){
                btn_left.setVisibility(View.GONE);
                btn_left.setEnabled(false);
                btn_right.setText("التالي");
            }else{
                btn_left.setVisibility(View.GONE);
                btn_left.setEnabled(false);
                btn_right.setText("إنهاء");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}