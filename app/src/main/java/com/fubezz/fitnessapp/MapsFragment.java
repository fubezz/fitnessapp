package com.fubezz.fitnessapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

public class MapsFragment extends Fragment {

    private final LatLng HAMBURG = new LatLng(53.558, 9.927);
    private final LatLng KIEL = new LatLng(53.551, 9.993);
    private GoogleMap map;
    private RunSession session;

    public MapsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        Bundle b = this.getArguments();
        RunSession session = new DbHandler(this.getContext()).getRunSession(b.getLong("runsession"));
        String locations = session.getLocations();
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.getUiSettings().setMapToolbarEnabled(false);
        if (locations != null && locations.length() > 0 && locations.charAt(locations.length()-1)=='x') {
            locations = locations.substring(1, locations.length()-1);
            String[] loc = locations.split("><");
            if(loc.length > 1){
                PolygonOptions polLoc = new PolygonOptions();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (String l: loc){
                    String[] pos = l.split("/");
                    int latitude = Integer.parseInt(pos[1]);
                    int longitude = Integer.parseInt(pos[0]);
                    LatLng p = new LatLng(latitude,longitude);
                    polLoc.add(p);
                    builder.include(p);
                }
                map.addPolygon(polLoc.strokeColor(Color.RED));
                LatLngBounds bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                map.moveCamera(cu);
                map.animateCamera(cu);

            }
        }


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
