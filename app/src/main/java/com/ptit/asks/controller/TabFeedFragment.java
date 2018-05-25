package com.ptit.asks.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.ptit.asks.R;
import com.ptit.asks.adapter.QuestionListAdapter;
import com.ptit.asks.model.Question;
import com.ptit.asks.util.AsksUtil;

public class TabFeedFragment extends Fragment {

    /*
    Fragment cho việc Tab newfeed, hiển thị ra list các câu hỏi đã được người dùng publish,
    giao diện ứng với fragment_tab_feed.xml
     */

    public static final String ARG_PAGE = "ARG_PAGE";
    public int mPageNo;

    private Spinner spinnerQuestion;
    private int currentPage = 1;
    private boolean isLoading = true;
    private int defaultFilter = 0;

    //Dùng để sử dụng QuestionListAdapter với ListView các câu hỏi
    private ListView lvQuestion;
    private QuestionListAdapter adapter;
    private List<Question> myArrayQuestion;

    //API url
    private String getQuestionUrl = "https://laravel-demo-deploy.herokuapp.com/api/v0/questions";

    //
    public static TabFeedFragment newInstance(int pageNo) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        TabFeedFragment fragment = new TabFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
        myArrayQuestion = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_feed, container, false);

        //Ánh xạ các thành phần trong view
        spinnerQuestion = (Spinner) view.findViewById(R.id.spinner);
        lvQuestion = (ListView) view.findViewById(R.id.lv_question);

        if (myArrayQuestion.size() < 1) {
            getQuestionList(1, defaultFilter);
        } else {
            adapter = new QuestionListAdapter(getContext(), myArrayQuestion);
            lvQuestion.setAdapter(adapter);
        }

        //Xử lý sự kiện nhấn vào câu hỏi trong ListView
        lvQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idQuestion = myArrayQuestion.get(position).getId() + "";
                Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
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
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.questionSwipe);
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
                    defaultFilter = 1;
                    myArrayQuestion.clear();
                    getQuestionList(1, defaultFilter);
                } else {
                    defaultFilter = 0;
                    myArrayQuestion.clear();
                    getQuestionList(1, defaultFilter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //Hàm gọi API xử lý việc lấy danh sách câu hỏi
    private void getQuestionList(int pageNo, int status) {
        String addUrl;
        if (status == 1) {
            //danh sách câu hỏi đã được trả lời
            addUrl = "/solve";
        } else {
            //danh sách câu hỏi chưa được trả lời
            addUrl = "";
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.getQuestionUrl + addUrl + "?page=" + pageNo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        JSONArray data = res.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject q;
                            q = data.getJSONObject(i);
                            String username = q.getJSONObject("user").getJSONObject("data").getString("username");
                            myArrayQuestion.add(new Question(
                                    q.getInt("id"),
                                    q.getString("title"),
                                    username,
                                    q.getInt("voteCount"),
                                    q.getString("updatedAt"),
                                    q.getInt("status")
                            ));
                        }
                        if (myArrayQuestion.size() < 17) {
                            adapter = new QuestionListAdapter(getContext(), myArrayQuestion);
                            lvQuestion.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        isLoading = false;
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Unknow error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        AsksUtil.getmInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}
