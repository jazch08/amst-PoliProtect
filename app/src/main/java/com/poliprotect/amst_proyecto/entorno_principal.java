package com.poliprotect.amst_proyecto;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.poliprotect.amst_proyecto.R;
import com.poliprotect.amst_proyecto.data.Coordenada;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Collections;

public class entorno_principal extends AppCompatActivity implements AdapterItemHorario.OnItemClickListener, AdapterItemRuta.OnItemClickListener, AdapterItemSelectReport.OnItemClickListener,AdapterItemReporte.OnItemClickListener {

    // Declaracion de los elementos relacionado a la interfaz grafica
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private View headerView;
    private Menu menuNav;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRef;

    // Declaracion del Callback que representa la conexion de iternet
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager mConnectivityManager;

    // Declaracion de los elementos relacionado a googleMaps
    private MapView mapView;
    private GoogleMap googleMap;

    // Declaracion de variables relacionada a los permisos
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    // Declaracion de Adaptadores
    private AdapterItemHorario adpaterHorarios;
    private AdapterItemRuta adpaterRutas;

    private AdapterItemReporte adpaterReportes;
    private AdapterItemSelectReport adaptarSelectReport;

    // Referencia de la base de datos
    DatabaseReference db_reference;

    // Listas de elementos de la base de datos para la creacion de reporte
    List<String> listRutas = new ArrayList<>();
    List<String> listHoras = new ArrayList<>();
    List<String> listRutaBuses = new ArrayList<>();
    List<String> listSelectReport = new ArrayList<>();

    List<String> listReportes = new ArrayList<>();


    // Variables auxiliares para la creacion de reporte
    Boolean isReported = false;
    String rutaSelected = null;
    String busSelected = null;
    String hourBusSelected = null;
    String reportSelected = null;
    double latitudeBusSelected;
    double longitudeBusSelected;

    String reporteSelected = null;

    //Son variables globales para confirmar reporte
    String strUrlFoto = null;
    String strLatitudReporte = null;
    String strLongitudReporte = null;
    String strTipoReporte = null;
    String strHoraReporte = null;
    String strFechaReporte = null;
    String strConfiramdoReporte = null;

    String strRutaReporte = null;

    String strRutaReporteOriginal = null;

    String strBusReporte = null;

    String strNombreReporteSeleccionado = null;
    String strHorarioReporte = null;

    String strMatriculaBusReporte = null;

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

