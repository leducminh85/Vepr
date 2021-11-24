package com.example.vepr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseFirestore mData;
    private TextView Email, Name, Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView bottomNavigationView=findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.idProfile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.idSearch:
                        startActivity(new Intent(getApplicationContext(), DestinationList.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.idMap:
                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.idProfile:
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseFirestore.getInstance();

        Email = (TextView)findViewById(R.id.idEmailProfile);
        Email.setText(mAuth.getCurrentUser().getEmail());

        Name = (TextView)findViewById(R.id.idProfile);
        Phone = (TextView)findViewById(R.id.idPhoneProfile);

        DocumentReference docRef = mData.collection("Users").document(mAuth.getCurrentUser().getUid().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.get("Name") != null)
                             Name.setText(document.get("Name").toString());
                        if (document.get("Phone") != null)
                            Phone.setText(document.get("Phone").toString());
                    }
                    }

            }
        });



        Button Logout = (Button) findViewById(R.id.btnLogOut);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), com.example.vepr.MainActivity.class));

            }
        });
    }
}