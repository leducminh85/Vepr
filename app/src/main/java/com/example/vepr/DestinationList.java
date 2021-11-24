package com.example.vepr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DestinationList extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    DestinationAdapter adapter;
    ArrayList<Destination> parkingDestinations= new ArrayList<>();
    ArrayList<Destination> repairDestinations= new ArrayList<>();
    ArrayList<Destination> storeDestinations= new ArrayList<>();
    ArrayList<Destination> saved= new ArrayList<>();
    Boolean isOpen=false;
    DatabaseReference data;

    FirebaseFirestore mData;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_list);

        Context context=getBaseContext();
        BottomNavigationView bottomNavigationView=findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.idSearch);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.idSearch:
                    case R.id.idMap:
                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.idProfile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        recyclerView=findViewById(R.id.idDesList);
        data = FirebaseDatabase.getInstance().getReference();
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String Lat = userSnapshot.child("Lat").getValue(String.class);
                    String Lng = userSnapshot.child("Lng").getValue(String.class);
                    String id = userSnapshot.getKey();
                    String Name = userSnapshot.child("Name").getValue(String.class);
                    String Phone = userSnapshot.child("Phone").getValue(String.class);
                    String Type = userSnapshot.child("Type").getValue(String.class);
                    String Website = userSnapshot.child("website").getValue(String.class);
                    String CloseTime = userSnapshot.child("Close time").getValue(String.class);
                    String OpenTime = userSnapshot.child("Open time").getValue(String.class);
                    String Address = userSnapshot.child("Address").getValue(String.class);


                    Destination des = new Destination(id,Name,Address,Phone,OpenTime,CloseTime,Lng,Lat,Website,Type);
                    if(des.type.equals("parking")) parkingDestinations.add(des);
                    if(des.type.equals("repair")) repairDestinations.add(des);
                    if(des.type.equals("store")) storeDestinations.add(des);
                }
                adapter=new DestinationAdapter(parkingDestinations,context);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("tag", "error");

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseFirestore.getInstance();
        mData.collection("Users").document(mAuth.getCurrentUser().getUid().toString()).collection("Save").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String Lat = document.getData().get("Lat").toString();
                                String Lng = document.getData().get("Lng").toString();
                                String id = document.getId();
                                String Name = document.getData().get("Name").toString();
                                String Phone = document.getData().get("Phone").toString();
                                String Type = document.getData().get("Type").toString();
                                String Website = document.getData().get("Website").toString();
                                String CloseTime = document.getData().get("CloseTime").toString();
                                String OpenTime = document.getData().get("OpenTime").toString();
                                String Address = document.getData().get("Address").toString();

                                Destination des = new Destination(id,Name,Address,Phone,OpenTime,CloseTime,Lng,Lat,Website,Type);
                                saved.add(des);
                            }
                        }
                        else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }

                }

        );

        
        FloatingActionButton fab=findViewById(R.id.fab);
        FloatingActionButton fabParking=findViewById(R.id.fabParking);
        FloatingActionButton fabRepair=findViewById(R.id.fabRepair);
        FloatingActionButton fabStore=findViewById(R.id.fabStore);
        FloatingActionButton fabSave=findViewById(R.id.fabSave);
        fabParking.hide();fabRepair.hide();fabStore.hide();fabSave.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    fabParking.hide();
                    fabRepair.hide();
                    fabStore.hide();
                    fabSave.hide();
                    isOpen=false;
                }
                else{
                    fabParking.show();
                    fabRepair.show();
                    fabStore.show();
                    fabSave.show();
                    isOpen=true;
                }
            }
        });
        fabParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter=new DestinationAdapter(parkingDestinations,context);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });
        fabRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter=new DestinationAdapter(repairDestinations,context);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });
        fabStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter=new DestinationAdapter(storeDestinations,context);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter=new DestinationAdapter(saved,context);
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}