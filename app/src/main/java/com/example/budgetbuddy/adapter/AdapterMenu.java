package com.example.budgetbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetbuddy.R;


public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.MyViewHolder> {

    Context context;
    String[] data;
    OnRcvListener onRcvListener;
    public AdapterMenu(Context context, String[] data, OnRcvListener onRcvListener) {
        this.context = context;
        this.data = data;
        this.onRcvListener = onRcvListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_menu, parent, false);

        return new MyViewHolder(v, onRcvListener);

    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMenu.MyViewHolder holder, int position) {

        holder.txtMenuList.setText(data[position]);

    }

    @Override
    public int getItemCount() {
        return data.length;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtMenuList;
        public OnRcvListener onRcvListener;
        public MyViewHolder(View view,OnRcvListener onRcvListener) {
            super(view);
            this.onRcvListener = onRcvListener;
            txtMenuList = view.findViewById(R.id.txtTitle);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRcvListener.onMenuClick(getAdapterPosition());
        }
    }

    public interface OnRcvListener{
        void onMenuClick(int position);
    }

}
