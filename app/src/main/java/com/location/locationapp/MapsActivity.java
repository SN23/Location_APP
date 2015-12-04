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
                //System.out.println(marker.getId());
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {

                final LatLng markerPos=marker.getPosition();
                final String markerLocationName=marker.getTitle();
                final String markerSubCategoryName=marker.getSnippet();

                final Button deleteButton = (Button)findViewById(R.id.delete);
                final Button nextButton = (Button)findViewById(R.id.next);
                final Button previousButton = (Button)findViewById(R.id.previous);
                final Button editButton = (Button)findViewById(R.id.edit_button);
                final Button doneButton = (Button)findViewById(R.id.done);
                final Button searchButton = (Button)findViewById(R.id.search_button);

                final EditText titleET = (EditText)findViewById(R.id.map_search);
                final EditText titleSetET = (EditText)findViewById(R.id.map_title);

                titleET.setHint("Edit Marker Title");

                deleteButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                doneButton.setVisibility(View.VISIBLE);

                searchButton.setVisibility(View.GONE);
                titleSetET.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                previousButton.setVisibility(View.GONE);



                deleteButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        RealmResults<Location> result = realm.where(Location.class)
                                .equalTo("PK", marker.getTitle() + String.valueOf(marker.getPosition().latitude) + String.valueOf(marker.getPosition().longitude))
                                .findAll();

                        realm.beginTransaction();
                        result.clear();
                        realm.commitTransaction();

                        marker.remove();

                        titleET.setHint("Enter Location Name");

                        doneButton.setVisibility(View.GONE);
                        editButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);

                        titleSetET.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        previousButton.setVisibility(View.VISIBLE);
                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        RealmResults<Location> result = realm.where(Location.class)
                                .equalTo("PK", marker.getTitle() + String.valueOf(marker.getPosition().latitude) + String.valueOf(marker.getPosition().longitude))
                                .findAll();

                        realm.beginTransaction();
                        result.clear();
                        realm.commitTransaction();

                        marker.remove();

                        String newMarkerLocationName = String.valueOf(titleET.getText());

                        MarkerOptions markerOptions =
                                new MarkerOptions().position(markerPos)
                                        .title(newMarkerLocationName)
                                        .snippet(markerSubCategoryName);
                        mMap.addMarker(markerOptions);

                        //This is the Realm logic.
                        Location loc = new Location(); //Initalizes the Location class
                        loc.setLatitude(markerOptions.getPosition().latitude); //Sets the lat
                        loc.setLongitude(markerOptions.getPosition().longitude); //Sets the long
                        loc.setName(newMarkerLocationName); //Sets the name
                        loc.setPK(newMarkerLocationName+String.valueOf(markerOptions.getPosition().latitude) + String.valueOf(markerOptions.getPosition().longitude)); //Sets the PK (Might want to make this less bad)
                        realm.beginTransaction(); //Starts the save
                        realm.copyToRealmOrUpdate(loc); //Saves
                        realm.commitTransaction(); //Ends the save

                    }
                });


                doneButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        titleET.setHint("Enter Location Name");

                        doneButton.setVisibility(View.GONE);
                        editButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);

                        titleSetET.setVisibility(View.VISIBLE);
                        searchButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        previousButton.setVisibility(View.VISIBLE);

                    }
                });

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
        EditText setTitle = (EditText)findViewById(R.id.map_title);
        String title = setTitle.getText().toString();


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
                loc.setPK(title+String.valueOf(addressList.get(0).getLatitude()) + String.valueOf(addressList.get(0).getLongitude())); //Sets the PK (Might want to make this less bad)
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

        if(counter==Markers.size())
        {
            counter=0;
        }
            Marker mkr;
            counter++;
            mkr=Markers.get(counter);
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



}
