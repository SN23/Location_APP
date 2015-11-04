package com.location.locationapp;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Gets the inputted address from user and uses it to get longitude and latitude
     * to place a marker at given location
     *
     */
    public void onSearch(View view)
    {

        EditText search = (EditText)findViewById(R.id.map_search);
        String location = search.getText().toString();
        hideKeyboard(this);
        List<Address> addressList=null;

        if(location!=null & location!="")
        {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList=(geocoder.getFromLocationName(location, 1)); // 1 indicates number of results
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addressList.get(0)!=null)
            {
                LatLng latlng = new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
                mMap.addMarker(new MarkerOptions().position(latlng).title("User Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));  //20 is the zoom level
            }


        }

    }


    public static void hideKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
