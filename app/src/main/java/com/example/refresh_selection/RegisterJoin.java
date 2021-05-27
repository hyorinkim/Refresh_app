package com.example.refresh_selection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterJoin extends AppCompatActivity {

    private EditText join_id, join_password, join_name, join_birthday;
    private RadioButton man,woman;
    private RadioGroup radio;
    private Button join_button, check_button;
    private AlertDialog dialog;
    private boolean validate = false;
    private String UserSex;
    static final String[] List_menu={"12:00-14:00","14:00-16:00","16:00-18:00","18:00-20:00","20:00-22:00","22:00-00:00"};
    static final String[] List_menu2={"","","","","",""};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.join);
        //설문조사 먼저?
        ///////////////////////////////////////////////
//        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, List_menu) ;
//
//        ListView listview = (ListView) findViewById(R.id.survey_act_time) ;
//        listview.setAdapter(adapter) ;
//
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            // 코드 계속 ...
//            @Override
//            public void onItemClick(AdapterView parent, View v, int position, long id) {
//
//                // get TextView's Text.
//                String strText = (String) parent.getItemAtPosition(position) ;
//                //배열일까 그냥 하나의 스트링일까... 나중에 바꿀수도
//
//
//                //volley로 데이터베이스에 보냅니다. 회원가입 마지막에서
//
//                // TODO : use strText
//            }
//        }) ;
//
//        //////////////////////장소 카테고리
//        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, List_menu) ;
//
//        ListView listview2 = (ListView) findViewById(R.id.survey_place) ;
//        listview2.setAdapter(adapter2) ;
//
//        listview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            // 코드 계속 ...
//            @Override
//            public void onItemClick(AdapterView parent, View v, int position, long id) {
//
//                // get TextView's Text.
//                String strText = (String) parent.getItemAtPosition(position) ;
//                //배열일까 그냥 하나의 스트링일까... 나중에 바꿀수도
//
//
//                //volley로 데이터베이스에 보냅니다. 회원가입 마지막에서
//
//                // TODO : use strText
//            }
//        }) ;

///////////////////////////////////////////////////////
        //아이디값 찾아주기
        join_id = findViewById( R.id.join_id );
        join_password = findViewById( R.id.join_password );
        join_name = findViewById( R.id.join_name );
        join_birthday = findViewById(R.id.join_birthday);
//        join_sex = findViewById(R.id.join_sex);
        man=findViewById(R.id.man);//성별
        woman=findViewById(R.id.woman);
        radio=(RadioGroup) findViewById(R.id.radio);
        //라디오 그룹 클릭 리스너
        RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(RadioGroup radio, @IdRes int i) {
                if(i == R.id.man){
                        UserSex=man.getText().toString();
                }
                else if(i == R.id.woman){
                    UserSex=woman.getText().toString();
                }
            }
        };
        radio.setOnCheckedChangeListener(radioGroupButtonChangeListener);





        //아이디 중복 체크
        check_button = findViewById(R.id.check_button);
        check_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String UserId = join_id.getText().toString();

                if (validate) {
                    return; //검증 완료
                }

                if (UserId.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterJoin.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }
//                RequestQueue queue = Volley.newRequestQueue(RegisterJoin.this);
//                String url = "http://3.143.147.178:3000/api/user/"+UserId;
//                // Request a string response from the provided URL.
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // Display the first 500 characters of the response string.
////                                textView.setText("Response is: "+ response.substring(0,500));
//                                Log.d("ddd","ddd");
//                            }
//                        },null
//                );

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
//                            Log.d("Response is: "+ response.substring(0,500),"아이디 중복 확인");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterJoin.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                join_id.setEnabled(false); //아이디값 고정
                                validate = true; //검증 완료
                                check_button.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterJoin.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(UserId, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterJoin.this);
                queue.add(validateRequest);
            }
        });


        //회원가입 버튼 클릭 시 수행
        join_button = findViewById( R.id.join_button );
        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String UserId = join_id.getText().toString();
                final String UserPwd = join_password.getText().toString();
                final String UserName = join_name.getText().toString();
                final String UserBirthday = join_birthday.getText().toString();

                  //man이 true 여자가 false니까 하나만 갖고 검사하자.
//                final String UserSex =join_sex.getText().toString();


                //아이디 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterJoin.this);
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //한 칸이라도 입력 안했을 경우
                if (UserId.equals("") || UserPwd.equals("") || UserName.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterJoin.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject( response );
                            boolean success = jsonObject.getBoolean( "success" );

                            //회원가입 성공시
//                            if(UserPwd.equals(PassCk)) {
                                if (success) {
                                    validate=false;
                                    Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", UserName), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterJoin.this, LoginActivity.class);
                                    startActivity(intent);

                                    //회원가입 실패시
                                } else {
                                    validate=false;
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
//                            } else {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterJoin.this);
//                                dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
//                                dialog.show();
//                                return;
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                //서버로 Volley를 이용해서 요청 설문조사결과도 함께
                JoinRequest registerRequest = new JoinRequest( UserId, UserPwd, UserName, UserBirthday, UserSex, responseListener);
                RequestQueue queue = Volley.newRequestQueue( RegisterJoin.this );
                queue.add( registerRequest );
            }
        });
    }
}