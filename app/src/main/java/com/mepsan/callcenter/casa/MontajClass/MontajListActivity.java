package com.mepsan.callcenter.casa.MontajClass;

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

public class MontajListActivity extends AppCompatActivity {


    private SessionManager session;
    private ListViewDemoAdapter adapter;
    private String service_id;
    private String users_id;
    private ArrayList<ListViewItem> MontajItems;
    ListView listView;
    private String MontajStatusu = "5";
    private ProgressBar progressBar;
    private ProgressDialog progress;
    private int progressStatus = 10;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Action Bar içinde kullanılacak menü öğelerini inflate edelim
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_montaj_detay, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.montaj_activity_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MontajListActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("MONTAJLAR");
        //ListView Start
        listView = (ListView) findViewById(R.id.list);
        MontajItems = new ArrayList<ListViewItem>();
        adapter = new ListViewDemoAdapter(this, MontajItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListViewItem item = (ListViewItem) listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplication(), Ariza_Detay_Activity.class);
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
        progress.setMessage("Sistemdeki Montajlar Sorgulanıyor.");
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
                String urlParameters = "servis_id="+service_id+"&kapanis_servis_personel_id="+users_id+"&statu_id="+MontajStatusu+"";
                return yeni.select("http://coman.mepsan.com.tr/Casa/Montaj/MontajListesi.php",urlParameters);
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
            JSONArray posts = response.optJSONArray("montajlistesi");
            for(int i=0; i< posts.length();i++ ){

                JSONObject post = posts.optJSONObject(i);
                String id = post.optString("id");
                String montaj_kodu = post.optString("montaj_kodu");
                String istasyon_adi = post.optString("istasyon_adi");
                String dagitim_sirketi = post.optString("dagitim_sirketi");
                String baslik = "Istasyon : "+istasyon_adi;
                String aciklama = "Montaj Kodu : "+ montaj_kodu +" / Dağıtım Şirketi : "+ dagitim_sirketi;
                MontajItems.add(new ListViewItem(id, ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_montaj, null), baslik, aciklama, ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_next, null)));
                progressStatus = 100;
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
            case R.id.acik_montaj:
                MontajStatusu = "5";
                adapter.clear();
                progressBar.setProgress(10);
                progress.setMessage("Sistemdeki Açık Montajlar Sorgulanıyor.");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(true);
                progress.show();
                progress.setProgress(10);
                new AsyncTask<String,String,String>(){
                    @Override
                    protected String doInBackground(String... params) {

                        Servis_Connection yeni=new Servis_Connection();
                        String urlParameters = "servis_id="+service_id+"&kapanis_servis_personel_id="+users_id+"&statu_id="+MontajStatusu+"";
                        return yeni.select("http://coman.mepsan.com.tr/Casa/Montaj/MontajListesi.php",urlParameters);
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s != null) {
                            parseResult(s);
                        }
                    }
                }.execute();
                return true;
            case R.id.kapanis_onay_bekleyen_montaj:
                MontajStatusu = "7";
                adapter.clear();
                progressBar.setProgress(10);
                progress.setMessage("Sistemdeki Kapalı Montajlar Sorgulanıyor.");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(true);
                progress.show();
                progress.setProgress(10);

                new AsyncTask<String,String,String>(){
                    @Override
                    protected String doInBackground(String... params) {
                        Servis_Connection yeni=new Servis_Connection();
                        String urlParameters = "servis_id="+service_id+"&kapanis_servis_personel_id="+users_id+"&statu_id="+MontajStatusu+"";
                        return yeni.select("http://coman.mepsan.com.tr/Casa/Montaj/MontajListesi.php",urlParameters);

                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s != null) {
                            parseResult(s);
                        }
                    }
                }.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
