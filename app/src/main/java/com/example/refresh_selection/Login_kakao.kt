package com.example.refresh_selection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import org.json.JSONObject


class Login_kakao: AppCompatActivity(){
    @SuppressLint("WrongViewCast")
    val testUrl = "http://3.143.147.178:3000/api/user/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_kakao)

        // 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                //Login Fail
                Log.e("login", "Kakao Login Failed :", error)
            }
            else if (token != null) {
                //Login Success
                // 사용자 정보 요청 (기본)
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("user info fail", "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        Log.i("user info success", "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"+
                                "\n성별: ${user.kakaoAccount?.gender}"+
                                "\n나이: ${user.kakaoAccount?.ageRange}")

                        val myJson = JSONObject()
                        myJson.put("UserNumber",user.id)//회원번호
                        myJson.put("Sex",user.kakaoAccount?.gender)//성별
                        myJson.put("AgeRange",user.kakaoAccount?.ageRange)//나이범위
                        myJson.put("Image",user.kakaoAccount?.profile?.thumbnailImageUrl)//프로필사진
                        myJson.put("NickName",user.kakaoAccount?.profile?.nickname)//닉네임
                        val requestBody = myJson.toString()
                        /* myJson에 아무 데이터도 put 하지 않았기 때문에 requestBody는 "{}" 이다 */

                        val testRequest = object : StringRequest(Method.POST, testUrl , Response.Listener { response ->
                            println("서버 Response 수신: $response")//가져오기 성공
                        }, Response.ErrorListener { error ->
                            Log.d("ERROR", "서버 Response 가져오기 실패: $error")
                        }) {
                            override fun getBodyContentType(): String {
                                return "application/json; charset=utf-8"
                            }

                            override fun getBody(): ByteArray {
                                return requestBody.toByteArray()
                            }
                            /* getBodyContextType에서는 요청에 포함할 데이터 형식을 지정한다.
                             * getBody에서는 요청에 JSON이나 String이 아닌 ByteArray가 필요하므로, 타입을 변경한다. */
                        }

                        Volley.newRequestQueue(this).add(testRequest)


                    }
                }
                startMainActivity()//메인화면으로 넘어가는 함수
            }
        }
        var kakao_login_btn: AppCompatImageButton?=null
        kakao_login_btn=findViewById<AppCompatImageButton>(R.id.kakao_login_bt)
        kakao_login_btn.setOnClickListener{
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            UserApiClient.instance.run {
                if (isKakaoTalkLoginAvailable(this@Login_kakao)) {
                    loginWithKakaoTalk(this@Login_kakao, callback = callback)
                } else {
                    loginWithKakaoAccount(this@Login_kakao, callback = callback)
                }
            }
        }



    }

    fun testVolley(context: Context, success: (Boolean) -> Unit) {

        val myJson = JSONObject()
//        myJson.put("UserNumber",user.id)//회원번호
//        myJson.put("Sex",user.kakaoAccount?.gender)//성별
//        myJson.put("AgeRange",user.kakaoAccount?.ageRange)//나이범위
//        myJson.put("Image",user.kakaoAccount?.profile?.thumbnailImageUrl)//프로필사진
//        myJson.put("NickName",user.kakaoAccount?.profile?.nickname)//닉네임
        val requestBody = myJson.toString()
        /* myJson에 아무 데이터도 put 하지 않았기 때문에 requestBody는 "{}" 이다 */

        val testRequest = object : StringRequest(Method.POST, testUrl , Response.Listener { response ->
            println("서버 Response 수신: $response")
            success(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "서버 Response 가져오기 실패: $error")
            success(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
            /* getBodyContextType에서는 요청에 포함할 데이터 형식을 지정한다.
             * getBody에서는 요청에 JSON이나 String이 아닌 ByteArray가 필요하므로, 타입을 변경한다. */
        }

        Volley.newRequestQueue(context).add(testRequest)
    }

    fun startMainActivity() {
        startActivity(Intent(this, MainActivity_travel::class.java))
    }

}