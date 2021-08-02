package com.example.gardengnome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OHChangeRecyclerAdapter extends RecyclerView.Adapter<OHChangeRecyclerAdapter.ViewHolder>{

    private ArrayList<OHChange> ohChangeList;
    private Context context;

    public OHChangeRecyclerAdapter(ArrayList<OHChange> ohChangeList, Context context) {
        this.ohChangeList = ohChangeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.oh_list_item, null);
        return new OHChangeRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OHChangeRecyclerAdapter.ViewHolder holder, int position) {

        OHChange ohChange = ohChangeList.get(position);

        holder.palletIDTextView.setText(ohChange.getPalletID());
        holder.changedByTextView.setText(ohChange.getChangedBy());
        holder.previousQuantityTextView.setText(String.valueOf(ohChange.getPreviousQuantity()));
        holder.newQuantityTextView.setText(String.valueOf(ohChange.getNewQuantity()));
    }

    @Override
    public int getItemCount() {
        return ohChangeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView palletIDTextView, changedByTextView, previousQuantityTextView, newQuantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            palletIDTextView = (TextView) itemView.findViewById(R.id.palletIDTextView);
            changedByTextView = (TextView) itemView.findViewById(R.id.changedByTextView);
            previousQuantityTextView = (TextView) itemView.findViewById(R.id.previousQuantityTextView);
            newQuantityTextView = (TextView) itemView.findViewById(R.id.newQuantityTextView);
        }
    }
}
