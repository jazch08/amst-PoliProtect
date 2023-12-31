package com.poliprotect.amst_proyecto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.poliprotect.amst_proyecto.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class iniciar_sesion extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private EditText etUsuario;
    CheckBox checkboxShowPassword;
    EditText editTextPassword;

    // Declaracion del Callback que representa la conexion de iternet
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager mConnectivityManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        // Definir la operacion de los estados de conexion de internet
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    // Esta conectado a internet
                    Toast.makeText(iniciar_sesion.this, "Conexion Restablecida", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    // Se perdió la conexión a Internet
                    Toast.makeText(iniciar_sesion.this, "No hay conexion", Toast.LENGTH_SHORT).show();
                }
            };
        }

        // Registrar el NetworkCallback
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectivityManager.registerNetworkCallback(request, networkCallback);
        }

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
        database = FirebaseDatabase.getInstance();

        // Si ya esta logeado, lo ingresa directamente a la pantalla principal
        if (mAuth.getCurrentUser() != null) {
            SharedPreferences sp = getSharedPreferences("login_admin", Context.MODE_PRIVATE);
            String userType = sp.getString("userType","");

            // Comprueba se es administrador, obteniendo el tipo de usuario guardado en cache
            if (userType.equals("Administrador")){
                String name = sp.getString("nombre","Nombre sin cargar");
                String noPhoto = "https://firebasestorage.googleapis.com/v0/b/poliprotect-6b93c.appspot.com/o/usuarios%2FnoPhoto.png?alt=media&token=4f1b180a-7394-4bb7-9750-dfcdbb58c514";
                String photo = sp.getString("photo",noPhoto);
                updateUI(name,mAuth.getCurrentUser().getEmail(),photo);
            }
            else
                updateUI(mAuth.getCurrentUser());
        }

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
        if (msg != null) {
            if (msg.equals("cerrarSesion")) {
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

        if (isConectedInternet()) {
            if (usuario.length() > 0 && contraseña.length() > 0) {
                mAuth.signInWithEmailAndPassword(usuario, contraseña)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String email = mAuth.getCurrentUser().getEmail().toString();
                                    String Uid = mAuth.getUid().toString();
                                    DatabaseReference refDataBase = database.getReference("Data_app/usuarios");
                                    updateUIEmail(email, Uid, refDataBase);

                                } else {
                                    Toast.makeText(iniciar_sesion.this, "Correo o contraseña invalido", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else if (usuario.length() > 0 && contraseña.length() <= 0) {
                Toast.makeText(getApplicationContext(),
                        "Por favor ingrese su contraseña",
                        Toast.LENGTH_SHORT).show();
            } else if (usuario.length() <= 0 && contraseña.length() > 0) {
                Toast.makeText(getApplicationContext(),
                        "Por favor ingrese su usuario",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Por favor ingrese su usuario y contraseña",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(iniciar_sesion.this,
                    "No hay conexion",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUIEmail(String email, String Uid, DatabaseReference refDataBase){
        Toast.makeText(iniciar_sesion.this, "Abriendo su perfil", Toast.LENGTH_SHORT).show();
        refDataBase.child(Uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String name = String.valueOf(task.getResult().child("nombre").getValue());
                    String image = String.valueOf(task.getResult().child("photo").getValue());
                    updateUI(name,email,image);
                }
            }
        });
    }

    public void iniciarSesionGoogle(View v) {
        if (isConectedInternet()) {
            resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
        } else {
            Toast.makeText(iniciar_sesion.this, "No hay conexion", Toast.LENGTH_SHORT).show();
        }
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
            System.out.println(user.getDisplayName());
            info_user.put("user_type", "Estudiante");
            SharedPreferences sp = getSharedPreferences("login_admin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("userType", "Estudiante");
            editor.commit();
            finish();
            Intent intent = new Intent(this, entorno_principal.class);
            intent.putExtra("info_user", info_user);
            startActivity(intent);
        } else {
            System.out.println("sin registrarse");

        }
    }

    private void updateUI(String name, String email, String urlPhoto) {
        HashMap<String, String> info_user = new HashMap<String, String>();
        info_user.put("user_name", name);
        info_user.put("user_email", email);
        info_user.put("user_photo", urlPhoto);
        info_user.put("user_type", "Administrador");
        SharedPreferences sp = getSharedPreferences("login_admin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userType", "Administrador");
        editor.putString("nombre", name);
        editor.putString("photo", urlPhoto);
        editor.commit();
        finish();
        Intent intent = new Intent(this, entorno_principal.class);
        intent.putExtra("info_user", info_user);
        startActivity(intent);
    }

    private boolean isConectedInternet() {
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void registrarse(View view){
        Intent intent = new Intent(this, Register.class);

        startActivity(intent);
    }
}