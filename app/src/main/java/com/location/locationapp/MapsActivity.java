package com.location.locationapp;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import io.realm.*;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> Markers= new ArrayList<>();// Holds a list of Markers
    private static int counter = 0;

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
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).draggable(false).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.setMyLocationEnabled(true);

        final Realm realm = Realm.getInstance(this); //Initializes Realm
        RealmResults<Location> result = realm.where(Location.class).findAll();
        if (result.size()>0) {
            for (int i = 0; i < result.size(); i++) {
                Location tempLoc = result.get(i);
                LatLng tempLatLng = new LatLng(result.get(i).getLatitude(), result.get(i).getLongitude());
                String locationName = result.get(i).getName();
                MarkerOptions mo = new MarkerOptions().position(tempLatLng).title(result.get(i).getName());
                Marker mkr=mMap.addMarker(mo);

                Markers.add(mkr); //Adds Marker to ArrayList
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                System.out.println(marker.getId());
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {

                LatLng markerPos=marker.getPosition();
                String markerLocationName=marker.getTitle();
                String markerSubCategoryName=marker.getSnippet();


                System.out.println(marker.getTitle());

                final Button deleteButton = (Button)findViewById(R.id.delete);
                final Button nextButton = (Button)findViewById(R.id.next);
                final Button previousButton = (Button)findViewById(R.id.previous);

                deleteButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.GONE);
                previousButton.setVisibility(View.GONE);



                deleteButton.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        RealmResults<Location> result = realm.where(Location.class)
                                .equalTo("PK", (int)marker.getPosition().latitude + (int)marker.getPosition().longitude)
                                .findAll();

                        realm.beginTransaction();
                        result.clear();
                        realm.commitTransaction();

                        marker.remove();
                        deleteButton.setVisibility(View.GONE);
                        nextButton.setVisibility(View.VISIBLE);
                        previousButton.setVisibility(View.VISIBLE);
                    }
                });


//                marker.remove();
//
//                MarkerOptions markerOptions =
//                        new MarkerOptions().position(markerPos)
//                                .title(markerLocationName)
//                                .snippet(markerSubCategoryName);
//                mMap.addMarker(markerOptions);
                return false;

            }
        });



        }

    /**
     * Gets the inputted address from user and uses it to get longitude and latitude
     * to place a marker at given location
     *
     */
    public void onSearch(View view)
    {

        EditText search = (EditText)findViewById(R.id.map_search);
//        LinearLayout titleLayout = (LinearLayout)findViewById(R.id.title_layout);
        EditText setTitle = (EditText)findViewById(R.id.map_title);
        String title = setTitle.getText().toString();

//        titleLayout.setVisibility(view.VISIBLE); //If you click search, the EditText and Buttons to change the title become visible.

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
                MarkerOptions mo = new MarkerOptions().position(latlng).draggable(false).title(title);
                Marker mkr=mMap.addMarker(mo);

                Markers.add(mkr);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));  //20 is the zoom level

                //This is the Realm logic.
                Realm realm = Realm.getInstance(this); //Initializes Realm
                Location loc = new Location(); //Initalizes the Location class
                loc.setLatitude(addressList.get(0).getLatitude()); //Sets the lat
                loc.setLongitude(addressList.get(0).getLongitude()); //Sets the long
                loc.setName(title); //Sets the name
                loc.setPK((int)addressList.get(0).getLatitude() + (int)addressList.get(0).getLongitude()); //Sets the PK (Might want to make this less bad)
                realm.beginTransaction(); //Starts the save
                realm.copyToRealmOrUpdate(loc); //Saves
                realm.commitTransaction(); //Ends the save

            }

            search.setText("");
            setTitle.setText("");

        }

    }



    public static void hideKeyboard(Activity activity)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

//  Moves the screen to the next Marker location
    public void Next(View view){

        if(counter==Markers.size()-1)
        {
            counter=0;
        }
            Marker mkr;
            mkr=Markers.get(counter++);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mkr.getPosition(),15));  //20 is the zoom level
    }


    // Moves the screen to the previous Marker location
    public void Previous(View view)
    {
        if(counter==0){
            counter=Markers.size();
        }
        Marker mkr;
        counter--;
        mkr=Markers.get(counter);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mkr.getPosition(),15));  //20 is the zoom level
    }

//    public boolean onMarkerClick(Marker marker) { //Called when a marker has been clicked or tapped.
//
//        LatLng markerPos=marker.getPosition();
//        String markerLocationName=marker.getTitle();
//        String markerSubCategoryName=marker.getSnippet();
//
//        marker.remove();
//
//        MarkerOptions markerOptions =
//                new MarkerOptions().position(markerPos)
//                        .title(markerLocationName)
//                        .snippet(markerSubCategoryName);// Changing marker icon
//        mMap.addMarker(markerOptions);
//        return false;
//    }


}
