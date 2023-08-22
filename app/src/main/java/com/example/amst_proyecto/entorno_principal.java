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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class entorno_principal extends AppCompatActivity implements AdapterItemHorario.OnItemClickListener, AdapterItemRuta.OnItemClickListener, AdapterItemSelectReport.OnItemClickListener {

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

    // Declaracion de variables relacionada a los permisos
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    // Declaracion de Adaptadores
    private AdapterItemHorario adpaterHorarios;
    private AdapterItemRuta adpaterRutas;
    private AdapterItemSelectReport adaptarSelectReport;

    // Referencia de la base de datos
    DatabaseReference db_reference;

    // Listas de elementos de la base de datos para la creacion de reporte
    List<String> listRutas = new ArrayList<>();
    List<String> listHoras = new ArrayList<>();
    List<String> listRutaBuses = new ArrayList<>();
    List<String> listSelectReport = new ArrayList<>();

    // Variables auxiliares para la creacion de reporte
    Boolean isReported = false;
    String rutaSelected = null;
    String busSelected = null;
    String hourBusSelected = null;
    String reportSelected = null;
    double latitudeBusSelected;
    double longitudeBusSelected;


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

        // Inicializa la referencia de la base de datos
        db_reference = FirebaseDatabase.getInstance().getReference().child("Data_app");

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
                } else if (idItem == R.id.idEstudiantReporteMap) {
                    // Definir el titulo de la toolbar
                    toolbar.setTitle("Reportes");

                } else if (idItem == R.id.idEstudiantCerrarSesion) {
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
        // Busca el nombre de la ruta en la lista
        rutaSelected = listRutas.get(position);

        // Mensaje que indique el objeto seleccionado
        //Toast.makeText(this, "Elemento en la posición " + position + " clickeado", Toast.LENGTH_SHORT).show();

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

                        // Construccion del adptador para cargar los elementos a cada item
                        adpaterHorarios = new AdapterItemHorario(listHoras);
                        adpaterHorarios.setOnItemClickListener(entorno_principal.this);
                        recyclerViewHorario.setAdapter(adpaterHorarios);
                        loadDBHourRoute(rutaSelected);
                    }
                });
            }
        }, 1);
    }

    // Definir el evento onClick para el item de AdapterHorario
    @Override
    public void onItemHorarioClick(int position) {
        // Mensaje
        //Toast.makeText(this, "Elemento en la posición " + position + " clickeado", Toast.LENGTH_SHORT).show();

        // Adquiere el bus y el horario selecionado
        busSelected = listRutaBuses.get(position);
        hourBusSelected = listHoras.get(position);

        // Definir el titulo de la toolbar
        toolbar.setTitle("Reportar "+rutaSelected+" "+hourBusSelected);

        // Declara el objeto que representa el archivo xml que se piensa inflar
        CreateReportFragment createReportFragment = new CreateReportFragment();

        // Solicita el permiso para solicitar la ubicacion del dispositivo y usa googlemaps
        if (ContextCompat.checkSelfPermission(entorno_principal.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // El permiso no se ha concedido, se solicita al usuario
            ActivityCompat.requestPermissions(entorno_principal.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else { }

        loadDBPosition(rutaSelected, busSelected);

        createReportFragment.loadDataPosition(latitudeBusSelected, longitudeBusSelected,rutaSelected+" "+hourBusSelected);

        // Infla el archivo xml en el contenedor de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, createReportFragment)
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
                        RecyclerView recyclerViewSelectReport;
                        recyclerViewSelectReport = createReportFragment.requireView().findViewById(R.id.idRecycleViewSelectReport);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(entorno_principal.this);
                        recyclerViewSelectReport.setLayoutManager(layoutManager);

                        // Construccion del adptador para cargar los elementos a cada item
                        adaptarSelectReport = new AdapterItemSelectReport(listSelectReport);
                        adaptarSelectReport.setOnItemClickListener(entorno_principal.this);
                        recyclerViewSelectReport.setAdapter(adaptarSelectReport);
                        loadDBSelectReport();
                    }
                });
            }
        }, 1);
    }

    @Override
    public void onItemSelectReportClick(int position){
        // Mensaje
        //Toast.makeText(this, "Elemento en la posición " + position + " clickeado", Toast.LENGTH_SHORT).show();

        // Indica que se ha seleccionado un reporte posible

        reportSelected = listSelectReport.get(position).toString();
        if (reportSelected != null){
            isReported = true;

            adaptarSelectReport.isSelected(true, position);

            adaptarSelectReport.notifyDataSetChanged();
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

    // Este codigo permite definir los elementos para la opcion rutas buses en la interfaza grafica
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
                        // Se resetea las variables
                        isReported = false;
                        rutaSelected = null;
                        busSelected = null;
                        hourBusSelected = null;
                        reportSelected = null;

                        // Se implementa los elementos para el recycleView, en este caso los horarios
                        RecyclerView recyclerViewRuta;
                        recyclerViewRuta = rutasBusesFragment.requireView().findViewById(R.id.idRecycleViewListElements);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(entorno_principal.this);
                        recyclerViewRuta.setLayoutManager(layoutManager);

                        // Construccion del adptador para cargar los elementos a cada item
                        adpaterRutas = new AdapterItemRuta(listRutas);
                        adpaterRutas.setOnItemClickListener(entorno_principal.this);
                        recyclerViewRuta.setAdapter(adpaterRutas);
                        loadDBRoutes();
                    }
                });
            }
        }, 1);

    }

    // Permite cargar las rutas de la base de datos
    void loadDBRoutes(){
        db_reference.child("listado_buses")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listRutas.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            listRutas.add(snapshot.getKey().toString());
                        }
                        adpaterRutas.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    // Permite cargar los horarios de la base de datos
    void loadDBHourRoute(String routeSelected){
        db_reference.child("listado_buses").child(routeSelected)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listHoras.clear();
                        listRutaBuses.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            listHoras.add(snapshot.child("horario").getValue().toString());
                            listRutaBuses.add(snapshot.getKey().toString());
                        }
                        adpaterHorarios.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    // Permite obtener la posicion para generar el reporte
    void loadDBPosition(String routeSelected, String busSelected){
        db_reference.child("listado_buses").child(routeSelected).child(busSelected)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String latitudeSTR = dataSnapshot.child("posicion").child("latitud").getValue().toString();
                        String longitudeSTR = dataSnapshot.child("posicion").child("longitud").getValue().toString();

                        latitudeBusSelected = Double.parseDouble(latitudeSTR);
                        longitudeBusSelected = Double.parseDouble(longitudeSTR);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    // Permite obtener las situaciones posibles a reportar
    void loadDBSelectReport(){
        db_reference.child("tipos_indicentes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listSelectReport.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            listSelectReport.add(snapshot.getKey().toString());
                        }
                        adaptarSelectReport.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    // Decribe el envento onClick del boton reintentar que aparece cuando se pierde la conexion de internet
    public void onClickReintentar(View v){

        if(isConectedInternet()){
            Toast.makeText(this, "Conexion Restablecida", Toast.LENGTH_SHORT).show();
            inflarActRutaBuses();
        }
        else{
            Toast.makeText(this, "No hay Conexion", Toast.LENGTH_SHORT).show();
        }
    }

    // Describe el evento del boton del reporte
    public void onClickReport(View v){
        if(isReported){
            // Obtén la fecha y hora actual
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            // Define el formato deseado para la fecha y la hora
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            // Convierte la fecha y la hora en cadenas
            String formattedDate = dateFormat.format(currentDate);
            String formattedTime = timeFormat.format(currentDate);

            String date = formattedDate.replace("-", "");
            String hora = formattedTime.replace(":", "");

            String idReport = "reporte_"+date+"T"+hora;

            // Enviar el comando
            db_reference.child("listado_buses").child(rutaSelected).child(busSelected).child("comando").setValue("foto,"+date+"T"+hora);

            DatabaseReference reportesReference = db_reference.child("listado_buses").child(rutaSelected).child(busSelected).child("reportes");
            reportesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d("TAG", "reportSelected: " + reportSelected);

                        // Construir el objeto del nuevo reporte
                        Map<String, Object> parametersReport = new HashMap<>();
                        parametersReport.put("tipo", reportSelected);
                        parametersReport.put("confirmado", "false");
                        parametersReport.put("fecha", formattedDate);
                        parametersReport.put("foto", "NULL");
                        parametersReport.put("hora", formattedTime);
                        parametersReport.put("latitud", "NULL");
                        parametersReport.put("longitud", "NULL");

                        reportesReference.child(idReport).setValue(parametersReport);

                    } else {
                        Log.d("TAG", "El nodo de reportes no existe");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("TAG", "Error en la consulta: " + databaseError.getMessage());
                }
            });

            inflarActRutaBuses();

        } else {
            Toast.makeText(this, "Reporte no seleccionado", Toast.LENGTH_SHORT).show();
        }
    }
}