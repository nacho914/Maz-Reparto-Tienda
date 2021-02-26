package com.example.mazrepartotienda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Modelos.Pedidos;
import Modelos.TiemposMinutos;
import Modelos.UsuariosRestaurantes;

public class MainActivity_Pedido extends AppCompatActivity {

    //FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference("");

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Pedidos");
    DatabaseReference refUsuarios;
    DatabaseReference refConfiguracion = database.getReference("Configuracion/Tiempos");


    EditText mColonia;
    EditText mCalle;
    EditText mNumeroCasa;
    EditText mNombreComensal;
    EditText mPago;
    EditText mTelefono;
    TextView mPedidos;
    String sNombreNegocio;
    Spinner sMinutos;
    int iTotales=0;
    String keyRestaurante;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__pedido);

         mColonia= findViewById(R.id.mColonia);
         mCalle= findViewById(R.id.mCalle);
         mNumeroCasa=findViewById(R.id.mNumeroCasa);
         mNombreComensal= findViewById(R.id.mNombreComensal);
         mPago= findViewById(R.id.mPago);
         mTelefono= findViewById(R.id.mTelefono);
         mPago= findViewById(R.id.mPago);
         mPedidos=findViewById(R.id.mCantidadPedidos);
         mPedidos.setText("0");
        sMinutos = findViewById(R.id.sMinutos);
        progressDialog = new ProgressDialog(this);
        keyRestaurante = getIntent().getStringExtra("keyRestaurante");

        refUsuarios = database.getReference("Usuarios/UsuariosRestaurantes/"+keyRestaurante);
        //sNombreNegocio = "Panama";


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot PedidoSnapshot: dataSnapshot.getChildren()) {

                        String nana = PedidoSnapshot.getKey();
                        Pedidos pedido = PedidoSnapshot.getValue(Pedidos.class);

                        if(pedido.NombreNegocio.equals(sNombreNegocio))
                            iTotales++;
                    }
                    mPedidos.setText(String.valueOf(iTotales));
                    iTotales=0;

                }
                else{
                    mPedidos.setText("0");
                }

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        refConfiguracion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TiemposMinutos tie = snapshot.getValue(TiemposMinutos.class);
                String[] items= new String[(tie.minmax-tie.minmin)+1];
                int iHelp=0;
                for(int i=tie.minmin; i<=tie.minmax; i++)
                {
                    items[iHelp]=String.valueOf(i);
                    iHelp++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);
                sMinutos.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UsuariosRestaurantes Rest = snapshot.getValue(UsuariosRestaurantes.class);
                sNombreNegocio = Rest.Nombre;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void RealizaRegistro(View view) throws JSONException {

        progressDialog.setTitle("Maz Reparto");
        progressDialog.setMessage("Verificando Datos");
        progressDialog.show();

        DatabaseReference NewUserPush = ref.push();
        Pedidos pedido = llenarPedido();

        NewUserPush.setValue(pedido);
        enviarNotificaciones(pedido);

        mostrarDialogo("Pedidos","Su pedido fue correctamente enviado");
        progressDialog.dismiss();
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        hideSoftKeyboard(view);

    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Pedidos llenarPedido()
    {

        String sDireccion = " Colonia: " + mColonia.getText().toString() + " Calle: " + mCalle.getText().toString()
                + " Numero: " + mNumeroCasa.getText().toString();
        String sNombreCliente = mNombreComensal.getText().toString();
        //String sNombreCliente = ServerValue.TIMESTAMP.toString();
        long iTelefono = Long.parseLong(mTelefono.getText().toString());
        int iPrecio = Integer.parseInt(mPago.getText().toString());
        int iTiempo = Integer.parseInt(sMinutos.getSelectedItem().toString());


        Pedidos pedido = new Pedidos(sNombreNegocio, sDireccion, iTelefono, sNombreCliente, iPrecio, iTiempo,"",keyRestaurante);

        return pedido;
    }


    public void enviarNotificaciones(Pedidos pedido) throws JSONException
    {
        String postUrl = "https://fcm.googleapis.com/fcm/send";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject obj = new JSONObject();
        JSONObject objChild = new JSONObject();

        obj.put("to","/topics/NotificacionesPedidos");
        obj.put("direct_book_ok",true);

        objChild.put("body",pedido.Direccion);
        objChild.put("title","Un nuevo pedido cayo de "+ pedido.NombreNegocio);
        obj.put("notification",objChild);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, obj, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "key=AAAAyUXOSJI:APA91bHRcWTrD3LB50qTECUJsKB5pCaUL5pOZBzsMcQHEwX_xyEujXHVkKcB9DpoHM39x_6IWVAUDM3jJ8peL_6W7DmtOJArJUWGmnOtW6RKz9Q7Vaqb2SXiUC5ygyex9OTqUD3sZ7Bc");
                params.put("Content-Type", "application/json");

                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
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

     /*
    public void obtenerKeyCelulares(Pedidos pedido)
    {
        refUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String sKeyCelulares="";
                boolean bEsprimera=true;

                for (DataSnapshot UsuarioSnapshot: dataSnapshot.getChildren()) {

                    Usuarios user = UsuarioSnapshot.getValue(Usuarios.class);

                    if(user.IdTipoUsuario==0)
                    {
                        if(bEsprimera) {
                            sKeyCelulares = user.keyNotificaciones;
                            bEsprimera = false;
                        }
                        else
                        {
                            sKeyCelulares=sKeyCelulares+","+user.keyNotificaciones;
                        }
                    }
                }

                try {
                    enviarNotificaciones(pedido,sKeyCelulares);
                    mostrarDialogo("Pedidos","Su pedido fue correctamente enviado");
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
                mostrarDialogo("Autenticación","Ocurrio el siguiente problema: "+error.toException());
            }
        });
    }*/
}

