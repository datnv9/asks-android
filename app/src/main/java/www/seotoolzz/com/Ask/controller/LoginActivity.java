package www.seotoolzz.com.Ask.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    EditText edPassword;
    EditText edNameUser;
    TextView txtToSignUp;

    private String loginUrl = "http://laravel-demo-deploy.herokuapp.com/api/v0/auth/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.edNameUser = (EditText)findViewById(R.id.edtEmail);
        this.edPassword = (EditText)findViewById(R.id.edtPassword);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        txtToSignUp = (TextView) findViewById(R.id.txtToSignup);
        txtToSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent newIntent =  new Intent(getBaseContext(),SignUpActivity.class);
                startActivity(newIntent);
            }
        });
    }

    private void login() {
        final String email = this.edNameUser.getText().toString();
        final String password = this.edPassword.getText().toString();

        if (email.trim().length() < 1 || password.trim().length() < 1) {
            Toast.makeText(getApplicationContext(), "Please fill all field", Toast.LENGTH_LONG).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, this.loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);
                        Log.d("LOGIN_RES", res.toString());
                        int code = res.getJSONObject("meta").getInt("status");

                        if (code == 700) {
                            // Get token and save in local storage
                            String token = res.getJSONObject("data").getString("token");
                            SharedPreferences.Editor sharePrefs = getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE).edit();
                            sharePrefs.putString("token", "Bearer " + token);
                            sharePrefs.commit();
                            // Switch to MainActivity
                            Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.getMessage() == null) {
                        Log.d("VOLLEY_ERROR", "Unknow error");
                        Toast.makeText(getApplicationContext(), "Unknow error", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("VOLLEY_ERROR", "ERROR: " + error.getMessage());
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