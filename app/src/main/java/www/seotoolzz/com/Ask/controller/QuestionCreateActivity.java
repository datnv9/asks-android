package www.seotoolzz.com.Ask.controller;

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

import www.seotoolzz.com.Ask.R;
import www.seotoolzz.com.Ask.util.AsksUtil;

public class QuestionCreateActivity extends AppCompatActivity {

    /*
    Activity cho việc tạo câu hỏi, giao diện ứng với activity_question_create.xml
     */

    Button btnPublish, btnDraft;
    EditText edTitle;
    EditText edQuestion;

    //API url
    private String createQuestionUrl = "http://laravel-demo-deploy.herokuapp.com/api/v0/questions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_create);

        //ActionBar cho nút quay lại
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Ánh xạ các thành phần trong view
        edTitle = (EditText) findViewById(R.id.editTextCreateTitle);
        edQuestion = (EditText) findViewById(R.id.editTextCreateQuestion);
        btnPublish = (Button) findViewById(R.id.btnPublishCreate);
        btnDraft = (Button) findViewById(R.id.btnDraftCreate);


        //Xử lý sự kiện ấn nút gửi câu hỏi
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQuestion(1);
            }
        });

        //Xử lý sự kiện ấn nút gửi câu hỏi bản nháp
        btnDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQuestion(0);
            }
        });
    }

    //Hàm gọi API xử lý việc gửi câu hỏi
    private void createQuestion(int status) {
        final String title = this.edTitle.getText().toString();
        final String question = this.edQuestion.getText().toString();
        final int questionStatus = status;

        //Kiểm tra độ dài tiêu đề và câu hỏi
        if (title.trim().length() < 1 || question.trim().length() < 1) {
            Toast.makeText(getApplicationContext(), "Please fill at least title and questions field", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, this.createQuestionUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);
                        int code = res.getJSONObject("meta").getInt("status");
                        if (code == 700) {
                            Toast.makeText(getApplicationContext(), "Publish success", Toast.LENGTH_SHORT).show();
                            Intent changeView = new Intent(QuestionCreateActivity.this, MainActivity.class);
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
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences sharePrefs = QuestionCreateActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                    params.put("Authorization", sharePrefs.getString("token", null));
                    return params;
                }
            };
            AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
        }
    }
}
