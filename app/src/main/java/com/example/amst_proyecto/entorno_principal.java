package com.example.amst_proyecto;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class entorno_principal extends AppCompatActivity implements OnMapReadyCallback, AdapterItemHorario.OnItemClickListener, AdapterItemRuta.OnItemClickListener,AdapterItemReporte.OnItemClickListener {

    // Declaracion de los elementos relacionado a la interfaz grafica
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private View headerView;
    private Menu menuNav;

    // Declaracion del Callback que representa la conexion de iternet
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager mConnectivityManager;

    // Declaracion de los elementos relacionado a googleMaps
    private MapView mapView;
    private GoogleMap googleMap;

    // Declaracion de variables relacionada a los permisos
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    // Declaracion de Adaptadores
    private AdapterItemHorario adapterHorario;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entorno_principal);

        // Definir la operacion de los estados de conexion de internet
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                // Esta conectado a internet
                Toast.makeText(entorno_principal.this, "Conexion Restablecida", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                // Se perdió la conexión a Internet
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new NoConexionInternetFragment()).commit();
            }
        };

        // Registrar el NetworkCallback
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        mConnectivityManager.registerNetworkCallback(request, networkCallback);

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
            if(isConectedInternet()){
                inflarActRutaBuses();
            } else{
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new NoConexionInternetFragment()).commit();
            }
        }

        // Define los eventos de los items del menu, sea para el administrador o el estudiante
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Aquí puedes manejar el evento onClick del elemento del menú seleccionado
                int idItem = item.getItemId();

                // Eventos para el administrador
                if(idItem == R.id.idAdminRutasBuses){
                    if(isConectedInternet()){
                        inflarActRutaBuses();
                    }
                } else if (idItem == R.id.idAdminVisualizarBuses) {

                } else if (idItem == R.id.idAdminReporteMap) {

                } else if (idItem == R.id.idEditarHorarios) {

                } else if (idItem == R.id.idAdminAgregarHorario) {

                } else if (idItem == R.id.idAdminListaReporte) {
                    if(isConectedInternet()){
                        inflarListaReporte();
                    }
                } else if (idItem == R.id.idAdminCerrarSesion) {
                    if(isConectedInternet()){
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Intent intent = new Intent(entorno_principal.this, iniciar_sesion.class);
                        intent.putExtra("msg", "cerrarSesion");
                        startActivity(intent);
                    }
                }

                // Eventos para el estudiante
                else if (idItem == R.id.idEstudianteRutasBuses) {
                    if(isConectedInternet()){
                        inflarActRutaBuses();
                    }
                }
                else if (idItem == R.id.idEstudiantReporteMap) {
                    // Definir el titulo de la toolbar
                    toolbar.setTitle("Reportes");

                }
                else if (idItem == R.id.idEstudiantCerrarSesion) {
                    if(isConectedInternet()){
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        Intent intent = new Intent(entorno_principal.this, iniciar_sesion.class);
                        intent.putExtra("msg", "cerrarSesion");
                        startActivity(intent);
                    }
                }

                // Cerrar el DrawerLayout después de seleccionar un elemento del menú
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });

    }



    // Definir el evenbto onClick para el item de AdapterRuta
    public void onItemRutaClick(int position) {
        // Mensaje que indique el objeto seleccionado
        Toast.makeText(this, "Elemento en la posición " + position + " clickeado", Toast.LENGTH_SHORT).show();

        // Definir el titulo de la toolbar
        toolbar.setTitle("Horarios de la ruta");

        // Declara el objeto que representa el archivo xml que se piensa inflar
        ListElementsFragment horariosBusesFragment = new ListElementsFragment();

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
                        recyclerViewHorario = horariosBusesFragment.requireView().findViewById(R.id.idRecycleViewListElements);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(entorno_principal.this);
                        recyclerViewHorario.setLayoutManager(layoutManager);

                        // Lista de los horarios de cada bus (Lista de ejemplo)
                        List<String> horas = new ArrayList<>();
                        horas.add("14:00");
                        horas.add("16:00");
                        horas.add("20:00");
                        horas.add("16:00");
                        horas.add("20:00");

                        // Construccion del adptador para cargar los elementos a cada item


                        AdapterItemHorario adpaterHorarios;
                        adpaterHorarios = new AdapterItemHorario(horas);
                        adpaterHorarios.setOnItemClickListener(entorno_principal.this);
                        recyclerViewHorario.setAdapter(adpaterHorarios);
                    }
                });
            }
        }, 1);
    }

    // Definir el evento onClick para el item de AdapterHorario
    @Override
    public void onItemHorarioClick(int position) {
        // Mensaje
        Toast.makeText(this, "Elemento en la posición " + position + " clickeado", Toast.LENGTH_SHORT).show();

        // Declara el objeto que representa el archivo xml que se piensa inflar
        UbicacionBusesFragment ubicacionBusesFragment = new UbicacionBusesFragment();

        // Solicita el permiso para solicitar la ubicacion del dispositivo y usa googlemaps
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

        ubicacionBusesFragment.loadDataPosition(-2.19616, -79.88621,"Bus Ruta N");

        // Infla el archivo xml en el contenedor de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ubicacionBusesFragment)
                .commit();
    }

    // Define los parametros del elemnto que representa googleMaps
    @Override
    public void onMapReady(GoogleMap map) {

        map.getUiSettings().setMapToolbarEnabled(false); // Deshabilita la barra de herramientas
        map.getUiSettings().setZoomControlsEnabled(true); // Deshabilita los controles de zoom
        map.setBuildingsEnabled(false); // Deshabilita los edificios en 3D
        map.setIndoorEnabled(false); // Deshabilita la vista interior

        // Configurar opciones del mapa, por ejemplo:
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Agregar un marcador en una ubicación específica, por ejemplo:
        LatLng location = new LatLng(-2.19616, -79.88621);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(location)
                .title("Guayaquil");
        map.addMarker(markerOptions);

        // Mover la cámara al marcador
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectivityManager.unregisterNetworkCallback(networkCallback);
        }

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

        // Tipo de usuario
        String userType = info_user.get("user_type");

        // Declaracion de elementos
        TextView txt_name = headerView.findViewById(R.id.idUserName);
        TextView txt_email = headerView.findViewById(R.id.idUserEmail);
        ImageView imv_photo = headerView.findViewById(R.id.idProfileImage);
        TextView txt_typeUser = headerView.findViewById(R.id.idTextViewRol);

        // Definir los elementos del usuario
        txt_name.setText(info_user.get("user_name"));
        txt_email.setText(info_user.get("user_email"));
        String photo = info_user.get("user_photo");
        Picasso.get().load(photo).into(imv_photo);
        txt_typeUser.setText(userType);


        System.out.println(userType);
        // Dependiendo del rol del usuario se infla el menú correspondiente
        if (userType.equals("Estudiante")) {
            getMenuInflater().inflate(R.menu.menu_principal_estudiante, menuNav);
        } else {
            getMenuInflater().inflate(R.menu.menu_principal_administrador, menuNav);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    // Permite verificar la conexion a internet
    private boolean isConectedInternet(){
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Este codigo permite definir los elemntos para la opcion rutas buses en la interfaza grafica
    private void inflarActRutaBuses(){
        // Definir el titulo de la toolbar
        toolbar.setTitle("Rutas de los Buses");

        // Declara el objeto que representa el archivo xml que se piensa inflar
        ListElementsFragment rutasBusesFragment = new ListElementsFragment();

        // Infla el archivo xml en el contenedor de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, rutasBusesFragment)
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
                        RecyclerView recyclerViewRuta;
                        recyclerViewRuta = rutasBusesFragment.requireView().findViewById(R.id.idRecycleViewListElements);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(entorno_principal.this);
                        recyclerViewRuta.setLayoutManager(layoutManager);

                        // Lista de ejemplo para comprobar el codigo realizado
                        List<String> rutas = new ArrayList<>();
                        rutas.add("Ruta 1");
                        rutas.add("Ruta 2");
                        rutas.add("Ruta 3");
                        rutas.add("Ruta 4");
                        rutas.add("Ruta 5");
                        rutas.add("Ruta 6");

                        // Construccion del adptador para cargar los elementos a cada item
                        AdapterItemRuta adpaterRutas;
                        adpaterRutas = new AdapterItemRuta(rutas);
                        adpaterRutas.setOnItemClickListener(entorno_principal.this);
                        recyclerViewRuta.setAdapter(adpaterRutas);
                    }
                });
            }
        }, 1);
    }

    //
    public void onClickReintentar(View v){

        if(isConectedInternet()){
            Toast.makeText(this, "Conexion Restablecida", Toast.LENGTH_SHORT).show();
            inflarActRutaBuses();
        }
        else{
            Toast.makeText(this, "No hay Conexion", Toast.LENGTH_SHORT).show();
        }
    }

    private void inflarListaReporte(){
        // Definir el titulo de la toolbar
        toolbar.setTitle("Listado de Reportes");

        // Declara el objeto que representa el archivo xml que se piensa inflar
        ListElementsFragment listadoReporteFragment = new ListElementsFragment();

        // Infla el archivo xml en el contenedor de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, listadoReporteFragment)
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
                        RecyclerView recyclerViewRuta;
                        recyclerViewRuta = listadoReporteFragment.requireView().findViewById(R.id.idRecycleViewListElements);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(entorno_principal.this);
                        recyclerViewRuta.setLayoutManager(layoutManager);

                        // Lista de ejemplo para comprobar el codigo realizado
                        List<String> listReporte = new ArrayList<>();
                        listReporte.add("Reporte 1");
                        listReporte.add("Reporte 2");
                        listReporte.add("Reporte 3");
                        listReporte.add("Reporte 4");
                        listReporte.add("Reporte 5");
                        listReporte.add("Reporte 6");

                        // Construccion del adptador para cargar los elementos a cada item
                        AdapterItemReporte adpaterReportes;
                        adpaterReportes = new AdapterItemReporte(listReporte);
                        adpaterReportes.setOnItemClickListener(entorno_principal.this);
                        recyclerViewRuta.setAdapter(adpaterReportes);
                    }
                });
            }
        }, 1);
    }

    public static String convertToFormattedString(String input) {
        String dateString = input.substring(8, 16); // Extract "20230813T"
        String timeString = input.substring(17,23); // Extract "192030"

        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HHmmss");
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            Date date = inputDateFormat.parse(dateString);
            Date time = inputTimeFormat.parse(timeString);

            String formattedDate = outputDateFormat.format(date);
            String formattedTime = outputTimeFormat.format(time);

            return "Reporte " + formattedDate + " " + formattedTime;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}