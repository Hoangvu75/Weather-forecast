package com.example.weatherapplication.getDeviceLocation;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weatherapplication.MainActivity;
import com.example.weatherapplication.R;
import com.example.weatherapplication.StartActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class getLocationFunctions {
    private static double latitude;
    private static double longitude;
    private static String address;

    private static Dialog loadingDialog;

    public static void getCurrentLocation(Context context) {
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.dialog_custom_progress);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(this);
                if (locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                    latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                    Location location = new Location("provider");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    fetchAddress(location, context);

                    Handler handler = new Handler();
                    Runnable runnable = () -> {
                        if (address != null) {
                            loadingDialog.dismiss();
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                            ((StartActivity) context).finish();
                        }
                    };
                    handler.postDelayed(runnable, 100);
                }
            }
        }, Looper.getMainLooper());
    }

    public static class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            address = resultData.getString(Constants.RESULT_DATA_KEY);
        }
    }

    private static void fetchAddress(Location location, Context context) {
        ResultReceiver resultReceiver = new AddressResultReceiver(new Handler());
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        context.startService(intent);
    }

    public static double getLatitude() { return latitude; }

    public static double getLongitude() { return longitude; }

    public static String getAddress() { return address; }
}
