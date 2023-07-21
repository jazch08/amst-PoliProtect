package com.example.amst_proyecto;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;


public class iniciar_sesion extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private EditText etUsuario;
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
        etUsuario = findViewById(R.id.editTextText);

        // CheckBox que permite visualizar u ocultar la contraseña.
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

        mAuth = FirebaseAuth.getInstance();

        // Configurar opciones de inicio de sesión con Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Crear el cliente de inicio de sesión con Google
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Evento de cerrar sesion
        Intent intentCerrarSesion = getIntent();
        String msg = intentCerrarSesion.getStringExtra("msg");
        if(msg != null){
            if(msg.equals("cerrarSesion")){
                cerrarSesion();
            }
        }
    }

    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
    }

    public void iniciarSesion(View v) {
        String usuario = etUsuario.getText().toString();
        String contraseña = editTextPassword.getText().toString();

        //mAuth.signInWithEmailAndPassword(usuario, contraseña)
        //        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        //            @Override
        //            public void onComplete(@NonNull Task<AuthResult> task) {
        //                if (task.isSuccessful()) {
        //                } else {
        //                    Toast.makeText(iniciar_sesion.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
        //                }
        //            }
        //        });
    }

    public void iniciarSesionGoogle(View v) {
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }
    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) firebaseAuthWithGoogle(account);
                } catch (ApiException e) {
                    Log.w("TAG", "Fallo el inicio de sesión con google.", e);
                }
            }
        }
    });

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),
                null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        System.out.println("error");
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            HashMap<String, String> info_user = new HashMap<String, String>();
            info_user.put("user_name", user.getDisplayName());
            info_user.put("user_email", user.getEmail());
            info_user.put("user_photo", String.valueOf(user.getPhotoUrl()));
            finish();
            Intent intent = new Intent(this, entorno_principal.class);
            intent.putExtra("info_user", info_user);
            startActivity(intent);
        } else {
            System.out.println("sin registrarse");
        }
    }
}