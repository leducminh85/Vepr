package com.example.vepr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    Destination mDes;
    String UserID;
    FirebaseAuth mAuth;
    FirebaseFirestore mData;
    String link ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent=getIntent();
        mDes=(Destination)intent.getSerializableExtra("desObject");
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseFirestore.getInstance();
        if(mDes!=null) setFullInformation(mDes);


    }

    private void setFullInformation(Destination mDes) {
        ImageView directionButton = findViewById(R.id.idDirectionButton);
        ImageView saveButton = findViewById(R.id.idSaveButton);
        ImageView callButton = findViewById(R.id.idPhoneButton);

        TextView desName = findViewById(R.id.idDesName);
        TextView addressTextView = findViewById(R.id.idDetailAddress);
        TextView timeTextView = findViewById(R.id.idDetailTime);
        TextView typeTextView = findViewById(R.id.idDetailType);
        TextView phoneNumberTextView = findViewById(R.id.idDetailPhone);
        TextView websiteTextView = findViewById(R.id.idDetailWebsite);

        //Set information
        if (!mDes.desName.equals("null")) desName.setText(mDes.desName);
        if (!mDes.address.equals("null")) addressTextView.setText(mDes.address);
        if (!mDes.openTime.equals("null") && !mDes.closeTime.equals("null")) {
            String time = "Open Time-Close Time: " + mDes.openTime + " - " + mDes.closeTime;
            timeTextView.setText(time);
        }
        if (!mDes.phone.equals("null")) phoneNumberTextView.setText(mDes.phone);
        if (mDes.website != null) websiteTextView.setText(mDes.website);
        if (!mDes.type.equals("null")) typeTextView.setText(mDes.type);

        //Set onclick
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                assert mDes != null;
                intent.setData(Uri.parse("tel:" + mDes.phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        phoneNumberTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                assert mDes != null;
                intent.setData(Uri.parse("tel:" + mDes.phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        websiteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert mDes != null;
                Uri webpage = Uri.parse(mDes.website);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, MapsActivity.class);
                intent.putExtra("markerCheck", true);
                intent.putExtra("marker", mDes.id);

                intent.putExtra("swR", true);
                intent.putExtra("swL",true);
                intent.putExtra("key", R.raw.standard11);
                intent.putExtra("ST",true);
                intent.putExtra("DK", false);
                intent.putExtra("AU", false);
                startActivity(intent);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                assert mDes != null;
                hashMap.put("Name", mDes.desName);
                hashMap.put("Address", mDes.address);
                hashMap.put("Phone", mDes.phone);
                hashMap.put("Lat", mDes.latitude);
                hashMap.put("Lng", mDes.longitude);
                hashMap.put("CloseTime", mDes.closeTime);
                hashMap.put("OpenTime", mDes.openTime);
                hashMap.put("Type", mDes.type);
                hashMap.put("Website", mDes.website);

                UserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                DocumentReference documentReference = mData.collection("Users").document(UserID).collection("Save").document(mDes.id);

                documentReference.set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(DetailActivity.this, "Save Successfully!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
    public void onClick(View v) {
    }

}