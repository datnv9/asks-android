package www.seotoolzz.com.Ask.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import www.seotoolzz.com.Ask.R;
import www.seotoolzz.com.Ask.util.AsksUtil;

import static android.content.Context.MODE_PRIVATE;

public class TabProfileFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    public int mPageNo;
    public View view;

    //
    public static TabProfileFragment newInstance(int pageNo) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        TabProfileFragment fragment = new TabProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_profile, container, false);

        //Ánh xạ các thành phần trong view
        Button btnToLogin = (Button) view.findViewById(R.id.btnLogin);
        Button btnToSignUp = (Button) view.findViewById(R.id.btnToSignUp);
        Button btnLogout = (Button) view.findViewById(R.id.btnLogout);
        Button btnMyQuestion = (Button) view.findViewById(R.id.btnMyQuestion);

        //Gọi hàm xử lý sự kiện các nút bấm
        btnToLogin.setOnClickListener(this);
        btnToSignUp.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnMyQuestion.setOnClickListener(this);

        //Kiểm tra đã đăng nhập hay chưa
        if (AsksUtil.isLogin((AppCompatActivity) getActivity())) {
            SharedPreferences sharePrefs = getActivity().getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE);
            String username = sharePrefs.getString("username", null);
            String userEmail = sharePrefs.getString("email", null);

            TextView edtUsername = (TextView) view.findViewById(R.id.username);
            TextView edtUserEmail = (TextView) view.findViewById(R.id.userEmail);

            //Hiển thị các thông tin và chức năng khi đã đăng nhập
            edtUsername.setText(username);
            edtUserEmail.setText(userEmail);

            loginSuccess();
        }

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

    public void loginSuccess() {
        LinearLayout layOutLoginSuccess = (LinearLayout) view.findViewById(R.id.layOutLoginSucess);
        layOutLoginSuccess.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        //Hàm xử lý sự kiện các nút bấm
        switch (v.getId()) {
            case R.id.btnLogin:
                Intent changeView = new Intent(getActivity(), ProfileLoginActivity.class);
                startActivity(changeView);
                break;
            case R.id.btnToSignUp:
                Intent changeViewSignUp = new Intent(getActivity(), ProfileSignUpActivity.class);
                startActivity(changeViewSignUp);
                break;
            case R.id.btnLogout:
                SharedPreferences.Editor sharePrefs = getActivity().getApplicationContext().getSharedPreferences("ASKS", MODE_PRIVATE).edit();
                sharePrefs.clear();
//                sharePrefs.commit();
                sharePrefs.apply();
                Toast.makeText(getContext(), "Logout success", Toast.LENGTH_SHORT).show();
                Intent changeViewMain = new Intent(getActivity(), MainActivity.class);
                startActivity(changeViewMain);
                break;
            case R.id.btnMyQuestion:
                Intent changeViewQuestion = new Intent(getActivity(), QuestionOfUserActivity.class);
                startActivity(changeViewQuestion);
                break;
        }
    }
}
