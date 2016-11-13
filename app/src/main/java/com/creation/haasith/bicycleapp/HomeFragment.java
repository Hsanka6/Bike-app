package com.creation.haasith.bicycleapp;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback
{


    public HomeFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


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
        LatLng marker = new LatLng(37.87435, -122.257115);
        LatLng marker1 = new LatLng(37.87495, -122.257315);
        LatLng marker2 = new LatLng(37.87415, -122.257515);

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

        googleMap.addMarker(new MarkerOptions()
                .position(marker)
                .title("Jan's Bike")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_white_24dp)));

        googleMap.addMarker(new MarkerOptions()
                .position(marker1)
                .title("Yasushi's Bike")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_white_24dp)));

        googleMap.addMarker(new MarkerOptions()
                .position(marker2)
                .title("Bernie's Bike")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_white_24dp)));


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));


        //googleMap.addMarker(new MarkerOptions().title("Hello Google Maps!").position(marker));

    }
}
