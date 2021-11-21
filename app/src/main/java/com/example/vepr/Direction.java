package com.example.vepr;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Direction {
    private GoogleMap mMap;
    private Context mContext;
    private Marker mStart, mDest;
    private Polyline route;


    private final String API_URL = "https://api.mapbox.com/directions/v5/mapbox/driving/%s,%s;%s,%s?" +
            "annotations=maxspeed&overview=full&geometries=geojson&access_token=%s";
    
    public Direction(GoogleMap mMap, Context context){
        this.mMap = mMap;
        this.mContext=context;
    }
    
    public void StartDirection(LatLng start, LatLng dest){
       // clearDirection();
        requestDirection(start,dest);
    }



    private void clearDirection() {
        if (mDest != null){
            mDest.remove();
            mDest = null;
        }
        if (route != null){
            route.remove();
            route = null;
        }
    }

    @SuppressLint("DefaultLocale")
    private void requestDirection(LatLng start, LatLng dest) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = String.format(API_URL, start.longitude, start.latitude,
                                            dest.longitude, dest.latitude,
                                            mContext.getString(R.string.mapbox_token));
        Log.d("@@@ url", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("@@@ response", response);
                        ArrayList<LatLng> points = parseJsonResult(start, dest, response);
                        drawRoute(points);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("@@@ error", error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private void drawRoute(ArrayList<LatLng> points) {
        if (points.size() < 1) return;

        mDest = mMap.addMarker(new MarkerOptions()
                .position(points.get(points.size()-1))
                .title("Start")
                .snippet("text")
        );
        route = mMap.addPolyline(new PolylineOptions()
                .addAll(points)
                .color(Color.BLUE)
        );


    }

    private ArrayList<LatLng> parseJsonResult(LatLng start, LatLng dest, String response){
        ArrayList<LatLng> points = new ArrayList<>();

        try{
            JSONObject jsonResult = new JSONObject(response);
            JSONArray jsonPoints = jsonResult.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates");
            for (int i=0; i<jsonPoints.length(); ++i)
            {
                JSONArray tmp = jsonPoints.getJSONArray(i);
                double lat = tmp.getDouble(1);
                double lng = tmp.getDouble(0);
                points.add(new LatLng(lat, lng));
            }

            points.add(0,start);
            points.add(dest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return points;
    }

    public double GetLen(ArrayList<LatLng> points)
    {
        float[] result = new float[1];
        LatLng old = points.get(0);
        for (int i=1; i < points.size(); ++i)
            Location.distanceBetween(old.latitude,old.longitude,points.get(i).latitude, points.get(i).longitude, result);
        return result[0];
    }
}
