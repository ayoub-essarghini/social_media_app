package com.blogweb.khawatiri_with_paanel.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.Activities.AddQuoteActivity;
import com.blogweb.khawatiri_with_paanel.Activities.AuthActivity;
import com.blogweb.khawatiri_with_paanel.Activities.CommentActivity;
import com.blogweb.khawatiri_with_paanel.Adapters.QuotesAdapter;
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


public class HomeFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Quote> arrayList;
    private SwipeRefreshLayout refreshLayout;
    public static QuotesAdapter postsAdapter;
    private SharedPreferences sharedPreferences;

    private ImageView img_profile_home;
    private CardView card_add;
    RelativeLayout layout_empty;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();

        return view;
    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.quotes_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        img_profile_home = view.findViewById(R.id.img_profile_home);
        card_add = view.findViewById(R.id.card_add);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        layout_empty = view.findViewById(R.id.empty_quote);



        // getQuotes();
        if (sharedPreferences.getString("photo","").equals("null")) {
            img_profile_home.setImageResource(R.drawable.avatar);
        }else {
            Picasso.get().load(Constans.URL + "storage/profiles/" +sharedPreferences.getString("photo","") ).into(img_profile_home);
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQuotes();
            }
        });

        card_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), AddQuoteActivity.class));
            }
        });

    }

    private void getQuotes() {

        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        StringRequest request = new StringRequest(Request.Method.GET, Constans.QUOTES, response -> {

            try {

                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    JSONArray array = new JSONArray(object.getString("quotes"));
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject postObject = array.getJSONObject(i);
                        JSONObject userObject = postObject.getJSONObject("user");
                        User user = new User();
                        user.setId(userObject.getInt("id"));
                        user.setUserName(userObject.getString("name"));
                        user.setPhoto(userObject.getString("photo"));

                        Quote post = new Quote();
                        post.setId(postObject.getInt("id"));
                        post.setQuote(postObject.getString("quote"));
                        post.setCategory_id(postObject.getInt("category_id"));
                        post.setUser(user);
                        post.setLikes(postObject.getInt("likesCount"));
                        post.setComments(postObject.getInt("commentCount"));
                        post.setSelfLike(postObject.getBoolean("selfLike"));
                        arrayList.add(post);

                    }

                    if (arrayList.size()==0){
                        layout_empty.setVisibility(View.VISIBLE);
                    }else {
                        layout_empty.setVisibility(View.GONE);
                        postsAdapter = new QuotesAdapter(getContext(), arrayList);
                        recyclerView.setAdapter(postsAdapter);

                    }



                }else {
                    if (object.getString("message").equals("Token expired")){
                        Toast.makeText(getContext(), "token expired", Toast.LENGTH_SHORT).show();
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
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
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
    public void onResume() {
        super.onResume();

        getQuotes();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {

            getQuotes();
        }
        super.onHiddenChanged(hidden);
    }
}