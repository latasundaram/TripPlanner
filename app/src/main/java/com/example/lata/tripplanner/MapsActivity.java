package com.example.lata.tripplanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ParsingAsync.IData {

    private GoogleMap mMap;
    LocationManager locationManager;
    android.location.LocationListener locationListener;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Trip trip;
    ArrayList<LocationDetails> locationList;
    ArrayList<LatLng> markerPoints;
    Location mLastLocation;
    ProgressDialog pd;
    LatLngBounds.Builder boundsBuilder;
    LatLngBounds bounds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        trip = (Trip) this.getIntent().getSerializableExtra("placesList");

        locationList = trip.getTripPlaces();
        markerPoints = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading route..");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boundsBuilder=new LatLngBounds.Builder();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                final int zoomWidth = getResources().getDisplayMetrics().widthPixels;
                final int zoomHeight = getResources().getDisplayMetrics().heightPixels;
                final int zoomPadding = (int) (zoomWidth * 0.10);
                bounds = boundsBuilder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,zoomWidth,zoomHeight,zoomPadding));
                //mMap.animateCamera(CameraUpdateFactory.zoomIn());
                Log.d("demo","camera moved");
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        for (LocationDetails loc : locationList) {
            LatLng latLng = new LatLng(loc.getLocLatitude(), loc.getLocLongitude());
            markerPoints.add(latLng);
            boundsBuilder.include(latLng);
            mMap.addMarker(new MarkerOptions().position(latLng).title(loc.getLocationName()));
        }

        if(markerPoints.size()>1) {
            LatLng startLoc=markerPoints.get(0);
            String url = getUrl(markerPoints);
            new RoutesAsync(MapsActivity.this).execute(url);
        }
        else if(markerPoints.size()==1) {
            pd.dismiss();
            Toast.makeText(this, "Only one place added", Toast.LENGTH_SHORT).show();
        }

    }

    private String getUrl(ArrayList<LatLng> points) {

        // Origin of route
        String str_origin = "origin=" + points.get(0).latitude + "," + points.get(0).longitude;

        // Destination of route
        String str_dest = "destination=" + points.get(0).latitude + "," + points.get(0).longitude;

        //waypoints
        String waypoints = "waypoints=" + points.get(1).latitude + "," + points.get(1).longitude;

        for (int i = 2; i < points.size() - 1; i++) {
            waypoints = waypoints + "|" + points.get(i).latitude + "," + points.get(i).longitude;
        }

        // Sensor enabled
        //String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.d("demo", url);

        return url;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void drawRoute(PolylineOptions polylineOptions) {
        if(polylineOptions!=null) {
            mMap.addPolyline(polylineOptions);
            pd.dismiss();
        }
        else
        {
            pd.dismiss();
            Toast.makeText(this, "No routes found!", Toast.LENGTH_SHORT).show();
        }
    }
}
