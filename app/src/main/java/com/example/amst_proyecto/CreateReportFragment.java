package com.example.amst_proyecto;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CreateReportFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;

    private double latitude;
    private double longitude;

    private String nombreBus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_report, container, false);

        mapView = rootView.findViewById(R.id.createReportMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    public void loadDataPosition(double _latitude, double _longitude, String _nombreBus){
        latitude = _latitude;
        longitude = _longitude;
        nombreBus = _nombreBus;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        map.getUiSettings().setMapToolbarEnabled(false); // Deshabilita la barra de herramientas
        map.getUiSettings().setZoomControlsEnabled(true); // Deshabilita los controles de zoom
        map.setBuildingsEnabled(false); // Deshabilita los edificios en 3D
        map.setIndoorEnabled(false); // Deshabilita la vista interior

        // Configurar opciones del mapa, por ejemplo:
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Agregar un marcador en una ubicación específica, por ejemplo:
        LatLng location = new LatLng(latitude, longitude);

        // Mover la cámara al marcador
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 19));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(location)
                                .title(nombreBus);
                        map.addMarker(markerOptions);
                    }
                });
            }
        }, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
