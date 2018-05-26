package com.ptit.asks.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import com.ptit.asks.R;
import com.ptit.asks.model.Question;
import com.ptit.asks.util.AsksUtil;

public class QuestionUpdateActivity extends AppCompatActivity {

    /*
    Activity cho việc sửa câu hỏi, giao diện ứng với activity_question_update.xml
     */

    private Button btnUpdate, btnDraft, btnCancel;
    private EditText edTitle;
    private EditText edContent;
    private String questionId;
    private Question questionModel;

    //API url
    private String questionUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/questions/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_update);

        //ActionBar cho nút quay lại
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Ánh xạ các thành phần trong view
        this.edTitle = (EditText) findViewById(R.id.editTextUpdateTitle);
        this.edContent = (EditText) findViewById(R.id.editTextUpdateQuestion);
        btnUpdate = (Button) findViewById(R.id.btnPublishUpdate);
        btnDraft = (Button) findViewById(R.id.btnDraftUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancelUpdate);

        //Lấy questionId từ Intent
        Intent recIntent = getIntent();
        questionModel = (Question) recIntent.getSerializableExtra("question");
        questionId = String.valueOf(questionModel.getId());

        edTitle.setText(questionModel.getTitle());
        edContent.setText(questionModel.getContent());

        //Xử lý sự kiện nhấn sửa câu hỏi
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(1);
            }
        });

        //Xử lý sự kiện nhấn sửa câu hỏi thành bản nháp
        btnDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuestion(0);
            }
        });

        //Xử lý sự kiện nhấn hủy việc sửa câu hỏi
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeView = new Intent(QuestionUpdateActivity.this, QuestionDetailActivity.class);
                changeView.putExtra("id", String.valueOf(questionId));
                startActivity(changeView);
            }
        });
    }

    //Hàm gọi API xử lý việc cập nhật thông tin câu hỏi
    private void updateQuestion(int status) {
        final String title = this.edTitle.getText().toString();
        final String question = this.edContent.getText().toString();
        final int questionStatus = status;

        //Kiểm tra độ dài tiêu đề và câu hỏi
        if (title.trim().length() < 1 || question.trim().length() < 1) {
            Toast.makeText(getApplicationContext(), "Please fill at least title and questions field", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, this.questionUrl + "update", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);
                        int code = res.getJSONObject("meta").getInt("status");
                        if (code == 700) {
                            Toast.makeText(getApplicationContext(), "Update success", Toast.LENGTH_SHORT).show();
                            Intent changeView = new Intent(QuestionUpdateActivity.this, MainActivity.class);
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
                    params.put("title", title);
                    params.put("content", question);
                    params.put("status", String.valueOf(questionStatus));
                    params.put("id", String.valueOf(questionId));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences sharePrefs = QuestionUpdateActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                    params.put("Authorization", sharePrefs.getString("token", null));
                    return params;
                }
            };
            AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
        }
    }
}
