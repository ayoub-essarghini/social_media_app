package com.blogweb.khawatiri_with_paanel.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.Activities.CommentActivity;
import com.blogweb.khawatiri_with_paanel.Constans;
import com.blogweb.khawatiri_with_paanel.Fragments.HomeFragment;
import com.blogweb.khawatiri_with_paanel.Models.Comment;
import com.blogweb.khawatiri_with_paanel.Models.Quote;
import com.blogweb.khawatiri_with_paanel.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder> {
        private Context context;
        private ArrayList<Comment> list;

        private SharedPreferences preferences;
        private ProgressDialog dialog;


    public CommentsAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;

        this.preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        this.dialog = new ProgressDialog(context);
        dialog.setCancelable(false);


    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment,parent,false);


        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

        Comment comment = list.get(position);
        Picasso.get().load(comment.getUser().getPhoto()).into(holder.img_profile);
        holder.txtName.setText(comment.getUser().getUserName());
        holder.date.setText(comment.getDate());
        holder.txt_comment.setText(comment.getComment());

        if (preferences.getInt("id",0)!=comment.getUser().getId()){
            holder.btn_delete.setVisibility(View.GONE);
        }else {
            holder.btn_delete.setVisibility(View.VISIBLE);
            holder.btn_delete.setOnClickListener(v->{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("هل أنت متأكد من الحذف ؟");
                builder.setPositiveButton("حذف", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteComment(comment.getId(),position);

                    }
                });
                builder.setNegativeButton("تراجع", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            });

        }



    }

    private void deleteComment(int id, int position) {
        dialog.setMessage("Deleting..");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constans.DELETE_COMMENT, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("sucess")){
                    list.remove(position);
                    Quote post = HomeFragment.arrayList.get(CommentActivity.post_position);
                    post.setComments(post.getComments()-1);
                    HomeFragment.arrayList.set(CommentActivity.post_position,post);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    notifyDataSetChanged();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            dialog.dismiss();

        },error -> {
            error.printStackTrace();
            dialog.dismiss();

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id",id+"");
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{

        private ImageView img_profile;
        private TextView txtName,txt_comment,date;
        private ImageButton btn_add_comment,btn_delete;


        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            img_profile = itemView.findViewById(R.id.img_comment_profile);
            txtName = itemView.findViewById(R.id.text_commentName);
            txt_comment = itemView.findViewById(R.id.text_commentDesc);
            date = itemView.findViewById(R.id.text_commentDate);
            btn_add_comment = itemView.findViewById(R.id.btnAddComment);
            btn_delete = itemView.findViewById(R.id.btn_delete_comment);
        }
    }
}
