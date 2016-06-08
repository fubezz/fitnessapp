package com.fubezz.fitnessapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapsFragment extends Fragment {

    private final LatLng HAMBURG = new LatLng(53.558, 9.927);
    private final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;

    public MapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

//        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
//
//        //map.setMyLocationEnabled(true);
//        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        map.getUiSettings().setMapToolbarEnabled(false);
//
//        Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
//                .title("Hamburg"));
//        Marker kiel = map.addMarker(new MarkerOptions()
//                .position(KIEL)
//                .title("Kiel")
//                .snippet("Kiel is cool")
//                .icon(BitmapDescriptorFactory
//                        .fromResource(R.mipmap.ic_launcher)));
//
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
//
//        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        return rootView;
    }
}
