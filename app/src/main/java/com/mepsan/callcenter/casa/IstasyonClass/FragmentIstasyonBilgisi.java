package com.mepsan.callcenter.casa.IstasyonClass;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mepsan.callcenter.casa.R;
import com.mepsan.callcenter.casa.Servis_Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FragmentIstasyonBilgisi extends Fragment {


    private TextView TextIstasyonKodu;
    private TextView TextTransferTarihi;
    private TextView TextUrunGrubu;
    private TextView TextIstasyon;
    private TextView TextSehir;
    private TextView TextIlce;
    private TextView TextTelefon;
    private TextView TextAdres;
    private TextView TextStatu;
    private TextView TextAkaryakit;
    private TextView TextLPG;
    private TextView TextCNG;
    private TextView TextEskiİstasyon;
    private TextView TextAkaryakitLisans;
    private TextView TextLPGLisans;
    private TextView TextCNGLisans;
    private String ist_id;
    private ProgressDialog progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.istasyon_fragment_bilgisi, container, false);


        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        ist_id = mBundle.getString("istasyon_id");
        container = (LinearLayout)v.findViewById(R.id.container);


        TextIstasyonKodu = (TextView) v.findViewById(R.id.ist_kodu);
        TextTransferTarihi = (TextView) v.findViewById(R.id.ist_transfer_tar);
        TextUrunGrubu = (TextView) v.findViewById(R.id.ist_urun_grubu);
        TextIstasyon = (TextView) v.findViewById(R.id.ist_istasyon);
        TextSehir = (TextView) v.findViewById(R.id.ist_sehir);
        TextIlce = (TextView) v.findViewById(R.id.ist_ilce);
        TextTelefon = (TextView) v.findViewById(R.id.ist_telefon);
        TextAdres = (TextView) v.findViewById(R.id.ist_adres);
        TextStatu = (TextView) v.findViewById(R.id.ist_statu);
        TextAkaryakit = (TextView) v.findViewById(R.id.ist_akaryakit);
        TextLPG = (TextView) v.findViewById(R.id.ist_lpg);
        TextCNG = (TextView) v.findViewById(R.id.ist_cng);
        TextEskiİstasyon = (TextView) v.findViewById(R.id.ist_eski_istasyon);
        TextAkaryakitLisans = (TextView) v.findViewById(R.id.ist_akaryakit_lisans);
        TextLPGLisans = (TextView) v.findViewById(R.id.ist_lpg_lisans);
        TextCNGLisans = (TextView) v.findViewById(R.id.ist_cng_lisans);


        progress = new ProgressDialog(getActivity());
        progress.setMessage("İstasyon Bilgisi Alınıyor..");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        progress.setCancelable(false);

        String urlParameters = "istasyon_id="+ist_id;
        new Servis().execute("http://coman.mepsan.com.tr/Casa/Istasyon/IstasyonBilgisi.php",urlParameters);

        return v;
    }

    public static FragmentIstasyonBilgisi newInstance(String text) {

        FragmentIstasyonBilgisi f = new FragmentIstasyonBilgisi();
        Bundle b = new Bundle();
        b.putString("istasyon_id", text);
        f.setArguments(b);
        return f;
    }

    private class Servis extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Servis_Connection servis=new Servis_Connection();
            return servis.select(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String a) {
            super.onPostExecute("1");

            if (a != null) {
                JSONObject response = null;
                try {
                    response = new JSONObject(a);
                    JSONArray posts = response.optJSONArray("istasyonbilgisi");
                    for (int i = 0; i < posts.length(); i++) {

                        JSONObject post = posts.optJSONObject(i);
                        TextIstasyonKodu.setText(post.optString("kod"));
                        if(post.optString("durum").equals("t"))
                            TextStatu.setText("Aktif");
                        else
                            TextStatu.setText("Pasif");
                        TextUrunGrubu.setText(post.optString("grupadi"));
                        TextAkaryakit.setText(post.optString("akaryakit"));
                        if(post.optString("lpg").equals("null"))
                            TextLPG.setText("yok");
                        else
                            TextLPG.setText(post.optString("lpg"));

                        if(post.optString("cng").equals("null"))
                            TextCNG.setText("yok");
                        else
                            TextCNG.setText(post.optString("cng"));

                        TextIstasyon.setText(post.optString("istasyon_adi"));
                        TextSehir.setText(post.optString("sehir"));
                        TextIlce.setText(post.optString("ilce"));

                        TextTelefon.setText("yok");

                        TextAdres.setText(post.optString("adres"));

                        if(post.optString("eskiistasyon").equals("null"))
                            TextEskiİstasyon.setText("yok");
                        else
                            TextEskiİstasyon.setText(post.optString("eskiistasyon"));

                        if(post.optString("eskiistasyon").equals("null"))
                            TextTransferTarihi.setText("yok");
                        else
                            TextTransferTarihi.setText(post.optString("transfer_tarihi"));

                        TextAkaryakitLisans.setText(post.optString("akaryakit_lisans"));
                        if(post.optString("eskiistasyon").equals("null"))
                            TextLPGLisans.setText("yok");
                        else
                            TextLPGLisans.setText(post.optString("lpg_lisans"));
                        if(post.optString("eskiistasyon").equals("null"))
                            TextCNGLisans.setText("yok");
                        else
                            TextCNGLisans.setText(post.optString("cng_lisans"));
                        IstasyonDetayActivity.ist_enlem = post.optString("enlem");
                        IstasyonDetayActivity.ist_boylam = post.optString("boylam");
                        progress.hide();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            progress.dismiss();
        }
    }
}
