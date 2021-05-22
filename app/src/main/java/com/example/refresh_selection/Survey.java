package com.example.refresh_selection;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class Survey extends AppCompatActivity {

    static final String[] List_menu={"12:00-14:00","14:00-16:00","16:00-18:00","18:00-20:00","20:00-22:00","22:00-00:00"};
//활동시간대 선택할 아이템들
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.survey) ;

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, List_menu) ;

        ListView listview = (ListView) findViewById(R.id.survey_act_time) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 코드 계속 ...
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position) ;
                //배열일까 그냥 하나의 스트링일까... 나중에 바꿀수도
                //volley로 데이터베이스에 보냅니다.

                // TODO : use strText
            }
        }) ;
    }
}
