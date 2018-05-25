package com.ptit.asks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

import com.ptit.asks.R;
import com.ptit.asks.controller.QuestionDetailActivity;
import com.ptit.asks.model.Answer;
import com.ptit.asks.util.AsksUtil;

public class AnswerListAdapter extends BaseAdapter {

    /*
    Adapter chuyển đổi danh sách các câu trả lời về dạng hiển thị định nghĩa trong
    item_answer_list.xml và đưa vào listview các câu trả lời
     */

    private Context myContext;
    private List<Answer> myAnswer;
    private int userId;
    private String token;
    private int ownerId;
    private String questionId;
    private AlertDialog.Builder alert;

    //API url
    private String removeAnswerUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/answers/remove";
    private String markAsSolveUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/answers/solve";

    public AnswerListAdapter(Context myContext, List<Answer> myAnswer, int userId, AlertDialog.Builder alert, String token, String questionId, int ownerId) {
        this.myContext = myContext;
        this.myAnswer = myAnswer;
        this.userId = userId;
        this.alert = alert;
        this.token = token;
        this.questionId = questionId;
        this.ownerId = ownerId;
    }

    @Override
    public int getCount() {
        return myAnswer.size();
    }

    @Override
    public Object getItem(int position) {
        return myAnswer.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint
                ("ViewHolder") View v = View.inflate(myContext, R.layout.item_answer_list, null);

        final int pos = position;
        final int oId = ownerId;

        //Ánh xạ các thành phần trong view
        TextView tvContent = (TextView) v.findViewById(R.id.txtContentAnswer);
        TextView tvUserName = (TextView) v.findViewById(R.id.txtUserNameAnswer);
        TextView tvTime = (TextView) v.findViewById(R.id.txtTimeAnswer);
        Button btnSolve = (Button) v.findViewById(R.id.btnSolveAnswer);
        Button btnDelete = (Button) v.findViewById(R.id.btnDeleteAnswer);

        //Gán nội dung các đoạn text
        tvContent.setText(myAnswer.get(position).getContent());
        tvUserName.setText(myAnswer.get(position).getUserName());
        tvTime.setText(myAnswer.get(position).getTime());

        //Đổi màu nút nếu câu trả lời là đúng
        if (String.valueOf(myAnswer.get(position).getVoteNumber()).equals("true")) {
            btnSolve.setBackgroundColor(Color.parseColor("#00C851"));
            btnSolve.setTextColor(Color.WHITE);
            btnSolve.setText("Solved");
        }

        //Xử lý sự kiện ấn nút SOLVE
        btnSolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kiểm tra xem có phải người đặt câu hỏi không
                if (userId == oId) {
                    alert.setTitle("Info");
                    alert.setMessage("Mark this answer as result?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            markAsSolve(myAnswer.get(pos).getId());
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.show();
                } else {
                    Toast.makeText(myContext.getApplicationContext(), "You do not own this question", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Xử lý sự kiện ấn nút DELETE
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userId == myAnswer.get(pos).getUserId()) {
                    //Kiểm tra xem có phải người viết câu trả lời không
                    alert.setTitle("Warning");
                    alert.setMessage("Are you sure to delete this answer?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteQuestion(myAnswer.get(pos).getId());
                        }
                    });
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.show();
                } else {
                    Toast.makeText(myContext.getApplicationContext(), "You do not own this answer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        v.setTag(myAnswer.get(position).getId());
        return v;
    }

    //Hàm gọi API xử lý việc đánh dấu câu trả lời là đúng
    private void markAsSolve(int answerId) {
        final int id = answerId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, markAsSolveUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        Toast.makeText(myContext, "Mark as solve success", Toast.LENGTH_LONG).show();
                        Intent changeView = new Intent(myContext, QuestionDetailActivity.class);
                        changeView.putExtra("id", String.valueOf(questionId));
                        changeView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        myContext.startActivity(changeView);
                    } else {
                        Toast.makeText(myContext, res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    Toast.makeText(myContext, "Unknow error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(myContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };
        AsksUtil.getmInstance(myContext).addToRequestQueue(stringRequest);
    }

    //Hàm gọi API xử lý việc xóa câu trả lời
    private void deleteQuestion(int answerId) {
        final int id = answerId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, removeAnswerUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        Toast.makeText(myContext, "Delete success", Toast.LENGTH_LONG).show();
                        Intent changeView = new Intent(myContext, QuestionDetailActivity.class);
                        changeView.putExtra("id", String.valueOf(questionId));
                        changeView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        myContext.startActivity(changeView);
                    } else {
                        Toast.makeText(myContext, res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    Toast.makeText(myContext, "Unknow error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(myContext, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(id));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", token);
                return params;
            }
        };
        AsksUtil.getmInstance(myContext).addToRequestQueue(stringRequest);
    }
}
