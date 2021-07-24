package com.example.gardengnome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddSKUActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Button logoutButton, addPalletButton;
    private EditText skuEditText, quantityEditText;
    private TextView userName, palletIDPlaceholder;
    private Spinner locationSpinner, rowSpinner, colSpinner, heightSpinner;
    private ProgressBar progressBar;
    private LinearLayout heightSpinnerLayout;
    FirebaseUser user;
    DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_skuactivity);

        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AddSKUActivity.this, MainActivity.class));
            }
        });

        addPalletButton = (Button) findViewById(R.id.addPalletButton);
        addPalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPallet();
            }
        });

        skuEditText = (EditText) findViewById(R.id.skuEditText);
        quantityEditText = (EditText) findViewById(R.id.quantityEditText);
        userName = (TextView) findViewById(R.id.userTextView);
        getUser();

        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        locationSpinner.setOnItemSelectedListener(this);

        rowSpinner = (Spinner) findViewById(R.id.rowSpinner);
        colSpinner = (Spinner) findViewById(R.id.columnSpinner);
        heightSpinner = (Spinner) findViewById(R.id.heightSpinner);
        progressBar = (ProgressBar) findViewById(R.id.addSKUProgressBar);

        String[] locationList = getResources().getStringArray(R.array.locationList);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, locationList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        rowSpinner  = (Spinner) findViewById(R.id.rowSpinner);
        colSpinner = (Spinner) findViewById(R.id.columnSpinner);

        heightSpinner = (Spinner) findViewById(R.id.heightSpinner);
        String[] heightList = getResources().getStringArray(R.array.height);
        ArrayAdapter adapterHeight = new ArrayAdapter(this, android.R.layout.simple_spinner_item, heightList);
        adapterHeight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightSpinner.setAdapter(adapterHeight);

        heightSpinnerLayout = (LinearLayout) findViewById(R.id.heightSpinnerLayout);

        reference = FirebaseDatabase.getInstance().getReference("Inventory");

        palletIDPlaceholder = (TextView) findViewById(R.id.palletIDPlaceholder);
        getPalletID();
    }

    private void addPallet() {

        String location, currentUser, palletID;
        int quantity, sku;

        palletID = palletIDPlaceholder.getText().toString();
        Toast.makeText(this, palletID, Toast.LENGTH_SHORT).show();

        currentUser = userName.getText().toString();

        sku = Integer.parseInt(skuEditText.getText().toString());
        quantity = Integer.parseInt(quantityEditText.getText().toString());

        if(sku == 0) {
            skuEditText.setError("Please Enter a SKU.");
            skuEditText.requestFocus();
            return;
        }

        if(quantity == 0) {
            quantityEditText.setError("Please Enter Quantity.");
            quantityEditText.requestFocus();
            return;
        }

        if(palletID == null && !palletID.isEmpty()) {
            Toast.makeText(AddSKUActivity.this, palletID + "", Toast.LENGTH_LONG).show();
            return;
        }
        else {

            if (heightSpinnerLayout.getVisibility() == View.GONE) {
                location = locationSpinner.getSelectedItem().toString() + "-" + rowSpinner.getSelectedItem().toString() + "-" + colSpinner.getSelectedItem().toString();
                Pallet pallet = new Pallet(palletID, sku, quantity, location, currentUser);

                progressBar.setVisibility(View.VISIBLE);
                reference.child(sku + "").child("Pallets").child(palletID).setValue(pallet).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(AddSKUActivity.this, "Pallet Added Successfully.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            updatePalletID(palletID);
                        }
                        else {
                            Toast.makeText(AddSKUActivity.this, "Error Adding Pallet, Please Try Again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            } else {
                location = locationSpinner.getSelectedItem().toString() + "-" + rowSpinner.getSelectedItem().toString() + "-" + colSpinner.getSelectedItem().toString() + "-" + heightSpinner.getSelectedItem().toString();
                Pallet pallet = new Pallet(palletID, sku, quantity, location, currentUser);

                progressBar.setVisibility(View.VISIBLE);
                reference.child(sku + "").child("Pallets").child(palletID).setValue(pallet).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(AddSKUActivity.this, "Pallet Added Successfully.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            updatePalletID(palletID);
                        }
                        else {
                            Toast.makeText(AddSKUActivity.this, "Error Adding Pallet, Please Try Again.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView.getId() == R.id.locationSpinner) {
            String location = adapterView.getItemAtPosition(i).toString();

            switch(location) {

                case ("F "):
                    heightSpinnerLayout.setVisibility(View.VISIBLE);

                    String[] fenceRows = getResources().getStringArray(R.array.fenceRows);
                    ArrayAdapter adapterFRows = new ArrayAdapter(this, android.R.layout.simple_spinner_item, fenceRows);
                    adapterFRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rowSpinner.setAdapter(adapterFRows);

                    String[] fenceCols = getResources().getStringArray(R.array.fenceCols);
                    ArrayAdapter adapterFCols = new ArrayAdapter(this, android.R.layout.simple_spinner_item, fenceCols);
                    adapterFCols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    colSpinner.setAdapter(adapterFCols);
                    break;
                case ("BP"):
                    heightSpinnerLayout.setVisibility(View.VISIBLE);

                    String[] behindPadRows = getResources().getStringArray(R.array.behindPadRows);
                    ArrayAdapter adapterBPRows = new ArrayAdapter(this, android.R.layout.simple_spinner_item, behindPadRows);
                    adapterBPRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rowSpinner.setAdapter(adapterBPRows);

                    String[] behindPadCols = getResources().getStringArray(R.array.behindPadCols);
                    ArrayAdapter adapterBPCols = new ArrayAdapter(this, android.R.layout.simple_spinner_item, behindPadCols);
                    adapterBPCols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    colSpinner.setAdapter(adapterBPCols);
                    break;
                case ("PL"):
                    heightSpinnerLayout.setVisibility(View.GONE);

                    String[] padLeftRows = getResources().getStringArray(R.array.padLeftRows);
                    ArrayAdapter adapterPLRows = new ArrayAdapter(this, android.R.layout.simple_spinner_item, padLeftRows);
                    adapterPLRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rowSpinner.setAdapter(adapterPLRows);

                    String[] padLeftCols = getResources().getStringArray(R.array.padLeftCols);
                    ArrayAdapter adapterPLCols = new ArrayAdapter(this, android.R.layout.simple_spinner_item, padLeftCols);
                    adapterPLCols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rowSpinner.setAdapter(adapterPLCols);
                    break;
                case ("PR"):
                    heightSpinnerLayout.setVisibility(View.GONE);

                    String[] padRightRows = getResources().getStringArray(R.array.padRightRows);
                    ArrayAdapter adapterPRRows = new ArrayAdapter(this, android.R.layout.simple_spinner_item, padRightRows);
                    adapterPRRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rowSpinner.setAdapter(adapterPRRows);

                    String[] padRightCols = getResources().getStringArray(R.array.padRightCols);
                    ArrayAdapter adapterPRCols = new ArrayAdapter(this, android.R.layout.simple_spinner_item, padRightCols);
                    adapterPRCols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    colSpinner.setAdapter(adapterPRCols);
                    break;
                case ("C "):
                    heightSpinnerLayout.setVisibility(View.GONE);

                    String[] curbRows = getResources().getStringArray(R.array.curbRows);
                    ArrayAdapter adapterCurbRows = new ArrayAdapter(this, android.R.layout.simple_spinner_item, curbRows);
                    adapterCurbRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rowSpinner.setAdapter(adapterCurbRows);

                    String[] curbCols = getResources().getStringArray(R.array.curbCols);
                    ArrayAdapter adapterCurbCols = new ArrayAdapter(this, android.R.layout.simple_spinner_item, curbCols);
                    adapterCurbCols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    colSpinner.setAdapter(adapterCurbCols);
                    break;
                case ("WF"):
                    heightSpinnerLayout.setVisibility(View.GONE);

                    String[] walmartFenceRows = getResources().getStringArray(R.array.walmartFenceRows);
                    ArrayAdapter adapterWFRows = new ArrayAdapter(this, android.R.layout.simple_spinner_item, walmartFenceRows);
                    adapterWFRows.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rowSpinner.setAdapter(adapterWFRows);

                    String[] walmartFenceCols = getResources().getStringArray(R.array.behindPadCols);
                    ArrayAdapter adapterWFCols = new ArrayAdapter(this, android.R.layout.simple_spinner_item, walmartFenceCols);
                    adapterWFCols.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    colSpinner.setAdapter(adapterWFCols);
                    break;
                default:
                    heightSpinnerLayout.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void getUser() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfle = snapshot.getValue(User.class);

                if(userProfle != null) {
                    String[] fullname = userProfle.fullname.split(" ");
                    userName.setText(fullname[0]);
                    Toast.makeText(AddSKUActivity.this, "Fullname Recieved", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(AddSKUActivity.this, "No profile.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddSKUActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getPalletID() {

        reference.child("newPalletID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String palletID = snapshot.getValue(String.class);
                palletIDPlaceholder.setText(palletID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updatePalletID(String newPalletID) {
        int palletID = Integer.parseInt(newPalletID);
        palletID++;
        reference.child("newPalletID").setValue(palletID + "");
    }
}