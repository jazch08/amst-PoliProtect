package com.poliprotect.amst_proyecto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.poliprotect.amst_proyecto.R;
import com.poliprotect.amst_proyecto.data.Usuario;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class Register extends AppCompatActivity {
    ImageView imgPerfil;
    FloatingActionButton btnPerfil;
    Uri uri;
    FirebaseStorage storage;
    StorageReference storageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgPerfil = findViewById(R.id.imgPerfil);
        btnPerfil = findViewById(R.id.btnPerfil);

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("usuarios");

        mDatabase = FirebaseDatabase.getInstance().getReference("Data_app/usuarios");
    }

    public void cargarImgPerfil(View view){
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        this.uri = uri;
        imgPerfil.setImageURI(uri);
    }

    public void registrarse(View view) {
        EditText nombre = findViewById(R.id.txt_nombre);
        EditText correo = findViewById(R.id.txt_correo);
        EditText contrasena = findViewById(R.id.txt_contrasena);

        boolean nombreNoVacio = nombre.getText().length()>0;
        boolean correoNoVacio = correo.getText().length()>0;
        boolean contrasenaNoVacio = contrasena.getText().length()>0;
        boolean imagenNoVacio = this.uri != null;

        System.out.println("----------------------------------");
        System.out.println(imagenNoVacio);
        System.out.println("----------------------------------");

        if(nombreNoVacio && correoNoVacio && contrasenaNoVacio && imagenNoVacio){
            imgPerfil.setDrawingCacheEnabled(true);
            imgPerfil.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imgPerfil.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            String correoText = String.valueOf(correo.getText());
            String contrasenaText = String.valueOf(contrasena.getText());
            String nombreText = String.valueOf(nombre.getText());

            mAuth.createUserWithEmailAndPassword(correoText, contrasenaText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "Usuario creado",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                String nombreImagen = user.getUid() + ".jpg";
                                StorageReference imagenRef = storageRef.child(nombreImagen);
                                UploadTask uploadTask = imagenRef.putBytes(data);
                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }
                                        return imagenRef.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Register.this, "Datos guardados e iniciando sesion",
                                                    Toast.LENGTH_SHORT).show();
                                            Uri downloadUri = task.getResult();
                                            Usuario usuario = new Usuario(nombreText,downloadUri.toString());
                                            mDatabase.child(user.getUid()).setValue(usuario);
                                            updateUI(nombreText,correoText,downloadUri.toString());
                                        } else {
                                            Toast.makeText(Register.this, "Fallo al subir photo de perfil",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Register.this, "Fallo al registrarse",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        else{
            Toast.makeText(Register.this, "Existen campos sin llenar", Toast.LENGTH_SHORT).show();
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

    public void goIniciarSesion(View view){
        Intent intent = new Intent(this, iniciar_sesion.class);
        startActivity(intent);
    }
}