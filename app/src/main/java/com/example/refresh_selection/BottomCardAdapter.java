package com.example.refresh_selection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BottomCardAdapter extends RecyclerView.Adapter<BottomCardAdapter.Viewholder> {
    private Context context;
    private ArrayList<BottomCard> BottomCardArrayList;

    //생성자
    public BottomCardAdapter(Context context, ArrayList<BottomCard> BottomCardArrayList){
        this.context=context;
        this.BottomCardArrayList=BottomCardArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_cardview,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        BottomCard card = BottomCardArrayList.get(position);
        holder.btmIV.setImageResource(card.getImg());
        holder.btmNameTV.setText(card.getBottom_space_name());
        holder.btmDesTV.setText(card.getBottom_space_description());
        holder.btmDis.setText(card.getDistance());
        holder.btmPrice.setText(card.getPrice());
    }

    @Override
    public int getItemCount() {
        return BottomCardArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView btmIV;
        private TextView btmNameTV,btmDesTV,btmDis,btmPrice;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            btmIV= itemView.findViewById(R.id.btmIV);
            btmNameTV=itemView.findViewById(R.id.bottom_space_name);
            btmDesTV=itemView.findViewById(R.id.bottom_space_description);
            btmDis=itemView.findViewById(R.id.bottom_space_distance);
            btmPrice=itemView.findViewById(R.id.bottom_space_price);

        }
    }
}
