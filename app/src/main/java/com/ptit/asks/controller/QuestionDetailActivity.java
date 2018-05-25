package com.ptit.asks.controller;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ptit.asks.R;
import com.ptit.asks.adapter.AnswerListAdapter;
import com.ptit.asks.model.Answer;
import com.ptit.asks.util.AsksUtil;

public class QuestionDetailActivity extends AppCompatActivity {

    /*
    Activity cho việc xem chi tiết một câu hỏi cùng các câu trả lời của nó, với id của câu hỏi
    truyền qua intent, giao diện ứng với activity_question_detail.xml
     */

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvUserName;
    private TextView tvNumberVote;
    private TextView tvDate;
    private TextView tvAnswerNumber;
    private Button addAnswer;
    private Button btnEdit;
    private Button btnDelete;
    private ImageButton btnUpVote;
    private ImageButton btnDownVote;
    private LinearLayout btnLayout;

    private String questionId;
    private int previousVote;
    private int ownerId;

    //Dùng để sử dụng AnswerListAdapter với ListView các câu trả lời
    private ListView lvAnswer;
    private AnswerListAdapter adapter;
    private List<Answer> myArrayAnswer;

    //API url
    private String getQuestionUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/questions/";
    private String getAnswerUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/answers/";
    private String voteUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/questions/vote";

