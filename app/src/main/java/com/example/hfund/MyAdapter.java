package com.example.hfund;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Chits> itemList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        private ConstraintLayout main;
        public MyViewHolder(final View parent) {
            super(parent);
            title = (TextView) parent.findViewById(R.id.title);
            subtitle = (TextView) parent.findViewById(R.id.subtitle);
            main = (ConstraintLayout) parent.findViewById(R.id.main);
            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(getPosition()), Toast.LENGTH_SHORT).show();

                    Chits chits=itemList.get(getPosition());
                    Intent intent =new Intent(itemView.getContext(),DetailChitActivity.class);
                    intent.putExtra("chitId",chits.getCreatedOn().toString());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
    public MyAdapter(List<Chits>itemList){
        this.itemList=itemList;
    }
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Chits row=itemList.get(position);
        Log.d("commodity : ",row.getName()+" "+row.getMonths());
        String name=row.getName();
        Long months=row.getMonths();
        holder.title.setText(name);
        holder.subtitle.setText(months+"");

    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }
}