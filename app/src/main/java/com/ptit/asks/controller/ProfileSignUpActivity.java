package com.ptit.asks.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.ptit.asks.R;
import com.ptit.asks.util.AsksUtil;

public class ProfileSignUpActivity extends AppCompatActivity {

    /*
    Activity cho việc đăng ký tài khoản, giao diện ứng với activity_profile_sign_up.xml
     */

    private EditText edtUsername;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;
    private TextView txtToLogin;
    private Button btnSignUp;

    //API url
    private String signupUrl = "http://laravel-demo-deploy.herokuapp.com/api/v0/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_sign_up);

        //ActionBar cho nút quay lại
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Ánh xạ các thành phần trong view
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPasswordConfirm = (EditText) findViewById(R.id.edtPasswordConfirm);
        txtToLogin = (TextView) findViewById(R.id.txtToLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignup);

        //Xử lý sự kiện ấn nút Sign up
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        //Xử lý sự kiện ấn đến Login
        txtToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeView = new Intent(ProfileSignUpActivity.this, MainActivity.class);
                ProfileSignUpActivity.this.startActivity(changeView);
            }
        });
    }

    //Hàm gọi API xử lý việc gửi thông tin đăng ký
    private void signup() {
        final String username = this.edtUsername.getText().toString();
        final String email = this.edtEmail.getText().toString();
        final String password = this.edtPassword.getText().toString();
        final String passwordConf = this.edtPasswordConfirm.getText().toString();

        //Kiểm tra độ dài thông tin đã nhập
        if (username.trim().length() < 1
                || email.trim().length() < 1
                || password.trim().length() < 1
                || passwordConf.trim().length() < 1) {
            Toast.makeText(getApplicationContext(), "Please fill all field", Toast.LENGTH_SHORT).show();
        } else if (!password.trim().equals(passwordConf.trim())) {
            Toast.makeText(getApplicationContext(), "Confirm password do not match", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, this.signupUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);
                        int code = res.getJSONObject("meta").getInt("status");
                        if (code == 700) {
                            Toast.makeText(getApplicationContext(), res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ProfileLoginActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.getMessage() == null) {
                        Toast.makeText(getApplicationContext(), "Unknow error", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("username", username);
                    params.put("password", password);
                    params.put("password_confirm", passwordConf);
                    return params;
                }
            };
            AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
        }
    }
}
