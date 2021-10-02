package com.example.refresh_selection

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient


class Login_kakao: AppCompatActivity(){
    @SuppressLint("WrongViewCast")
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
                startMainActivity()//메인화면으로 넘어가는 함수
            }
        }
        var kakao_login_btn: Button?=null
        kakao_login_btn=findViewById<Button>(R.id.kakao_login_bt)
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
    fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}