package com.infurza.infurzaapplication.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infurza.infurzaapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EWasteBuyFragment extends Fragment {


    public EWasteBuyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ewaste_buy, container, false);
    }

}
