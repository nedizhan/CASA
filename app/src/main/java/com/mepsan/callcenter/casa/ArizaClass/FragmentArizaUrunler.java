
package com.mepsan.callcenter.casa.ArizaClass;

        import android.app.Dialog;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.content.res.ResourcesCompat;
        import android.util.DisplayMetrics;
        import android.util.TypedValue;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.RelativeLayout;
        import android.widget.Spinner;
        import android.widget.TextView;

        import com.mepsan.callcenter.casa.Adapter.ArizaListVievStokAdapter;
        import com.mepsan.callcenter.casa.Adapter.UrunListAdapter;
        import com.mepsan.callcenter.casa.ListViewItem;
        import com.mepsan.callcenter.casa.R;
        import com.mepsan.callcenter.casa.Servis_Connection;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;


public class FragmentArizaUrunler extends Fragment {

    //componentler
    private ListView stok_liste;
    private View cizgi,cizgi3;
    private TextView deg_urun,degis_urun_durumu,takilan_adi_text,
                        sokulen_adi_text,takilan_seri_text,sokulen_seri_text,
                        stok_secti,stok_eklenmedi_texti;
    public static TextView stok_adi;
    private ListView stok_ariza_list,list_urun;
    private Button parca_ekle;
    LinearLayout stoklist_layout,urunlist_layout;
    RelativeLayout r1,r2;
    ProgressBar progress_arizatip,progress_urun;

    //adapterler
    ArrayAdapter<String> s_adapter;
    private UrunListAdapter urun_adapter;
    private ArizaListVievStokAdapter adapter;

    //listlere
    public static ArrayList<ListViewItem> ArizaStokItems;
    List<ArizaDegisenStokYapisi> urunler=new ArrayList<>();
    ArrayList<String> val = new ArrayList<>();
    public static List<String> stok_adlari=new ArrayList<>();
    public static List<String> stok_idleri=new ArrayList<>();
    List<String> altstok_adlari=new ArrayList<>();
    List<String> altstok_idleri=new ArrayList<>();

