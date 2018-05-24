package www.seotoolzz.com.Ask.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import www.seotoolzz.com.Ask.util.AsksUtil;
import www.seotoolzz.com.Ask.R;
import www.seotoolzz.com.Ask.model.Answer;
import www.seotoolzz.com.Ask.adapter.AnswerListAdapter;
import android.widget.LinearLayout;

public class QuestionDetailActivity extends AppCompatActivity {

    private ListView lvAnswer;
    private AnswerListAdapter adapter;
    private List<Answer> myArrayAnswer;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvUserName;
    private TextView tvNumberVote;
    private TextView tvDate;
    private TextView tvAnswerNumber;
    private String questionId;
    private int previousVote;
    private LinearLayout btnLayout;
    private int ownerId;
    private String getQuestionUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/questions/";
    private String getAnswerUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/answers/";
    private String voteUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/questions/vote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_question);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tvTitle = (TextView) findViewById(R.id.txtTitle);
        tvContent = (TextView) findViewById(R.id.txtContent);
        tvUserName = (TextView) findViewById(R.id.txtUserName);
        tvNumberVote = (TextView) findViewById(R.id.txtVoteNumber);
        tvAnswerNumber = (TextView) findViewById(R.id.txtNumberAnswer);
        tvDate = (TextView) findViewById(R.id.tvDate);
        btnLayout = (LinearLayout) findViewById(R.id.btnLayout);

        lvAnswer = (ListView)findViewById(R.id.listAnswer);
        myArrayAnswer = new ArrayList<>();

        Intent recIntent = getIntent();
        questionId = recIntent.getStringExtra("id");
        getQuestion(questionId);
        getAnswerList(1);

        Button addAnswer = (Button) findViewById(R.id.btnAddAnswer);
        addAnswer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(),
                            "Please login before do this",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    Intent intent = new Intent(QuestionDetailActivity.this, AnswerActivity.class);
                    intent.putExtra("id", String.valueOf(questionId));
                    startActivity(intent);
                }
            }
        });

        Button btnEdit = (Button) findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(),
                            "Please login before do this",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    Intent intent = new Intent(QuestionDetailActivity.this, UpdateQuestionActivity.class);
                    intent.putExtra("id", String.valueOf(questionId));
                    startActivity(intent);
                }
            }
        });

        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(),
                            "Please login before do this",
                            Toast.LENGTH_LONG
                    ).show();
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

        ImageButton btnUpVote = (ImageButton) findViewById(R.id.upVote);
        btnUpVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(),
                            "Please login before do this",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    updateVote(1);
                }
            }
        });

        ImageButton btnDownVote = (ImageButton) findViewById(R.id.downVote);
        btnDownVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AsksUtil.isLogin(QuestionDetailActivity.this)) {
                    Toast.makeText(getApplicationContext(),
                            "Please login before do this",
                            Toast.LENGTH_LONG
                    ).show();
                } else {
                    updateVote(-1);
                }
            }
        });
    }

    private void getQuestion(String questionId)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.getQuestionUrl + questionId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            try {
                JSONObject res = new JSONObject(response);
                int code = res.getJSONObject("meta").getInt("status");
                if (code == 700) {
                    // Get token and save in local storage
                    JSONObject data = res.getJSONObject("data");
                    tvTitle.setText(data.getString("title"));
                    tvContent.setText(data.getString("content"));
                    tvNumberVote.setText(String.valueOf(data.getInt("voteCount")));
                    ownerId = data.getJSONObject("user").getJSONObject("data").getInt("id");
                    if (data.getInt("status") == 2) {
                        tvNumberVote.setBackgroundColor(Color.parseColor("#00C853"));
                        tvNumberVote.setTextColor(Color.WHITE);
                    }
                    tvDate.setText(data.getString("updatedAt"));
                    tvUserName.setText("Asks by: " + data.getJSONObject("user").getJSONObject("data").getString("username"));
                    previousVote = data.getInt("voteCount");
                    SharedPreferences sharePrefs = QuestionDetailActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                    int userId = sharePrefs.getInt("userId", 0);
                    if (userId == data.getJSONObject("user").getJSONObject("data").getInt("id")) {
                        btnLayout.setVisibility(View.VISIBLE);
                    }
                    Log.d("QUESTION_DETAIL_RES", data.toString());
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
        });
        www.seotoolzz.com.Ask.util.AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
    }

    private void deleteQuestion()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.getQuestionUrl + "remove", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    Log.d("LOGIN_RES", res.toString());
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        Toast.makeText(getApplicationContext(), "Delete success", Toast.LENGTH_LONG).show();
                        Intent changeView = new Intent(QuestionDetailActivity.this, MainActivity.class);
                        startActivity(changeView);
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
        www.seotoolzz.com.Ask.util.AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateVote(int value)
    {
        final int inputValue = value;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.voteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    Log.d("LOGIN_RES", res.toString());
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        int newValue = previousVote + inputValue;
                        tvNumberVote.setText(String.valueOf(newValue));
                        Toast.makeText(getApplicationContext(), "Vote success", Toast.LENGTH_LONG).show();
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
        www.seotoolzz.com.Ask.util.AsksUtil.getmInstance(this).addToRequestQueue(stringRequest);
    }

    private void getAnswerList(int pageNo) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.getAnswerUrl + questionId + "?page=" + pageNo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");

                    if (code == 700) {
                        // Get token and save in local storage
                        JSONArray data = res.getJSONArray("data");
                        Log.d("ANSWER_RES", data.toString());
                        for(int i = 0; i < data.length(); i++) {
                            JSONObject a;
                            a = data.getJSONObject(i);
                            String username = a.getJSONObject("user").getJSONObject("data").getString("username");
                            int userId = a.getJSONObject("user").getJSONObject("data").getInt("id");
                            Log.d("USER_ID", userId + "");
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
                        SharedPreferences sharePrefs =  QuestionDetailActivity.this.getSharedPreferences("ASKS", MODE_PRIVATE);
                        int userId = sharePrefs.getInt("userId", 0);
                        String token = sharePrefs.getString("token", null);
                        AlertDialog.Builder alert = new AlertDialog.Builder(QuestionDetailActivity.this);
                        adapter = new AnswerListAdapter(getApplicationContext(), myArrayAnswer, userId, alert, token, questionId, ownerId);
                        lvAnswer.setAdapter(adapter);
                        setListViewHeightBasedOnChildren(lvAnswer);
                    } else {
                        Toast.makeText(QuestionDetailActivity.this.getApplicationContext(), res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(QuestionDetailActivity.this.getApplicationContext(), "Unknow error", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("VOLLEY_ERROR", "ERROR: " + error.getMessage());
                    Toast.makeText(QuestionDetailActivity.this.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        www.seotoolzz.com.Ask.util.AsksUtil.getmInstance(QuestionDetailActivity.this).addToRequestQueue(stringRequest);
    }

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
}