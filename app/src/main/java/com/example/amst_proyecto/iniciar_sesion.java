package com.example.amst_proyecto;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;


public class iniciar_sesion extends AppCompatActivity {

    CheckBox checkboxShowPassword;
    EditText editTextPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        // Declaracion de los elementos de la interfaz grafica
        checkboxShowPassword = findViewById(R.id.idCheckboxShowPassword);
        editTextPassword = findViewById(R.id.idEditTextPassword);




        checkboxShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Acciones a realizar cuando cambia el estado del CheckBox
                if (isChecked) {

                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }

    public void iniciarSesionGoogle(View v){

    }

    public void onRegisterClick(View v){

    }

    public void onOlvidadoClick(View v){

    }

    public void onIniciarSesionClick(View v){

    }
}