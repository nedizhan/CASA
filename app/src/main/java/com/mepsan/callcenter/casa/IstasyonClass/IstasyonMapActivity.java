package com.mepsan.callcenter.casa.IstasyonClass;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.MapView;
import com.mepsan.callcenter.casa.R;

public class IstasyonMapActivity extends AppCompatActivity {

    static public MapView mMapView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Action Bar içinde kullanılacak menü öğelerini inflate edelim
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.istasyon_list_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.istasyon_activity_map);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Action Bar öğelerindeki basılmaları idare edelim
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;
            case R.id.harita_gorunumu:
                Intent intent = new Intent(getApplication(), IstasyonMapActivity.class);
                startActivity(intent);
                return true;
            case R.id.liste_gorunumu:
                Intent intent2 = new Intent(getApplication(), IstasyonListActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
