package com.mepsan.callcenter.casa.ArizaClass;

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


public class FragmentArizaBilgisi extends Fragment {


    private TextView TextArizaKodu;
    private TextView TextAcilisZamani;
    private TextView TextDagitimFirmasi;
    private TextView TextIstasyon;
    private TextView TextSehir;
    private TextView TextIlce;
    private TextView TextTelefon;
    private TextView TextAdres;
    private TextView TextStatu;
    private TextView TextAltStatu;
    private TextView TextUrunGrubu;
    private TextView TextOnemi;
    private TextView TextYetkiliPersonel;
    private TextView TextAtananPersonel;
    private TextView TextKapanisZamani;
    private TextView TextHedefMudahele;
    private TextView TextacilisAciklamasi;
    private String ArizaId;
    static public String Ariza_Enlem;
    static public String Ariza_Boylam;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ariza_fragment_bilgisi, container, false);


        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        ArizaId = mBundle.getString("ArizaId");
        container = (LinearLayout)v.findViewById(R.id.container);


        TextArizaKodu = (TextView) v.findViewById(R.id.ist_kodu);
        TextAcilisZamani = (TextView) v.findViewById(R.id.txt_acilis_zamani);
        TextDagitimFirmasi = (TextView) v.findViewById(R.id.txt_dag_firmasi);
        TextIstasyon = (TextView) v.findViewById(R.id.txt_istasyon);
        TextSehir = (TextView) v.findViewById(R.id.ist_sehir);
        TextIlce = (TextView) v.findViewById(R.id.txt_ilce);
        TextTelefon = (TextView) v.findViewById(R.id.txt_telefon);
        TextAdres = (TextView) v.findViewById(R.id.txt_adres);
        TextStatu = (TextView) v.findViewById(R.id.ist_statu);
        TextAltStatu = (TextView) v.findViewById(R.id.txt_alt_statu);
        TextUrunGrubu = (TextView) v.findViewById(R.id.ist_akaryakit);
        TextOnemi = (TextView) v.findViewById(R.id.ist_lpg);
        TextYetkiliPersonel = (TextView) v.findViewById(R.id.ist_eski_istasyon);
        TextAtananPersonel = (TextView) v.findViewById(R.id.ist_akaryakit_lisans);
        TextKapanisZamani = (TextView) v.findViewById(R.id.ist_lpg_lisans);
        TextHedefMudahele = (TextView) v.findViewById(R.id.ist_cng_lisans);
        TextacilisAciklamasi = (TextView) v.findViewById(R.id.txt_aciklama);


        String urlParameters = "ArizaId="+ArizaId;
        new Servis().execute("http://coman.mepsan.com.tr/Casa/Ariza/ArizaBilgisi.php",urlParameters);

        return v;
    }

    public static FragmentArizaBilgisi newInstance(String text) {

        FragmentArizaBilgisi f = new FragmentArizaBilgisi();
        Bundle b = new Bundle();
        b.putString("ArizaId", text);
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
                    JSONArray posts = response.optJSONArray("arizabilgisi");
                    for (int i = 0; i < posts.length(); i++) {

                        JSONObject post = posts.optJSONObject(i);
                        TextArizaKodu.setText(post.optString("ariza_kodu"));
                        TextAcilisZamani.setText(post.optString("acilis_zamani"));
                        TextDagitimFirmasi.setText(post.optString("dagitim_sirketi"));
                        TextIstasyon.setText(post.optString("istasyon"));
                        TextSehir.setText(post.optString("sehir"));
                        TextIlce.setText(post.optString("ilce"));
                        TextTelefon.setText(post.optString("telefon"));
                        TextAdres.setText(post.optString("adres"));
                        TextStatu.setText(post.optString("statu"));
                        TextAltStatu.setText(post.optString("statu"));
                        TextUrunGrubu.setText(post.optString("urungrubu"));
                        TextOnemi.setText(post.optString("onem"));
                        TextYetkiliPersonel.setText(post.optString("yetkili"));
                        TextAtananPersonel.setText(post.optString("atananpersonel"));
                        TextKapanisZamani.setText(post.optString("kapanis_zamani"));
                        TextHedefMudahele.setText(post.optString("servis_hedef_mudahele"));
                        TextacilisAciklamasi.setText(post.optString("acilis_aciklama"));
                        Ariza_Enlem = post.optString("enlem");
                        Ariza_Boylam = post.optString("boylam");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
