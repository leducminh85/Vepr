package com.example.vepr;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vepr.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LatLng startP;
    private LatLng endP;
    private Direction mDirection;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;

    private EditText edtLoc;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        edtLoc = findViewById(R.id.edtLoc);

        db = FirebaseFirestore.getInstance();
        SetLocationMarker();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Move camera to HCM
        // LatLng HCM = new LatLng(10.8166261, 106.6289181);
        LatLng HCM = new LatLng(10.8270147, 106.6264741);


        addMarkerOnMap(HCM.latitude, HCM.longitude,"HCM");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(HCM));

        mMap.setOnMarkerClickListener(this);

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
        mMap.setMyLocationEnabled(true);
        updateLastLocation();

       // addMarkerOnMap(lastLocation.getLatitude(), lastLocation.getLongitude(), "MP");

      //  mMap.setOnMapClickListener(this);
//        LatLng st = new LatLng(10.8166261, 106.6289181);
//        startP = st;
//        LatLng end = new LatLng(10.8270147, 106.6264741);
//        endP = end;
//        mDirection = new Direction(mMap,this);
    }

    private void SetLocationMarker() {
        DocumentReference docRef = db.collection("Location").document("0001");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                       // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        edtLoc.setText("1");

                    } else {
                        //Log.d(TAG, "No such document");
                        edtLoc.setText("2");

                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                    edtLoc.setText("3");

                }
            }
        });


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
                    edtLoc.setText(String.format("%f,%f",location.getLatitude(),location.getLongitude()));
                }
            }
        });
    }

    private Marker addMarkerOnMap(double lat, double lng, String name) {
        LatLng position = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(name)
                .alpha(0.5f)
                .draggable(true)
                .visible(true);
        Marker marker = mMap.addMarker(markerOptions);
        return marker;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        //Goto pos's infor

        //mDirection.StartDirection(startP,endP);

        return false;
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }



}
