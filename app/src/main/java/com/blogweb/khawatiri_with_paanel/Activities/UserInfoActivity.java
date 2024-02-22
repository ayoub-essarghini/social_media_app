package com.blogweb.khawatiri_with_paanel.Activities;

import static com.blogweb.khawatiri_with_paanel.Constans.SAVE_USER_INFO;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.MainActivity;
import com.blogweb.khawatiri_with_paanel.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {
    private TextInputLayout layoutname, layout_lname;
    private TextInputEditText fname, lname;
    private TextView select_photo;
    private ImageView profile_img;
    private Button btn_continue;
    private  static final int GALLERY_ADD_PROFILE = 1;
    private Bitmap bitmap=null;
    private SharedPreferences user_pref;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        init();
    }


    private void init() {
        dialog =new ProgressDialog(this);
        dialog.setCancelable(false);
        user_pref = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutname = findViewById(R.id.txt_layout_nameUserInfo);
       // layout_lname = findViewById(R.id.txt_layout_lastnameUserInfo);
        fname = findViewById(R.id.txt_name);
      //  lname = findViewById(R.id.txt_lastname);
        select_photo = findViewById(R.id.select_photo);
        profile_img = findViewById(R.id.profile_img);
        btn_continue = findViewById(R.id.btn_continue);

        //pick photo from gallery
        select_photo.setOnClickListener(v-> {

            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,GALLERY_ADD_PROFILE);
        });

        btn_continue.setOnClickListener(v->{
            if (validate()){
                saveUserInfo();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_ADD_PROFILE && resultCode==RESULT_OK){
            Uri imgUri = data.getData();
            profile_img.setImageURI(imgUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean validate(){


        if (fname.getText().toString().isEmpty()) {
            layoutname.setErrorEnabled(true);
            layoutname.setError("First Name is required");
            return false;
        }
       /* if (lname.getText().toString().isEmpty()) {
            layout_lname.setErrorEnabled(true);
            layout_lname.setError("Last Name is required");
            return false;
        }

        */


        return true;
    }

    private void saveUserInfo() {

        dialog.setMessage("Saving..");
        dialog.show();
        String first_name = fname.getText().toString().trim();
       // String last_name = lname.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, SAVE_USER_INFO, response ->{

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    SharedPreferences.Editor editor = user_pref.edit();
                    editor.putString("photo",object.getString("photo"));
                    editor.apply();
                    startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
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
                                SharedPreferences.Editor editor = user_pref.edit();
                                editor.clear();
                                editor.apply();
                                startActivity(new Intent(UserInfoActivity.this, AuthActivity.class));
                                finish();
                            }
                        },2500);

                    }
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            dialog.dismiss();

        },error -> {
            error.printStackTrace();
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }){

            //add token to headers

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String token = user_pref.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);

                return map;
            }

            //add params


            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("name",first_name);
               // map.put("lastname",last_name);
                map.put("photo",bitmapToString(bitmap));

                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
        queue.add(request);
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

            byte [] array = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(array,Base64.DEFAULT);
        }
        return "";
    }
}