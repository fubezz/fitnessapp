package com.fubezz.fitnessapp;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap map = null;
    private RunSession session;

    public MapsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        Bundle b = this.getArguments();
        session = new DbHandler(this.getContext()).getRunSession(b.getLong("runsession"));

        SupportMapFragment frag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        frag.getMapAsync(this);

        return rootView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(true);

        List<Location> locs = session.getListofLocations();
        if(locs != null) {
            PolylineOptions polLineOp = new PolylineOptions().width(15).color(Color.RED);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Location l : locs) {
                LatLng p = new LatLng(l.getLatitude(), l.getLongitude());
                polLineOp.add(p);
                builder.include(p);
            }
            // map is the Map Object
            map.addPolyline(polLineOp);
            final LatLngBounds bounds = builder.build();
            final int padding = 200; // offset from edges of the map in pixels

            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    // map.moveCamera(cu);
                    map.animateCamera(cu);
                }
            });
        }
    }
}
