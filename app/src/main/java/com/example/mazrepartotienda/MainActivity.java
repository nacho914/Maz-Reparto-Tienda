package com.example.mazrepartotienda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
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
import Retroapi.APIService.APIService;
import Retroapi.APIService.APIService2;
import Retroapi.APIService.ApiUtils;
import Retroapi.APIService.ApiUtils2;
import Retroapi.Model.POSTSENDNOTIFICATIONS;
import Retroapi.Model.Post;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;

public class MainActivity extends AppCompatActivity {

    private APIService mAPIService;
    private APIService2 mAPIService2;

    private final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private final String serverKey = "key=" + "AAAAyUXOSJI:APA91bHRcWTrD3LB50qTECUJsKB5pCaUL5pOZBzsMcQHEwX_xyEujXHVkKcB9DpoHM39x_6IWVAUDM3jJ8peL_6W7DmtOJArJUWGmnOtW6RKz9Q7Vaqb2SXiUC5ygyex9OTqUD3sZ7Bc";
    private final String contentType = "application/json";
    public TextView mPedidos;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Pedidos/Activos");

    public  String keyRestaurante;
    int iTotales=0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Maz Reparto");
        progressDialog.setMessage("Cargando Datos");
        progressDialog.show();

        keyRestaurante = getIntent().getStringExtra("keyRestaurante");
        mPedidos=findViewById(R.id.mCantidadPedidos);
        CargaTotales();

    }

    public void sendNotifications(View view) throws JSONException {

        JSONObject obj = new JSONObject();
        JSONObject objChild = new JSONObject();

        obj.put("to","fKlBJVmmRw-OAVTxBgJR-D:APA91bHF_IFOa71KvkIuP_ejFdjLRMq7XWhsHvhtiAwYvgswRqD5H_fY9o192PZcEPKH1wHmiIjQeybWE5_jYplcFohjf2l-tXNbaC99G7HcU7B8BANzfbSrjnIUhtEe2aKRkBioJ0bn");
        obj.put("direct_book_ok",true);

        objChild.put("body","Lo mandaste desde una app!");
        objChild.put("title","nacho");
        obj.put("notification",objChild);

        mAPIService2 = ApiUtils2.getAPIService();
        mAPIService = ApiUtils.getAPIService();

        //sendPost(obj);
        Log.w("nana", "Antes");
        //sendPost2("foo", "bar");
        boolean t=true;
        //PruebaFinal pru = new PruebaFinal("fKlBJVmmRw-OAVTxBgJR-D:APA91bHF_IFOa71KvkIuP_ejFdjLRMq7XWhsHvhtiAwYvgswRqD5H_fY9o192PZcEPKH1wHmiIjQeybWE5_jYplcFohjf2l-tXNbaC99G7HcU7B8BANzfbSrjnIUhtEe2aKRkBioJ0bn",t);
        //sendPost(pru);
        sendNotification3(obj);
        //Log.w("nana", obj.toString());
    }

    public void sendPost(PruebaFinal pru) {

        Log.w("nana", "uno");
        mAPIService.savePost(pru).enqueue(new Callback<POSTSENDNOTIFICATIONS>() {
            @Override
            public void onResponse(Call<POSTSENDNOTIFICATIONS> call, Response<POSTSENDNOTIFICATIONS> response) {

                if(response.isSuccessful()) {
                    //showResponse(response.body().toString());
                    Log.w("nana", "Entro2");
                    Log.w("TAG", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<POSTSENDNOTIFICATIONS> call, Throwable t) {
                Log.w("TAG", "Unable to submit post to API.");
                Log.w("nana", "Entro2");
            }
        });
    }

    public void sendPost2(String title, String body) {
        mAPIService2.savePost(title, body, 1).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(response.isSuccessful()) {
                    //showResponse(response.body().toString());
                    Log.i("nana", "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                Log.e("nana", t.getMessage());
            }
        });
    }

    public void sendPost3() throws JSONException {

        String topic = "fKlBJVmmRw-OAVTxBgJR-D:APA91bHF_IFOa71KvkIuP_ejFdjLRMq7XWhsHvhtiAwYvgswRqD5H_fY9o192PZcEPKH1wHmiIjQeybWE5_jYplcFohjf2l-tXNbaC99G7HcU7B8BANzfbSrjnIUhtEe2aKRkBioJ0bn"; //topic has to match what the receiver subscribed to

        JSONObject notification =new  JSONObject();
        JSONObject notifcationBody = new  JSONObject();


        notifcationBody.put("title", "Enter_title");
        notifcationBody.put("message","nana");   //Enter your notification message
        notification.put("to", topic);
        notification.put("data", notifcationBody);


        sendNotification3(notification);
    }

    private void sendNotification3(JSONObject notification) {

        String postUrl = "https://fcm.googleapis.com/fcm/send";
        RequestQueue requestQueue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, notification, new com.android.volley.Response.Listener<JSONObject>() {
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
/*
        JsonObjectRequest JASON = new JsonObjectRequest(FCM_API,notification,
                com.android.volley.Response.Listener<JsonObjectRequest>{}, com.android.volley.Response.ErrorListener{})
         jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
                Response.Listener<JSONObject> { response ->
                        Log.i("TAG", "onResponse: $response")
                        msg.setText("")
                },
                Response.ErrorListener {
            //Toast.makeText(this@MainActivity, "Request error", Toast.LENGTH_LONG).show()
            //Log.i("TAG", "onErrorResponse: Didn't work")
        }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    */}

    public void mandarOtraActividad(View view)
    {
        Intent intent = new Intent(this, MainActivity_Pedido.class);
        intent.putExtra("keyRestaurante", keyRestaurante);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void cerrarSesion(View view)
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this,MainActivity_Login.class);
        startActivity(intent);
    }

    public void CargaTotales()
    {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot PedidoSnapshot: dataSnapshot.getChildren()) {

                        Pedidos pedido = PedidoSnapshot.getValue(Pedidos.class);

                        if(pedido.RestauranteKey.equals(keyRestaurante))
                            iTotales++;
                    }
                    mPedidos.setText(String.valueOf(iTotales));
                    iTotales=0;
                    progressDialog.dismiss();

                }
                else{
                    mPedidos.setText("0");
                    progressDialog.dismiss();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    public void sendList(View view)
    {
        Intent intent = new Intent(this,MainActivity_list.class);
        intent.putExtra("keyRestaurante", keyRestaurante);
        startActivity(intent);
    }
}

