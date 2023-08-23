package com.example.amst_proyecto;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.amst_proyecto.data.Coordenada;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class UbicacionesBusesMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;

    private double latitude;
    private double longitude;

    private String nombreBus;

    private ArrayList<Coordenada> coordenadas;

    private ArrayList<LatLng> locations = new ArrayList<LatLng>();

    private ArrayList<Marker> tmpMarkers = new ArrayList<>();
    private ArrayList<Marker> Markers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ubicacion_buses, container, false);

        mapView = rootView.findViewById(R.id.mapViewImage);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    public void loadDataPosition(double _latitude, double _longitude, String _nombreBus){
        latitude = _latitude;
        longitude = _longitude;
        nombreBus = _nombreBus;
    }

    public void setNombreArrayList(ArrayList<Coordenada> coordenadas) {
        this.coordenadas = coordenadas;
    }

    @Override
    public String toString() {
        return "UbicacionesBusesMapFragment{" +
                "coordenadas=" + coordenadas +
                '}';
    }

    @Override
    public void onMapReady(GoogleMap map) {
        locations.clear();
        map.getUiSettings().setMapToolbarEnabled(false); // Deshabilita la barra de herramientas
        map.getUiSettings().setZoomControlsEnabled(true); // Deshabilita los controles de zoom
        map.setBuildingsEnabled(false); // Deshabilita los edificios en 3D
        map.setIndoorEnabled(false); // Deshabilita la vista interior

        // Configurar opciones del mapa, por ejemplo:
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Agregar un marcador en una ubicación específica, por ejemplo:
        for (Coordenada coordenada : coordenadas){
            System.out.println(coordenada);
            LatLng location = new LatLng(coordenada.getLatitude(), coordenada.getLongitude());
            locations.add(location);
            // Mover la cámara al marcador
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        for(Marker marker : Markers){
                            marker.remove();
                        }

                        for(int i=0 ; i<locations.size(); i++){
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(locations.get(i))
                                    .title(coordenadas.get(i).getNombreBus());
                            System.out.println("********************************************");
                            System.out.println(markerOptions.getPosition().latitude);
                            System.out.println(markerOptions.getPosition().longitude);
                            System.out.println("********************************************");
                            tmpMarkers.add(map.addMarker(markerOptions));
                        }
                        Markers.clear();
                        Markers.addAll(tmpMarkers);
                    }
                });
            }
        }, 1500);
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