package com.example.refresh_selection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText login_id, login_password;
    private Button login_button, join_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.login );

        login_id = findViewById( R.id.login_id );
        login_password = findViewById( R.id.login_password );

        join_button = findViewById( R.id.join_button);
        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( LoginActivity.this, RegisterJoin.class );
                startActivity( intent );
            }
        });


        login_button = findViewById( R.id.login_button );
        login_button.setOnClickListener( new View.OnClickListener() {//로그인 버튼클릭
            @Override
            public void onClick(View view) {
                boolean exist;
                String UserId = login_id.getText().toString();
                String UserPwd = login_password.getText().toString();
//아이디 비번 읽어오기
//                Intent intent = new Intent( LoginActivity.this, MainActivity.class );
//                startActivity( intent );
                Login(UserId, UserPwd);


//                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
//                String url = "http://3.143.147.178:3000/api/user/login";
////                // Request a string response from the provided URL.
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject( response );
//                                    boolean success = jsonObject.getBoolean( "success" );
//                                    //로그인 할때 성공 줄때 설문조사 존재여부도 같이 넘겨 줄 순 없나? 일단 보류 회원가입에서 설문조사 하는 것
//                                    boolean survey = jsonObject.getBoolean("SurveyExist");
//
//                                    if(success) {//로그인 성공시
//
//                                        if(survey){//설문조사결과 있을 때 로그인에서 메인화면으로 가기
//                                            Intent intent = new Intent( LoginActivity.this, MainActivity.class );
//                                            startActivity( intent );//로그인 성공해서 메인 화면으로 간다.
//                                        }else{//설문조사 결과 없을때 로그인에서 설문조사 화면으로 가기
//                                            Intent intent =new Intent(LoginActivity.this,Survey.class);
//                                            intent.putExtra("UserId",UserId);
//                                            startActivity(intent);
//                                        }
//
//                                    } else {//로그인 실패시
//                                        Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
//                                        return;
//                                    }
//
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
//                        params.put("UserId",UserId);
//                        params.put("UserPwd",UserPwd);
//                        return params;
//                    }
//                };
//
//                queue.add( stringRequest );
//                Response.Listener<String> responseListener = new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject( response );
//                            boolean success = jsonObject.getBoolean( "success" );
//                            //로그인 할때 성공 줄때 설문조사 존재여부도 같이 넘겨 줄 순 없나? 일단 보류 회원가입에서 설문조사 하는 것
//                            boolean survey = jsonObject.getBoolean("SurveyExist");
//
//                            if(success) {//로그인 성공시
////                                Toast.makeText( getApplicationContext(), String.format("%s님 환영합니다.", UserName), Toast.LENGTH_SHORT ).show();
//                                if(survey){//설문조사결과 있을 때 로그인에서 메인화면으로 가기
//                                    Intent intent = new Intent( LoginActivity.this, MainActivity.class );
//                                    startActivity( intent );//로그인 성공해서 메인 화면으로 간다.
//                                }else{//설문조사 결과 없을때 로그인에서 설문조사 화면으로 가기
//                                    Intent intent =new Intent(LoginActivity.this,Survey.class);
//                                    intent.putExtra("UserId",UserId);
//                                    startActivity(intent);
//                                }
////                                intent.putExtra( "UserId", UserId );
////                                intent.putExtra( "UserPwd", UserPwd );
////                                intent.putExtra( "UserName", UserName );// 전달을 해줘야하는 곳이 있나?
//
//                            } else {//로그인 실패시
//                                Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
//                                return;
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//                LoginRequest loginRequest = new LoginRequest( UserId, UserPwd, responseListener );
//                RequestQueue queue = Volley.newRequestQueue( LoginActivity.this );
//                queue.add( loginRequest );

//                //로그인 성공후 설문조사 결과 확인
//                Response.Listener<String> responseListener_survey = new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject( response );
//                            exist = jsonObject.getBoolean( "exist" );
//
//                            if(exist) {//설문조사 결과 있을 때
//                                //로그인 성공일때 로그인 화면에서 main화면으로
//                            } else {//설문조사 결과 없을 때
//                                //로그인 성공일때 로그인화면에서 초기설문조사
//
//                                return;
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
////                LoginRequest loginRequest = new LoginRequest( UserId, responseListener );
////                RequestQueue queue = Volley.newRequestQueue( LoginActivity.this );
////                queue.add( loginRequest );


            }
        });
    }

    private void Login(String userId, String userPwd) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String url = "http://3.143.147.178:3000/api/user/login";
        JSONObject testjson = new JSONObject();
        try {
            testjson.put("UserId", userId);
            testjson.put("UserPwd", userPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, testjson, new Response.Listener() {

            @Override
            public void onResponse(Object response) {
                try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            boolean success = jsonObject.getBoolean( "success" );
                            //로그인 할때 성공 줄때 설문조사 존재여부도 같이 넘겨 줄 순 없나? 일단 보류 회원가입에서 설문조사 하는 것
                            boolean survey = jsonObject.getBoolean("SurveyExist");

                            if(success) {//로그인 성공시

                                if(survey){//설문조사결과 있을 때 로그인에서 메인화면으로 가기
                                    Intent intent = new Intent( LoginActivity.this, MainActivity.class );
                                    startActivity( intent );//로그인 성공해서 메인 화면으로 간다.
                                }else{//설문조사 결과 없을때 로그인에서 설문조사 화면으로 가기
                                    Intent intent =new Intent(LoginActivity.this, Survey.class);
                                    intent.putExtra("UserId", userId);
                                    startActivity(intent);
                                }

                            } else {//로그인 실패시
                                //Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
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
    }
}