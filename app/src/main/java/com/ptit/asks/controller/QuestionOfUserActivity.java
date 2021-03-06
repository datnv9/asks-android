package com.ptit.asks.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.ptit.asks.adapter.QuestionListAdapter;
import com.ptit.asks.model.Question;
import com.ptit.asks.util.AsksUtil;

public class QuestionOfUserActivity extends AppCompatActivity {

    /*
    Activity hiển thị ra list các câu hỏi đã được lưu của người dùng đăng nhập,
    giao diện ứng với activity_question_of_user.xml
     */

    private boolean isLoading = true;
    private int currentPage = 1;
    private Spinner spinnerQuestion;
    private int defaultFilter = 1;

    //Dùng để sử dụng QuestionListAdapter với ListView các câu hỏi
    private ListView lvQuestion;
    private QuestionListAdapter adapter;
    private List<Question> myArrayQuestion;

    //API url
    private String getUserQuestionUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/questions/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_of_user);

        //Ánh xạ các thành phần trong view
        spinnerQuestion = (Spinner) findViewById(R.id.spinner);
        lvQuestion = (ListView) findViewById(R.id.lvMyQuestion);
        myArrayQuestion = new ArrayList<>();

        if (myArrayQuestion.size() >= 1) {
            adapter = new QuestionListAdapter(getApplicationContext(), myArrayQuestion);
            isLoading = false;
        } else {
            getQuestionList(1, defaultFilter);
        }

        //Xử lý sự kiện nhấn vào câu hỏi trong ListView
        lvQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idQuestion = myArrayQuestion.get(position).getId() + "";
                Intent intent = new Intent(QuestionOfUserActivity.this, QuestionDetailActivity.class);
                intent.putExtra("id", String.valueOf(idQuestion));
                startActivity(intent);
            }
        });

        //Xử lý sự kiện kéo xuống trong ListView
        lvQuestion.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstItem, int visibleItem, int totalItem) {
                if (firstItem + visibleItem == totalItem && totalItem != 0) {
                    if (!isLoading) {
                        isLoading = true;
                        currentPage += 1;
                        getQuestionList(currentPage, defaultFilter);
                    }
                }
            }
        });

        //Xử lý sự kiện kéo lên đầu để reset trong ListView
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) findViewById(R.id.questionSwipe);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myArrayQuestion.clear();
                getQuestionList(1, defaultFilter);
                swipeLayout.setRefreshing(false);
            }
        });

        //Xử lý sự kiện chọn loại câu hỏi để filter
        spinnerQuestion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = parent.getItemAtPosition(position).toString();
                if (value.equals("Solve")) {
                    defaultFilter = 2;
                    myArrayQuestion.clear();
                    getQuestionList(1, defaultFilter);
                } else if (value.equals("Draft")) {
                    defaultFilter = 0;
                    myArrayQuestion.clear();
                    getQuestionList(1, defaultFilter);
                } else {
                    defaultFilter = 1;
                    myArrayQuestion.clear();
                    getQuestionList(1, defaultFilter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Hàm gọi API xử lý việc lấy danh sách câu hỏi
    private void getQuestionList(int pageNo, int status) {
        String value = "";
        if (status == 1) {
            //danh sách câu hỏi chưa được trả lời
            value = "";
        } else if (status == 0) {
            //danh sách câu hỏi nháp
            value = "/draft";
        } else if (status == 2) {
            //danh sách câu hỏi đã được trả lời
            value = "/solve";
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.getUserQuestionUrl + value + "?page=" + pageNo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        JSONArray data = res.getJSONArray("data");
                        if (data.length() > 0) {
                            //Duyệt mảng danh sách câu hỏi
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject q;
                                q = data.getJSONObject(i);
                                int userId = q.getJSONObject("user").getJSONObject("data").getInt("id");
                                String username = q.getJSONObject("user").getJSONObject("data").getString("username");
                                myArrayQuestion.add(new Question(
                                        q.getInt("id"),
                                        q.getString("title"),
                                        q.getString("content"),
                                        username,
                                        userId,
                                        q.getInt("voteCount"),
                                        q.getString("updatedAt"),
                                        q.getInt("status")
                                ));
                            }
                            if (currentPage == 1) {
                                adapter = new QuestionListAdapter(getApplicationContext(), myArrayQuestion);
                                lvQuestion.setAdapter(adapter);
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        }
                        if (myArrayQuestion.size() == 0) {
                            Toast.makeText(QuestionOfUserActivity.this.getApplicationContext(), "You don't have any question", Toast.LENGTH_SHORT).show();
                        }
                        isLoading = false;
                    } else {
                        Toast.makeText(QuestionOfUserActivity.this.getApplicationContext(), res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    Toast.makeText(QuestionOfUserActivity.this.getApplicationContext(), "Unknow error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuestionOfUserActivity.this.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences sharePrefs = QuestionOfUserActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                params.put("Authorization", sharePrefs.getString("token", null));
                return params;
            }
        };
        AsksUtil.getmInstance(QuestionOfUserActivity.this).addToRequestQueue(stringRequest);
    }

}
