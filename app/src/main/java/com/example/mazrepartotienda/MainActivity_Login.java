package com.example.mazrepartotienda;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import Modelos.UsuariosRestaurantes;

public class MainActivity_Login extends AppCompatActivity {

    EditText usuario;
    EditText password;
    private ProgressDialog progressDialog;
    public FirebaseAuth mAuth;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Usuarios/UsuariosRestaurantes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__login);

        mAuth = FirebaseAuth.getInstance();

        Objects.requireNonNull(getSupportActionBar()).hide();
        usuario =findViewById(R.id.m_username);
        password =findViewById(R.id.m_password);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        progressDialog.setTitle("Maz Reparto");
        progressDialog.setMessage("Verificando Datos");
        progressDialog.show();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            obtenerKeyTrabajador(currentUser.getEmail());
        }
        else
        {
            progressDialog.dismiss();
        }

    }

    public void LoginUser(View view)
    {

        progressDialog.setTitle("Maz Reparto");
        progressDialog.setMessage("Verificando Datos");
        progressDialog.show();

        if(!usuario.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(usuario.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    verificarUsuario();

                }
                else {

                    progressDialog.dismiss();
                    mostrarDialogo("Autenticación","Favor de verificar sus datos");
                }

            });
        }
        else
        {
            progressDialog.dismiss();
            mostrarDialogo("Autenticación","Favor de ingresar sus datos");
        }

    }

    public  void verificarUsuario()
    {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                boolean bCerrarSesion=true;
                for (DataSnapshot UsuarioSnapshot: dataSnapshot.getChildren()) {

                    UsuariosRestaurantes user = UsuarioSnapshot.getValue(UsuariosRestaurantes.class);

                    assert user != null;
                    if(user.Correo.equals(usuario.getText().toString()))
                    {
                        actualizarKeyCelular(UsuarioSnapshot.getKey());
                        bCerrarSesion=false;
                        break;
                    }
                }
                if(bCerrarSesion)
                {
                    progressDialog.dismiss();
                    mostrarDialogo("Autenticación","Favor de verificar que la aplicación instalada sea la correcta");
                    FirebaseAuth.getInstance().signOut();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
                mostrarDialogo("Autenticación","Ocurrio el siguiente problema: "+error.toException());
                FirebaseAuth.getInstance().signOut();
            }
        });
    }


    public  void obtenerKeyTrabajador(String sCorreo)
    {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot UsuarioSnapshot: dataSnapshot.getChildren()) {

                    UsuariosRestaurantes user = UsuarioSnapshot.getValue(UsuariosRestaurantes.class);

                    assert user != null;
                    if(user.Correo.equals(sCorreo))
                    {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("KeyTrabajador", UsuarioSnapshot.getKey());
                        startActivity(intent);
                        break;
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                mostrarDialogo("Autenticación","Ocurrio el siguiente problema: "+error.toException());
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    public void actualizarKeyCelular(String key)
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                progressDialog.dismiss();
                mostrarDialogo("Autenticación","Ocurrio el siguiente problema al registrar el token: "+task.getException());
                return;
            }

            // Get new FCM registration token
            String token = task.getResult();
            ref.child(key).child("keyNotificaciones").setValue(token);

            progressDialog.dismiss();
            Intent intent = new Intent(MainActivity_Login.this, MainActivity.class);
            intent.putExtra("KeyTrabajador", key);
            startActivity(intent);

        });

    }

    public  void mostrarDialogo(String sTitulo, String sMensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(sTitulo);
        builder.setMessage(sMensaje);
        //builder.setPositiveButton("OK", null);
        builder.setNeutralButton("Entendido",null);
        builder.setInverseBackgroundForced(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}