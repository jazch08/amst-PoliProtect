package com.example.amst_proyecto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class Register extends AppCompatActivity {
    ImageView imgPerfil;
    FloatingActionButton btnPerfil;
    Uri uri;
    FirebaseStorage storage;
    StorageReference storageRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgPerfil = findViewById(R.id.imgPerfil);
        btnPerfil = findViewById(R.id.btnPerfil);

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("usuarios");
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
        boolean imagenNoVacio = this.uri.toString().length()>0;

        if(nombreNoVacio && correoNoVacio && contrasenaNoVacio && imagenNoVacio){
            imgPerfil.setDrawingCacheEnabled(true);
            imgPerfil.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imgPerfil.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            String correoText = String.valueOf(correo.getText());
            String contrasenaText = String.valueOf(contrasena.getText());

            mAuth.createUserWithEmailAndPassword(correoText, contrasenaText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Register.this, "Fallo al registrarse",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }
        else{
            Toast.makeText(Register.this, "Existen campos sin llenar", Toast.LENGTH_SHORT).show();
        }
    }
}