        // Obtener instancia de BD
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Obtener referencia de BD
        databaseRef = firebaseDatabase.getReference("Data_app");

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
                    inflarUbicacionesBusesMapa();
                }  else if (idItem == R.id.idEditarHorarios) {
                    editarHorario();
                } else if (idItem == R.id.idAdminAgregarHorario) {
                    agregarHorario();

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

    private void editarHorario() {
        // Definir el titulo de la toolbar
        toolbar.setTitle("Crear rutas y horarios");

        // Declara el objeto que representa el archivo xml que se piensa inflar
        EditarHorariosFragment editarHorariosFragment = new EditarHorariosFragment();

        // Infla el archivo xml en el contenedor de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, editarHorariosFragment)
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
                        TimePicker timePicker = findViewById(R.id.timePickerEd);
                        timePicker.setIs24HourView(true);

                        Spinner spEd = findViewById(R.id.spRutasEd);
                        Spinner spNombreEd = findViewById(R.id.spNombresBusesEd);
                        ArrayList<String> buses = new ArrayList<>();
                        ArrayList<String> busesNombres = new ArrayList<>();

                        DatabaseReference listadoBusesRef = databaseRef.child("listado_buses");

                        ValueEventListener postListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                buses.clear();

                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                    buses.add(data.getKey());
                                }
                                ArrayAdapter<String> adapterVIEW = new ArrayAdapter<>(entorno_principal.this, android.R.layout.simple_dropdown_item_1line,buses);
                                spEd.setAdapter(adapterVIEW);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            }
                        };
                        listadoBusesRef.addValueEventListener(postListener);

                        spEd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String rutaSeleccionada = spEd.getSelectedItem().toString();

                                DatabaseReference listadoRutaRef = databaseRef.child("listado_buses").child(rutaSeleccionada);
                                ValueEventListener postListenerBus = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        busesNombres.clear();

                                        for(DataSnapshot data : dataSnapshot.getChildren()){
                                            busesNombres.add(data.getKey());
                                        }
                                        ArrayAdapter<String> adapterVIEWBus = new ArrayAdapter<>(entorno_principal.this, android.R.layout.simple_dropdown_item_1line,busesNombres);
                                        spNombreEd.setAdapter(adapterVIEWBus);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Getting Post failed, log a message
                                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                    }
                                };
                                listadoRutaRef.addValueEventListener(postListenerBus);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }
                });
            }
        }, 1);
    }

    public void editarHorarioClick(View view){
        TextView txtMatricula = findViewById(R.id.txtMatriculaEd);
        TimePicker timePicker = findViewById(R.id.timePickerEd);
        Spinner spEd = findViewById(R.id.spRutasEd);
        Spinner spNombreEd = findViewById(R.id.spNombresBusesEd);
        DatabaseReference buses = databaseRef.child("listado_buses");

        Boolean matriculaLleno = txtMatricula.getText().length()>0;

        String horario = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            horario = timePicker.getHour() + "h" + timePicker.getMinute();
        }else {
            timePicker.getCurrentHour();
            timePicker.getCurrentMinute();
            horario = timePicker.getCurrentHour() + "h" + timePicker.getCurrentMinute();
        }

        boolean rutaSelected = spEd.getSelectedItem() != null;
        boolean busSelected = spNombreEd.getSelectedItem() != null;

        if(matriculaLleno && rutaSelected && busSelected){
            System.out.println(spEd.getSelectedItem().toString());
            System.out.println(spNombreEd.getSelectedItem().toString());
            DatabaseReference matricularef = buses.child(spEd.getSelectedItem().toString()).child(spNombreEd.getSelectedItem().toString()).child("matricula");
            DatabaseReference horarioref = buses.child(spEd.getSelectedItem().toString()).child(spNombreEd.getSelectedItem().toString()).child("horario");
            matricularef.setValue(txtMatricula.getText().toString());
            horarioref.setValue(horario);
            Toast.makeText(entorno_principal.this, "Horario creado", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(entorno_principal.this, "Elementos sin llenar", Toast.LENGTH_SHORT).show();
        }

    }

    private void agregarHorario() {
        // Definir el titulo de la toolbar
        toolbar.setTitle("Crear rutas y horarios");

        // Declara el objeto que representa el archivo xml que se piensa inflar
        CrearHorariosFragment crearHorariosFragment = new CrearHorariosFragment();

        // Infla el archivo xml en el contenedor de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, crearHorariosFragment)
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
                        TimePicker timePicker = findViewById(R.id.timePickerCr);
                        timePicker.setIs24HourView(true);

                        Spinner spCr = findViewById(R.id.spRutasCr);
                        ArrayList<String> buses = new ArrayList<>();

                        DatabaseReference listadoBusesRef = databaseRef.child("listado_buses");

                        ValueEventListener postListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                buses.clear();

                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                    buses.add(data.getKey());
                                }
                                ArrayAdapter<String> adapterVIEW = new ArrayAdapter<>(entorno_principal.this, android.R.layout.simple_dropdown_item_1line,buses);
                                spCr.setAdapter(adapterVIEW);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            }
                        };
                        listadoBusesRef.addValueEventListener(postListener);
                    }
                });
            }
        }, 1);
    }

    public void CrearRutaClick(View view){
        TextView txtNombreRuta = findViewById(R.id.txtNombreRutasCr);
        DatabaseReference buses = databaseRef.child("listado_buses");

        if(txtNombreRuta.getText().length()>=0){
            buses.child(txtNombreRuta.getText().toString()).setValue("");
            Toast.makeText(entorno_principal.this, "Ruta Creada", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(entorno_principal.this, "Llene el campo de nombre de ruta", Toast.LENGTH_SHORT).show();
        }
    }

    public void crearHorarioClick(View view){
        Spinner sp = findViewById(R.id.spRutasCr);
        DatabaseReference buses = databaseRef.child("listado_buses");
        TextView txtNombreBus = findViewById(R.id.txtNombreBusCr);
        TextView txtMatricula = findViewById(R.id.txtMatriculaCr);
        TimePicker timePicker = findViewById(R.id.timePickerCr);

        String horario = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            horario = timePicker.getHour() + "h" + timePicker.getMinute();
        }else{
            timePicker.getCurrentHour();
            timePicker.getCurrentMinute();
            horario = timePicker.getCurrentHour() + "h" + timePicker.getCurrentMinute();
        }

        boolean nombreBusLleno = txtNombreBus.getText().length()>0;
        boolean matriculaLlena = txtMatricula.getText().length()>0;


        if(nombreBusLleno && matriculaLlena){
            Map<String, Object> parametersReport = new HashMap<>();
            Map<String, Object> posicion = new HashMap<>();
            posicion.put("latitud", 0);
            posicion.put("longitud", 0);
            posicion.put("satelites", 0);

            parametersReport.put("bateria", 0);
            parametersReport.put("comando", "Done");
            parametersReport.put("conected", "connected");
            parametersReport.put("conexion", "");
            parametersReport.put("conexion_tiempo", "");
            parametersReport.put("horario", horario);
            parametersReport.put("matricula", txtMatricula.getText().toString());
            parametersReport.put("posicion",posicion );
            parametersReport.put("reportado", false);
            parametersReport.put("reportado", "");

            buses.child(sp.getSelectedItem().toString()).child(txtNombreBus.getText().toString()).setValue(parametersReport);
            Toast.makeText(entorno_principal.this, "Horario creado", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(entorno_principal.this, "Elementos sin llenar", Toast.LENGTH_SHORT).show();
        }
    }

    public void djdjd(View view){
        Spinner sp = findViewById(R.id.spRutasCr);
        DatabaseReference buses = databaseRef.child("listado_buses");
        TextView txtNombreBus = findViewById(R.id.txtNombreBusCr);
        TextView txtMatricula = findViewById(R.id.txtMatriculaCr);
        TimePicker timePicker = findViewById(R.id.timePickerCr);

        String horario = "";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            horario = timePicker.getHour() + "h" + timePicker.getMinute();
        }else{
            timePicker.getCurrentHour();
            timePicker.getCurrentMinute();
            horario = timePicker.getCurrentHour() + "h" + timePicker.getCurrentMinute();
        }

        boolean nombreBusLleno = txtNombreBus.getText().length()>0;
        boolean matriculaLlena = txtMatricula.getText().length()>0;


        if(nombreBusLleno && matriculaLlena){
            Map<String, Object> parametersReport = new HashMap<>();
            Map<String, Object> posicion = new HashMap<>();
            posicion.put("latitud", 0);
            posicion.put("longitud", 0);
            posicion.put("satelites", 0);

            parametersReport.put("bateria", 0);
            parametersReport.put("comando", "Done");
            parametersReport.put("conected", "connected");
            parametersReport.put("conexion", "");
            parametersReport.put("conexion_tiempo", "");
            parametersReport.put("horario", horario);
            parametersReport.put("matricula", txtMatricula.getText().toString());
            parametersReport.put("posicion",posicion );
            parametersReport.put("reportado", false);
            parametersReport.put("reportado", "");

            buses.child(sp.getSelectedItem().toString()).child(txtNombreBus.getText().toString()).setValue(parametersReport);
            Toast.makeText(entorno_principal.this, "Horario creado", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(entorno_principal.this, "Elementos sin llenar", Toast.LENGTH_SHORT).show();
        }
    }

    private void inflarUbicacionesBusesMapa(){

        //Barra se pantalla
        toolbar.setTitle("Ubicaciones de todos los buses");

        // Declara el objeto que representa el archivo xml que se piensa inflar
        UbicacionesBusesMapFragment ubicacionesBusesMapaFragment = new UbicacionesBusesMapFragment();

        // Solicita el permiso para solicitar la ubicacion del dispositivo y usa googlemaps si es que no lo tiene
        if (ContextCompat.checkSelfPermission(entorno_principal.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // El permiso no se ha concedido, se solicita al usuario
            ActivityCompat.requestPermissions(entorno_principal.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        DatabaseReference listadoBusesRef = databaseRef.child("listado_buses");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("-----------------------------------");
                ArrayList<Coordenada> listadoCoordenadas= new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    for (DataSnapshot dataU : data.getChildren()){
                        String nombre = data.getKey() + " " +  dataU.child("horario").getValue();
                        double latitude = Double.parseDouble(dataU.child("posicion").child("latitud").getValue().toString());
                        double longitude = Double.parseDouble(dataU.child("posicion").child("longitud").getValue().toString());
                        Coordenada coordenada = new Coordenada(latitude,longitude,nombre);
                        listadoCoordenadas.add(coordenada);
                    }
                }
                ubicacionesBusesMapaFragment.setNombreArrayList(listadoCoordenadas);
                System.out.println("-----------------------------------");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        listadoBusesRef.addValueEventListener(postListener);

        // Infla el archivo xml en el contenedor de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ubicacionesBusesMapaFragment)
                .commit();

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
    void loadDBRoutes() {
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
    }// Permite cargar los horarios de la base de datos
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

    // Permite obtener las situaciones posibles a reportar

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



                        // Construccion del adptador para cargar los elementos a cada item
                        adpaterReportes = new AdapterItemReporte(listReportes);
                        adpaterReportes.setOnItemClickListener(entorno_principal.this);
                        recyclerViewRuta.setAdapter(adpaterReportes);
                        loadDBReporte();
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

    // Permite cargar las rutas de la base de datos
    void loadDBReporte() {
        db_reference.child("listado_buses")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        listReportes.clear();
                        for (DataSnapshot snapshotRutas : dataSnapshot.getChildren())
                        {
                            //listRutas.add(snapshotRutas.getKey().toString());
                            for (DataSnapshot snapshotBuses : snapshotRutas.getChildren())
                            {
                                for (DataSnapshot snapshotReportes : snapshotBuses.child("reportes").getChildren())
                                {
                                    //Collections.sort(convertToFormattedString(snapshotReportes.getKey().toString()), Collections.reverseOrder());
                                    /*if( "false".equals(snapshotReportes.child("confirmado").getValue().toString()) )
                                    {
                                        listReportes.add(convertToFormattedString(snapshotReportes.getKey().toString()));
                                        Collections.sort(listReportes, Collections.reverseOrder());
                                    }*/

                                    if (snapshotReportes.hasChild("confirmado") && snapshotReportes.child("confirmado").getValue() != null) {
                                        if( "false".equals(snapshotReportes.child("confirmado").getValue().toString()) )
                                        {
                                            listReportes.add(convertToFormattedString(snapshotReportes.getKey().toString()));
                                            Collections.sort(listReportes, Collections.reverseOrder());
                                        }
                                    }

                                }
                            }
                        }
                        adpaterReportes.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    @Override
    public void onItemReporteClick(int position) {
        // Mensaje
        //Toast.makeText(this, "Elemento en la posición " + position + " clickeado", Toast.LENGTH_SHORT).show();

        // Adquiere el bus y el horario selecionado
        reporteSelected = listReportes.get(position);


        // Definir el titulo de la toolbar
        toolbar.setTitle(reporteSelected);

        // Declara el objeto que representa el archivo xml que se piensa inflar
        CreateConfirmationReportFragment createConfirmationReportFragment = new CreateConfirmationReportFragment();


        // Solicita el permiso para solicitar la ubicacion del dispositivo y usa googlemaps
        if (ContextCompat.checkSelfPermission(entorno_principal.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // El permiso no se ha concedido, se solicita al usuario
            ActivityCompat.requestPermissions(entorno_principal.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else { }

        loadDatosReporteDB(reporteSelected);


        // Infla el archivo xml en el contenedor de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, createConfirmationReportFragment)
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
                        //RecyclerView recyclerViewSelectReport;
                        //recyclerViewSelectReport = createReportFragment.requireView().findViewById(R.id.idRecycleViewSelectReport);
                        //LinearLayoutManager layoutManager = new LinearLayoutManager(entorno_principal.this);
                        //recyclerViewSelectReport.setLayoutManager(layoutManager);

                        ImageView imvPhotoReport;
                        imvPhotoReport = createConfirmationReportFragment.requireView().findViewById(R.id.imageViewReporteConfirmar);
                        TextView txtTipoEmergencia;
                        txtTipoEmergencia = createConfirmationReportFragment.requireView().findViewById(R.id.textViewValorTipoReporte);
                        TextView txtLatitud;
                        txtLatitud = createConfirmationReportFragment.requireView().findViewById(R.id.textViewValorLatitudReporte);
                        TextView txtLongitud;
                        txtLongitud = createConfirmationReportFragment.requireView().findViewById(R.id.textViewValorLongitudReporte);

                        TextView txtRutaReporte;
                        txtRutaReporte = createConfirmationReportFragment.requireView().findViewById(R.id.textRutaDelReporte);



                        Picasso.get().load(strUrlFoto).into(imvPhotoReport);
                        txtTipoEmergencia.setText(strTipoReporte);
                        txtLatitud.setText(strLatitudReporte);
                        txtLongitud.setText(strLongitudReporte);

                        txtRutaReporte.setText(strRutaReporte + " - "+ strHorarioReporte + " - "+  strMatriculaBusReporte);

                    }
                });
            }
        }, 1);
    }

    void loadDatosReporteDB(String reporteSelected){
        db_reference.child("listado_buses")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        strUrlFoto           = null;
                        strLatitudReporte    = null;
                        strLongitudReporte   = null;
                        strTipoReporte       = null;
                        strHoraReporte       = null;
                        strFechaReporte      = null;
                        strConfiramdoReporte = null;
                        strRutaReporte       = null;
                        strRutaReporteOriginal = null;
                        strNombreReporteSeleccionado = null;
                        strBusReporte        = null;
                        strHorarioReporte    = null;
                        strMatriculaBusReporte = null;
                        for (DataSnapshot snapshotRutas : dataSnapshot.getChildren())
                        {
                            //listRutas.add(snapshotRutas.getKey().toString());

                            for (DataSnapshot snapshotBuses : snapshotRutas.getChildren())
                            {
                                for (DataSnapshot snapshotReportes : snapshotBuses.child("reportes").getChildren())
                                {
                                    if(reporteSelected.equals(convertToFormattedString(snapshotReportes.getKey().toString())))
                                    {
                                        strRutaReporte       = convertirAFormatoCorrecto(snapshotRutas.getKey().toString()) ;
                                        strRutaReporteOriginal = snapshotRutas.getKey().toString();
                                        strBusReporte         =  snapshotBuses.getKey().toString();
                                        strNombreReporteSeleccionado = snapshotReportes.getKey().toString();
                                        strHorarioReporte    = snapshotBuses.child("horario").getValue().toString();
                                        strMatriculaBusReporte = snapshotBuses.child("matricula").getValue().toString();
                                        strUrlFoto           = snapshotReportes.child("foto").getValue().toString();
                                        strLatitudReporte    = snapshotReportes.child("latitud").getValue().toString();
                                        strLongitudReporte   = snapshotReportes.child("longitud").getValue().toString();
                                        strTipoReporte       = snapshotReportes.child("tipo").getValue().toString();
                                        strHoraReporte       = snapshotReportes.child("hora").getValue().toString();
                                        strFechaReporte      = snapshotReportes.child("fecha").getValue().toString();
                                        strConfiramdoReporte = snapshotReportes.child("confirmado").getValue().toString();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });

    }

    public String convertirAFormatoCorrecto(String input) {
        String[] palabras = input.split("_");
        StringBuilder builder = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                builder.append(Character.toUpperCase(palabra.charAt(0)));
                builder.append(palabra.substring(1).toLowerCase());
                builder.append(" ");
            }
        }

        return builder.toString().trim();
    }

    public void onClickConfirmationReport(View v){

        // Enviar el comando
        db_reference.child("listado_buses").child(strRutaReporteOriginal).child(strBusReporte).child("reportes").child(strNombreReporteSeleccionado).child("confirmado").setValue("true");
        inflarListaReporte();
    }

    public void onClickEliminarReport(View v){

        // Enviar el comando
        db_reference.child("listado_buses").child(strRutaReporteOriginal).child(strBusReporte).child("reportes").child(strNombreReporteSeleccionado).removeValue();
        inflarListaReporte();
    }
}