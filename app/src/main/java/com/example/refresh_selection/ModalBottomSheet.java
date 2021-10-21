package com.example.refresh_selection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ModalBottomSheet extends BottomSheetDialogFragment {
    String TAG = "bottomSheet";
    private BottomSheetListener mListener;
    private View view;
    //바텀 시트 안에 카드뷰
    BottomCardAdapter bottomCardAdapter;
    private RecyclerView bottomRV;
    private ArrayList<BottomCard> bottomCardArrayList;//카드 정보 저장

    public ModalBottomSheet(){//empty constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mListener = (BottomSheetListener) getContext();
        view = inflater.inflate(R.layout.bottom_sheet, container, false);
        makeMapSpaceCard(view);
        return view;
    }

    //장소 카드 만듣
    public void makeMapSpaceCard(View mainview) {
        bottomRV = (RecyclerView) mainview.findViewById(R.id.bottom_sheet_recycleview);//activitiy_map : bottomsheet layout
        bottomRV.setHasFixedSize(true);
        bottomCardArrayList= new ArrayList<>();
        bottomCardArrayList.add(new BottomCard("대청호1","호수가 아름답다.","300m","1000원",R.drawable.test_img));
        bottomCardArrayList.add(new BottomCard("대청호1","호수가 아름답다.","300m","1000원",R.drawable.test_img));
        bottomCardArrayList.add(new BottomCard("대청호1","호수가 아름답다.","300m","1000원",R.drawable.test_img));
//        Context con = mainview.getChildAt(1).getContext();
//        Log.d("context",con+"");
        bottomCardAdapter =new BottomCardAdapter(mainview.getContext(),bottomCardArrayList);//content가 문제인거 같은데..
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainview.getContext(), LinearLayoutManager.VERTICAL, false);
        bottomRV.setLayoutManager(linearLayoutManager);
        bottomRV.setAdapter(bottomCardAdapter);
    }

    public interface BottomSheetListener {
        void onButtonClicked();
    }
}
