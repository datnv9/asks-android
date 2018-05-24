package www.seotoolzz.com.Ask.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class AnswerCreateActivity extends AppCompatActivity {

    /*
    Activity cho việc tạo câu trả lời cho câu hỏi với id của câu hỏi truyền qua intent,
    giao diện ứng với activity_answer_create.xml
     */

    private String questionId;
    private EditText editTextAnswer;
    private Button btnPostAnswer;

    //API url
    private String createAnswerUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/answers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_create);

        //ActionBar cho nút quay lại
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Lấy questionId từ Intent
        Intent recIntent = getIntent();
        questionId = recIntent.getStringExtra("id");

        //Ánh xạ các thành phần trong view
        editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);
        btnPostAnswer = (Button) findViewById(R.id.btnPostAnswer);

        //Xử lý hiển thị keyboeard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //Xử lý sự kiện ấn nút gửi câu trả lời
        btnPostAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postQuestion(questionId);
            }
        });
    }

    //Hàm gọi API xử lý việc gửi câu trả lời
    private void postQuestion(String questionId) {
        final String id = questionId;
        final String content = editTextAnswer.getText().toString();

        //Kiểm tra độ dài câu trả lời
        if (content.trim().length() < 1) {
            Toast.makeText(getApplicationContext(), "Please fill your answer", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, createAnswerUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);
                        int code = res.getJSONObject("meta").getInt("status");
                        if (code == 700) {
                            Toast.makeText(getApplicationContext(), "Create answer success", Toast.LENGTH_SHORT).show();
                            Intent changeView = new Intent(AnswerCreateActivity.this, QuestionDetailActivity.class);
                            changeView.putExtra("id", id);
                            startActivity(changeView);
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
                    params.put("questions_id", id);
                    params.put("content", content);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences sharePrefs = AnswerCreateActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                    params.put("Authorization", sharePrefs.getString("token", null));
                    return params;
                }
            };
            AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
        }
    }
}
