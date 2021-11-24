package com.example.vepr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vepr.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Direction mDirection;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;
    private List<MarkerOptions> MarkerListRp ;
    private List<MarkerOptions> MarkerListPk ;
    private List<MarkerOptions> MarkerListSt ;
    private List<Marker> MarkerList ;
    private int valueTheme;

    private FirebaseFirestore mData;
    private FirebaseAuth mAuth;
    private DatabaseReference data;
    String directName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            valueTheme = extras.getInt("key");}



        BottomNavigationView bottomNavigationView=findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(R.id.idMap);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.idSearch:
                        startActivity(new Intent(getApplicationContext(), DestinationList.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.idMap:
                    case R.id.idProfile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        data = FirebaseDatabase.getInstance().getReference();

        MarkerListRp = new ArrayList<MarkerOptions>();
        MarkerListPk = new ArrayList<MarkerOptions>();
        MarkerListSt = new ArrayList<MarkerOptions>();
        MarkerList = new ArrayList<Marker>();

        Button repair = (Button)findViewById(R.id.repairBtn);
        Button parking = (Button)findViewById(R.id.parkingBtn);
        Button Store = (Button)findViewById(R.id.storeBtn);
        repair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMarkerOnMap(MarkerList);
                addMarkerOnMap(MarkerListRp);

            }
        });

        parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMarkerOnMap(MarkerList);

                addMarkerOnMap(MarkerListPk);
            }
        });

        Store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMarkerOnMap(MarkerList);

                addMarkerOnMap(MarkerListSt);

            }
        });
        Button themeBtn = (Button) findViewById(R.id.themeBtn);
        themeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, change_theme.class);
                intent.putExtra("swR",extras.getBoolean("swR"));
                intent.putExtra("swL",extras.getBoolean("swL"));
                intent.putExtra("ST",extras.getBoolean("ST"));
                intent.putExtra("DK",extras.getBoolean("DK"));
                intent.putExtra("AU",extras.getBoolean("AUR"));
                intent.putExtra("key",extras.getInt("key"));
                startActivity(intent);
            }
        });
    }

    private void addMarkerOnMap(List<MarkerOptions> List){
       for (MarkerOptions i:List)
            MarkerList.add(mMap.addMarker(i));
    }
    private void removeMarkerOnMap(List<Marker> List){
        for (Marker i:List)
           if (i!= null)
            i.remove();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        SetLocationMarker();

            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            MapsActivity.this, valueTheme));

        Bundle extras = getIntent().getExtras();
        if (extras.getBoolean("markerCheck")) {
            directName = extras.getString("marker");
            DrawDirect();
        }

        //Move camera to HCM
        LatLng HCM = new LatLng(10.8270147, 106.6264741);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(HCM));

        mMap.setOnMarkerClickListener(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
        updateLastLocation();
        mDirection = new Direction (mMap, this);
    }

    private void DrawDirect() {
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String Lat = userSnapshot.child("Lat").getValue(String.class);
                    String Lng = userSnapshot.child("Lng").getValue(String.class);
                    String id = userSnapshot.getKey();
                    if(id.equals(directName)) {
                        mDirection.StartDirection(new LatLng(Double.valueOf(Lat), Double.valueOf(Lng)), new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(new LatLng(Double.valueOf(Lat), Double.valueOf(Lng)))
                                .alpha(1)
                                .draggable(true)
                                .visible(true)
                                .title(userSnapshot.getKey());
                        mMap.addMarker(markerOptions);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("tag","error");

            }
        });
    }

    private void SetLocationMarker() {
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String Lat = userSnapshot.child("Lat").getValue(String.class);
                    String Lng = userSnapshot.child("Lng").getValue(String.class);
                    String Name = userSnapshot.child("Name").getValue(String.class);
                    String Type = userSnapshot.child("Type").getValue(String.class);

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(new LatLng(Double.valueOf(Lat), Double.valueOf(Lng)))
                            .alpha(1)
                            .draggable(true)
                            .visible(true)
                            .title(userSnapshot.getKey());
                    if (Type.equals("repair"))
                          MarkerListRp.add(markerOptions);
                    else
                        if (Type.equals("parking"))
                            MarkerListPk.add(markerOptions);
                        else
                            MarkerListSt.add(markerOptions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("tag", "error");

            }
        });
       // Log.d("TAG", String.valueOf(MarkerListRp.isEmpty()));
    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lastLocation = location;
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
       // startActivity(new Intent(getApplicationContext(), com.example.vepr.DetailActivity.class));
//        Intent intent = new Intent(MapsActivity.this, DetailActivity.class);
//        intent.putExtra("marker_key", marker.getId());
//        startActivity(intent);

        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String Lat = userSnapshot.child("Lat").getValue(String.class);
                    String Lng = userSnapshot.child("Lng").getValue(String.class);
                    String id = userSnapshot.getKey();
                    if(id.equals(marker.getTitle()))
                        mDirection.StartDirection(new LatLng(Double.valueOf(Lat),Double.valueOf(Lng)),new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("tag","error");

            }
        });
        return false;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }
}
