package com.blogweb.khawatiri_with_paanel.Activities;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.Adapters.CategoriesAdaper;
import com.blogweb.khawatiri_with_paanel.Constans;
import com.blogweb.khawatiri_with_paanel.Fragments.HomeFragment;
import com.blogweb.khawatiri_with_paanel.Models.Category;
import com.blogweb.khawatiri_with_paanel.Models.Quote;
import com.blogweb.khawatiri_with_paanel.Models.User;
import com.blogweb.khawatiri_with_paanel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddQuoteActivity extends AppCompatActivity {
    private Button btnAddPost;
    private EditText txtDesc;
    private ProgressDialog dialog;
    private SharedPreferences sharedPreferences;
    private Spinner spinner;
    private List<Category> categories_list;
    int category_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);
        init();
    }

    private void init() {

        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        btnAddPost = findViewById(R.id.btn_add_quote);
        txtDesc = findViewById(R.id.txtQuote);

        spinner = findViewById(R.id.spinner);
        categories_list = new ArrayList<>();
        getCategories();




        btnAddPost.setOnClickListener(v -> {
            if (!txtDesc.getText().toString().isEmpty()) {
                post();
            } else {
                Toast.makeText(this, "post quote is required", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post() {
        HomeFragment.arrayList = new ArrayList<>();
      //  AccountFragment.arrayList = new ArrayList<>();

        dialog.setMessage("Posting..");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constans.ADD_QUOTE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject postObject = object.getJSONObject("quote");
                    JSONObject userObject = postObject.getJSONObject("user");
                    User user = new User();
                    user.setId(userObject.getInt("id"));
                    user.setUserName(userObject.getString("name"));
                    user.setPhoto(userObject.getString("photo"));

                    Quote post = new Quote();
                    post.setId(postObject.getInt("id"));
                    post.setUser(user);
                    post.setLikes(0);
                    post.setComments(0);
                    post.setSelfLike(false);

                    HomeFragment.arrayList.add(0,post);
                    HomeFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    HomeFragment.postsAdapter.notifyDataSetChanged();

                    //   AccountFragment.arrayList.add(0,post);
                   // AccountFragment.recyclerView.getAdapter().notifyItemInserted(0);
                   // AccountFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "تمت الاضافة بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
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
                                startActivity(new Intent(AddQuoteActivity.this, AuthActivity.class));
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
            Toast.makeText(this, "المرجو اعادة المحاولة", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);

                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("quote", txtDesc.getText().toString().trim());
                map.put("category_id",category_id+"");

                return map;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(AddQuoteActivity.this);
        queue.add(request);
    }

    private void getCategories(){
        categories_list = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET,Constans.CATEGORIES,response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")){
                    Toast.makeText(this, "done categ", Toast.LENGTH_SHORT).show();
                    JSONArray quotes = object.getJSONArray("categories");

                    for (int i = 0; i < quotes.length(); i++) {
                        JSONObject quoteObject = quotes.getJSONObject(i);
                        Category category = new Category();
                        category.setId(quoteObject.getInt("id"));
                        category.setCategory_name(quoteObject.getString("category_name"));

                        categories_list.add(category);


                    }
                    ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item,categories_list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                            Category category1 = (Category) parent.getSelectedItem();
                            category_id = category1.getId();

                            Toast.makeText(AddQuoteActivity.this, ""+category_id, Toast.LENGTH_SHORT).show();
                            // displayCategoryData(category1);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

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
                                startActivity(new Intent(AddQuoteActivity.this, AuthActivity.class));
                            }
                        },2500);


                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        },error -> {

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void getSelectedCategory(View v){
        Category category =(Category) spinner.getSelectedItem();
        displayCategoryData(category);
    }
    private void displayCategoryData(Category category){
        int id = category.getId();
     //   String categ_name = category.getCategory_name();

      //  String category_data = "categ_name: "+categ_name+ " id: "+id;
        Toast.makeText(this, ""+id, Toast.LENGTH_LONG).show();

    }


    public void btnBack(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}