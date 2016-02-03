package com.mepsan.callcenter.casa;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {


 
      @Override
      public void onReceive(final Context context, final Intent intent) {
      
       isNetworkAvailable(context); //receiver çalıştığı zaman çağırılacak method
      }
      
      private boolean isNetworkAvailable(Context context) {
       ConnectivityManager connectivity = (ConnectivityManager) 
         context.getSystemService(Context.CONNECTIVITY_SERVICE); //Sistem ağını dinliyor internet var mı yok mu

       if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
            
                        if(!MainActivity.connected){ //internet varsa
                            MainActivity.connected = true;

                        }
                    return true;
                    }
                }
            }
       }Toast.makeText(context.getApplicationContext(),"Lütfen bir ağa bağlanın..",Toast.LENGTH_SHORT).show();
          MainActivity.connected = false;
       return false;
      }
}