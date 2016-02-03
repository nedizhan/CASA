package com.mepsan.callcenter.casa.ArizaClass;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mepsan.callcenter.casa.R;

public class FragmentArizaFormu extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ariza_fragment_formu, container, false);

        return v;
    }

    public static FragmentArizaFormu newInstance(String text) {

        FragmentArizaFormu f = new FragmentArizaFormu();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);

        return f;
    }




}