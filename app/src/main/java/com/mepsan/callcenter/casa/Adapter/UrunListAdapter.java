package com.mepsan.callcenter.casa.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mepsan.callcenter.casa.ArizaClass.ArizaDegisenStokYapisi;
import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaUrunler;
import com.mepsan.callcenter.casa.R;
import com.mepsan.callcenter.casa.Servis_Connection;

import java.util.List;


public class UrunListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context c;
    List<ArizaDegisenStokYapisi> urunler;
    ArrayAdapter<String> s_adapter;
    int width,height;
    public UrunListAdapter(Activity activity, List<ArizaDegisenStokYapisi> urun,Context context,int width,int height) {

        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        urunler = urun;
        c=context;
        this.width=width;
        this.height=height;
    }

    @Override
    public int getCount() {
        return urunler.size();
    }

    @Override
    public ArizaDegisenStokYapisi getItem(int position) {
        return urunler.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View satirView;

        satirView = mInflater.inflate(R.layout.ariza_urun_parca_list_item, null);

        TextView takilan_adi = (TextView) satirView.findViewById(R.id.textView43);
        TextView takilen_seri = (TextView) satirView.findViewById(R.id.textView44);
        TextView sokulen_adi = (TextView) satirView.findViewById(R.id.textView46);
        TextView sokulen_seri = (TextView) satirView.findViewById(R.id.textView47);

        if(width>height){//cihaz yan
            takilan_adi.setLayoutParams(new LinearLayout.LayoutParams((width / 8) - 2, LinearLayout.LayoutParams.WRAP_CONTENT));
            takilen_seri.setLayoutParams(new LinearLayout.LayoutParams((width / 8) - 2, LinearLayout.LayoutParams.WRAP_CONTENT));
            sokulen_adi.setLayoutParams(new LinearLayout.LayoutParams((width / 8) - 2, LinearLayout.LayoutParams.WRAP_CONTENT));
            sokulen_seri.setLayoutParams(new LinearLayout.LayoutParams((width / 8) - 2, LinearLayout.LayoutParams.WRAP_CONTENT));
        }else{//cihaz dik
            takilan_adi.setLayoutParams(new LinearLayout.LayoutParams((width / 4) - 2, LinearLayout.LayoutParams.WRAP_CONTENT));
            takilen_seri.setLayoutParams(new LinearLayout.LayoutParams((width / 4) - 2, LinearLayout.LayoutParams.WRAP_CONTENT));
            sokulen_adi.setLayoutParams(new LinearLayout.LayoutParams((width / 4) - 2, LinearLayout.LayoutParams.WRAP_CONTENT));
            sokulen_seri.setLayoutParams(new LinearLayout.LayoutParams((width / 4) - 2, LinearLayout.LayoutParams.WRAP_CONTENT));
        }



        takilan_adi.setText(urunler.get(position).getTakilan_stok_adi());
        sokulen_adi.setText(urunler.get(position).getSokulen_stok_adi());
        takilen_seri.setText(urunler.get(position).getTakilan_seri_no());
        sokulen_seri.setText(urunler.get(position).getSokulen_seri_no());

        //degisen parca listesine tıkladıgımda editlemek veya silmek icin alert dıalog acılacak
        satirView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog a_dialog = new Dialog(c);
                a_dialog.setContentView(R.layout.ariza_stok_dialog);
                a_dialog.setTitle("Ürün Değiştir veya Sil");

                final Spinner takilan_adi = (Spinner) a_dialog.findViewById(R.id.spinner);
                final Spinner sokulen_adi = (Spinner) a_dialog.findViewById(R.id.spinner3);
                final EditText takilan_seri = (EditText) a_dialog.findViewById(R.id.editText);
                final EditText sokulen_seri = (EditText) a_dialog.findViewById(R.id.editText2);
                Button ekle = (Button) a_dialog.findViewById(R.id.stok_ekle_btn);
                Button iptal = (Button) a_dialog.findViewById(R.id.stok_iptal_btn);
                Button sil = (Button) a_dialog.findViewById(R.id.stok_sil_btn);

                ekle.setBackgroundResource(R.mipmap.ic_kaydet);
                s_adapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, FragmentArizaUrunler.stok_adlari);
                s_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                takilan_adi.setAdapter(s_adapter);
                sokulen_adi.setAdapter(s_adapter);

                //acılan componentlerın ıcını doldurduk
                takilan_adi.setSelection(FragmentArizaUrunler.stok_adlari.indexOf(urunler.get(position).getTakilan_stok_adi()));
                sokulen_adi.setSelection(FragmentArizaUrunler.stok_adlari.indexOf(urunler.get(position).getSokulen_stok_adi()));
                takilan_seri.setText(urunler.get(position).getTakilan_seri_no());
                sokulen_seri.setText(urunler.get(position).getSokulen_seri_no());


                ekle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String tak_seri = takilan_seri.getText().toString();
                        final String sok_seri = sokulen_seri.getText().toString();
                        final String sok_id = FragmentArizaUrunler.stok_idleri.get(sokulen_adi.getSelectedItemPosition());
                        final String tak_id = FragmentArizaUrunler.stok_idleri.get(takilan_adi.getSelectedItemPosition());

                        //SECILEN STOK ADI SİSTEME GONDERİLİP GUNCELLENECEK
                        new AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                Servis_Connection servis = new Servis_Connection();
                                return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaDegisenStok.php", "ariza_degisim_id=" + urunler.get(position).getAriza_id() +
                                        "&sokulen_stok_id=" + sok_id + "&takilan_stok_id=" + tak_id + "&takilan_seri_no=" + tak_seri + "&sokulen_seri_no=" + sok_seri + "&tip=2");
                            }

                            @Override
                            protected void onPostExecute(String a) {
                                super.onPostExecute("1");
                                //liste yenılenecek
                                urunler.get(position).setSokulen_stok_adi(sokulen_adi.getSelectedItem().toString());
                                urunler.get(position).setTakilan_stok_adi(takilan_adi.getSelectedItem().toString());
                                urunler.get(position).setSokulen_stok_id(FragmentArizaUrunler.stok_adlari.indexOf(sokulen_adi.getSelectedItem().toString()));
                                urunler.get(position).setTakilan_stok_id(FragmentArizaUrunler.stok_adlari.indexOf(takilan_adi.getSelectedItem().toString()));
                                urunler.get(position).setSokulen_seri_no(sok_seri);
                                urunler.get(position).setTakilan_seri_no(tak_seri);
                                notifyDataSetChanged();
                            }
                        }.execute();
                        a_dialog.dismiss();
                    }
                });

                sil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //silme islemi olacak
                        //silmek istiyormusunz alert ile sor

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
                        alertDialogBuilder.setTitle("Değişen Ürün Sil");
                        alertDialogBuilder
                                .setMessage("Ürünü silmek istediğinize emin misiniz?")
                                .setCancelable(false)
                                .setIcon(R.mipmap.ic_uyari)
                                .setPositiveButton("Evet",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //silmek istiyor ise o urun sılınecek
                                        new AsyncTask<String, Void, String>() {
                                            @Override
                                            protected String doInBackground(String... params) {
                                                Servis_Connection servis = new Servis_Connection();
                                                return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaDegisenStok.php", "ariza_degisim_id=" + urunler.get(position).getAriza_id() + "&tip=4");
                                            }

                                            @Override
                                            protected void onPostExecute(String a) {
                                                super.onPostExecute("1");
                                                //liste yenılenecek
                                                urunler.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        }.execute();
                                        a_dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //silme işlemi iptal edildi alert kapatıldı
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                });

                iptal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a_dialog.dismiss();
                    }
                });

                a_dialog.show();
            }
        });

        return satirView;

    }
}