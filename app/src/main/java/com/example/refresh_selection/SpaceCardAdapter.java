package com.example.refresh_selection;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceCardAdapter extends RecyclerView.Adapter<SpaceCardAdapter.Viewholder>{
    private Context context;
    private ArrayList<SpaceCard> SpaceCardArrayList;
    HashMap<Integer,Boolean> map = new HashMap<>();

    //생성자
    public SpaceCardAdapter(Context context, ArrayList<SpaceCard> SpaceCardArrayList){
        this.context=context;
        this.SpaceCardArrayList=SpaceCardArrayList;
    }
    @NonNull
    @Override
    public SpaceCardAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpaceCardAdapter.Viewholder holder, int position) {
        SpaceCard card =SpaceCardArrayList.get(position);
        holder.spaceIV.setImageResource(card.getImage());
        holder.spaceNameTV.setText(card.getSpace_name());
        Log.d("space_name",card.getSpace_name());
        holder.spaceDescriptionTV.setText(card.getDescription());
        holder.spaceDescription2TV.setText(card.getDescription2());
        holder.heart_bt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(holder.check_heart==false){
                    holder.heart_bt.setImageResource(R.drawable.heart_bt_active);
                    holder.check_heart=true;//활성화
                    map.put(position,true);//모델이 학습해야할 카드뷰의 위치 저장
                }else{
                    holder.heart_bt.setImageResource(R.drawable.heart_bt);
                    holder.check_heart=false;//비활성화
                    map.put(position,false);// 모델이 학습해야할 카드뷰의 위치 삭제
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return SpaceCardArrayList.size();
    }
    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView spaceIV;
        private TextView spaceNameTV, spaceDescriptionTV,spaceDescription2TV;
        private ImageButton heart_bt,cal_bt;
        private Boolean check_heart=false;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            spaceIV = itemView.findViewById(R.id.space_image);
            spaceNameTV = itemView.findViewById(R.id.space_name);
            spaceDescriptionTV = itemView.findViewById(R.id.space_description);
            spaceDescription2TV = itemView.findViewById(R.id.space_description2);
            heart_bt=itemView.findViewById(R.id.heart_bt);
            cal_bt= itemView.findViewById(R.id.cal_bt);
        }
    }
}
