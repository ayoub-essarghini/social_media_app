package com.blogweb.khawatiri_with_paanel.Adapters;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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
import com.blogweb.khawatiri_with_paanel.Models.Quote;
import com.blogweb.khawatiri_with_paanel.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountPostAdapter extends RecyclerView.Adapter<AccountPostAdapter.PostHolder> {


    private Context context;
    private ArrayList<Quote> list;
    private ArrayList<Quote> listAll;
    private SharedPreferences sharedPreferences;


    public AccountPostAdapter(Context context, ArrayList<Quote> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotes_item, parent, false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {
        Quote post = list.get(position);
        Picasso.get().load(Constans.URL + "storage/profiles/" + post.getUser().getPhoto()).into(holder.img_profile);
        holder.txtName.setText(post.getUser().getUserName());
        holder.txtComments.setText("" + post.getComments() +" تعليق");
        holder.txtLikes.setText(post.getLikes() + " إعجاب");
        holder.txtQuote.setText(post.getQuote());

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

        holder.txtComments.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getId());
            intent.putExtra("postPosition", position);
            context.startActivity(intent);

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
                            Intent i = new Intent(context, EditQuoteActivity.class);
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





    class PostHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtQuote, txtLikes, txtComments;
        private ImageView img_profile, btnLike;
        private CardView card_share, btnComment;
        private ImageButton btn_option;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.profile_name_quote);
            txtLikes = itemView.findViewById(R.id.text_likes);
            txtQuote = itemView.findViewById(R.id.quote_text);
            txtComments = itemView.findViewById(R.id.text_comment);
            img_profile = itemView.findViewById(R.id.img_profile_quote);
            btn_option = itemView.findViewById(R.id.option_menu);
            btnLike = itemView.findViewById(R.id.fav_btn);
            btnComment = itemView.findViewById(R.id.comment_btn);
            card_share = itemView.findViewById(R.id.card_share);
            btn_option.setVisibility(View.GONE);
        }
    }
}
