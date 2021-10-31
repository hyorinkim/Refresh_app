package com.example.refresh_selection;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CalenderFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    ViewGroup calFragmentView;
    String TAG="MATERIAL_DATE_PICKER";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        calFragmentView =(ViewGroup)inflater.inflate(R.layout.calendar_kotlin, container, false);
//        CalendarView calview = calFragmentView.findViewById(R.id.calView);
//        calview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {//날짜에 변경이 일어날때
//            @Override//날짜 선택이벤트
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                checkDay(year,month,dayOfMonth,"id?");
//            }
//        });
        return calFragmentView;
    }

    private void checkDay(int year, int month, int dayOfMonth, String userId) {//해당날짜에 스케쥴예약 있는지 확인하는 것
        String fname=year+"-"+month+"-"+dayOfMonth+"-"+userId+".txt";
        FileInputStream FIS=null;
        try {
            FIS=calFragmentView.getContext().openFileInput(fname);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void saveSchedule(String Day){//스케쥴 저장 파일에 여행내용저장
        FileOutputStream FOS=null;
        try {
            FOS=calFragmentView.getContext().openFileOutput(Day, calFragmentView.getContext().MODE_NO_LOCALIZED_COLLATORS);
            String content="";//getText
            FOS.write((content).getBytes());
            FOS.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }
}
