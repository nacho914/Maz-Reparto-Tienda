package com.example.mazrepartotienda;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import Modelos.UsuariosRestaurantes;

public class MainActivity_Splash extends AppCompatActivity {

    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Usuarios/UsuariosRestaurantes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__splash);

        mAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @Override
    public void onStart() {
        super.onStart();

        revisarSesion();

    }

    public void revisarSesion()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            esperar(1,currentUser.getEmail());
        }
        else
        {
            esperar(0,"");
        }

    }

    public void esperar(int iCamino,String sCorreo)
    {

        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed(() -> {
            if(isInternetAvailable()) {
                if (iCamino == 1) {
                    obtenerKeyTrabajador(sCorreo);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity_Login.class);
                    startActivity(intent);
                }

            }
            else {
                mostrarDialogo("Maz Store","Lo sentimos no encontramos internet disponible");
            }

        }, SPLASH_DISPLAY_LENGTH);
    }

    public  void obtenerKeyTrabajador(String sCorreo)
    {

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                mostrarDialogo("Autenticación","Ocurrio el siguiente problema: "+error.toException());
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    public  void mostrarDialogo(String sTitulo, String sMensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(sTitulo);
        builder.setMessage(sMensaje);
        //builder.setPositiveButton("OK", null);

        builder.setNeutralButton("Intentar de nuevo", (dialog, which) -> revisarSesion());

        builder.setInverseBackgroundForced(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}