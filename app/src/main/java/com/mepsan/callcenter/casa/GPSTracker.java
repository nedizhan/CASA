package com.mepsan.callcenter.casa;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaBilgisi;
import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaKonumu;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;



    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude



    // Asgari mesafe metre cinsinden Güncellemeler değiştirmek için
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // Milisaniye cinsinden güncellemeleri arasındaki minimum süre
    private static final long MIN_TIME_BW_UPDATES = 10000; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS Ayarları");

        // Setting Dialog Message
        alertDialog.setMessage("GPS etkin değil. Etkinleştirmek için ayarlara gitmek ister misiniz?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Ayarlar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Hayır Teşekkürler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {






        FragmentArizaKonumu.gps = new GPSTracker(getApplication());
        // check if GPS enabled
        if(FragmentArizaKonumu.gps.canGetLocation()){

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            FragmentArizaKonumu.SERVIS_LOKASYONU = new LatLng(latitude,longitude);

        }


        FragmentArizaKonumu.ARIZA_LOKASYONU = new LatLng(Double.parseDouble(FragmentArizaBilgisi.Ariza_Enlem),Double.parseDouble(FragmentArizaBilgisi.Ariza_Boylam));

        FragmentArizaKonumu.googleMap = FragmentArizaKonumu.mMapView.getMap();

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(location.getLatitude(),location.getLongitude()));
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_green_marker));
        FragmentArizaKonumu.googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);
        addMarkers();
        //FragmentArizaKonumu.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FragmentArizaKonumu.SERVIS_LOKASYONU,18));


    }




    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + FragmentArizaKonumu.SERVIS_LOKASYONU.latitude + "," + FragmentArizaKonumu.SERVIS_LOKASYONU.longitude
                + "|" + "|" +  FragmentArizaKonumu.ARIZA_LOKASYONU.latitude + ","
                + FragmentArizaKonumu.ARIZA_LOKASYONU.longitude;

        String OriDest = "origin="+FragmentArizaKonumu.SERVIS_LOKASYONU.latitude+","+FragmentArizaKonumu.SERVIS_LOKASYONU.longitude+"&destination="+FragmentArizaKonumu.ARIZA_LOKASYONU.latitude+","+FragmentArizaKonumu.ARIZA_LOKASYONU.longitude;

        String sensor = "sensor=false";
        String params = OriDest+"&%20"+waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }


    private void addMarkers() {
        if (FragmentArizaKonumu.googleMap != null) {
            FragmentArizaKonumu.googleMap.clear();
            FragmentArizaKonumu.googleMap.addMarker(new MarkerOptions().position(FragmentArizaKonumu.ARIZA_LOKASYONU).title("").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_azure_marker)));
            FragmentArizaKonumu.googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_green_marker)));
        }
    }



    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask_control().execute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            try{
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    Random r = new Random();
                    int i1 = r.nextInt(255);
                    int i2 = r.nextInt(255);
                    int i3 = r.nextInt(255);


                    polyLineOptions.addAll(points);
                    polyLineOptions.width(15);
                    polyLineOptions.color(Color.rgb(i1,i2,i3));
                }
                FragmentArizaKonumu.googleMap.addPolyline(polyLineOptions);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }




    private class ParserTask_control extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            try{
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(20);
                    polyLineOptions.color(Color.rgb(0,188,255));
                }
                FragmentArizaKonumu.googleMap.addPolyline(polyLineOptions);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }



    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}