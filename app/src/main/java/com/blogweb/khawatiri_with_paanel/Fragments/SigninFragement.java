package com.blogweb.khawatiri_with_paanel.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogweb.khawatiri_with_paanel.Activities.AuthActivity;
import com.blogweb.khawatiri_with_paanel.Constans;
import com.blogweb.khawatiri_with_paanel.MainActivity;
import com.blogweb.khawatiri_with_paanel.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SigninFragement extends Fragment {

    private View view;
    private TextInputLayout layout_email, layout_password;
    private TextInputEditText txt_email, txt_password;
    private TextView text_signup;
    private Button btn_signin;
    private ProgressDialog progressDialog;

    public SigninFragement() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin_fragement, container, false);
        init();
        return view;
    }

    private void init() {
        layout_email = view.findViewById(R.id.txt_layout_email_signin);
        layout_password = view.findViewById(R.id.txt_layout_password_signin);
        txt_email = view.findViewById(R.id.txt_email_sign_in);
        txt_password = view.findViewById(R.id.txt_password_sign_in);
        text_signup = view.findViewById(R.id.txt_signup);
        btn_signin = view.findViewById(R.id.sign_in_btn);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        text_signup.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAthContainer, new SignupFragement()).commit();
        });

        btn_signin.setOnClickListener(v -> {
            //valid fields first
            if (validate()) {
                login();
            }
        });
        txt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txt_email.getText().toString().isEmpty()) {
                    layout_email.setErrorEnabled(false);

                }
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_password.getText().toString().length() > 7) {
                    layout_password.setErrorEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {
        if (txt_email.getText().toString().isEmpty()) {
            layout_email.setErrorEnabled(true);
            layout_email.setError("Email is required");
            return false;
        }
        if (txt_password.getText().toString().length() < 8) {
            layout_password.setErrorEnabled(true);
            layout_password.setError("Required at least 8 characters");
            return false;
        }
        return true;
    }

    private void login() {
        progressDialog.setMessage("Sign in..");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constans.Login, response -> {
            //we get response if connection success
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");
                    SharedPreferences userpref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userpref.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putString("name", user.getString("name"));
                    editor.putInt("id", user.getInt("id"));
                  //  editor.putString("lastname", user.getString("lastname"));
                    editor.putString("photo", user.getString("photo"));
                    editor.putBoolean("isLoggedIn",true);
                    editor.apply();
                    startActivity(new Intent(((AuthActivity) getContext()), MainActivity.class));
                    ((AuthActivity) getContext()).finish();

                    Toast.makeText(getContext(), "login success", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "login faild", Toast.LENGTH_SHORT).show();

            }
            progressDialog.dismiss();
        }, error -> {
            //error if connection not success
            error.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(getContext(), ""+error, Toast.LENGTH_SHORT).show();

        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", txt_email.getText().toString().trim());
                map.put("password", txt_password.getText().toString());

                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}