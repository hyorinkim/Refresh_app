package com.example.refresh_selection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Survey_place extends AppCompatActivity {

    static final String[] List_menu2={"카페", "음식점", "문화시설", "관광명소", "대형마트","공원"};
    Button commit;
    ArrayList<String> place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_place) ;
        place=new ArrayList<String>();
        //배열로 바꿔줘야하나?
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, List_menu2);
        ListView listview2 = (ListView) findViewById(R.id.survey_place_table);
        listview2.setAdapter(adapter2);
        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                String s = (String) parent.getItemAtPosition(position);
                Log.d("선택한 장소", s);
                // get TextView's Text.
                SparseBooleanArray checkedItems = listview2.getCheckedItemPositions();
                int count = adapter2.getCount();

                for (int i = count - 1; i >= 0; i--) {
                    if (checkedItems.get(i)) {//체크 된 것이면 arraylist에 넣어라
                        place.add((String) parent.getItemAtPosition(i));
                    } else if(place.contains((String) parent.getItemAtPosition(i))) {//체크 안 됐고 arraylist에 있으면 지워라 
                        place.remove((String) parent.getItemAtPosition(i));
                    }
                }
                //배열
                // TODO : use strText
            }
        });
        //확인 버튼 눌렀을때 활동시간대, 장소카테고리 설문조사결과 surveyRequest로 넘겨줌

        //volley로 데이터베이스에 보냅니다. 회원가입 마지막에서?
        //서버로 Volley를 이용해서 요청 설문조사결과도 함께
        commit = findViewById(R.id.survey_commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String act_time =intent.getStringExtra("act_time");
                String UserId= intent.getStringExtra("UserId");
                Log.d("act_time",act_time);
                Log.d("UserId",UserId);

                RequestQueue queue = Volley.newRequestQueue(Survey_place.this);
                String url = "http://3.143.147.178:3000/api/research";
                JSONObject testjson = new JSONObject();
                try {
                    String data= new Gson().toJson(place);
                    testjson.put("ActiveTime",act_time);
                    testjson.put("UserId",UserId);
                    testjson.put("PreferPlace",place.toArray());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, testjson, new Response.Listener() {

                    @Override
                    public void onResponse(Object response) {
                        try {
                                    JSONObject jsonObject = new JSONObject(response.toString());
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success) {//설문조사가 잘 저장됨
                                        Intent intent = new Intent(Survey_place.this, MainActivity.class);
                                        startActivity(intent);//설문조사 저장후 메인으로 넘어감
                                    } else {//설문조사가 안 저장됨
                                        Log.d("survey 저장안됨", success + "");
                                        return;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        Log.d("ddd","ddd");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);

//                RequestQueue queue = Volley.newRequestQueue(Survey_place.this);
//                String url = "http://3.143.147.178:3000/api/research";
////                // Request a string response from the provided URL.
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // Display the first 500 characters of the response string.
////                                textView.setText("Response is: "+ response.substring(0,500));
//                                try {
//                                    JSONObject jsonObject = new JSONObject(response);
//                                    boolean success = jsonObject.getBoolean("success");
//
//                                    if (success) {//설문조사가 잘 저장됨
//                                        Intent intent = new Intent(Survey_place.this, MainActivity.class);
//                                        startActivity(intent);//설문조사 저장후 메인으로 넘어감
//                                    } else {//설문조사가 안 저장됨
//                                        Log.d("survey 저장안됨", success + "");
//                                        return;
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                Log.d("ddd","ddd");
//                            }
//                        },new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                }){
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<>();
//                        String data= new Gson().toJson(place);
//                        params.put("UserId",UserId);
//                        params.put("ActiveTime",act_time);
//                        params.put("PreferPlace",data);
//                        return params;
//                    }
//                };
//                queue.add( stringRequest );
//                Response.Listener<String> responseListener = new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            boolean success = jsonObject.getBoolean("success");
//
//                            if (success) {//설문조사가 잘 저장됨
//                                Intent intent = new Intent(Survey_place.this, MainActivity.class);
//                                startActivity(intent);//설문조사 저장후 메인으로 넘어감
//                            } else {//설문조사가 안 저장됨
//                                Log.d("survey 저장안됨", success + "");
//                                return;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                };

//                SurveyRequest surveyRequest = new SurveyRequest(UserId,act_time, place, responseListener);//여기에 로그인 성공한 아이디도 같이 줘야하나?
//                RequestQueue queue = Volley.newRequestQueue(Survey_place.this);
//                queue.add(surveyRequest);
            }
        });
    }

}
