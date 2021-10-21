package com.example.refresh_selection;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class Calendar_java extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_kotlin);

        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //date.setText(dayOfMonth+"/" + (month+1) + "/" + year);
                Log.d("date",dayOfMonth+"/"+(month+1)+"/"+year);
            }
        }, mYear, mMonth, mDay);
//        datePickerDialog.show();


//        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
//        materialDateBuilder.setTitleText("SELECT A DATE");
//
//        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
//        materialDatePicker.show(getSupportFragmentManager(),"MATERIAL_DATE_PICKER");

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
