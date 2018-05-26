package com.ptit.asks.controller;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ptit.asks.R;
import com.ptit.asks.model.Search;

public class TabSearchFragment extends Fragment {

    Button btnStartDate,btnEndDate,btnSearch;
    TextView textStartDate, textEndDate;
    EditText editTextSearchTitle,editTextAskBy;
    private FragmentActivity myContext;

    public static final String ARG_PAGE = "ARG_PAGE";
    public int mPageNo;
    public View view;

    //
    public static TabSearchFragment newInstance(int pageNo) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        TabSearchFragment fragment = new TabSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_search, container, false);
        btnStartDate = (Button)view.findViewById(R.id.btnStartDate);
        btnEndDate = (Button)view.findViewById(R.id.btnEndDate);
        editTextSearchTitle = (EditText)view.findViewById(R.id.editTextSearchTitle);
        editTextAskBy = (EditText)view.findViewById(R.id.editTextAskBy);
        textStartDate = (TextView)view.findViewById(R.id.textStartDate);
        textEndDate = (TextView)view.findViewById(R.id.textEndDate);
        btnSearch = (Button)view.findViewById(R.id.btnSearch);

        textStartDate.setText("");
        textEndDate.setText("");

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogfragment = new DatePickerDialogStartDate();
                android.app.FragmentManager fragManager = myContext.getFragmentManager();
                dialogfragment.show(fragManager ,"Select Start Date");
            }
        });
        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogfragment = new DatePickerDialogEndDate();
                android.app.FragmentManager fragManager = myContext.getFragmentManager();
                dialogfragment.show(fragManager ,"Select End Date");
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String QuestionTitle = editTextSearchTitle.getText().toString();
                String AskBy = editTextAskBy.getText().toString();
                String StartDate = textStartDate.getText().toString();
                String EndDate = textEndDate.getText().toString();
                Search searchModel = new Search(QuestionTitle,AskBy,StartDate,EndDate);
                Intent changeViewResult = new Intent(getActivity(), SearchResultActivity.class);
                changeViewResult.putExtra("searchModel", searchModel);
                startActivity(changeViewResult);
            }
        });

        return view;
    }

    public static class DatePickerDialogStartDate extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            String textStartDate = String.valueOf(day)+ ":" + String.valueOf(month+1) + ":" + String.valueOf(year);
            Toast.makeText(getActivity(),textStartDate,Toast.LENGTH_LONG).show();
            TextView textview = (TextView)getActivity().findViewById(R.id.textStartDate);
            textview.setText(textStartDate);

        }
    }

    public static class DatePickerDialogEndDate extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day){
            String textEndDate = String.valueOf(day)+ ":" + String.valueOf(month+1) + ":" + String.valueOf(year);
            Toast.makeText(getActivity(),textEndDate,Toast.LENGTH_LONG).show();
            TextView textview = (TextView)getActivity().findViewById(R.id.textEndDate);
            textview.setText(textEndDate);

        }
    }

}

