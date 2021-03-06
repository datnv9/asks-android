package com.ptit.asks.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.ptit.asks.R;
import com.ptit.asks.model.Question;

public class QuestionListAdapter extends BaseAdapter {

    /*
    Adapter chuyển đổi danh sách các câu hoit về dạng hiển thị định nghĩa trong
    item_question_list.xml và đưa vào listview các câu hỏi
     */

    private Context myContext;
    private List<Question> myQuestion;

    public QuestionListAdapter(Context myContext, List<Question> myQuestion) {
        this.myContext = myContext;
        this.myQuestion = myQuestion;
    }

    @Override
    public int getCount() {
        return myQuestion.size();
    }

    @Override
    public Object getItem(int position) {
        return myQuestion.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        @SuppressLint
                ("ViewHolder") View v = View.inflate(myContext, R.layout.item_question_list, null);

        //Ánh xạ các thành phần trong view
        TextView tvTitle = (TextView) v.findViewById(R.id.txtTitle);
        TextView tvUserName = (TextView) v.findViewById(R.id.txtUserName);
        TextView tvNumberVote = (TextView) v.findViewById(R.id.txtVoteNumber);
        TextView tvDate = (TextView) v.findViewById(R.id.createDate);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.voteBgr);

        //Gán nội dung các đoạn text
        tvTitle.setText(myQuestion.get(position).getTitle());
        tvUserName.setText("Ask by: " + myQuestion.get(position).getUsername());
        tvNumberVote.setText(myQuestion.get(position).getVote() + "");
        tvDate.setText(myQuestion.get(position).getDate());

        if (myQuestion.get(position).isSolve() == 2) {
            tvNumberVote.setTextColor(Color.parseColor("#00C851"));
        }
        if (myQuestion.get(position).isSolve() == 0) {
            tvNumberVote.setTextColor(Color.parseColor("#FFBB33"));
        }

        v.setTag(myQuestion.get(position).getId());

        return v;
    }

}
