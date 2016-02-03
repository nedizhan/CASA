package com.mepsan.callcenter.casa.IstasyonClass;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mepsan.callcenter.casa.GPSTracker;
import com.mepsan.callcenter.casa.HttpConnection;
import com.mepsan.callcenter.casa.PathJSONParser;
import com.mepsan.callcenter.casa.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class IstasyonMapActivity2 extends AppCompatActivity {

    static public MapView mMapView;
    static public GoogleMap googleMap;
    static public GPSTracker gps;

    static public LatLng SERVIS_LOKASYONU = new LatLng(0,0);
    static public LatLng ARIZA_LOKASYONU = new LatLng(0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.istasyon_activity_map2);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try {
            MapsInitializer.initialize(this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            SERVIS_LOKASYONU = new LatLng(latitude,longitude);
            // \n is for new line
            //Toast.makeText(getActivity(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        ARIZA_LOKASYONU = new LatLng(0,0);

        googleMap = mMapView.getMap();

        MarkerOptions options = new MarkerOptions();
        options.position(SERVIS_LOKASYONU);
        options.position(ARIZA_LOKASYONU);
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_azure_marker));
        googleMap.addMarker(options);
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ARIZA_LOKASYONU, 7));
        addMarkers();


    }



    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + SERVIS_LOKASYONU.latitude + "," + SERVIS_LOKASYONU.longitude
                + "|" + "|" +  ARIZA_LOKASYONU.latitude + ","
                + ARIZA_LOKASYONU.longitude;
        String OriDest = "origin="+SERVIS_LOKASYONU.latitude+","+SERVIS_LOKASYONU.longitude+"&destination="+ARIZA_LOKASYONU.latitude+","+ARIZA_LOKASYONU.longitude;

        String sensor = "sensor=false";
        String params = OriDest+"&%20"+waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }


    private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(ARIZA_LOKASYONU).title("First Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_azure_marker)));
            googleMap.addMarker(new MarkerOptions().position(SERVIS_LOKASYONU).title("Second Point").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_green_marker)));
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

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(10);
                    polyLineOptions.color(Color.rgb(0, 188, 255));
                }
                googleMap.addPolyline(polyLineOptions);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }



    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }








}
