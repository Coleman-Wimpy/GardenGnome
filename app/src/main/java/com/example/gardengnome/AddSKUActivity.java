package com.example.gardengnome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

    private Button logoutButton, addPalletButton, skuChangeButton;
    private EditText skuEditText, quantityEditText;
    private TextView userName, palletIDPlaceholder;
    private Spinner locationSpinner, rowSpinner, colSpinner, heightSpinner;
    private ProgressBar progressBar;
    private LinearLayout heightSpinnerLayout;
    DatabaseReference reference;
    private String userID;
    private ArrayList<User> userList;
    FirebaseUser currentUser;

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
                finish();
            }
        });

        addPalletButton = (Button) findViewById(R.id.addPalletButton);
        addPalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPallet();
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        userID = currentUser.getUid();
        Log.d("userID", userID);
        userList = new ArrayList<>();
        String userRoleID;

        final TextView userHolder = (TextView) findViewById(R.id.userHolder);

        reference.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //User user = snapshot.getValue(User.class);
                userList.add(snapshot.getValue(User.class));
                String roleID = userList.get(0).userRoleID + "";
                Log.d("user role", "" + userList.get(0).userRoleID);
                userHolder.setText(roleID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddSKUActivity.this, "Error getting User", Toast.LENGTH_LONG).show();
            }
        });

        Log.d("userrole: ", userHolder.getText().toString());
        String id = userHolder.getText().toString().trim();
        //int userId = Integer.parseInt(id);
        int userId = 2;

        skuEditText = (EditText) findViewById(R.id.skuEditText);
        Intent intent = getIntent();
        String skuIntent = intent.getStringExtra("sku");
        if(skuIntent != null) {
            skuEditText.setText(skuIntent);
        }

        skuChangeButton = (Button) findViewById(R.id.skuChangeButton);
        skuChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userId != 2) {
                    Log.w("userrole: ", id);
                    AlertDialog alertDialog = new AlertDialog.Builder(AddSKUActivity.this)
                            .setTitle("Access Denied")
                            .setMessage("You do not have access for this feature.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create();
                    alertDialog.show();
                }
                else {

                    Dialog dialog = new Dialog(AddSKUActivity.this);
                    dialog.setContentView(R.layout.edit_skus);

                    EditText skuChangeEditText = (EditText) dialog.findViewById(R.id.skuChangeEditText);
                    Button addSKUButton = (Button) dialog.findViewById(R.id.skuAddButton);
                    Button deleteSKUButton = (Button) dialog.findViewById(R.id.skuDeleteButton);

                    dialog.show();

                    addSKUButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            String sku = skuChangeEditText.getText().toString();

                            if (sku.isEmpty()) {
                                skuChangeEditText.setError("Please enter a SKU.");
                                skuChangeEditText.requestFocus();
                                return;
                            }

                            if (Integer.parseInt(sku) == 0) {
                                skuChangeEditText.setError("Please enter a valid SKU");
                                skuChangeEditText.requestFocus();
                                return;
                            }

                            AlertDialog alertDialog = new AlertDialog.Builder(AddSKUActivity.this)
                                    .setTitle("Add New SKU into Inventory")
                                    .setMessage("Do you want to add this sku: " + sku + " into the system?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            reference.child("Inventory")
                                                    .child(sku)
                                                    .setValue("Active").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(AddSKUActivity.this, "SKU added!", Toast.LENGTH_SHORT).show();
                                                        dialogInterface.dismiss();
                                                    } else {
                                                        Toast.makeText(AddSKUActivity.this, "Error adding SKU. Please Try again.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create();
                            alertDialog.show();
                        }
                    });

                    deleteSKUButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String sku = skuChangeEditText.getText().toString();

                            if (sku.isEmpty()) {
                                skuChangeEditText.setError("Please enter a SKU.");
                                skuChangeEditText.requestFocus();
                                return;
                            }

                            if (Integer.parseInt(sku) == 0) {
                                skuChangeEditText.setError("Please enter a valid SKU");
                                skuChangeEditText.requestFocus();
                                return;
                            }

                            AlertDialog alertDialog = new AlertDialog.Builder(AddSKUActivity.this)
                                    .setTitle("Delete SKU from Inventory")
                                    .setMessage("Do you want to delete this sku: " + sku + " into the system?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            reference.child("Inventory")
                                                    .child(sku)
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(AddSKUActivity.this, "SKU deleted successfully.", Toast.LENGTH_SHORT).show();
                                                        dialogInterface.dismiss();
                                                    } else {
                                                        Toast.makeText(AddSKUActivity.this, "Error deleting SKU. Please try again.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create();
                            alertDialog.show();
                        }
                    });
                }
            }
        });


        quantityEditText = (EditText) findViewById(R.id.quantityEditText);
        userName = (TextView) findViewById(R.id.userTextView);
        userName.setText(intent.getStringExtra("username"));

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

        palletIDPlaceholder = (TextView) findViewById(R.id.palletIDPlaceholder);
        getPalletID();
    }

    private void addPallet() {

        String location, currentUser, palletID, quantity, sku;
        int skuInt, quantityInt;

        palletID = palletIDPlaceholder.getText().toString();
        //Toast.makeText(this, palletID, Toast.LENGTH_SHORT).show();

        currentUser = userName.getText().toString();

        sku = skuEditText.getText().toString();
        quantity = quantityEditText.getText().toString();

        if(sku.isEmpty()) {
            skuEditText.setError("Please Enter a SKU.");
            skuEditText.requestFocus();
            return;
        }
        else{
            skuInt= Integer.parseInt(sku);
        }

        if(skuInt == 0) {
            skuEditText.setError("Please enter a SKU not equal to 0.");
            skuEditText.requestFocus();
            return;
        }

        if(quantity.isEmpty()) {
            quantityEditText.setError("Please Enter Quantity.");
            quantityEditText.requestFocus();
            return;
        }else {
            quantityInt = Integer.parseInt(quantity);
        }

        if(quantityInt == 0) {
            quantityEditText.setError("Quantity must be greater than 0");
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
                Pallet pallet = new Pallet(palletID, skuInt, quantityInt, location, currentUser);

                progressBar.setVisibility(View.VISIBLE);
                reference.child("Inventory")
                        .child(sku + "")
                        .child("Pallets")
                        .child(palletID)
                        .setValue(pallet).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                Pallet pallet = new Pallet(palletID, skuInt, quantityInt, location, currentUser);

                progressBar.setVisibility(View.VISIBLE);
                reference.child("Inventory")
                        .child(sku + "")
                        .child("Pallets")
                        .child(palletID)
                        .setValue(pallet).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                case ("F"):
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
                case ("C"):
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

    private void getPalletID() {

        reference.child("Inventory").child("newPalletID").addValueEventListener(new ValueEventListener() {
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
        reference.child("Inventory").child("newPalletID").setValue(palletID + "");
    }
}