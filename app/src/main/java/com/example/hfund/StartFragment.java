package com.example.hfund;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


public class StartFragment extends Fragment {

    GridView simpleGrid;
    int logos[] = {R.drawable.ic_menu_camera,R.drawable.ic_menu_camera,R.drawable.ic_menu_camera, R.drawable.ic_menu_camera,
            R.drawable.ic_menu_camera, R.drawable.ic_menu_camera};
    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
                View root=inflater.inflate(R.layout.fragment_start, container, false);


        simpleGrid = (GridView)root.findViewById(R.id.gridview); // init GridView
        // Create an object of CustomAdapter and set Adapter to GirdView
        CustomAdapter customAdapter = new CustomAdapter(getContext(), logos);
        simpleGrid.setAdapter(customAdapter);
        // implement setOnItemClickListener event on GridView
        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set an Intent to Another Activity

                //TODO send position to GalleryFragment
                Log.d("position", String.valueOf(position));
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("position", position);
                editor.apply();
/*
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, new GalleryFragment(), "NewFragmentTag");
                ft.addToBackStack(null);
                ft.commit();

 */

            }
        });

                return root;
    }
}
