package com.blogweb.khawatiri_with_paanel.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.Activities.AddQuoteActivity;
import com.blogweb.khawatiri_with_paanel.Activities.AuthActivity;
import com.blogweb.khawatiri_with_paanel.Activities.EditProfile;
import com.blogweb.khawatiri_with_paanel.Adapters.AccountPostAdapter;
import com.blogweb.khawatiri_with_paanel.Constans;
import com.blogweb.khawatiri_with_paanel.MainActivity;
import com.blogweb.khawatiri_with_paanel.Models.Quote;
import com.blogweb.khawatiri_with_paanel.Models.User;
import com.blogweb.khawatiri_with_paanel.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Quote> arrayList;
    private AccountPostAdapter accountPostAdapter;
    private ImageView img_profile, logout;
    private TextView posts_count, txtname;
    private Button edit_profile_btn;
    private JSONObject userObject;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.profile_recycler);
        img_profile = view.findViewById(R.id.profile_image);
        logout = view.findViewById(R.id.logout);
        posts_count = view.findViewById(R.id.number_quotes);
        edit_profile_btn = view.findViewById(R.id.edit_btn);
        txtname = view.findViewById(R.id.profile_name);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        txtname.setText(sharedPreferences.getString("name",""));


        if (sharedPreferences.getString("photo","") != "null") {
            Picasso.get().load(Constans.URL + "storage/profiles/" + sharedPreferences.getString("photo","")).into(img_profile);
        }

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), EditProfile.class);
                try {
                    i.putExtra("img_url",userObject.getString("photo"));
                    i.putExtra("name",userObject.getString("name"));
                    startActivity(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    private void getData() {

        arrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.POST, Constans.MY_QUOTES, response -> {

            try {

                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("quotes"));
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject postObject = array.getJSONObject(i);
                        userObject = postObject.getJSONObject("user");
                        User user = new User();
                        user.setId(userObject.getInt("id"));
                        user.setUserName(userObject.getString("name"));
                        user.setPhoto(userObject.getString("photo"));

                        Quote post = new Quote();
                        post.setId(postObject.getInt("id"));
                        post.setUser(user);
                        post.setLikes(postObject.getInt("likesCount"));
                        post.setComments(postObject.getInt("commentCount"));
                        post.setQuote(postObject.getString("quote"));
                        post.setSelfLike(postObject.getBoolean("selfLike"));
                        arrayList.add(post);

                    }
                    JSONObject userObject = object.getJSONObject("user");

                    posts_count.setText(arrayList.size() + "");

                    accountPostAdapter = new AccountPostAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(accountPostAdapter);
                }else {
                    if (object.getString("message").equals("Token expired")){
                        ProgressDialog builder = new ProgressDialog(getContext());
                        builder.setCancelable(false);
                        builder.setMessage("انتهت مدة جلستك ");
                        builder.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(getContext(), AuthActivity.class));
                                ((MainActivity) getContext()).finish();
                            }
                        },2500);
                    }
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
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


    private void logout() {

        StringRequest request = new StringRequest(Request.Method.GET, Constans.LOGOUT, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent(getContext(), AuthActivity.class));
                    ((MainActivity) getContext()).finish();
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
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getData();
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }


}