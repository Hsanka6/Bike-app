package com.creation.haasith.bicycleapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static com.creation.haasith.bicycleapp.R.id.postVehicleMap;


public class PostVehicleFragment extends Fragment implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, View.OnClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private EditText priceET, extraNotesET;
    private Button startDateButton, endDateButton;
    private TextView startDateTV, endDateTV;
    private Spinner vehicleTypeSpinner;
    boolean switchDateDialog;
    private ImageButton vehicleImagePosted;
    PostedVehicle ps;
    private Uri imageUri;

    private ProgressDialog vehiclePostProgress;

    private StorageReference storageReference;

    LocationRequest mLocationRequest;

    LatLng currentPosition;


    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private TextView fillerPicText;

    Location mLastLocation;
    double lat = 0.0, lng = 0.0;

    GoogleMap mMap;

    Marker mCurrLocationMarker;


    protected GoogleApiClient mGoogleApiClient;


    private final static int GALLERY_IMAGE = 1;

    String vehicleType = "";

    private FragmentActivity myContext;

    private DatabaseReference mDatabase;
// ...

    public PostVehicleFragment()
    {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity)
    {
        myContext = (FragmentActivity) activity;
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
        vehicleImagePosted = (ImageButton) v.findViewById(R.id.postedVehicleImage);
        fillerPicText = (TextView) v.findViewById(R.id.picfiller);


        vehiclePostProgress = new ProgressDialog(getActivity());

        setHasOptionsMenu(true);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        vehicleTypeSpinner = (Spinner) v.findViewById(R.id.vehicleTypeSpinner);

        startDateButton.setOnClickListener(this);


        endDateButton.setOnClickListener(this);

        setUpSpinner();

        vehicleImagePosted.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                fillerPicText.setText("");
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.setType("image/*");
                startActivityForResult(getImage, GALLERY_IMAGE);

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();


        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE && resultCode == RESULT_OK)
        {
            imageUri = data.getData();

            vehicleImagePosted.setImageURI(imageUri);


        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_add_vehicle, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle presses on the action bar items
        switch (item.getItemId())
        {
            case R.id.postVehicleButton:

                vehiclePostProgress.setMessage("Posting Vehicle");
                vehiclePostProgress.show();
                final String startDateText = startDateTV.getText().toString();
                final String endDateText = endDateTV.getText().toString();
                final double price = Double.parseDouble(priceET.getText().toString());
                final String extraNotes = extraNotesET.getText().toString();


                mAuth = FirebaseAuth.getInstance();

                mAuthListener = new FirebaseAuth.AuthStateListener()
                {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
                    {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null)
                        {

                            final DatabaseReference dbChild = mDatabase.child("Vehicle posted");


                            StorageReference filePath = storageReference.child("Posted Vehicles").child(imageUri.getLastPathSegment());

                            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                            {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                    ps = new PostedVehicle(downloadUrl.toString(), startDateText, endDateText, price, lat, lng, vehicleType, extraNotes);


                                    Log.e("here", "JJK");

                                    dbChild.push().setValue(ps);

                                    vehiclePostProgress.dismiss();
                                    Toast.makeText(getActivity(), "Successfully posted Vehicle", Toast.LENGTH_LONG).show();


                                }
                            });
                        } else
                        {
                            Log.e("failed", "failed");
                            Toast.makeText(getActivity(), "Failed to upload picture", Toast.LENGTH_LONG).show();
                            vehiclePostProgress.dismiss();
                        }
                    }

                };
                mAuth.addAuthStateListener(mAuthListener);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
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

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(postVehicleMap);


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


        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.v("item", (String) parent.getItemAtPosition(position));

                String selectedItem = (String) parent.getItemAtPosition(position);
                if (selectedItem.equals("Bicycle"))
                {

                    vehicleType = selectedItem;


                } else if (selectedItem.equals("Skateboard"))
                {
                    vehicleType = selectedItem;


                } else if (selectedItem.equals("Scooter"))
                {
                    vehicleType = selectedItem;


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // TODO Auto-generated method stub
            }
        });

    }


    @Override
    public void onMapReady(final GoogleMap googleMap)
    {
        mMap = googleMap;

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



    }


    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
//        LatLng currentPositon = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(currentPositon);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        markerOptions.draggable(true);
//        mCurrLocationMarker = mMap.addMarker(markerOptions);


        final LatLng[] position = {new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())};
        mMap.addMarker(new MarkerOptions().position(position[0]).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_black_24dp)));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {
            @Override
            public void onMarkerDragStart(Marker marker)
            {

            }

            @Override
            public void onMarkerDrag(Marker marker)
            {

                position[0] = marker.getPosition();


            }

            @Override
            public void onMarkerDragEnd(Marker marker)
            {
                position[0] = marker.getPosition();
                lat = position[0].latitude;
                lng = position[0].longitude;


            }
        });



        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position[0]));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        String date = "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        Log.e("date", date);
        if (!switchDateDialog)
        {
            //gets startdate
            startDateTV.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

        } else
        {
            //gets endDate
            endDateTV.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

        }

    }

    @Override
    public void onConnected(Bundle bundle)
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
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
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
}
