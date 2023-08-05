package com.example.amst_proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class entorno_principal extends AppCompatActivity implements OnMapReadyCallback, AdapterItemHorario.OnItemClickListener {

    // Declaracion de los elementos relacionado a la interfaz grafica
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private View headerView;
    private Menu menuNav;

    // Declaracion de los elementos relacionado a googleMaps
    private MapView mapView;
    private GoogleMap googleMap;

    // Declaracion de variables relacionada a los permisos
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    // Declaracion de Adaptadores
    private AdapterItemHorario adapterHorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entorno_principal);

        // Declaracion de elemento visuales
        toolbar = findViewById(R.id.idToolBar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.idNavigationView);
        drawerLayout = findViewById(R.id.idDrawerLayout);
        headerView = navigationView.getHeaderView(0);
        menuNav = navigationView.getMenu();

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Habilitar el ícono del menú en la barra de acción
        // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);

        // Configurar el listener para abrir/cerrar el menú lateral
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Carga los elementos del usuario en el menu
        loadDataUser();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HorariosBusesFragment()).commit();
            navigationView.setCheckedItem(R.id.idAdminHorariosBuses);
        }

        // Define los eventos de los items del menu, sea para el administrador o el estudiante
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Aquí puedes manejar el evento onClick del elemento del menú seleccionado
                int idItem = item.getItemId();

                // Eventos para el administrador
                if(idItem == R.id.idAdminHorariosBuses){


                } else if (idItem == R.id.idAdminVisualizarBuses) {

                } else if (idItem == R.id.idAdminReporteMap) {

                } else if (idItem == R.id.idEditarHorarios) {

                } else if (idItem == R.id.idAdminAgregarHorario) {

                } else if (idItem == R.id.idAdminListaReporte) {

                } else if (idItem == R.id.idAdminCerrarSesion) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Intent intent = new Intent(entorno_principal.this, iniciar_sesion.class);
                    intent.putExtra("msg", "cerrarSesion");
                    startActivity(intent);
                }

                // Eventos para el estudiante
                else if (idItem == R.id.idEstudianteHorariosBuses) {
                    // Definir el titulo de la toolbar
                    toolbar.setTitle("Horarios de los Buses");

                    // Infla el archivo xml en el contenedor de la actividad
                    //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HorrariosBusesFragment()).commit();

                    // Declara el objeto que representa el archivo xml que se piensa inflar
                    HorariosBusesFragment horariosBusesFragment = new HorariosBusesFragment();

                    // Infla el archivo xml en el contenedor de la actividad
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, horariosBusesFragment)
                            .commit();

                    // Las modificaciones de elementos de la interfaz grafica se generan despues de 1ms
                    // Nota: Se debe esperar un tiempo para inflar el xml, o si no genera error.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {

                                    // Se implementa los elementos para el recycleView, en este caso los horarios
                                    RecyclerView recyclerViewHorario;
                                    recyclerViewHorario = horariosBusesFragment.requireView().findViewById(R.id.idRecycleViewHorarios);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(entorno_principal.this);
                                    recyclerViewHorario.setLayoutManager(layoutManager);

                                    List<String> horasinicio = new ArrayList<>();
                                    List<String> horasfin = new ArrayList<>();

                                    // Agregar arreglos a la lista de listas
                                    horasinicio.add("14:00");
                                    horasinicio.add("16:00");
                                    horasinicio.add("20:00");

                                    horasfin.add("16:00");
                                    horasfin.add("18:00");
                                    horasfin.add("20:00");

                                    adapterHorario = new AdapterItemHorario(horasinicio,horasfin);
                                    adapterHorario.setOnItemClickListener(entorno_principal.this);
                                    recyclerViewHorario.setAdapter(adapterHorario);

                                }
                            });
                        }
                    }, 1);

                } else if (idItem == R.id.idEstudiantReporteMap) {
                    // Definir el titulo de la toolbar
                    toolbar.setTitle("Reporte");

                    // Declara el objeto que representa el archivo xml que se piensa inflar
                    UbicacionBusesFragment ubicacionBusesFragment = new UbicacionBusesFragment();

                    // Infla el archivo xml en el contenedor de la actividad
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, ubicacionBusesFragment)
                            .commit();

                    // Las modificaciones de elementos de la interfaz grafica se generan despues de 1ms
                    // Nota: Se debe esperar un tiempo para inflar el xml, o si no genera error.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (ContextCompat.checkSelfPermission(entorno_principal.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // El permiso no se ha concedido, se solicita al usuario
                                ActivityCompat.requestPermissions(entorno_principal.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION_PERMISSION);
                            } else {
                                // El permiso ya se ha concedido, puedes realizar las operaciones relacionadas con la ubicación
                                // aquí mismo o en algún otro lugar de tu código.
                            }

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mapView = ubicacionBusesFragment.requireView().findViewById(R.id.mapViewImage);
                                    mapView.onCreate(savedInstanceState);
                                    mapView.getMapAsync(entorno_principal.this);
                                }
                            });


                        }
                    }, 1);

                } else if (idItem == R.id.idEstudiantCerrarSesion) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Intent intent = new Intent(entorno_principal.this, iniciar_sesion.class);
                    intent.putExtra("msg", "cerrarSesion");
                    startActivity(intent);
                }

                // Cerrar el DrawerLayout después de seleccionar un elemento del menú
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

    }

    // Definir el evento onClick para in item de AdapterHorario
    @Override
    public void onItemClick(int position) {
        // Mensaje
        Toast.makeText(this, "Elemento en la posición " + position + " clickeado", Toast.LENGTH_SHORT).show();
/*
        // Declara el objeto que representa el archivo xml que se piensa inflar
        UbicacionBusesFragment ubicacionBusesFragment = new UbicacionBusesFragment();

        // Infla el archivo xml en el contenedor de la actividad


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ubicacionBusesFragment)
                .commit();

        // Las modificaciones de elementos de la interfaz grafica se generan despues de 1ms
        // Nota: Se debe esperar un tiempo para inflar el xml, o si no genera error.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (ContextCompat.checkSelfPermission(entorno_principal.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    // El permiso no se ha concedido, se solicita al usuario
                    ActivityCompat.requestPermissions(entorno_principal.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_PERMISSION);
                } else {
                    // El permiso ya se ha concedido, puedes realizar las operaciones relacionadas con la ubicación
                    // aquí mismo o en algún otro lugar de tu código.
                }

                mapView = ubicacionBusesFragment.requireView().findViewById(R.id.mapViewImage);
                //mapView.onCreate(savedInstanceState);
                mapView.getMapAsync(entorno_principal.this);



            }
        }, 1);*/
    }


    // Define los parametros del elemnto que representa googleMaps
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        map.getUiSettings().setMapToolbarEnabled(false); // Deshabilita la barra de herramientas
        map.getUiSettings().setZoomControlsEnabled(true); // Deshabilita los controles de zoom
        map.setBuildingsEnabled(false); // Deshabilita los edificios en 3D
        map.setIndoorEnabled(false); // Deshabilita la vista interior

        // Configurar opciones del mapa, por ejemplo:
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Agregar un marcador en una ubicación específica, por ejemplo:
        LatLng location = new LatLng(-2.19616, -79.88621);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("Guayaquil");
        googleMap.addMarker(markerOptions);

        // Mover la cámara al marcador
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    private void loadDataUser(){
        // Obtener datos del usuario obtenidos de la actividad anterior
        Intent intent = getIntent();
        HashMap<String, String> info_user = (HashMap<String, String>) intent.getSerializableExtra("info_user");
        //System.out.println("Informacion"+ info_user);

        // Declaracion de elementos
        TextView txt_name = headerView.findViewById(R.id.idUserName);
        TextView txt_email = headerView.findViewById(R.id.idUserEmail);
        ImageView imv_photo = headerView.findViewById(R.id.idProfileImage);

        // Definir los elementos del usuario
        txt_name.setText(info_user.get("user_name"));
        txt_email.setText(info_user.get("user_email"));
        String photo = info_user.get("user_photo");
        Picasso.get().load(photo).into(imv_photo);

        // Dependiendo del rol del usuario se infla el menú correspondiente
        if (false) {
            getMenuInflater().inflate(R.menu.menu_principal_administrador, menuNav);
        } else {
            getMenuInflater().inflate(R.menu.menu_principal_estudiante, menuNav);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}