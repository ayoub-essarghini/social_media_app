package com.blogweb.khawatiri_with_paanel.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.Constans;
import com.blogweb.khawatiri_with_paanel.Fragments.HomeFragment;
import com.blogweb.khawatiri_with_paanel.Models.Category;
import com.blogweb.khawatiri_with_paanel.Models.Quote;
import com.blogweb.khawatiri_with_paanel.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditQuoteActivity extends AppCompatActivity {
    private int post_id = 0, position = 0,categ_id=0;
    private String tDesc;
    private EditText txtDesc;
    private Button btn_edit_post;
    private Spinner spinner;
    private List<Category> categories_list;
    int category_id;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quote);
        init();
    }


    private void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        txtDesc = findViewById(R.id.txtDescEditPost);
        btn_edit_post = findViewById(R.id.btn_edit_post);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        position = getIntent().getIntExtra("position", 0);
        post_id = getIntent().getIntExtra("postId", 0);
        categ_id = getIntent().getIntExtra("category_id", 0);
        tDesc = getIntent().getStringExtra("txt");

        txtDesc.setText(tDesc);
        spinner = findViewById(R.id.spinner);
        categories_list = new ArrayList<>();
        getCategories();

        btn_edit_post.setOnClickListener(v -> {
            if (!txtDesc.getText().toString().isEmpty()) {
                updatePost();
            } else {
                Toast.makeText(this, "المرجو ملأ الخانة أولا", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void updatePost() {
        progressDialog.setMessage("Updating..");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constans.UPDATE_QUOTE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {

                    Quote post = HomeFragment.arrayList.get(position);
                    post.setQuote(txtDesc.getText().toString());
                    HomeFragment.arrayList.set(position,post);
                    HomeFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    HomeFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "تم التعديل بنجاح", Toast.LENGTH_SHORT).show();
                    finish();

                }else {
                    if (object.getString("message").equals("Token expired")){
                        Toast.makeText(this, "token expired", Toast.LENGTH_SHORT).show();
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
                                startActivity(new Intent(EditQuoteActivity.this, AuthActivity.class));
                                finish();
                            }
                        },2500);

                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            progressDialog.dismiss();

        }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "المرجو اعادة المحاولة", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

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
                map.put("id",post_id+"");
                map.put("quote",txtDesc.getText().toString());
                map.put("category_id",category_id+"");


                return map;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(EditQuoteActivity.this);
        queue.add(request);
    }

    private void getCategories(){
        categories_list = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET,Constans.CATEGORIES,response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")){
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
                    for (int i = 0; i < categories_list.size(); i++) {

                        if (categories_list.get(i).getId()==categ_id){
                            spinner.setSelection(i);

                        }

                    }
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                            Category category1 = (Category) parent.getSelectedItem();
                            category_id = category1.getId();

                           // Toast.makeText(EditQuoteActivity.this, ""+category_id, Toast.LENGTH_SHORT).show();
                            // displayCategoryData(category1);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            Toast.makeText(EditQuoteActivity.this, "المرجو اختيار قسم", Toast.LENGTH_SHORT).show();
                        }
                    });

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

    public void cancelEdit(View view) {
        super.onBackPressed();
    }
}