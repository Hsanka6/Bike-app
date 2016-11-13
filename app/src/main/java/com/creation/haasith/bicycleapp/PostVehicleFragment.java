package com.creation.haasith.bicycleapp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;


public class PostVehicleFragment extends Fragment implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, View.OnClickListener
{
    private EditText priceET, extraNotesET;
    private Button startDateButton,endDateButton;
    private TextView startDateTV, endDateTV;
    private Spinner vehicleTypeSpinner;
    boolean switchDateDialog;


    private FragmentActivity myContext;



    public PostVehicleFragment()
    {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);// Inflate the layout for this fragment
        priceET = (EditText) v.findViewById(R.id.priceET);
        extraNotesET = (EditText) v.findViewById(R.id.extraNotesET);
        startDateButton = (Button) v.findViewById(R.id.startDateButton);
        endDateButton = (Button) v.findViewById(R.id.endDateButton);
        startDateTV = (TextView) v.findViewById(R.id.startDateTV);
        endDateTV = (TextView) v.findViewById(R.id.endDateTV);


        vehicleTypeSpinner = (Spinner) v.findViewById(R.id.vehicleTypeSpinner);

        startDateButton.setOnClickListener(this);


        endDateButton.setOnClickListener(this);




        return v;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startDateButton:
                switchDateDialog = false;
                java.util.Calendar now = java.util.Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(myContext.getFragmentManager(), "Datepickerdialog");

                break;
            case R.id.endDateButton:
                switchDateDialog = true;
                java.util.Calendar end = java.util.Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        this,
                        end.get(Calendar.YEAR),
                        end.get(Calendar.MONTH),
                        end.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(myContext.getFragmentManager(), "Datepickerdialog");
                break;

        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);




        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.postVehicleMap);
        fragment.getMapAsync(this);
    }


    private void setUpSpinner()
    {

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.vehicles, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        vehicleTypeSpinner.setAdapter(staticAdapter);


        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));

                String selectedItem = (String) parent.getItemAtPosition(position);
                if(selectedItem.equals("Bicycle"))
                {
                    Toast.makeText(getActivity(), "bicycle selected", Toast.LENGTH_SHORT).show();




                }
                else if (selectedItem.equals("Skateboard"))
                {
                    Toast.makeText(getActivity(), "skateboard selected", Toast.LENGTH_SHORT).show();



                }
                else if(selectedItem.equals("Scooter"))
                {
                    Toast.makeText(getActivity(), "scooter selected", Toast.LENGTH_SHORT).show();



                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        LatLng marker = new LatLng(37.87435, -122.257115);


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
                .title("Location for pick up")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 16));

    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        Log.e("date", date);
        if(!switchDateDialog)
        {
            //gets startdate
            startDateTV.setText((monthOfYear + 1) + "/" + dayOfMonth  + "/" + year);

        }
        else
        {
            //gets endDate
            endDateTV.setText((monthOfYear + 1) + "/" + dayOfMonth  + "/" + year);

        }

    }
}