    //degiskenler
    private String ArizaStokId;
    private String ArizaId;
    String secenek="";
    int stok_list_pozisyon,width,height;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ariza_fragment_urunler, container, false);

        //component tanımlamaları
        stok_secti=(TextView) v.findViewById(R.id.stok_sectimi);
        takilan_adi_text =(TextView) v.findViewById(R.id.textView45);
        takilan_seri_text =(TextView) v.findViewById(R.id.textView51);
        sokulen_adi_text =(TextView) v.findViewById(R.id.textView52);
        sokulen_seri_text =(TextView) v.findViewById(R.id.textView53);
        degis_urun_durumu = (TextView) v.findViewById(R.id.txt_urun_degis_durumu);
        deg_urun= (TextView) v.findViewById(R.id.txt_deg_urun);
        stok_liste = (ListView) v.findViewById(R.id.list);
        stok_ariza_list = (ListView) v.findViewById(R.id.listView);
        stok_adi = (TextView) v.findViewById(R.id.txt_stok_adi);
        cizgi = (View) v.findViewById(R.id.view2);
        cizgi3 = (View) v.findViewById(R.id.view4);
        list_urun = (ListView) v.findViewById(R.id.listView2);
        parca_ekle = (Button) v.findViewById(R.id.urun_degisikligi_ekle);
        stoklist_layout=(LinearLayout) v.findViewById(R.id.stoklist_layout);
        urunlist_layout=(LinearLayout) v.findViewById(R.id.urunlist_layout);
        stok_eklenmedi_texti=(TextView) v.findViewById(R.id.textView56);
        r1=(RelativeLayout) v.findViewById(R.id.relativeLayout2);
        r2=(RelativeLayout) v.findViewById(R.id.r2);
        progress_arizatip=(ProgressBar) v.findViewById(R.id.progressBar_arizatip);
        progress_urun=(ProgressBar) v.findViewById(R.id.progressBar_urun);

        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        ArizaId = mBundle.getString("ArizaId");

        urunler.clear();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        height= displaymetrics.heightPixels;



        ArizaStokItems = new ArrayList<ListViewItem>();
        adapter = new ArizaListVievStokAdapter(getActivity(), ArizaStokItems);
        stok_liste.setAdapter(adapter);

        urun_adapter = new UrunListAdapter(getActivity(),urunler,getActivity(),width,height);
        list_urun.setAdapter(urun_adapter);

        secenek="ariza_stoklari_getir";
        String urlParameters = "ArizaId="+ArizaId;
        new Servis().execute("http://coman.mepsan.com.tr/Casa/Ariza/ArizaStoklari.php", urlParameters);


        //degistirilen stok ekleme
        parca_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //takilan sokulen eklenecek alert dialog ac
                //alertte degisiklik eklenınce eger urunler sayısı 0 dan farklı ıse textview i kapatacaksın

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.ariza_stok_dialog);
                dialog.setTitle("Değişen Ürün Ekle");

                final Spinner takilan_adi = (Spinner) dialog.findViewById(R.id.spinner);
                final Spinner sokulen_adi = (Spinner) dialog.findViewById(R.id.spinner3);
                final EditText takilan_seri = (EditText) dialog.findViewById(R.id.editText);
                final EditText sokulen_seri = (EditText) dialog.findViewById(R.id.editText2);
                Button ekle = (Button) dialog.findViewById(R.id.stok_ekle_btn);
                Button iptal = (Button) dialog.findViewById(R.id.stok_iptal_btn);
                Button sil = (Button) dialog.findViewById(R.id.stok_sil_btn);
                sil.setVisibility(View.GONE);

                s_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, altstok_adlari);
                s_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                takilan_adi.setAdapter(s_adapter);
                sokulen_adi.setAdapter(s_adapter);

                ekle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        urunler.clear();
                        //gonderilecek parametreler
                        final String sokulen_id = altstok_idleri.get(sokulen_adi.getSelectedItemPosition());
                        final String takilan_id = altstok_idleri.get(takilan_adi.getSelectedItemPosition());
                        final String takilan_serino = takilan_seri.getText().toString();
                        final String sokulen_serino = sokulen_seri.getText().toString();

                        if (!takilan_serino.equals("") && !sokulen_serino.equals("")) {
                            //degisen stok bılgısını gonderip ekledık
                            new AsyncTask<String, Void, String>() {
                                @Override
                                protected String doInBackground(String... params) {
                                    Servis_Connection servis = new Servis_Connection();
                                    return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaDegisenStok.php", "ariza_stok_id=" + ArizaStokId + "&sokulen_stok_id=" + sokulen_id + "&takilan_stok_id=" + takilan_id + "&takilan_seri_no=" + takilan_serino + "&sokulen_seri_no=" + sokulen_serino + "&tip=1");
                                }

                                @Override
                                protected void onPostExecute(String a) {
                                    super.onPostExecute("1");
                                    //tekrar degısen urunlerı cektıkk
                                    new AsyncTask<String, Void, String>() {
                                        @Override
                                        protected String doInBackground(String... params) {
                                            Servis_Connection servis = new Servis_Connection();
                                            return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaDegisenStok.php", "arizastokid=" + ArizaStokId + "&tip=0");
                                        }

                                        @Override
                                        protected void onPostExecute(String gelen) {
                                            super.onPostExecute("1");
                                            Degisen_Stok_Listele(gelen);
                                        }
                                    }.execute();
                                }
                            }.execute();

                            degis_urun_durumu.setVisibility(View.GONE);//değişiklik eklenmedi yazan texti kapattık
                            urun_adapter.notifyDataSetChanged();//adapteri guncelledık

                            //eklenen degişiklik sisteme gönderilecekk!!!!
                            if (urunler.size() != 0) {//urunun degisen parcası varsa
                                degis_urun_durumu.setVisibility(View.GONE);
                                takilan_adi_text.setVisibility(View.VISIBLE);
                                takilan_seri_text.setVisibility(View.VISIBLE);
                                sokulen_adi_text.setVisibility(View.VISIBLE);
                                sokulen_seri_text.setVisibility(View.VISIBLE);
                                cizgi3.setVisibility(View.VISIBLE);
                            }
                            dialog.dismiss();
                        }
                    }
                });

                iptal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                }

        });

        //stok listesine tıklama olayı
        stok_liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ListViewItem item = (ListViewItem) stok_liste.getItemAtPosition(position);
                ArizaStokId = item.id;
                stok_list_pozisyon = position;
                val.clear();
                altstok_idleri.clear();
                altstok_adlari.clear();
                progress_urun.setVisibility(View.VISIBLE);
                progress_arizatip.setVisibility(View.VISIBLE);

                stok_secti.setVisibility(View.GONE);
                stok_adi.setText(item.getTitle() + " Arıza Tipleri");
                stok_adi.setVisibility(View.VISIBLE);
                cizgi.setVisibility(View.VISIBLE);
                cizgi3.setVisibility(View.VISIBLE);
                deg_urun.setVisibility(View.VISIBLE);//değiştirilen urunler textını goster
                parca_ekle.setVisibility(View.VISIBLE);//degisiklik ekleme butonu
                degis_urun_durumu.setVisibility(View.VISIBLE);

                urunler.clear();//takılan sokulen urunler lıstesını temızledık

                //burada o stoka ait degisen parcalar cekildi ! listeye eklendi
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        Servis_Connection servis = new Servis_Connection();
                        return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaDegisenStok.php", "arizastokid=" + ArizaStokId + "&tip=0");
                    }

                    @Override
                    protected void onPostExecute(String gelen) {
                        super.onPostExecute("1");
                        Degisen_Stok_Listele(gelen);
                    }
                }.execute();

                //stoka ait alt stokları cekip listeye ekle
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        Servis_Connection servis = new Servis_Connection();
                        return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/AltStoklar.php", "ariza_stok_id=" + ArizaStokId);
                    }

                    @Override
                    protected void onPostExecute(String gelen) {
                        super.onPostExecute("1");
                        if (gelen != null) {//sonuc null degılse
                            try {
                                JSONObject response = new JSONObject(gelen);
                                JSONArray posts = response.optJSONArray("stoklar");
                                for (int i = 0; i < posts.length(); i++) {
                                    JSONObject post = posts.optJSONObject(i);
                                    altstok_adlari.add(post.optString("stokadi"));
                                    altstok_idleri.add(post.optString("id"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //spinner adapterleri yenilenecek
                    }
                }.execute();

                secenek = "stok_arizatipleri_getir";
                String urlParameters = "StokId=" + ArizaStokId;
                new Servis().execute("http://coman.mepsan.com.tr/Casa/Ariza/StokArizaTipleri.php", urlParameters); //Ariza tiplerini getir

            }
        });
        //stok adını degistirmek icin stokları cekip listeye ekledik
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                Servis_Connection servis=new Servis_Connection();
                return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/Stoklar.php","");
            }

            @Override
            protected void onPostExecute(String a) {
                super.onPostExecute("1");
                if (a != null) {//sonuc null degılse
                    try {
                        JSONObject response = new JSONObject(a);
                        JSONArray posts = response.optJSONArray("stoklar");
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.optJSONObject(i);
                            stok_adlari.add(post.optString("stokadi"));
                            stok_idleri.add(post.optString("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();

        return v;
    }//create sonu


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        height= displaymetrics.heightPixels;

        if(width>height){//yan cevrılmıs
            height=height-(actionBarHeight*2);

            stok_liste.setLayoutParams(new LinearLayout.LayoutParams(width/2, LinearLayout.LayoutParams.WRAP_CONTENT));
            r1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,height/2));
            r2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,height/2));
        }else {//dik ise
            //eger mobile degil ise ekranı 3e bol
            height=(height-(actionBarHeight*2))/3;
            stok_liste.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
            r1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
            r2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));

            takilan_adi_text.setLayoutParams(new LinearLayout.LayoutParams(width/4, LinearLayout.LayoutParams.WRAP_CONTENT));
            takilan_seri_text.setLayoutParams(new LinearLayout.LayoutParams(width/4, LinearLayout.LayoutParams.WRAP_CONTENT));
            sokulen_adi_text.setLayoutParams(new LinearLayout.LayoutParams(width/4, LinearLayout.LayoutParams.WRAP_CONTENT));
            sokulen_seri_text.setLayoutParams(new LinearLayout.LayoutParams(width/4, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public void Degisen_Stok_Listele(String a){
        if (a != null) {//sonuc null degılse
            try {
                JSONObject response = new JSONObject(a);
                JSONArray posts = response.optJSONArray("arizadegisenlistele");
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    ArizaDegisenStokYapisi ekle=new ArizaDegisenStokYapisi();
                    ekle.setAriza_id(Integer.parseInt(post.optString("id")));
                    ekle.setSokulen_stok_adi(post.optString("sokulen_stok_adi"));
                    ekle.setSokulen_stok_id(Integer.parseInt(post.optString("sokulen_stok_id")));
                    ekle.setSokulen_seri_no(post.optString("sokulen_seri_no"));
                    ekle.setTakilan_stok_adi(post.optString("takilan_stok_adi"));
                    ekle.setTakilan_stok_id(Integer.parseInt(post.optString("takilan_stok_id")));
                    ekle.setTakilan_seri_no(post.optString("takilan_seri_no"));
                    urunler.add(ekle);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        urun_adapter.notifyDataSetChanged();
        if (urunler.size() != 0){//urunun degisen parcası varsa
            degis_urun_durumu.setVisibility(View.GONE);
            takilan_adi_text.setVisibility(View.VISIBLE);
            takilan_seri_text.setVisibility(View.VISIBLE);
            sokulen_adi_text.setVisibility(View.VISIBLE);
            sokulen_seri_text.setVisibility(View.VISIBLE);
            cizgi3.setVisibility(View.VISIBLE);
        }else {//stokun degısen parcası yoksa
            takilan_adi_text.setVisibility(View.GONE);
            takilan_seri_text.setVisibility(View.GONE);
            sokulen_adi_text.setVisibility(View.GONE);
            sokulen_seri_text.setVisibility(View.GONE);
            cizgi3.setVisibility(View.GONE);
        }
        progress_urun.setVisibility(View.GONE);
    }

    public static FragmentArizaUrunler newInstance(String text) {

        FragmentArizaUrunler f = new FragmentArizaUrunler();
        Bundle b = new Bundle();
        b.putString("ArizaId", text);
        f.setArguments(b);
        return f;
    }

    //serviss
    private class Servis extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Servis_Connection servis=new Servis_Connection();
            return servis.select(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String a) {
            super.onPostExecute("1");
            if (a != null) {//sonuc null degılse
                try{
                    JSONObject response = new JSONObject(a);

                    if(secenek.equals("stok_arizatipleri_getir")) {//listview tıklama içinden cagırıldı-stok Ariza tipleri getir
                        JSONArray posts = response.optJSONArray("arizatip");
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.optJSONObject(i);
                            val.add(post.optString("tip"));
                        }
                        progress_arizatip.setVisibility(View.GONE);
                        stok_ariza_list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, val));
                        adapter.notifyDataSetChanged();

                    }else if(secenek.equals("ariza_stoklari_getir")){//listview Ariza stoklarını ekle

                        JSONArray posts = response.optJSONArray("arizastoklari");
                        for(int i=0; i< posts.length();i++ ){
                            JSONObject post = posts.optJSONObject(i);
                            String id = post.optString("id");
                            String baslik = post.optString("stokadi");
                            String aciklama = post.optString("serino");
                            ArizaStokItems.add(new ListViewItem(id, ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_stok, null), baslik, aciklama, ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_next, null)));
                        }
                        //eger Ariza stok sayısı 0 ise yazı goster
                        if(posts.length()==0) {
                            stoklist_layout.setVisibility(View.GONE);
                            urunlist_layout.setVisibility(View.GONE);
                            stok_eklenmedi_texti.setVisibility(View.VISIBLE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
