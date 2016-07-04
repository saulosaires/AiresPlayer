package com.airesplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.airesplayer.R;

public class EmptyFragment extends Fragment {

    public static EmptyFragment newInstance() {

        return new EmptyFragment();
    }

    public EmptyFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_empty, container, false);
    }


}
