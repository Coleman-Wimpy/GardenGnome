package com.example.gardengnome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SearchSKUActivity extends AppCompatActivity{

    private EditText skuEditText;
    private Button logoutButton, addPalletButton, ohButton;
    private ImageButton skuSearchButton;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    FirebaseUser user;
    DatabaseReference reference;
    private String userID;
    private ArrayList<Pallet> palletList;
    private ArrayList<User> userProfile;
    private Dialog dialog;
    private Boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sku);

        logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SearchSKUActivity.this, MainActivity.class));
                finish();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        userID = user.getUid();
        userProfile = new ArrayList<>();

        final TextView fullnameTextView = (TextView) findViewById(R.id.userTextView);

        reference.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userProfile.add(snapshot.getValue(User.class));
                isAdmin(snapshot.getValue(User.class).userRoleID);

                if (userProfile != null) {
                    String[] fullname = userProfile.get(0).fullname.split(" ");

                    fullnameTextView.setText(fullname[0]);
                    //Toast.makeText(SearchSKUActivity.this, "Fullname Recieved", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SearchSKUActivity.this, "No profile.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchSKUActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
            }
        });

        addPalletButton = (Button) findViewById(R.id.addSKUButton);
        addPalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchSKUActivity.this, AddSKUActivity.class);
                intent.putExtra("username", fullnameTextView.getText().toString().trim());
                startActivity(intent);
            }
        });

        skuEditText = (EditText) findViewById(R.id.skuEditText);
        skuSearchButton = (ImageButton) findViewById(R.id.skuSearchButton);

        ohButton = (Button) findViewById(R.id.ohButton);
        ohButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAdmin){
                    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(SearchSKUActivity.this)
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
                    String sku = skuEditText.getText().toString().trim();
                    if (sku.isEmpty()) {
                        skuEditText.setError("Please enter a SKU");
                        skuEditText.requestFocus();
                        return;
                    }

                    Dialog ohChangeDialog = new Dialog(SearchSKUActivity.this);
                    ohChangeDialog.setContentView(R.layout.oh_changes);

                    RecyclerView ohChangeRecyclerView = (RecyclerView) ohChangeDialog.findViewById(R.id.ohRecyclerView);
                    ohChangeRecyclerView.setHasFixedSize(true);
                    ohChangeRecyclerView.setLayoutManager(new LinearLayoutManager(SearchSKUActivity.this));
                    ArrayList<OHChange> ohChangeList = new ArrayList<>();
                    OHChangeRecyclerAdapter adapter1 = new OHChangeRecyclerAdapter(ohChangeList, SearchSKUActivity.this);
                    ohChangeRecyclerView.setAdapter(adapter1);

                    TextView skuTextView = (TextView) ohChangeDialog.findViewById(R.id.ohSkuTextView);

                    ohChangeDialog.show();
                    skuTextView.setText(sku);

                    reference.child("Inventory").child(sku).child("OH_Changes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ohChangeList.clear();
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    OHChange ohChange = dataSnapshot.getValue(OHChange.class);
                                    ohChangeList.add(ohChange);
                                }
                                adapter1.notifyDataSetChanged();
                            } else {
                                palletList.clear();
                                adapter.notifyDataSetChanged();
                                AlertDialog alertDialog = new AlertDialog.Builder(SearchSKUActivity.this)
                                        .setTitle("On-Hand Changes")
                                        .setMessage("No On-Hand Changes exist for this sku")
                                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                ohChangeDialog.cancel();
                                            }
                                        }).create();
                                alertDialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        palletList = new ArrayList<>();
        adapter = new RecyclerAdapter(this, palletList, userProfile);
        recyclerView.setAdapter(adapter);

        skuSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sku = skuEditText.getText().toString().trim();

                reference.child("Inventory").child(sku).child("Pallets").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        palletList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Pallet pallet = dataSnapshot.getValue(Pallet.class);
                                //long childCount = dataSnapshot.getChildrenCount();
                                // Log.d("query", childCount + "");
                                Log.d("pallet", pallet.toString());
                                palletList.add(pallet);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            palletList.clear();
                            adapter.notifyDataSetChanged();
                            AlertDialog alertDialog = new AlertDialog.Builder(SearchSKUActivity.this)
                                    .setTitle("Pallet Search")
                                    .setMessage("No pallets exist for this sku. Would you like to create one?")
                                    .setPositiveButton("Add a Pallet", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(SearchSKUActivity.this, AddSKUActivity.class);
                                            intent.putExtra("sku", sku);
                                            intent.putExtra("username", fullnameTextView.getText().toString());
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).create();
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void isAdmin(int roleID) {
        if(roleID == 2){
            isAdmin = true;
        }
    }
}