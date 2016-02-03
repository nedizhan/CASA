package com.mepsan.callcenter.casa.IstasyonClass;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;


import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaAciklama;
import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaBilgisi;
import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaFormu;
import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaKonumu;
import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaResimleri;
import com.mepsan.callcenter.casa.ArizaClass.FragmentArizaUrunler;
import com.mepsan.callcenter.casa.R;

public class IstasyonDetayActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public String IstasyonId,userId;
    public static String ist_enlem,ist_boylam;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_istasyon_detay, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.istasyon_activity_detay);
        IstasyonDetayActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle paketim;
        paketim = getIntent().getExtras();
        IstasyonId    = paketim.getString("id");
        userId    = paketim.getString("user_id");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_ist);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container_ist);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_ist);


        TelephonyManager manager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){

        }else{
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_ist);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.ariza_fragment_detay, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //return PlaceholderFragment.newInstance(position + 1);
            switch(position) {

                case 0: FragmentIstasyonBilgisi a = new FragmentIstasyonBilgisi();
                    Bundle bundle = new Bundle();
                    bundle.putInt("key", 1);
                    a.setArguments(bundle);
                    return FragmentIstasyonBilgisi.newInstance(IstasyonId);
                case 1: return FragmentIstasyonEnvanter.newInstance(IstasyonId);
                default: return FragmentIstasyonBilgisi.newInstance(IstasyonId);
            }
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    setTitle("İstasyon Detayı");
                    return "İstasyon Bilgisi";
                case 1:
                    setTitle("Envanter");
                    return "Envanter";
                case 2:
                    setTitle("İstasyon Konumu");
                    return "Konum";

            }
            return null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }
    }
}
