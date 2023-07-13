package com.example.amst_proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class entorno_principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entorno_principal);

        toolbar = findViewById(R.id.idToolBar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.idNavigationView);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.idDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Habilitar el ícono del menú en la barra de acción
        // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setHomeButtonEnabled(true);



        // Configurar el listener para abrir/cerrar el menú lateral
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HorrariosBusesFragment()).commit();
            navigationView.setCheckedItem(R.id.idAdminHorariosBuses);
        }

    }

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    if (drawerToggle.onOptionsItemSelected(item)) {
    //        return true;
    //    }
    //    return super.onOptionsItemSelected(item);
    //}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.idAdminHorariosBuses)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HorrariosBusesFragment()).commit();
        else if (item.getItemId() == R.id.idAdminVisualizarBuses)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new UbicacionBusesFragment()).commit();

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}