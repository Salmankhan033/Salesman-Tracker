package com.example.smts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.smts.salesman.LoginSalesman;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class UserTypeSelection_Activity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    Button btndistri, btnsales, btncontinue;
    String TAG = "TAG";
    TextView tv_newuser;
    int i;
    public static String latLong;
    //    public static boolean isGPSEnabled;
    public LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);
        context = UserTypeSelection_Activity.this;

        if (checkAndRequestPermissions()) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//            latLong = getCoordinates(UserSelectionActivity.this);
        } else {
            checkAndRequestPermissions();
        }


        tv_newuser = findViewById(R.id.tv_newuser);

        btndistri = (Button) findViewById(R.id.btn_distributor_id);
        btnsales = (Button) findViewById(R.id.btn_salesman_id);
        btncontinue = (Button) findViewById(R.id.btn_continue);

        btndistri.setOnClickListener(this);
        btnsales.setOnClickListener(this);
        btncontinue.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {

        Drawable d = btndistri.getBackground();

        Drawable s = btnsales.getBackground();
        switch (v.getId()) {
            case R.id.btn_distributor_id:
                if (i == 2) {
                    btnsales.setBackgroundResource(R.drawable.rect_outlinegrey);
                    btnsales.setTextColor(getResources().getColor(R.color.col2));
                }
                btndistri.setBackgroundResource(R.drawable.rect_outlineblue);
                btndistri.setTextColor(getResources().getColor(R.color.col));

                i = 1;
                /*Intent D = new Intent(context, DistributorLoginActivity.class);
                startActivity(D);
                */
                break;
            case R.id.btn_salesman_id:
                if (i == 1) {
                    btndistri.setBackgroundResource(R.drawable.rect_outlinegrey);
                    btndistri.setTextColor(getResources().getColor(R.color.col2));
                }

                btnsales.setBackgroundResource(R.drawable.rect_outlineblue);
                btnsales.setTextColor(getResources().getColor(R.color.col));
                i = 2;
                /*       Intent S = new Intent(context, SalesmanLoginActivity.class);
                startActivity(S);
*/
                break;

            case R.id.btn_continue:

                if (isNetworkAvailable(this)){

                    login();

//                 Toast.makeText(context, "Randon Id = "+randInt(1, 1000000), Toast.LENGTH_SHORT).show();

                }else {
                    openWifISettings(this);
                }
        }
    }

    public static int randInt(int min, int max) {

        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
    private void login() {
        if (i == 1) {
            Log.d(TAG, "onClick: DistributorLoginActivity");
            Intent D = new Intent(context, AdminLoginActivity.class);
            startActivity(D);
        } else if (i == 2) {

            Log.d(TAG, "onClick: SalesmanLoginActivity");
            Intent S = new Intent(context, LoginSalesman.class);
            startActivity(S);
        } else {
            Toast.makeText(context, "select role", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public static boolean isEmpty(String str) {

        return TextUtils.isEmpty(str);
    }

    public static boolean isEmpty_et(EditText text) {

        CharSequence str = text.getText().toString();

        return TextUtils.isEmpty(str);
    }
    public static boolean isValidEmail(String email) {

        if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null;
    }

    public static void openWifISettings(final Activity activity) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(activity);
        mDialog.setCancelable(true);
        mDialog.setTitle("Sorry..!");
        mDialog.setMessage("No Internet Connection");
        mDialog.setPositiveButton("Open WiFi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                activity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });

        mDialog.setNegativeButton("Mobile Data", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                activity.startActivity(intent);
            }
        });
        mDialog.create().show();
    }

    public static String getCurrentDate()
    {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(new Date());
    }

    public static String getCurrentDateTime()
    {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }
    @SuppressLint("SimpleDateFormat")
    public  static String getCurrentDateTimeCustom(String formatPattern)
    {
//        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
        DateFormat df = new SimpleDateFormat(formatPattern);
        String str_dateTime = df.format(Calendar.getInstance().getTime());

//        TimeZone timeZone = TimeZone.getDefault();
//        str_timeZone = timeZone.getID();
//
//        df = new SimpleDateFormat("z");
//        str_timeZoneName = df.format(Calendar.getInstance().getTime());
        return str_dateTime;
    }
//    public String getCoordinates(final Activity activity) {
//
//        LocationRequest locationRequest = new LocationRequest();
//
//        if (ActivityCompat.checkSelfPermission(UserSelectionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//            LocationServices.getFusedLocationProviderClient(activity)
//                    .requestLocationUpdates(locationRequest, new LocationCallback() {
//                        @Override
//                        public void onLocationResult(LocationResult locationResult) {
//                            super.onLocationResult(locationResult);
//                            LocationServices.getFusedLocationProviderClient(activity).removeLocationUpdates(this);
//
//                            if (locationResult != null && locationResult.getLocations().size() > 0) {
//
//                                int latestLocationIndex = locationResult.getLocations().size() - 1;
//
//                                String latitude = String.valueOf(locationResult.getLocations().get(latestLocationIndex).getLatitude());
//                                String longitude = String.valueOf(locationResult.getLocations().get(latestLocationIndex).getLongitude());
//
//                                latLong = latitude + "," + longitude;
//                            }
//                        }
//                    }, Looper.getMainLooper());
//        }
//
//        return String.valueOf(latLong);
//    }

    private boolean checkAndRequestPermissions() {

        int camera = ContextCompat.checkSelfPermission(UserTypeSelection_Activity.this, Manifest.permission.CAMERA);
        int phoneState = ContextCompat.checkSelfPermission(UserTypeSelection_Activity.this, Manifest.permission.READ_PHONE_STATE);
        int location = ContextCompat.checkSelfPermission(UserTypeSelection_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();


        if (phoneState != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (location != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {


            int REQUEST_ID_MULTIPLE_PERMISSIONS = 10001;

            ActivityCompat.requestPermissions(UserTypeSelection_Activity.this, listPermissionsNeeded.toArray
                    (new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) UserTypeSelection_Activity.this);
                    Toast.makeText(UserTypeSelection_Activity.this, "permission", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(UserTypeSelection_Activity.this, "permission denied, boo! Disable th", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
