package com.example.refresh_selection;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CalenderFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup calFragmentView =(ViewGroup)inflater.inflate(R.layout.calendar_java, container, false);

        return calFragmentView;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