    //Hàm set chiều cao cho ListView dựa trên số phần tử
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount())) + listAdapter.getCount() * 40;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        //ActionBar cho nút quay lại
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Lấy questionId từ Intent
        Intent recIntent = getIntent();
        questionId = recIntent.getStringExtra("id");

        //Ánh xạ các thành phần trong view
        tvTitle = (TextView) findViewById(R.id.txtTitle);
        tvContent = (TextView) findViewById(R.id.txtContent);
        tvUserName = (TextView) findViewById(R.id.txtUserName);
        tvNumberVote = (TextView) findViewById(R.id.txtVoteNumberDetail);
        tvAnswerNumber = (TextView) findViewById(R.id.txtNumberAnswer);
        tvDate = (TextView) findViewById(R.id.tvDate);
        btnLayout = (LinearLayout) findViewById(R.id.btnLayout);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        lvAnswer = (ListView) findViewById(R.id.listAnswer);
        addAnswer = (Button) findViewById(R.id.btnAddAnswer);
        btnUpVote = (ImageButton) findViewById(R.id.upVote);
        btnDownVote = (ImageButton) findViewById(R.id.downVote);

        myArrayAnswer = new ArrayList<>();

        //Lấy các thông tin về câu hỏi và các câu trả lời từ server
        getQuestion(questionId);
        getAnswerList(1);

        //Xử lý sự kiện nhấn sửa câu hỏi
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(), "Please login before do this", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(QuestionDetailActivity.this, QuestionUpdateActivity.class);
                    intent.putExtra("id", String.valueOf(questionId));
                    startActivity(intent);
                }
            }
        });

        //Xử lý sự kiện nhấn xóa câu hỏi
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(), "Please login before do this", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(QuestionDetailActivity.this);
                    alert.setTitle("Warning");
                    alert.setMessage("Are you sure to delete this question?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteQuestion();
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        });

        //Xử lý sự kiện nhấn thêm câu trả lời
        addAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(), "Please login before do this", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(QuestionDetailActivity.this, AnswerCreateActivity.class);
                    intent.putExtra("id", String.valueOf(questionId));
                    startActivity(intent);
                }
            }
        });

        //Xử lý sự kiện vote câu hỏi +1
        btnUpVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(), "Please login before do this", Toast.LENGTH_SHORT).show();
                } else {
                    updateVote(1);
                }
            }
        });

        //Xử lý sự kiện vote câu hỏi -1
        btnDownVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(), "Please login before do this", Toast.LENGTH_SHORT).show();
                } else {
                    updateVote(-1);
                }
            }
        });
    }

    //Hàm gọi API xử lý việc lấy thông tin câu hỏi
    private void getQuestion(String questionId) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.getQuestionUrl + questionId, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        JSONObject data = res.getJSONObject("data");
                        tvTitle.setText(data.getString("title"));
                        tvContent.setText(data.getString("content"));
                        tvNumberVote.setText(String.valueOf(data.getInt("voteCount")));
                        ownerId = data.getJSONObject("user").getJSONObject("data").getInt("id");
                        if (data.getInt("status") == 2) {
                            tvNumberVote.setTextColor(Color.parseColor("#00C853"));
                        }
                        tvDate.setText(data.getString("updatedAt"));
                        tvUserName.setText("Asks by: " + data.getJSONObject("user").getJSONObject("data").getString("username"));
                        previousVote = data.getInt("voteCount");

                        //Lấy id người dùng đang đăng nhập
                        SharedPreferences sharePrefs = QuestionDetailActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                        int userId = sharePrefs.getInt("userId", 0);

                        //Nếu người dùng là người đặt câu hỏi mới hiển thị lựa chọn sửa/xóa câu hỏi
                        if (userId == data.getJSONObject("user").getJSONObject("data").getInt("id")) {
                            btnLayout.setVisibility(View.VISIBLE);
                        }
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
        });
        AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
    }

    //Hàm gọi API xử lý việc xóa câu hỏi
    private void deleteQuestion() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.getQuestionUrl + "remove", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        Toast.makeText(getApplicationContext(), "Delete success", Toast.LENGTH_SHORT).show();
                        Intent changeView = new Intent(QuestionDetailActivity.this, MainActivity.class);
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
                params.put("id", String.valueOf(questionId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences sharePrefs = QuestionDetailActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                params.put("Authorization", sharePrefs.getString("token", null));
                return params;
            }
        };
        AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
    }

    //Hàm gọi API xử lý việc vote câu hỏi
    private void updateVote(int value) {
        final int inputValue = value;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.voteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        int newValue = previousVote + inputValue;
                        tvNumberVote.setText(String.valueOf(newValue));
                        Toast.makeText(getApplicationContext(), "Vote success", Toast.LENGTH_SHORT).show();
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
                params.put("value", String.valueOf(inputValue));
                params.put("id", String.valueOf(questionId));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences sharePrefs = QuestionDetailActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                params.put("Authorization", sharePrefs.getString("token", null));
                return params;
            }
        };
        AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
    }

    //Hàm gọi API xử lý việc lấy danh sách câu trả lời của câu hỏi
    private void getAnswerList(int pageNo) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.getAnswerUrl + questionId + "?page=" + pageNo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        JSONArray data = res.getJSONArray("data");
                        //Duyệt mảng danh sách câu trả lời
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject a;
                            a = data.getJSONObject(i);
                            String username = a.getJSONObject("user").getJSONObject("data").getString("username");
                            int userId = a.getJSONObject("user").getJSONObject("data").getInt("id");
                            myArrayAnswer.add(new Answer(
                                    a.getInt("id"),
                                    userId,
                                    a.getString("content"),
                                    username,
                                    a.getBoolean("solve"),
                                    a.getString("createdAt")
                            ));
                        }
                        tvAnswerNumber.setText(String.valueOf(data.length()));

                        //Lấy id người dùng đang đăng nhập
                        SharedPreferences sharePrefs = QuestionDetailActivity.this.getSharedPreferences("ASKS", MODE_PRIVATE);
                        int userId = sharePrefs.getInt("userId", 0);
                        String token = sharePrefs.getString("token", null);

                        //Truyền vào AnswerListAdapter để kiểm tra quyền người dùng với từng câu hỏi
                        AlertDialog.Builder alert = new AlertDialog.Builder(QuestionDetailActivity.this);
                        adapter = new AnswerListAdapter(getApplicationContext(), myArrayAnswer, userId, alert, token, questionId, ownerId);
                        lvAnswer.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(lvAnswer);
                    } else {
                        Toast.makeText(QuestionDetailActivity.this.getApplicationContext(), res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    Toast.makeText(QuestionDetailActivity.this.getApplicationContext(), "Unknow error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuestionDetailActivity.this.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AsksUtil.getmInstance(QuestionDetailActivity.this).addToRequestQueue(stringRequest);
    }
}
