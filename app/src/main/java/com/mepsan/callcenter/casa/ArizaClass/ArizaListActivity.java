package com.mepsan.callcenter.casa.ArizaClass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.mepsan.callcenter.casa.ListViewItem;
import com.mepsan.callcenter.casa.R;
import com.mepsan.callcenter.casa.Servis_Connection;
import com.mepsan.callcenter.casa.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ArizaListActivity extends AppCompatActivity {


    private SessionManager session;
    private ListViewDemoAdapter adapter;
    private String service_id;
    private String users_id;
    private ArrayList<ListViewItem> ArizaItems;
    ListView listView;
    private String ArizaStatusu = "1";
    private ProgressBar progressBar;
    private ProgressDialog progress;
    private int progressStatus = 10;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Action Bar içinde kullanılacak menü öğelerini inflate edelim
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ariza_activity_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArizaListActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("ARIZALAR");
        //ListView Start
        listView = (ListView) findViewById(R.id.list);
        ArizaItems = new ArrayList<ListViewItem>();
        adapter = new ListViewDemoAdapter(this, ArizaItems);
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
        progress.setMessage("Sistemdeki Arızalar Sorgulanıyor.");
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
                String urlParameters = "servisid="+service_id+"&personelid="+users_id+"&statu_id="+ArizaStatusu+"";
                return yeni.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaListesi.php",urlParameters);
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
            JSONArray posts = response.optJSONArray("arizalistesi");
            for(int i=0; i< posts.length();i++ ){

                JSONObject post = posts.optJSONObject(i);
                String id = post.optString("id");
                String ariza_kodu = post.optString("ariza_kodu");
                String urun_adi = post.optString("urun_adi");
                String istasyon = post.optString("istasyon");
                String onem = post.optString("onem");
                String statu = post.optString("statu");
                String baslik = onem +" / "+ istasyon;
                String aciklama = "Arıza Kodu : "+ ariza_kodu +" / Statüsü : "+ statu;
                ArizaItems.add(new ListViewItem(id, ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_ariza, null), baslik, aciklama, ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_next, null)));
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
            case R.id.acik_ariza:
                ArizaStatusu = "1";
                adapter.clear();
                progressBar.setProgress(10);
                progress.setMessage("Sistemdeki Açık Arızalar Sorgulanıyor.");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(true);
                progress.show();
                progress.setProgress(10);
                new AsyncTask<String,String,String>(){
                    @Override
                    protected String doInBackground(String... params) {

                        Servis_Connection yeni=new Servis_Connection();
                        String urlParameters = "servisid="+service_id+"&personelid="+users_id+"&statu_id="+ArizaStatusu+"";
                        return yeni.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaListesi.php",urlParameters);
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
            case R.id.onay_bekleyen_ariza:
                ArizaStatusu = "2";
                adapter.clear();
                progressBar.setProgress(10);
                progress.setMessage("Sistemdeki Kapalı Arızalar Sorgulanıyor.");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setIndeterminate(true);
                progress.show();
                progress.setProgress(10);

                new AsyncTask<String,String,String>(){
                    @Override
                    protected String doInBackground(String... params) {
                        Servis_Connection yeni=new Servis_Connection();
                        String urlParameters = "servisid="+service_id+"&personelid="+users_id+"&statu_id="+ArizaStatusu+"";
                        return yeni.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaListesi.php",urlParameters);

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
