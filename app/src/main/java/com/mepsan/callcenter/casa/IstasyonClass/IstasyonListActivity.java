package com.mepsan.callcenter.casa.IstasyonClass;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mepsan.callcenter.casa.Adapter.ListViewDemoAdapter;
import com.mepsan.callcenter.casa.ArizaClass.Ariza_Detay_Activity;
import com.mepsan.callcenter.casa.ListViewItem;
import com.mepsan.callcenter.casa.R;
import com.mepsan.callcenter.casa.Servis_Connection;
import com.mepsan.callcenter.casa.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class IstasyonListActivity extends AppCompatActivity {


    private SessionManager session;
    private ListViewDemoAdapter adapter;
    private String service_id;
    private String users_id;
    private ArrayList<ListViewItem> IstasyonItems;
    ListView listView;
    private String ArizaStatusu = "1";
    private ProgressBar progressBar;
    private ProgressDialog progress;
    private int progressStatus = 10;



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
        setContentView(R.layout.istasyon_activity_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        IstasyonListActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("ISTASYONLAR");

        //ListView Start
        listView = (ListView) findViewById(R.id.list);
        IstasyonItems = new ArrayList<ListViewItem>();
        adapter = new ListViewDemoAdapter(this, IstasyonItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListViewItem item = (ListViewItem) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplication(), IstasyonDetayActivity.class);
                intent.putExtra("id", item.id);
                intent.putExtra("user_id",users_id);
                startActivity(intent);
                Toast.makeText(getApplication(), "Itemlistid -> " + item.id, Toast.LENGTH_SHORT).show();
            }
        });
        //ListView End

        //Progress And ProgressBar Start
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setMax(100); //ProgressBar'ın Max değerini belirliyoruz.
        progressBar.setIndeterminate(false); //ProgressBar'ın tekrar eden bir animasyon ile çalışmasını önlüyoruz.
        progressBar.setProgress(10);
        initValues();
        progress = new ProgressDialog(this);
        progress.setMessage("Sistemdeki Istasyonlar Sorgulanıyor.");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        progress.setCancelable(false);
        progress.setProgress(10);
        //Progress And ProgressBar End

        // Session class instance Start
        session = new SessionManager(this.getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        service_id = user.get(SessionManager.KEY_SERVICEID);
        users_id = user.get(SessionManager.KEY_ID);
        // Session class instance End



        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... params) {
                Servis_Connection yeni=new Servis_Connection();
                String urlParameters = "";
                return yeni.select("http://coman.mepsan.com.tr/Casa/Istasyon/IstasyonListesi.php",urlParameters);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    parseResult(s);
                }
            }
        }.execute();
    }


    private void initValues() { //Başlangıç değerlerini set ediyoruz.
        progressBar.setProgress(progressStatus);
    }

    private void parseResult(String result) {

        try{

            progressBar.setProgress(10);
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("istasyonlistesi");
            for(int i=0; i< posts.length();i++ ){

                JSONObject post = posts.optJSONObject(i);
                String id = post.optString("id");
                String istasyon_adi = post.optString("istasyon_adi");
                String grup_adi = post.optString("grupadi");
                String kod = post.optString("kod");
                String durum = post.optString("durum");
                if(durum.equals("t")){ durum = "Aktif";} else { durum = "Pasif"; }

                String baslik = istasyon_adi +" / "+ grup_adi;
                String aciklama = "Istasyon Kodu : "+ kod +" / Durumu : "+ durum;
                IstasyonItems.add(new ListViewItem(id, ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_istasyon, null), baslik, aciklama, ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_next, null)));
                progressStatus += posts.length()*3;
                progressBar.setProgress(progressStatus);

            }
            progress.hide();
            adapter.notifyDataSetChanged();

        }catch (JSONException e){
            e.printStackTrace();
        }
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
                Intent intent1 = new Intent(IstasyonListActivity.this, IstasyonMapActivity2.class);
                startActivity(intent1);
                return true;
            case R.id.liste_gorunumu:
                Intent intent2 = new Intent(IstasyonListActivity.this, IstasyonListActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
