package com.mepsan.callcenter.casa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mepsan.callcenter.casa.ArizaClass.ArizaListActivity;
import com.mepsan.callcenter.casa.IstasyonClass.IstasyonListActivity;
import com.mepsan.callcenter.casa.MontajClass.MontajListActivity;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {


    SessionManager session;
    private Button battery;
    private Button wifikontrol;
    private TextView batteryPercent;
    public static Boolean connected=false;
    private NetworkChangeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, filter);

        MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();

        //Batarya iconu için tanımlanmış buton başlangıç
        battery = (Button) this.findViewById(R.id.battery);
        batteryPercent = (TextView) this.findViewById(R.id.batterypercent);
        getBatteryPercentage();
        //Batarya iconu için tanımlanmış buton bitiş



        Button btnAriza = (Button) findViewById(R.id.ariza);
        btnAriza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    Intent intent = new Intent(getApplication(), ArizaListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Lütfen bir ağa bağlanın!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnIstasyon = (Button) findViewById(R.id.btnistasyon);
        btnIstasyon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    Intent intent = new Intent(getApplication(), IstasyonListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Lütfen bir ağa bağlanın!", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btnMontaj = (Button) findViewById(R.id.btnmontaj);
        btnMontaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connected) {
                    Intent intent = new Intent(getApplication(), MontajListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Lütfen bir ağa bağlanın!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }






    private void getBatteryPercentage() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;
                }

                String BatteryPercent = Integer.toString(level);
                batteryPercent.setText("%"+BatteryPercent);

                if(level < 5)
                {
                    battery.setBackgroundResource(R.mipmap.battery_empty_icon);
                }
                else if(level < 15)
                {
                    battery.setBackgroundResource(R.mipmap.battery_1_icon);
                }
                else if(level < 25)
                {
                    battery.setBackgroundResource(R.mipmap.battery_2_icon);
                }
                else if(level < 50)
                {
                    battery.setBackgroundResource(R.mipmap.battery_3_icon);
                }
                else if(level < 70)
                {
                    battery.setBackgroundResource(R.mipmap.battery_3_icon);
                }
                else if(level < 85)
                {
                    battery.setBackgroundResource(R.mipmap.battery_4_icon);
                }
                else if(level < 100)
                {
                    battery.setBackgroundResource(R.mipmap.battery_full_icon);
                }
                else if(level == 100)
                {
                    battery.setBackgroundResource(R.mipmap.battery_full_icon);
                }


            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }


}
