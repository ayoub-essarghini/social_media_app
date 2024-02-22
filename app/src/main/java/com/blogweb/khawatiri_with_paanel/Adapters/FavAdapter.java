package com.blogweb.khawatiri_with_paanel.Adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.blogweb.khawatiri_with_paanel.Databases.favDB;
import com.blogweb.khawatiri_with_paanel.R;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.MyViewHolder> {


    public Context context;
    private boolean act_fav;
    private ArrayList<String> quotes;
    favDB myDB;
    View cardv;


    public FavAdapter(Context context, ArrayList<String> quotes, boolean act_fav) {
        this.act_fav = act_fav;
        this.context = context;
        this.quotes = quotes;
        myDB = new favDB(context);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fav_item, parent, false);

        return new MyViewHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.quote_text.setText(quotes.get(position));
        if (act_fav) {
            holder.btn_fav.setImageResource(R.drawable.ic_fav);

        }


        if (myDB.checkExistData(quotes.get(position)))
            holder.btn_fav.setImageResource(R.drawable.ic_fav);
        else
            holder.btn_fav.setImageResource(R.drawable.ic_border_fav);


        holder.wtsp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, holder.quote_text.getText().toString());
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.whatsapp");
                        context.startActivity(sendIntent);




            }
        });

        holder.msngr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, holder.quote_text.getText().toString());
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.facebook.orca");
                        context.startActivity(sendIntent);


            }
        });



        holder.btn_fav.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {



                if (act_fav) {
                    if (!myDB.checkExistData(quotes.get(position))) {
                        holder.toggleButton(true);
                        myDB.favQuotes(quotes.get(position));


                    } else {
                        holder.toggleButton(false);
                        myDB.deleteFav(quotes.get(position));
                        quotes.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();

                    }
                } else if (!myDB.checkExistData(quotes.get(position))) {
                    holder.toggleButton(true);
                    myDB.favQuotes(quotes.get(position));
                } else {
                    holder.toggleButton(false);
                    myDB.deleteFav(quotes.get(position));

                }

            }


        });
        holder.btn_copy.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {


                        ClipboardManager clipboardManager = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", holder.quote_text.getText().toString());
                        clipboardManager.setPrimaryClip(clipData);
                        Toasty.info(context, "تم النسخ بنجاح", Toast.LENGTH_SHORT,false).show();


            }


        });
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cardv = inflater.inflate(R.layout.card_text, null);

        String sharelink ="http://play.google.com/store/apps/details?id=" +context.getPackageName();

        holder.btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_SEND);
                    intent2.setType("text/plain");
                    intent2.putExtra(Intent.EXTRA_TEXT, holder.quote_text.getText().toString() + "\n تطبيق خواطري \n"+sharelink);
                    context.startActivity((Intent.createChooser(intent2, "أرسل إلى")));


            }
        });


    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView quote_text;
        ImageView btn_copy, btn_fav, btn_share,wtsp_btn,msngr_btn;
        RelativeLayout mainanim;
        Animation scaleup, scaledown;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quote_text = itemView.findViewById(R.id.quote);
            btn_copy = itemView.findViewById(R.id.copy_btn);
            btn_fav = itemView.findViewById(R.id.fav_btn);
            btn_share = itemView.findViewById(R.id.share_btn);
            msngr_btn = itemView.findViewById(R.id.msngr_btn);
            wtsp_btn = itemView.findViewById(R.id.wtsp_btn);
            mainanim = itemView.findViewById(R.id.mainanim);
            scaleup = AnimationUtils.loadAnimation(context,
                    R.anim.scale_up);
            scaledown = AnimationUtils.loadAnimation(context,
                    R.anim.scale_down);


            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim_main);
            mainanim.setAnimation(translate_anim);


        }

        public void toggleButton(boolean isFav) {
            if (isFav) {
                animateImage(btn_fav);
                btn_fav.setImageResource(R.drawable.ic_fav);

            } else {
                animateImage(btn_fav);
                btn_fav.setImageResource(R.drawable.ic_border_fav);

            }
        }

        public void animateImage(ImageView img){

            ScaleAnimation scaleAnimation = new ScaleAnimation(0,1f,0,1f,
                    Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            scaleAnimation.setFillAfter(true);
            scaleAnimation.setDuration(200);
            img.startAnimation(scaleAnimation);


        }





    }


}
