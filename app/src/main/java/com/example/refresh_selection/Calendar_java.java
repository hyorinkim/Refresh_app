package com.example.refresh_selection;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import androidx.appcompat.app.AppCompatActivity;

public class Calendar_java extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_kotlin);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        materialDatePicker.show(getSupportFragmentManager(),"MATERIAL_DATE_PICKER");

        //datePicker에 해당하는 프래그먼트 있어야한다.
//        DatePickerFragment DPF= new DatePickerFragment();
//        DPF.show(getSupportFragmentManager(),"MATERIAL_DATE_PICKER");


        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        Log.d("date",materialDatePicker.getHeaderText());
                    }
                });
//        materialDatePicker.addOnPositiveButtonClickListener(
//                new MaterialPickerOnPositiveButtonClickListener() {
//                    @Override
//                    public void onPositiveButtonClick(Object selection) {
//                        Log.d("date",materialDatePicker.getHeaderText());
//                    }
//                });

    }


}
