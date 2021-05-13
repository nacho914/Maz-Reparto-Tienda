package com.example.mazrepartotienda;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import Modelos.Pedidos;
import Modelos.UsuariosRestaurantes;

public class MainActivity_detalle extends AppCompatActivity {

    String key;
    String keyRestaurante;
    TextView mNombreNego;
    TextView mNombreCliente;
    TextView mDireccion;
    TextView mPrecio;
    TextView mTelefono;
    TextView mTiempo;
    TextView mNombreRepartidor;
    TextView mTelefonoRepartidor;
    Button mActualiza;
    private ProgressDialog progressDialog;
    boolean bEsApartado = false;
    int iTipoPedido;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Pedidos");
    DatabaseReference refTrabajador = database.getReference("Usuarios/UsuariosRepartidores");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_detalle);

        Bundle extras = getIntent().getExtras();
        key = extras.getString("keyPedido");
        keyRestaurante = extras.getString("keyRestaurante");
        iTipoPedido =Integer.parseInt(extras.getString("tipoPedido"));

        mNombreNego=findViewById(R.id.mNombreNegocio);
        mNombreCliente=findViewById(R.id.mNombreCliente);
        mDireccion=findViewById(R.id.mDirección);
        mPrecio=findViewById(R.id.mPrecio);
        mTelefono=findViewById(R.id.mTelefono);
        mTiempo=findViewById(R.id.mTiempo);
        mActualiza=findViewById(R.id.mBtnApartarPedido);
        mNombreRepartidor=findViewById(R.id.mNombreRepartidor);
        mTelefonoRepartidor =findViewById(R.id.mNumeroRepartidor);

        progressDialog = new ProgressDialog(this);

        progressDialog.setTitle("Maz Reparto");
        progressDialog.setMessage("Verificando Datos");
        progressDialog.show();

        if(iTipoPedido==0)
        {
            ref.child("Activos").child(key).addValueEventListener(returnListener());
        }
        if(iTipoPedido == 1)
        {
            bEsApartado=true;
            ref.child("Activos").child(key).addValueEventListener(returnListener());
        }
        if(iTipoPedido == 2) {
            mActualiza.setEnabled(false);
            mActualiza.setText("Pedido Finalizado");
            ref.child("Finalizados").child(key).addValueEventListener(returnListener());
        }
    }

    public ValueEventListener returnListener()
    {
        return new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    Pedidos pedido = dataSnapshot.getValue(Pedidos.class);
                    assert pedido != null;
                    mNombreNego.setText(pedido.NombreNegocio);
                    mNombreCliente.setText(pedido.NombreCliente);
                    mDireccion.setText(pedido.Direccion);
                    mPrecio.setText("$ " + pedido.Precio);
                    mTelefono.setText(String.valueOf(pedido.Telefono));
                    mTiempo.setText(HoraPedido(pedido));

                    if(pedido.TrabajadorKey.isEmpty()) {
                        mNombreRepartidor.setText("Repartidor no asignado");
                        progressDialog.dismiss();
                    }
                    else
                        refTrabajador.child(pedido.TrabajadorKey).addValueEventListener(returnListenerTrabajador());
                    //progressDialog.dismiss();
                }
                else {
                    progressDialog.dismiss();
                }

            }
            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        };
    }


    public ValueEventListener returnListenerTrabajador()
    {
        return new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    UsuariosRestaurantes usuariosRestaurantes = dataSnapshot.getValue(UsuariosRestaurantes.class);
                    assert usuariosRestaurantes != null;
                    mNombreRepartidor.setText(usuariosRestaurantes.Nombre);
                    mTelefonoRepartidor.setText(String.valueOf(usuariosRestaurantes.Telefono));

                }
                else {
                    mNombreRepartidor.setText("Repartidor no asignado");
                    mTelefonoRepartidor.setText("Repartidor no asignado");
                }
                progressDialog.dismiss();

            }
            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String HoraPedido(Pedidos pedido)
    {
        LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(pedido.getTimestampCreatedLong()),
                TimeZone.getDefault().toZoneId()).plusMinutes(pedido.TiempoPedido);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return triggerTime.format(formatter);
    }

    public void cancelarPedido(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelación de Pedido");
        builder.setMessage("¿Estas seguro que deseas cancelar el pedido ?");
        //builder.setPositiveButton("OK", null);

        builder.setNeutralButton("Si", (dialog, which) -> {
            ref.child("Activos").child(key).removeValue();
            Intent intent = new Intent(getApplicationContext(), MainActivity_list.class);
            intent.putExtra("keyRestaurante", keyRestaurante);
            startActivity(intent);
        });

        builder.setNegativeButton("No", null);


        builder.setInverseBackgroundForced(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*

    public  void
    mostrarDialogo(String sTitulo, String sMensaje,Boolean bFinaliza)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(sTitulo);
        builder.setMessage(sMensaje);
        //builder.setPositiveButton("OK", null);
        if(bFinaliza)
        {
            builder.setNeutralButton("Entendido", (dialog, which) -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity_list.class);
                intent.putExtra("keyRestaurante", keyRestaurante);
                startActivity(intent); });
        }
        else
        {
            builder.setNeutralButton("Entendido",null);
        }

        builder.setInverseBackgroundForced(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

     */
}