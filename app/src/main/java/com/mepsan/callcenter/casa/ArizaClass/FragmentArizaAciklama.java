package com.mepsan.callcenter.casa.ArizaClass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mepsan.callcenter.casa.R;
import com.mepsan.callcenter.casa.Servis_Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentArizaAciklama extends Fragment {


    Button aciklama;
    ListView aciklama_liste;
    TextView aciklama_txt;

    String userId,ArizaStokId;
    ArrayAdapter<String> adapter1;
    List<String> aciklamalar=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.ariza_fragment_aciklama, container, false);


        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        userId = mBundle.getString("user_id");
        ArizaStokId = mBundle.getString("ArizaId");

        aciklama=(Button) v.findViewById(R.id.aciklama_ekle_btn);
        aciklama_liste=(ListView) v.findViewById(R.id.list_aciklama);
        aciklama_txt=(TextView) v.findViewById(R.id.textView14);

        adapter1=new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, aciklamalar);
        aciklama_liste.setAdapter(adapter1);

        aciklamalar.clear();

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                Servis_Connection servis = new Servis_Connection();
                return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaAciklama.php", "ariza_id=" + ArizaStokId +"&tip=0");
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
                            aciklamalar.add(post.optString("aciklama"));
                        }
                        adapter1.notifyDataSetChanged();
                        if(posts.length()==0){
                            aciklama_txt.setVisibility(View.VISIBLE);
                        }else
                            aciklama_txt.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();

        aciklama_liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                alertDialogBuilder.setTitle("AÇIKLAMA");

                alertDialogBuilder
                        .setMessage(aciklamalar.get(position))
                        .setCancelable(false)
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        aciklama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.ariza_aciklama_dialog);
                dialog.setTitle("Değişen Ürün Ekle");

                final EditText aciklama_txt = (EditText) dialog.findViewById(R.id.editText3);
                Button ekle = (Button) dialog.findViewById(R.id.aciklama_tamam_btn);
                Button iptal = (Button) dialog.findViewById(R.id.button2);

                ekle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //acıklama sisteme eklenecek
                        String acikla=aciklama_txt.getText().toString();
                        new AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                Servis_Connection servis = new Servis_Connection();
                                return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaAciklama.php", "ariza_id=" + ArizaStokId +"&aciklama="+params[0]+"&servis_personel_id="+userId+"&tip=1");
                            }
                        }.execute(acikla);

                        aciklamalar.add(aciklama_txt.getText().toString());
                        adapter1.notifyDataSetChanged();
                        dialog.dismiss();
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
        return v;
    }

    public static FragmentArizaAciklama newInstance(String param1,String p2) {
        FragmentArizaAciklama f = new FragmentArizaAciklama();
        Bundle b = new Bundle();
        b.putString("ArizaId", param1);
        b.putString("user_id",p2);
        f.setArguments(b);
        return f;
    }



}
