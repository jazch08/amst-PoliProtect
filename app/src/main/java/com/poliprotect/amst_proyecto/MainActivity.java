package com.poliprotect.amst_proyecto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.poliprotect.amst_proyecto.R;

public class MainActivity extends AppCompatActivity {

    private static final int DELAY_TIME = 4000; // 4 segundos
    private Handler splash;
    private Intent intentLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Crear un nuevo Handler
        splash = new Handler();

        // Crear en intent a Iniciar Sesion
        intentLogin = new Intent(MainActivity.this, iniciar_sesion.class);

        // Ejecutar el Intent después del tiempo definido
        splash.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Crear el Intent que deseas ejecutar

                startActivity(intentLogin);
                finish(); // Opcional, para finalizar la actividad actual si deseas
            }
        }, DELAY_TIME);
    }
}