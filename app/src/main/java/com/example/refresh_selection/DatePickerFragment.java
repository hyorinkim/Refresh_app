package com.example.refresh_selection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.datepicker.MaterialDatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
    String TAG = "MATERIAL_DATE_PICKER";
    View view;
   MaterialDatePicker materialDatePicker;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mListener = (BottomSheetListener) getContext();
        view = inflater.inflate(R.layout.calendar_fragment, container, false);
        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");

        materialDatePicker = materialDateBuilder.build();
        return view;
    }
    public MaterialDatePicker getPicker(){
        return materialDatePicker;
    }
}
