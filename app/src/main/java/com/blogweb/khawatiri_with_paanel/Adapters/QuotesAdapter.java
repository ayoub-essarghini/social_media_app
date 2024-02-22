package com.blogweb.khawatiri_with_paanel.Adapters;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.Activities.CommentActivity;
import com.blogweb.khawatiri_with_paanel.Activities.EditQuoteActivity;
import com.blogweb.khawatiri_with_paanel.Constans;
import com.blogweb.khawatiri_with_paanel.Databases.favDB;
import com.blogweb.khawatiri_with_paanel.MainActivity;
import com.blogweb.khawatiri_with_paanel.Models.Quote;
import com.blogweb.khawatiri_with_paanel.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteHolder> {


    private Context context;
    private ArrayList<Quote> list;
    private boolean act_fav;
    favDB myDB;
    private ArrayList<Quote> listAll;
    private SharedPreferences sharedPreferences;


    public QuotesAdapter(Context context, ArrayList<Quote> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        myDB = new favDB(context);
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public QuoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotes_item, parent, false);

        return new QuoteHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull QuoteHolder holder, @SuppressLint("RecyclerView") int position) {
        Quote post = list.get(position);

        if (act_fav) {
            holder.add_to_fav.setImageResource(R.drawable.saved_icon);

        }


        if (myDB.checkExistData(post.getQuote()))
            holder.add_to_fav.setImageResource(R.drawable.saved_icon);
        else
            holder.add_to_fav.setImageResource(R.drawable.save_add);


        if (post.getUser().getPhoto() == "null") {
            holder.img_profile.setImageResource(R.drawable.avatar);
        } else {
            Picasso.get().load(Constans.URL + "storage/profiles/" + post.getUser().getPhoto()).into(holder.img_profile);

        }

        holder.txtName.setText(post.getUser().getUserName());
        holder.txtComments.setText("" + post.getComments() + "تعليق");
        holder.txtLikes.setText(post.getLikes() + "إعجاب");
        holder.txtQuote.setText(post.getQuote());


        holder.add_to_fav.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {



                if (act_fav) {
                    if (!myDB.checkExistData(post.getQuote())) {
                        holder.toggleButton(true);
                        myDB.favQuotes(post.getQuote());


                    } else {
                        holder.toggleButton(false);
                        myDB.deleteFav(post.getQuote());
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();

                    }
                } else if (!myDB.checkExistData(post.getQuote())) {
                    holder.toggleButton(true);
                    myDB.favQuotes(post.getQuote());
                } else {
                    holder.toggleButton(false);
                    myDB.deleteFav(post.getQuote());

                }

            }


        });


        holder.btnLike.setImageResource(
                post.isSelfLike() ? R.drawable.ic_baseline_favorite_24 : R.drawable.ic_border_fav
        );
        holder.btnLike.setOnClickListener(v -> {
            holder.btnLike.setImageResource(
                    post.isSelfLike() ? R.drawable.ic_baseline_favorite_24 : R.drawable.ic_border_fav
            );
            StringRequest request = new StringRequest(Request.Method.POST, Constans.LIKE_QUOTE, response -> {
                Quote mpost = list.get(position);
                try {
                    JSONObject object = new JSONObject(response);

                    if (object.getBoolean("success")) {
                        mpost.setSelfLike(!post.isSelfLike());
                        mpost.setLikes(mpost.isSelfLike() ? post.getLikes() + 1 : post.getLikes() - 1);
                        list.set(position, mpost);
                        notifyItemChanged(position);
                        notifyDataSetChanged();
                    } else {
                        holder.btnLike.setImageResource(
                                post.isSelfLike() ? R.drawable.ic_baseline_favorite_24 : R.drawable.ic_border_fav
                        );
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }, error -> {
                error.printStackTrace();

            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String token = sharedPreferences.getString("token", "");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("Authorization", "Bearer " + token);
                    return map;
                }

                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", post.getId() + "");
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(context);

            queue.add(request);

        });
        if (post.getUser().getId() == sharedPreferences.getInt("id", 0)) {
            holder.btn_option.setVisibility(View.VISIBLE);
        } else {
            holder.btn_option.setVisibility(View.GONE);

        }

        holder.card_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, post.getQuote().toString());
                context.startActivity(Intent.createChooser(i, "أرسل عبر"));


              /*  ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copy", post.getQuote().toString());
                clipboard.setPrimaryClip(clip);

               */
            }
        });


        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("postPosition", position);
            context.startActivity(intent);

        });
        holder.btn_option.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.btn_option);
            popupMenu.inflate(R.menu.menu_posts_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.item_edit_post:
                            Intent i = new Intent(((MainActivity) context), EditQuoteActivity.class);
                            i.putExtra("postId", post.getId());
                            i.putExtra("position", position);
                            i.putExtra("txt", post.getQuote());
                            i.putExtra("category_id", post.getCategory_id());
                            context.startActivity(i);
                            return true;


                        case R.id.item_delete_post:
                            deletePost(post.getId(), position);
                            return true;


                    }
                    return false;
                }
            });
            popupMenu.show();
        });


    }

    private void deletePost(int postId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("تأكيد");
        builder.setMessage("هل أنت متأكد من الحذف ؟");
        builder.setPositiveButton("حذف", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringRequest request = new StringRequest(Request.Method.POST, Constans.DELETE_QUOTE, response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);

                            Toast.makeText(context, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show();


                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }, error -> {
                    error.printStackTrace();

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String token = sharedPreferences.getString("token", "");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + token);

                        return map;
                    }

                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", postId + "");

                        return map;

                    }
                };
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        builder.setNegativeButton("تراجع", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();

    }


    @Override
    public int getItemCount() {
        return list.size();
    }




  /*  Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<Post> filtredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filtredList.addAll(listAll);
            } else {
                for (Post post : listAll) {
                    if (post.getDesc().toLowerCase().contains(charSequence.toString().toLowerCase()) || post.getUser().getUserName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filtredList.add(post);
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filtredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((Collection<? extends Post>) filterResults.values);
            notifyDataSetChanged();

        }
    };

    public Filter getFilter() {
        return filter;
      }
   */


    class QuoteHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtQuote, txtLikes, txtComments;
        private ImageView img_profile, btnLike;
        private CardView card_share, btnComment;
        private ImageButton btn_option,add_to_fav;

        public QuoteHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.profile_name_quote);
            txtLikes = itemView.findViewById(R.id.text_likes);
            txtQuote = itemView.findViewById(R.id.quote_text);
            txtComments = itemView.findViewById(R.id.text_comment);
            img_profile = itemView.findViewById(R.id.img_profile_quote);
            btn_option = itemView.findViewById(R.id.option_menu);
            btnLike = itemView.findViewById(R.id.fav_btn);
            add_to_fav = itemView.findViewById(R.id.add_to_fav);
            btnComment = itemView.findViewById(R.id.comment_btn);
            card_share = itemView.findViewById(R.id.card_share);
            btn_option.setVisibility(View.GONE);


        }

        public void toggleButton(boolean isFav) {
            if (isFav) {
                animateImage(add_to_fav);
                add_to_fav.setImageResource(R.drawable.saved_icon);

            } else {
                animateImage(add_to_fav);
                add_to_fav.setImageResource(R.drawable.save_add);

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
