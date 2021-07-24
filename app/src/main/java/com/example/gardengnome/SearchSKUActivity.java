package com.example.gardengnome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.List;
import java.util.function.Consumer;

public class SearchSKUActivity extends AppCompatActivity{

    private EditText skuEditText;
    private Button logoutButton, addPalletButton;
    private ImageButton skuSearchButton;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    FirebaseUser user;
    DatabaseReference reference;
    private String userID;
    private ArrayList<Pallet> palletList;

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
            }
        });

        addPalletButton = (Button) findViewById(R.id.addSKUButton);
        addPalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchSKUActivity.this, AddSKUActivity.class));
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        userID = user.getUid();

        final TextView fullnameTextView = (TextView) findViewById(R.id.userTextView);

        reference.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfle = snapshot.getValue(User.class);

                if (userProfle != null) {
                    String[] fullname = userProfle.fullname.split(" ");

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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        palletList = new ArrayList<>();
        adapter = new RecyclerAdapter(this, palletList);
        recyclerView.setAdapter(adapter);



        skuEditText = (EditText) findViewById(R.id.skuEditText);
        skuSearchButton = (ImageButton) findViewById(R.id.skuSearchButton);

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
                                long childCount = dataSnapshot.getChildrenCount();
                               // Log.d("query", childCount + "");
                                Log.d("pallet", pallet.toString());
                                palletList.add(pallet);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });



    }

}