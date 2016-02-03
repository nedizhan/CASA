package com.mepsan.callcenter.casa.ArizaClass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mepsan.callcenter.casa.Adapter.ImageAdapter;
import com.mepsan.callcenter.casa.Camera_capture;
import com.mepsan.callcenter.casa.FullImageActivity;
import com.mepsan.callcenter.casa.R;
import com.mepsan.callcenter.casa.Servis_Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class FragmentArizaResimleri extends Fragment {

    int width,height;
    LinearLayout layout1;
    Button b1,b2,refresh_once,refresh_sonra;
    public ImageView imgView;
    public TextView imgName;
    ProgressBar prog_sonra,prog_once;

    ProgressDialog prgDialog;
    public String encodedString;
    RequestParams params = new RequestParams();
    String imgPath, fileName;
    Bitmap bitmap_galeri,bmp_kamera;
    Dialog dialog;
    public static byte[] resim = null;
    private String ArizaId;
    private static int RESULT_LOAD_IMG = 1;
    GridView gridView,gridView1;
    ImageAdapter img_adapter_once,img_adapter_sonra;
    List<String> resim_isimleri_once=new ArrayList<>();
    List<String> resim_isimleri_sonra=new ArrayList<>();
    List<GridItem> resimler_once=new ArrayList<>();
    List<GridItem> resimler_sonra=new ArrayList<>();
    Boolean foto_tip=false;
    int once_resim_sayisi=0,sonra_resim_sayisi=0;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ariza_resimler, container, false);

        layout1=(LinearLayout) v.findViewById(R.id.layout_grid1);
        b1=(Button)v.findViewById(R.id.button);
        b2=(Button)v.findViewById(R.id.button3);
        prog_sonra=(ProgressBar) v.findViewById(R.id.progressBar_sonra);
        gridView = (GridView) v.findViewById(R.id.grid_view);
        gridView1=(GridView) v.findViewById(R.id.grid_view1);
        refresh_once=(Button) v.findViewById(R.id.btn_refresh);
        refresh_sonra=(Button) v.findViewById(R.id.btn_ref_sonra);
        prog_once=(ProgressBar) v.findViewById(R.id.progressBar_once);

        resim_isimleri_once.clear();
        resim_isimleri_sonra.clear();
        resimler_once.clear();
        resimler_sonra.clear();

        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        ArizaId = mBundle.getString("ArizaId");

        prgDialog = new ProgressDialog(getActivity());
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        img_adapter_once=new ImageAdapter(getActivity(),resimler_once);
        gridView.setAdapter(img_adapter_once);

        img_adapter_sonra=new ImageAdapter(getActivity(),resimler_sonra);
        gridView1.setAdapter(img_adapter_sonra);

        //arızaya ait resim isimleri cekıldı(once false--- sonra true)
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                Servis_Connection servis=new Servis_Connection();
                return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaResimleri.php?","ariza_id="+ArizaId);
            }
            @Override
            protected void onPostExecute(String a) {
                super.onPostExecute("1");

                if (a != null) {//sonuc null degılse
                    try {
                        JSONObject response = new JSONObject(a);
                        JSONArray posts = response.optJSONArray("resimler");
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.optJSONObject(i);
                            //once false -- sonra true
                            String as=post.getString("foto_tip");
                            if(as.equals("f")) {
                                resim_isimleri_once.add(post.getString("dosya_adi"));
                                //eger onceden resım ındırılmıs ıse resimler listesine ekle
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/casa/" + post.getString("dosya_adi") + ".jpg");
                                if (file.exists()) {//resim varsa
                                    GridItem item=new GridItem();
                                    item.setImage(BitmapFactory.decodeFile(file.getPath()));
                                    resimler_once.add(item);
                                    once_resim_sayisi++;
                                }
                            }
                            else {
                                resim_isimleri_sonra.add(post.getString("dosya_adi"));
                                File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/casa/" + post.getString("dosya_adi") + ".jpg");
                                if (file1.exists()) {//resim varsa
                                    GridItem item=new GridItem();
                                    item.setImage(BitmapFactory.decodeFile(file1.getPath()));
                                    resimler_sonra.add(item);
                                    sonra_resim_sayisi++;
                                }
                            }
                        }

                        if(resim_isimleri_once.size()!=once_resim_sayisi)//resim eksık
                            refresh_once.setVisibility(View.VISIBLE);
                        else
                            refresh_once.setVisibility(View.GONE);

                        if(resim_isimleri_sonra.size()!=sonra_resim_sayisi)//resim eksık
                            refresh_sonra.setVisibility(View.VISIBLE);
                        else
                            refresh_sonra.setVisibility(View.GONE);

                        img_adapter_once.notifyDataSetChanged();//gridview guncelle
                        img_adapter_sonra.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), FullImageActivity.class);
                i.putExtra("id", position);
                i.putExtra("resim_adi", resim_isimleri_once.get(position).toString());
                startActivity(i);
            }
        });
        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), FullImageActivity.class);
                i.putExtra("id", position);
                i.putExtra("resim_adi", resim_isimleri_sonra.get(position).toString());
                startActivity(i);
            }
        });

        //kamera iconuna tıklama- alert acılacak--------once
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto_tip=false;
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.ariza_camera_dialog);

                dialog.setTitle("Arıza Öncesi Resmi Yükle");
                imgView = (ImageView) dialog.findViewById(R.id.imgView);
                imgName=(TextView) dialog.findViewById(R.id.textView13);
                Button button_camera = (Button) dialog.findViewById(R.id.button_camera);
                Button button_yukle=(Button) dialog.findViewById(R.id.button_resim_ok);
                Button button_gallery = (Button) dialog.findViewById(R.id.button_gallery);
                Button button_close = (Button) dialog.findViewById(R.id.button_close);

                button_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent a = new Intent(getActivity().getApplicationContext(),Camera_capture.class);
                        startActivity(a);
                    }
                });

                button_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadImagefromGallery(v);
                    }
                });

                button_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        dialog.dismiss();
                    }
                });

                //resim yukle
                button_yukle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                         if(imgPath!=null || resim!=null){
                             dialog.dismiss();
                             uploadImage();
                        }else{
                             Toast.makeText(getActivity().getApplicationContext(),"Upload Yapabilmek İçin Resim Seçmelisiniz.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        //ariza sonrası resım ekleme
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                foto_tip=true;
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.ariza_camera_dialog);

                dialog.setTitle("Arıza Sonrası Resmi Yükle");
                imgView = (ImageView) dialog.findViewById(R.id.imgView);
                imgName=(TextView) dialog.findViewById(R.id.textView13);
                Button button_camera = (Button) dialog.findViewById(R.id.button_camera);
                Button button_yukle=(Button) dialog.findViewById(R.id.button_resim_ok);
                Button button_gallery = (Button) dialog.findViewById(R.id.button_gallery);
                Button button_close = (Button) dialog.findViewById(R.id.button_close);

                button_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent a = new Intent(getActivity().getApplicationContext(),Camera_capture.class);
                        startActivity(a);
                    }
                });

                button_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadImagefromGallery(v);
                    }
                });

                button_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        dialog.dismiss();
                    }
                });

                //resim yukle
                button_yukle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(imgPath!=null || resim!=null){
                            dialog.dismiss();
                            uploadImage();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(),"Upload Yapabilmek İçin Resim Seçmelisiniz.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        refresh_once.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resim_isimleri_once.clear();
                refresh_once.setVisibility(View.GONE);
                prog_once.setVisibility(View.VISIBLE);

                //indirilmemiş resimleri indir-guncelleme
                new AsyncTask<String, Void, String>(){
                    @Override
                    protected String doInBackground(String... params) {
                        Servis_Connection servis=new Servis_Connection();
                        return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaResimleri.php?","ariza_id="+ArizaId);
                    }
                    @Override
                    protected void onPostExecute(String a) {
                        super.onPostExecute("1");

                        if (a != null) {//sonuc null degılse
                            try {
                                JSONObject response = new JSONObject(a);
                                JSONArray posts = response.optJSONArray("resimler");

                                for (int i = 0; i < posts.length(); i++) {
                                    JSONObject post = posts.optJSONObject(i);
                                    if(post.getString("foto_tip").equals("f"))
                                        resim_isimleri_once.add(post.getString("dosya_adi"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new Resim_indir().execute("once");
                        }
                    }
                }.execute();

            }
        });

        refresh_sonra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resim_isimleri_sonra.clear();
                refresh_sonra.setVisibility(View.GONE);
                prog_sonra.setVisibility(View.VISIBLE);

                //indirilmemiş resimleri indir-guncelleme
                new AsyncTask<String, Void, String>(){
                    @Override
                    protected String doInBackground(String... params) {
                        Servis_Connection servis=new Servis_Connection();
                        return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaResimleri.php?","ariza_id="+ArizaId);
                    }
                    @Override
                    protected void onPostExecute(String a) {
                        super.onPostExecute("1");

                        if (a != null) {//sonuc null degılse
                            try {
                                JSONObject response = new JSONObject(a);
                                JSONArray posts = response.optJSONArray("resimler");

                                for (int i = 0; i < posts.length(); i++) {
                                    JSONObject post = posts.optJSONObject(i);
                                    if(post.getString("foto_tip").equals("t"))
                                        resim_isimleri_sonra.add(post.getString("dosya_adi"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new Resim_indir().execute("sonra");
                        }
                    }
                }.execute();

            }
        });

        return v;
    }

    //galeriyi cagırma
    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    //galeriden resim sectikten sonra calısır
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgPath = cursor.getString(columnIndex);
            cursor.close();
            imgView.setVisibility(View.VISIBLE);
            imgView.setImageBitmap(BitmapFactory .decodeFile(imgPath));
            String fileNameSegments[] = imgPath.split("/");
            fileName = fileNameSegments[fileNameSegments.length - 1];
            params.put("filename", fileName);
            imgName.setText(fileName);

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Seçim Yapmadınız..", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadImage() {

            prgDialog.setMessage("Resim Dönüştürülüyor.");
            prgDialog.show();
            encodeImagetoString();
    }

    // rresmi stringe cevirme
    public void encodeImagetoString() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                if(resim!=null){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp_kamera.compress(Bitmap.CompressFormat.JPEG,70, stream);
                    byte[] byte_arr = stream.toByteArray();
                    encodedString = Base64.encodeToString(byte_arr, 0);
                    resim=null;
                }else {
                    BitmapFactory.Options options = null;
                    options = new BitmapFactory.Options();
                    options.inSampleSize = 5;
                    bitmap_galeri = BitmapFactory.decodeFile(imgPath, options);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap_galeri.compress(Bitmap.CompressFormat.JPEG,70, stream);
                    byte[] byte_arr = stream.toByteArray();
                    encodedString = Base64.encodeToString(byte_arr, 0);
                }

                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                prgDialog.setMessage("Resim Yapılandırılıyor.");
                params.put("image", encodedString);


                makeHTTPCall();
            }
        }.execute(null, null, null);
    }

    //resmi yukleme http
    public void makeHTTPCall() {
        prgDialog.setMessage("Resim Yükleniyor....");
        AsyncHttpClient client = new AsyncHttpClient();

        client.post("http://coman.mepsan.com.tr/Casa/Ariza/UploadImage.php?ariza_id=" + ArizaId + "&foto_tip=" + foto_tip, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity().getApplicationContext(), "Resim Yüklendi.", Toast.LENGTH_LONG).show();

                imgPath = null;
                prgDialog.hide();
                resim = null;
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                if (!foto_tip) {
                    resim_isimleri_once.clear();
                    refresh_once.setVisibility(View.GONE);
                    prog_once.setVisibility(View.VISIBLE);
                    new AsyncTask<String, Void, String>(){
                        @Override
                        protected String doInBackground(String... params) {
                            Servis_Connection servis=new Servis_Connection();
                            return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaResimleri.php?","ariza_id="+ArizaId);
                        }
                        @Override
                        protected void onPostExecute(String a) {
                            super.onPostExecute("1");

                            if (a != null) {//sonuc null degılse
                                try {
                                    JSONObject response = new JSONObject(a);
                                    JSONArray posts = response.optJSONArray("resimler");

                                    for (int i = 0; i < posts.length(); i++) {
                                        JSONObject post = posts.optJSONObject(i);
                                        if(post.getString("foto_tip").equals("f"))
                                            resim_isimleri_once.add(post.getString("dosya_adi"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new Resim_indir().execute("once");
                            }
                        }
                    }.execute();

                } else {
                  //  refresh_sonra.setVisibility(View.VISIBLE);
                    resim_isimleri_sonra.clear();
                    refresh_sonra.setVisibility(View.GONE);
                    prog_sonra.setVisibility(View.VISIBLE);

                    //indirilmemiş resimleri indir-guncelleme
                    new AsyncTask<String, Void, String>(){
                        @Override
                        protected String doInBackground(String... params) {
                            Servis_Connection servis=new Servis_Connection();
                            return servis.select("http://coman.mepsan.com.tr/Casa/Ariza/ArizaResimleri.php?","ariza_id="+ArizaId);
                        }
                        @Override
                        protected void onPostExecute(String a) {
                            super.onPostExecute("1");

                            if (a != null) {//sonuc null degılse
                                try {
                                    JSONObject response = new JSONObject(a);
                                    JSONArray posts = response.optJSONArray("resimler");

                                    for (int i = 0; i < posts.length(); i++) {
                                        JSONObject post = posts.optJSONObject(i);
                                        if(post.getString("foto_tip").equals("t"))
                                            resim_isimleri_sonra.add(post.getString("dosya_adi"));
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new Resim_indir().execute("sonra");
                            }
                        }
                    }.execute();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Something went wrong at server end",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            "Server Bağlantısı Sağlanamıyor : "
                                    + statusCode, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


    @Override
    public void onPause(){
        super.onPause();
        resim=null;
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if(resim!=null) {//kameradan resım cekıldı ıse

            bmp_kamera = BitmapFactory.decodeByteArray(resim, 0, resim.length);
            BitmapDrawable bitdr = new BitmapDrawable(getActivity().getResources(), bmp_kamera);
            imgView.setImageDrawable(bitdr);
            imgView.setVisibility(View.VISIBLE);
            SimpleDateFormat bicim3=new SimpleDateFormat("hhmmss");
            GregorianCalendar gcalender=new GregorianCalendar();
            imgName.setText(bicim3.format(gcalender.getTime())+".jpg");
            params.put("filename", bicim3.format(gcalender.getTime())+".jpg");
        }


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
            layout1.setLayoutParams(new LinearLayout.LayoutParams(width/2, LinearLayout.LayoutParams.MATCH_PARENT));
        }else {//dik ise
            height=height-(actionBarHeight*2);
            layout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height / 2));
        }

    }

    //resim kaydetme
    private void saveImageToSD(Bitmap gelen,String adi) {

        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/casa";
            File dir = new File(file_path);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dir, adi+ ".jpg");

            FileOutputStream fOut;
            fOut = new FileOutputStream(file);
            gelen.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

        }catch (Exception e){
        }

    }


    public static FragmentArizaResimleri newInstance(String text) {

        FragmentArizaResimleri f = new FragmentArizaResimleri();
        Bundle b = new Bundle();
        b.putString("ArizaId", text);
        f.setArguments(b);

        return f;
    }


    private class Resim_indir extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // Simulates a background job.
           // params[0]- resim ismi
            Bitmap bitmap1=null;
            try {
                if(params[0].equals("once")) {//onceki resimleri indirme

                    resimler_once.clear();
                    for (int i = 0; i < resim_isimleri_once.size(); i++) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/casa/" + resim_isimleri_once.get(i) + ".jpg");

                        if (!file.exists()) {//resim yoksa
                            bitmap1 = BitmapFactory.decodeStream((InputStream) new URL("http://coman.mepsan.com.tr/Casa/uploadedimages/ariza/" + resim_isimleri_once.get(i) + ".jpg").getContent());
                            saveImageToSD(bitmap1, resim_isimleri_once.get(i));//kaydet
                        } else {
                            File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/casa/" + resim_isimleri_once.get(i) + ".jpg");
                            bitmap1 = BitmapFactory.decodeFile(file1.getPath());
                        }

                        GridItem item = new GridItem();
                        item.setImage(bitmap1);
                        resimler_once.add(item);

                    }
                }else if(params[0].equals("sonra")) {//sonraki reismleri indirme

                    resimler_sonra.clear();
                    for (int i = 0; i < resim_isimleri_sonra.size(); i++) {
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/casa/" + resim_isimleri_sonra.get(i) + ".jpg");

                        if (!file.exists()) {//resim yoksa
                            bitmap1 = BitmapFactory.decodeStream((InputStream) new URL("http://coman.mepsan.com.tr/Casa/uploadedimages/ariza/" + resim_isimleri_sonra.get(i) + ".jpg").getContent());
                            saveImageToSD(bitmap1, resim_isimleri_sonra.get(i));//kaydet
                        } else {
                            File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/casa/" + resim_isimleri_sonra.get(i) + ".jpg");
                            bitmap1 = BitmapFactory.decodeFile(file1.getPath());
                        }

                        GridItem item = new GridItem();
                        item.setImage(bitmap1);
                        resimler_sonra.add(item);

                    }
                }

            } catch (Exception e) {
            }
            return "a";
        }

        @Override
        protected void onPostExecute(String a) {
            super.onPostExecute("1");
            img_adapter_once.notifyDataSetChanged();
            img_adapter_sonra.notifyDataSetChanged();

            prog_once.setVisibility(View.GONE);
            prog_sonra.setVisibility(View.GONE);

        }
    }
}