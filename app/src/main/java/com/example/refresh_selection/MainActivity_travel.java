package com.example.refresh_selection;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity_travel extends AppCompatActivity implements NavigationHost {

    private BottomNavigationView bottomNavigationView; // 바텀네비게이션 뷰
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private FragmentMain fragmentMain;//프레임 레이아웃을 액티미티 메인 xml
    private FragmentMap fragmentMap;//프레임 레이아웃을 액티비티 맵 xml
    private FragmentProfile fragmentProfile;//프레임 레이아웃을 액티비티 프로파일 xml 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_main);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.trip_navigationView);//바텀 네비게이션
        //바텀네비게이션 선택한 아이템이 있을때
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


    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}
