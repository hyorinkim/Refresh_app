package com.example.refresh_selection;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Date;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

public class Calendar_java extends AppCompatActivity {
    DatePicker cal_picker;
    ImageButton schedule_add;
    ImageView select_date;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_kotlin);

        MaterialDatePicker.Builder<Pair<Long,Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        Pair p= Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(),
                MaterialDatePicker.todayInUtcMilliseconds());
        materialDateBuilder.setSelection(p);
        materialDateBuilder.setTheme(R.style.Theme_App);
//

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        select_date= findViewById(R.id.select_date);
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(),"MATERIAL_DATE_PICKER");
            }
        });

//        cal_picker=findViewById(R.id.cal_picker);//달력 picker 가져옴
        schedule_add=findViewById(R.id.addPlaceToSchedule);//스케쥴 추가 버튼 가져옴
        schedule_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long,Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long,Long> selection) {
                Long startDate= selection.first;
                Long endDate= selection.second;

                Date start=new Date(startDate);
                Date end = new Date(endDate);
                Log.d("startDate",start.toString());
                Log.d("endDate",end.toString());
                //확인 버튼 눌렀을때?
            }
        });
//
//        cal_picker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
//            @Override
//            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//
//            }
//        });

//        Calendar c = Calendar.getInstance();
//        int mYear = c.get(Calendar.YEAR);
//        int mMonth = c.get(Calendar.MONTH);
//        int mDay = c.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                //date.setText(dayOfMonth+"/" + (month+1) + "/" + year);
//                Log.d("date",dayOfMonth+"/"+(month+1)+"/"+year);
//            }
//        }, mYear, mMonth, mDay);
//        datePickerDialog.show();





        //datePicker에 해당하는 프래그먼트 있어야한다.
//        DatePickerFragment DPF= new DatePickerFragment();
//        DPF.show(getSupportFragmentManager(),"MATERIAL_DATE_PICKER");


//        materialDatePicker.addOnPositiveButtonClickListener(
//                new MaterialPickerOnPositiveButtonClickListener() {
//                    @Override
//                    public void onPositiveButtonClick(Object selection) {
//                        Log.d("date",materialDatePicker.getHeaderText());
//                    }
//                });
//        materialDatePicker.addOnPositiveButtonClickListener(
//                new MaterialPickerOnPositiveButtonClickListener() {
//                    @Override
//                    public void onPositiveButtonClick(Object selection) {
//                        Log.d("date",materialDatePicker.getHeaderText());
//                    }
//                });

    }


}
