package com.example.refresh_selection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class bottom_nav extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView; // 바텀네비게이션 뷰
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private FragmentMain fragmentMain;
    private FragmentMap fragmentMap;
    private FragmentProfile fragmentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.HomeItem:
                        setFrag(0);
                        break;
                    case R.id.MapItem:
                        setFrag(1);
                        break;
                    case R.id.MyPageItem:
                        setFrag(2);
                        break;
                }
                return true;
            }
        });

        fragmentMain = new FragmentMain();
        fragmentMap = new FragmentMap();
        fragmentProfile = new FragmentProfile();

        setFrag(0); // 첫화면 설정
    }
    // 프래그먼트 교체가 일어나는 메서드
    private void setFrag(int n){

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

        switch (n){
            case 0:
                transaction.replace(R.id.frameLayout, fragmentMain);
                transaction.commit();
                break;
            case 1:
                transaction.replace(R.id.frameLayout, fragmentMap);
                transaction.commit();
                break;
            case 2:
                transaction.replace(R.id.frameLayout, fragmentProfile);
                transaction.commit();
                break;
        }
    }
}