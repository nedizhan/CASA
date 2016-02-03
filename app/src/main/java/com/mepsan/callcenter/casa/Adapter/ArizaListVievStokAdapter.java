package com.mepsan.callcenter.casa.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaUrunler;
import com.mepsan.callcenter.casa.ListViewItem;
import com.mepsan.callcenter.casa.R;
import com.mepsan.callcenter.casa.Servis_Connection;

import java.util.List;


public class ArizaListVievStokAdapter extends ArrayAdapter<ListViewItem> {

    Context c;
    List<ListViewItem> items;
    public ArizaListVievStokAdapter(Context context, List<ListViewItem> items) {
        super(context, R.layout.listview_item, items);
        c=context;
        this.items=items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        ImageView isim_degistir;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.ariza_stok_listview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            viewHolder.ivIcon2 = (ImageView) convertView.findViewById(R.id.ivIcon2);
            isim_degistir = (ImageView) convertView.findViewById(R.id.ivIcon3);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);

            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        final ListViewItem item = getItem(position);
        viewHolder.ivIcon.setImageDrawable(item.icon);
        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvDescription.setText(item.description);
        viewHolder.ivIcon2.setImageDrawable(item.icon2);

        isim_degistir = (ImageView) convertView.findViewById(R.id.ivIcon3);

        //stok adi duzenleme butonu
        isim_degistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stok yapısındaki stok isimlerini gecici listeye aktardık. Adaptere baglayabılmek ıcın yaptık
                final ArrayAdapter<String> s_adapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, FragmentArizaUrunler.stok_adlari);
                s_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


                ////////////////////alert dialogu ac
                final Dialog dialog = new Dialog(c);
                dialog.setContentView(R.layout.ariza_stok_rename_dialog);
                dialog.setTitle("Stok İsmini Değiştir");

                final Spinner takilan_adi = (Spinner) dialog.findViewById(R.id.spinner2);
                Button guncelle = (Button) dialog.findViewById(R.id.stok_adi_degistir);
                Button iptal = (Button) dialog.findViewById(R.id.stok_adi_iptal);

                takilan_adi.setAdapter(s_adapter);

                takilan_adi.setSelection(FragmentArizaUrunler.stok_adlari.indexOf(item.getTitle()));//stok ismini spinnerda secili getirdik

                //stok adı guncellendı
                guncelle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //SECILEN STOK ADI SİSTEME GONDERİLİP GUNCELLENECEK

                        int index = FragmentArizaUrunler.stok_adlari.indexOf(takilan_adi.getSelectedItem().toString());//secilen stok isminin indexi
                        final String stok_id = FragmentArizaUrunler.stok_idleri.get(index);
                        new AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... params) {
                                Servis_Connection servis = new Servis_Connection();
                                return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaDegisenStok.php", "ariza_stok_id=" + item.id + "&stok_id=" + stok_id + "&tip=3");
                            }

                            @Override
                            protected void onPostExecute(String gelen) {
                                super.onPostExecute("1");
                                //stok listesi yenılenecek
                                FragmentArizaUrunler.ArizaStokItems.get(position).setTitle(takilan_adi.getSelectedItem().toString());
                                FragmentArizaUrunler.stok_adi.setText(takilan_adi.getSelectedItem().toString() + " Arıza Tipleri");
                                notifyDataSetChanged();
                            }
                        }.execute();
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
        return convertView;
    }



    private static class ViewHolder {
        ImageView ivIcon;
        ImageView ivIcon2;
        TextView tvTitle;
        TextView tvDescription;
    }
}