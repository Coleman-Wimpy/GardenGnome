package com.example.gardengnome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Pallet> palletList;
    private Context context;
    private Dialog dialog;


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

        holder.palletEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { editPallet(pallet); }
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

    private void editPallet(Pallet pallet){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_pallet_layout);

        EditText quantityEditText = (EditText) dialog.findViewById(R.id.quantityEditText);
        TextView palletIDTextView = (TextView) dialog.findViewById(R.id.palletIDTextView);
        TextView skuTextView = (TextView) dialog.findViewById(R.id.skuTextView);
        TextView locationTextView = (TextView) dialog.findViewById(R.id.locationTextView);
        Button updateButton = (Button) dialog.findViewById(R.id.updateButton);

        dialog.show();

        quantityEditText.setText(pallet.getQuantity() + "");
        palletIDTextView.setText(pallet.getPalletID());
        skuTextView.setText(pallet.getSku() + "");
        locationTextView.setText(pallet.getLocation());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Inventory");

                int newQuantity = Integer.parseInt(quantityEditText.getText().toString());

                if(newQuantity == 0) {
                    quantityEditText.setError("Quantity must be greater than 0.");
                    quantityEditText.requestFocus();
                    return;
                }

                reference.child(pallet.getSku() + "").child("Pallets").child(pallet.getPalletID()).child("quantity").setValue(newQuantity).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            dialog.cancel();
                        }
                        else{
                            Toast.makeText(context, "Error Updating Quantity. Try Again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView palletIdTextView, quantityTextView, skuTextView, locationTextView;
        ImageButton deleteImageButton, palletEditButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            palletIdTextView = (TextView) itemView.findViewById(R.id.palletIDTextView);
            quantityTextView = (TextView) itemView.findViewById(R.id.quantityTextView);
            skuTextView = (TextView) itemView.findViewById(R.id.skuTextView);
            locationTextView = (TextView) itemView.findViewById(R.id.locationTextView);
            deleteImageButton = (ImageButton) itemView.findViewById(R.id.palletDeleteButton);
            palletEditButton = (ImageButton) itemView.findViewById(R.id.palletEditButton);
        }
    }
}
