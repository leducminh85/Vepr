package com.example.vepr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vepr.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class change_theme extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mData;
    private Switch swR;
    private Switch swL;
    private Switch swST;
    private Switch swDK;
    private Switch swAU;

    int st[] = {R.raw.standard00,R.raw.standard01,R.raw.standard10,R.raw.standard11};
    int dk[] = {R.raw.dark00,R.raw.dark01,R.raw.dark10,R.raw.dark11};
    int au[] = {R.raw.aubergine00,R.raw.aubergine01,R.raw.aubergine10,R.raw.aubergine11};

    int valR = 1;
    int valL = 1;
    int current[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_theme);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        swR = findViewById(R.id.switchRoad);
        swL = findViewById(R.id.switchLand);
        swST = findViewById(R.id.switchStandard);
        swDK = findViewById(R.id.switchDark);
        swAU = findViewById(R.id.switchAubergine);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            swR.setChecked(extras.getBoolean("swR"));
            swL.setChecked(extras.getBoolean("swL"));
            swST.setChecked(extras.getBoolean("ST"));
            swDK.setChecked(extras.getBoolean("DK"));
            swAU.setChecked(extras.getBoolean("AU"));
        }

        valR = swR.isChecked()?1:0;
        valL = swL.isChecked()?1:0;
        current = swST.isChecked()? st: swDK.isChecked()? dk: au;


        Button btn = (Button) findViewById(R.id.buttonConf);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(change_theme.this, MapsActivity.class);
                i.putExtra("swR", swR.isChecked());
                i.putExtra("swL", swL.isChecked());
                i.putExtra("key", current[valR*2+valL]);
                i.putExtra("ST", swST.isChecked());
                i.putExtra("DK", swDK.isChecked());
                i.putExtra("AU", swAU.isChecked());
                startActivity(i);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        //Move camera to HCM
        LatLng HCM = new LatLng(10.8270147, 106.6264741);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(HCM));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int valueTheme = extras.getInt("key");
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            change_theme.this, valueTheme));
        }
        swR = findViewById(R.id.switchRoad);
        swL = findViewById(R.id.switchLand);

        swR.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
                        valR = swR.isChecked()?1:0;
                        mMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                        change_theme.this, current[valR * 2 + valL]));
                    }
                });

        swL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               valL =swL.isChecked()?1:0;
                mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                change_theme.this, current[valR * 2 + valL]));
            }
        });

        swDK.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
                        if (swDK.isChecked()) {
                            swST.setChecked(false);
                            swAU.setChecked(false);
                            current = dk;
                            mMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            change_theme.this, dk[valR * 2 + valL]));
                        }
                   }
                });

        swST.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
                        if (swST.isChecked()) {
                            swDK.setChecked(false);
                            swAU.setChecked(false);
                            current = st;
                            mMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            change_theme.this, st[valR * 2 + valL]));
                        }
                    }
                });

        swAU.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
                        if (swAU.isChecked()) {
                            swDK.setChecked(false);
                            swST.setChecked(false);
                            current = au;
                            mMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            change_theme.this, au[valR * 2 + valL]));
                        }
                    }
                });
    }
}
