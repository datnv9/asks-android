package com.ptit.asks.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import com.ptit.asks.util.AsksUtil;

public class MainActivity extends AppCompatActivity {

    /*
    Activity mở đầu của ứng dụng, gồm 3 Tab làm việc chính, và nút bấm thêm câu hỏi,
    giao diện ứng với activity_main.xml
     */

    //Nút thêm câu hỏi
    FloatingActionButton btnFab;

    //API url
    private String getUserUrl = "http://laravel-demo-deploy.herokuapp.com/api/v0/auth/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Kiểm tra tình trạng đăng nhập
        if (AsksUtil.isLogin((AppCompatActivity) MainActivity.this)) {
            android.content.SharedPreferences sharePrefs = MainActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
            if (sharePrefs.getString("username", "EMPTY").equals("EMPTY")) {
                getUserInfo();
            }
        }

        //Ánh xạ nút thêm câu hỏi
        btnFab = (FloatingActionButton) findViewById(R.id.fab);

        //Xử lý sự kiện khi nhấn nút thêm câu hỏi
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AsksUtil.isLogin(MainActivity.this)) {
                    Toast.makeText(getApplicationContext(), "Please login before do this", Toast.LENGTH_SHORT).show();
                } else {
                    //Chuyển sang Activity thêm câu hỏi
                    Intent changeView = new Intent(MainActivity.this, QuestionCreateActivity.class);
                    startActivity(changeView);
                }

            }
        });

        //Xử dụng ViewPager xử lý việc chuyển qua lại giữa các Tab
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
        }
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(viewPager);
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(pagerAdapter.getTabView(i));
            }
            mTabLayout.getTabAt(0).getCustomView().setSelected(true);
        }

    }

    //Hàm gọi API xử lý lấy thông tin người dùng đang đăng nhập
    private void getUserInfo() {
        com.android.volley.toolbox.StringRequest stringRequest = new StringRequest(Request.Method.GET, getUserUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject res = new JSONObject(response);
                    int code = res.getJSONObject("meta").getInt("status");
                    if (code == 700) {
                        JSONObject data = res.getJSONObject("data");
                        SharedPreferences.Editor sharePrefs = MainActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE).edit();
                        sharePrefs.putInt("userId", data.getInt("id"));
                        sharePrefs.putString("username", data.getString("username"));
                        sharePrefs.putString("email", data.getString("email"));
                        sharePrefs.putString("role", data.getString("role"));
                        sharePrefs.commit();
                    } else {
                        Toast.makeText(MainActivity.this.getApplicationContext(), res.getJSONObject("meta").getJSONObject("message").getString("main"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() == null) {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Unknow error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences sharePrefs = MainActivity.this.getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
                params.put("Authorization", sharePrefs.getString("token", null));
                return params;
            }
        };
        AsksUtil.getmInstance(MainActivity.this).addToRequestQueue(stringRequest);
    }

    //Lớp Adapter kế thừa FragmentPagerAdapter để định nghĩa các Tab
    private class MyPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;

        //Tab
        private final String[] mTabsTitle = {"Feed", "Search", "User"};
        //Tab icon
        private int[] mTabsIcons = {R.drawable.feed, R.drawable.search, R.drawable.user};

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        View getTabView(int position) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.toolbar, null);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(mTabsIcons[position]);
            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return TabFeedFragment.newInstance(1);
                case 1:
                    return TabSearchFragment.newInstance(2);
                case 2:
                    return TabProfileFragment.newInstance(3);
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }
}
