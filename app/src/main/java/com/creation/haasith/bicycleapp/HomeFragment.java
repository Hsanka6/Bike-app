package com.creation.haasith.bicycleapp;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener

{


    Location mLastLocation;

    GoogleMap map;
    LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;


    public HomeFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();




        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;



        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
        {
            @Override
            public View getInfoWindow(Marker marker)
            {

                //map.addMarker(new MarkerOptions().position(new LatLng(37.869003, -122.252138)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_black_24dp)));
                return null;

            }

            @Override
            public View getInfoContents(Marker marker)
            {
                View v = getActivity().getLayoutInflater().inflate(R.layout.map_info_window_dialog, null);

                TextView name = ((TextView)v.findViewById(R.id.ownerNameTV));
                ImageView vehicleImage = ((ImageView)v.findViewById(R.id.vehicleImage));
                TextView price = ((TextView)v.findViewById(R.id.vehiclePrice));


                name.setText("Jan");
                vehicleImage.setImageResource(R.drawable.bicycle_posted);
                price.setText("$10/hr");

                return v;
            }
        });

        LatLng marker = new LatLng(37.868061, -122.250035);
        LatLng marker1 = new LatLng(37.871485, -122.246851);
        LatLng marker2 = new LatLng(37.869097, -122.256141);



        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(marker)
                .title("Jan's Bike")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_black_24dp));

        Marker m = googleMap.addMarker(markerOptions);
        m.showInfoWindow();


        googleMap.addMarker(new MarkerOptions()
                .position(marker1)
                .title("Yasushi's Bike")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_black_24dp)));

        googleMap.addMarker(new MarkerOptions()
                .position(marker2)
                .title("Bernie's Bike")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_black_24dp)));




        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));


        //googleMap.addMarker(new MarkerOptions().title("Hello Google Maps!").position(marker));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(9000);
        mLocationRequest.setFastestInterval(9000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;

        LatLng currentPosition = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());

        map.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));




    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

}
