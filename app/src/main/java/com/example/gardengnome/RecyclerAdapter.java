package com.example.gardengnome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Pallet> palletList;
    private Context context;


    public RecyclerAdapter(Context context, ArrayList<Pallet> palletList){
        this.context = context;
        this.palletList = palletList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        Pallet pallet = palletList.get(position);

        holder.palletIdTextView.setText(pallet.getPalletID());
        holder.locationTextView.setText(pallet.getLocation());
        holder.skuTextView.setText(String.valueOf(pallet.getSku()));
        holder.quantityTextView.setText(String.valueOf(pallet.getQuantity()));

        holder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDelete(pallet);
            }
        });
    }

    @Override
    public int getItemCount() {
        return palletList.size();
    }

    private void deletePallet(Pallet pallet) {
        Task<Void> reference = FirebaseDatabase.getInstance().getReference().child("Inventory")
                .child(String.valueOf(pallet.getSku()))
                .child("Pallets")
                .child(String.valueOf(pallet.getPalletID())).removeValue();
    }

    private void alertDelete(Pallet pallet) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle("Delete Warning")
                .setMessage("You are about to delete a pallet.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePallet(pallet);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView palletIdTextView, quantityTextView, skuTextView, locationTextView;
        ImageButton deleteImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            palletIdTextView = (TextView) itemView.findViewById(R.id.palletIDTextView);
            quantityTextView = (TextView) itemView.findViewById(R.id.quantityTextView);
            skuTextView = (TextView) itemView.findViewById(R.id.skuTextView);
            locationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
            deleteImageButton = (ImageButton) itemView.findViewById(R.id.palletDeleteButton);
        }
    }
}
