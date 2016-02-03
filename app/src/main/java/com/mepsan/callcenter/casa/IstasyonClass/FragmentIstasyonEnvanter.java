package com.mepsan.callcenter.casa.IstasyonClass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mepsan.callcenter.casa.R;


public class FragmentIstasyonEnvanter extends Fragment {


    String ist_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.istasyon_fragment_envanter, container, false);


        Bundle mBundle = new Bundle();
        mBundle = getArguments();
        ist_id = mBundle.getString("istasyon_id");
        container = (LinearLayout)v.findViewById(R.id.container);


        return v;
    }

    public static FragmentIstasyonEnvanter newInstance(String text) {

        FragmentIstasyonEnvanter f = new FragmentIstasyonEnvanter();
        Bundle b = new Bundle();
        b.putString("istasyon_id", text);
        f.setArguments(b);
        return f;
    }


}
