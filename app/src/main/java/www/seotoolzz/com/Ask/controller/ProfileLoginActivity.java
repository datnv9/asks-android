package www.seotoolzz.com.Ask.controller;

import android.content.Intent;
import android.content.SharedPreferences;
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

import www.seotoolzz.com.Ask.R;
import www.seotoolzz.com.Ask.util.AsksUtil;

public class ProfileLoginActivity extends AppCompatActivity {

    /*
    Activity cho việc đăng nhập, giao diện ứng với activity_profile_login.xml
     */

    private Button btnLogin;
    private EditText edtNameUser;
    private EditText edtPassword;
    private TextView txtToSignUp;

    //API url
    private String loginUrl = "http://laravel-demo-deploy.herokuapp.com/api/v0/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_login);

        //ActionBar cho nút quay lại
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Ánh xạ các thành phần trong view
        edtNameUser = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtToSignUp = (TextView) findViewById(R.id.txtToSignup);

        //Xử lý sự kiện ấn nút Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        //Xử lý sự kiện ấn đến Sign up
        txtToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chuyển sang Intent SignUp
                Intent newIntent = new Intent(getBaseContext(), ProfileSignUpActivity.class);
                startActivity(newIntent);
            }
        });
    }

    //Hàm gọi API xử lý việc gửi thông tin đăng nhập
    private void login() {
        final String email = this.edtNameUser.getText().toString();
        final String password = this.edtPassword.getText().toString();

        //Kiểm tra độ dài thông tin đã nhập
        if (email.trim().length() < 1 || password.trim().length() < 1) {
            Toast.makeText(getApplicationContext(), "Please fill all field", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, this.loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);
                        int code = res.getJSONObject("meta").getInt("status");
                        if (code == 700) {
                            // Lấy token và lưu lại trong bộ nhớ
                            String token = res.getJSONObject("data").getString("token");
                            SharedPreferences.Editor sharePrefs = getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE).edit();
                            sharePrefs.putString("token", "Bearer " + token);
                            sharePrefs.commit();
                            // Chuyển về MainActivity
                            Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
                    params.put("password", password);
                    return params;
                }
            };
            AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
        }
    }
}
