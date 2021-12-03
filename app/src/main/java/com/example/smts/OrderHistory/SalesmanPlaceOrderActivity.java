package com.example.smts.OrderHistory;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.smts.R;
import com.example.smts.UserTypeSelection_Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.smts.salesman.SalesmanRegistrationActivity.isFirstTime;

public class SalesmanPlaceOrderActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    Context context;
    EditText et_orderId, et_SalesmanId, et_SalesmanName, et_CustomerId, et_CustomerName, et_mob, et_Quantity;
    Button btn_placeOrder;
    Spinner spinnerArea, spinnerProduct ;
    ArrayAdapter adapterArea;
    ArrayAdapter adapterProduct;
    public static ArrayList<String> area_list;
    public static ArrayList<String> product_list;
    FirebaseDatabase database;
    DatabaseReference ref;
    ModelOrder model;
    private int maxid = 0;

    ///////////////////
    private static final String TAG = "MainActivity";
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager locationManager;
    LatLng latLng;
    String latLong=null;
    ///////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String TAG = "TAG";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.salesman_place_order);
        context = this;

        findIds();
        findSpinners();

        if (checkAndRequestPermissions()){
        }else { checkAndRequestPermissions();}

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();    //check whether location service is enable or not in your  phone



        model = new ModelOrder();

        ref = FirebaseDatabase.getInstance().getReference().child("Salesman_Placed_Orders");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    maxid = (int) snapshot.getChildrenCount();
                }else {
                    ////
                }
                if (isFirstTime) {
                    Toast.makeText(context, "Oreder Placed Successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Order failed "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clearFields() {

        et_orderId.getText().clear();
        et_SalesmanId.getText().clear();
        et_SalesmanName.getText().clear();
        et_CustomerId.getText().clear();
        et_CustomerName.getText().clear();
        et_mob.getText().clear();
        et_Quantity.getText().clear();

        spinnerProduct.setSelection(0);
        spinnerArea.setSelection(0);
    }


    void findIds(){

        btn_placeOrder = findViewById(R.id.btn_PlaceOrder_id);

        et_orderId = findViewById(R.id.et_order_id);
        et_SalesmanId = findViewById(R.id.et_Salesman_id);
        et_SalesmanName = findViewById(R.id.et_SalesmanName_id);
        et_CustomerId = findViewById(R.id.et_Customer_id);
        et_CustomerName = findViewById(R.id.et_CustomerName_id);
        et_mob = findViewById(R.id.et_mob_id);
        et_Quantity = findViewById(R.id.et_Quantity_id);

        btn_placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isLocationEnabled()) {

                    if (UserTypeSelection_Activity.isNetworkAvailable(SalesmanPlaceOrderActivity.this)) {

                      if (latLng != null) { salesmanPlaceOrder_Service(); }
                      else {
                      Toast.makeText(SalesmanPlaceOrderActivity.this, "Please wait getting your location..!", Toast.LENGTH_SHORT).show(); }
                    }
                    else {
                        UserTypeSelection_Activity.openWifISettings(SalesmanPlaceOrderActivity.this);
                    }

                } else {
                    showAlert();
                }
            }
        });
    }

    private void findSpinners()
    {

        product_list = new ArrayList<>();
        product_list = new ArrayList<>();
        product_list.clear();
        product_list.add("--Select Product--");
        product_list.add("Product-1");
        product_list.add("Product-2");
        product_list.add("Product-3");
        product_list.add("Product-4");
        product_list.add("Product-5");
        product_list.add("Product-6");

        area_list = new ArrayList<>();
        area_list.clear();
        area_list.add("--Select Area--");
        area_list.add("Area-1");
        area_list.add("Area-2");
        area_list.add("Area-3");
        area_list.add("Area-4");
        area_list.add("Area-5");
        area_list.add("Area-6");

        spinnerArea = findViewById(R.id.spinner_Area_id);
        adapterArea = new ArrayAdapter<String>(SalesmanPlaceOrderActivity.this, android.R.layout.simple_spinner_dropdown_item,
                area_list);
        adapterArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(adapterArea);


        spinnerProduct = findViewById(R.id.spinner_Product_id);
        adapterProduct = new ArrayAdapter<String>(SalesmanPlaceOrderActivity.this, android.R.layout.simple_spinner_dropdown_item,
                product_list);
        adapterProduct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProduct.setAdapter(adapterProduct);



    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstTime = false;
    }

    //Add Salesman Details into database
    public void salesmanPlaceOrder_Service() {

//         latLong = getCoordinates(SalesmanPlaceOrderActivity.this);
//         latLong = "33°01'41.9\"N, 70°42'01.8\"E";
        String currentDate = UserTypeSelection_Activity.getCurrentDateTimeCustom("dd-MMM-yyyy");
        String currentTime = UserTypeSelection_Activity.getCurrentDateTimeCustom("HH:mm:ss");
        String orderId = et_orderId.getText().toString();
        String salesmanId = et_SalesmanId.getText().toString();
        String salesmanName = et_SalesmanName.getText().toString();
        String customerId = et_CustomerId.getText().toString();
        String customerName = et_CustomerName.getText().toString();
        String mob = et_mob.getText().toString();
        String quantity = et_Quantity.getText().toString();
        String product = spinnerProduct.getSelectedItem().toString();
        String workingArea = spinnerArea.getSelectedItem().toString();


        if (!orderId.isEmpty() && !salesmanId.isEmpty() && !salesmanName.isEmpty() && !customerId.isEmpty() && !customerName.isEmpty() && mob.length() == 11 && spinnerProduct.getSelectedItemPosition()!= 0 && spinnerArea.getSelectedItemPosition()!= 0) {

            model.setOrderId(orderId);
            model.setSalesmanId(salesmanId);
            model.setSalesmanName(salesmanName);
            model.setCustomerId(customerId);
            model.setCustomerName(customerName);
            model.setMob(mob);
            model.setQuantity(quantity);
            model.setProduct(product);
            model.setWorkingArea(workingArea);
            model.setLatLong(latLong);
            model.setCurrentDate(currentDate);
            model.setCurrentTime(currentTime);
            isFirstTime = true;

            // firebase
            ref.push().setValue(model);


        } else {
            if (orderId.isEmpty()){
                et_orderId.requestFocus();
                et_orderId.setError("enter Order id");
            } else if (salesmanId.length()!=13){
                et_SalesmanId.requestFocus();
                et_SalesmanId.setError("enter salesman id");
            }else if (salesmanName.isEmpty()){
                et_SalesmanName.requestFocus();
                et_SalesmanName.setError("enter salesman Name");
            }else if (customerId.isEmpty()){
                et_CustomerId.requestFocus();
                et_CustomerId.setError("enter customer id");
            }else if (customerName.isEmpty()){
                et_CustomerName.requestFocus();
                et_CustomerName.setError("enter customer Name");
            }else if (mob.length()!=11){
                et_mob.requestFocus();
                et_mob.setError("please enter valid Mobile Number");
            }else if (quantity.isEmpty()){
                et_Quantity.requestFocus();
                et_Quantity.setError("please enter Quantity Number");
            } else if (spinnerProduct.getSelectedItemPosition()== 0){
                spinnerProduct.performClick();
                Toast.makeText(context, "please select Product", Toast.LENGTH_SHORT).show();
            }else if (spinnerArea.getSelectedItemPosition()== 0){
                spinnerArea.performClick();
                Toast.makeText(context, "please select Salesman Working Area", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "please enter valid information", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void showSettingsAlert(final Activity activity) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity);

        //Setting Dialog Title
        alertDialog.setTitle("GPS is Disabled..!");

        //Setting Dialog Message
        alertDialog.setMessage("Please Enable GPS location Access");

        //On Pressing Setting button
        alertDialog.setPositiveButton("Go to Setting", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
            super.onBackPressed();

            finish();
            Intent salesman = new Intent(SalesmanPlaceOrderActivity.this, UserTypeSelection_Activity.class);
            startActivity(salesman);

        }

        ////////////////////////////////////////////////
        @Override
        public void onConnected(Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startLocationUpdates();
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLocation == null){
                startLocationUpdates();
            }
            if (mLocation != null) {
                // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
                //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
            } else {
                Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
            }
        }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }
    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
//        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
//        mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        latLong = location.getLatitude()+","+location.getLongitude();
//        Toast.makeText(this, "Update"+latLng, Toast.LENGTH_SHORT).show();
    }
    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkAndRequestPermissions() {

        int location = ContextCompat.checkSelfPermission(SalesmanPlaceOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();



        if (location != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {


            int REQUEST_ID_MULTIPLE_PERMISSIONS = 10001;

            ActivityCompat.requestPermissions(SalesmanPlaceOrderActivity.this, listPermissionsNeeded.toArray
                    (new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


}
