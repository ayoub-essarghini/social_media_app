package com.blogweb.khawatiri_with_paanel.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.Adapters.CommentsAdapter;
import com.blogweb.khawatiri_with_paanel.Constans;
import com.blogweb.khawatiri_with_paanel.Fragments.HomeFragment;
import com.blogweb.khawatiri_with_paanel.Models.Comment;
import com.blogweb.khawatiri_with_paanel.Models.Quote;
import com.blogweb.khawatiri_with_paanel.Models.User;
import com.blogweb.khawatiri_with_paanel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Comment> list;
    private EditText AddCommentText;
    private CommentsAdapter adapter;
    public static int post_position = 0;
    private int postId = 0;
    private SharedPreferences sharedPreferences;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        init();

    }



    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        post_position = getIntent().getIntExtra("postPosition", -1);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recycler_comments);
        AddCommentText = findViewById(R.id.txtAddComment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postId = getIntent().getIntExtra("postId", 0);
        getComments();
        String token = sharedPreferences.getString("token", "");
        Log.d("token",token);

    }

    private void getComments() {
        list = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, Constans.QUOTE_COMMENTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray comments = new JSONArray(object.getString("comments"));
                    for (int i = 0; i < comments.length(); i++) {
                        JSONObject comment = comments.getJSONObject(i);
                        JSONObject user = comment.getJSONObject("user");

                        User mUser = new User();
                        mUser.setId(user.getInt("id"));
                        mUser.setPhoto(Constans.URL+"storage/profiles/"+user.getString("photo"));
                        mUser.setUserName(user.getString("name"));

                        Comment mComment = new Comment();
                        mComment.setId(comment.getInt("id"));
                        mComment.setUser(mUser);
                        mComment.setDate(comment.getString("created_at"));
                        mComment.setComment(comment.getString("comment"));

                        list.add(mComment);
                    }

                }
                adapter = new CommentsAdapter(this,list);
                recyclerView.setAdapter(adapter);




            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);

                return map;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", postId +"");

                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    public void goBack(View view) {
        super.onBackPressed();
    }

    public void addComment(View view) {
        dialog.setMessage("Commenting..");
        dialog.show();
        String txtComment = AddCommentText.getText().toString();
        if (txtComment.length() > 0) {
            StringRequest request1 = new StringRequest(Request.Method.POST, Constans.ADD_COMMENT, response -> {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")) {
                        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
                        JSONObject comment = object.getJSONObject("comment");
                        JSONObject user = comment.getJSONObject("user");

                        User u = new User();
                        Comment c = new Comment();
                        u.setId(user.getInt("id"));
                        u.setPhoto(Constans.URL + "storage/profiles/" + user.getString("photo"));
                        u.setUserName(user.getString("name"));


                        c.setId(comment.getInt("id"));
                        c.setComment(comment.getString("comment"));
                        c.setDate(comment.getString("created_at"));
                        c.setUser(u);

                        Quote post = HomeFragment.arrayList.get(post_position);
                        post.setComments(post.getComments() + 1);
                        HomeFragment.arrayList.set(post_position, post);
                        HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                        list.add(c);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        AddCommentText.setText("");


                    }else {
                        if (object.getString("message").equals("Token expired")){
                            ProgressDialog builder = new ProgressDialog(this);
                            builder.setCancelable(false);
                            builder.setMessage("انتهت مدة جلستك ");

                            builder.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.clear();
                                    editor.apply();
                                    startActivity(new Intent(CommentActivity.this, AuthActivity.class));
                                    finish();
                                }
                            },2500);
                        }
                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                dialog.dismiss();

            }, error -> {
                error.printStackTrace();
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String token = sharedPreferences.getString("token", "");
                    HashMap<String,String> map = new HashMap<>();
                    map.put("Authorization", "Bearer " + token);

                    return map;
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("quote_id", postId +"");
                    map.put("comment", txtComment);

                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request1);
        }
    }
}