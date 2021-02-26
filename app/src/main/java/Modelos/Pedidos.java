package Modelos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class Pedidos {

    public String NombreNegocio;
    public String Direccion;
    public long Telefono;
    public String NombreCliente;
    public int Precio;
    public int TiempoPedido;
    public String TrabajadorKey;
    public String RestauranteKey;
    HashMap<String, Object> timestampCreated;


    public  Pedidos()
    {

    }

    public Pedidos(String sNombreNegocio, String sDireccion, long iTelefono, String sNombre, int iPrecio, int iTiempo, String sTrabajadorKey, String sRestauranteKey) {

        this.NombreNegocio=sNombreNegocio;
        this.Direccion= sDireccion;
        this.Telefono= iTelefono;
        this.NombreCliente=sNombre;
        this.Precio=iPrecio;
        this.TiempoPedido=iTiempo;
        this.TrabajadorKey = sTrabajadorKey;
        this.RestauranteKey=sRestauranteKey;

        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;

    }

    public HashMap<String, Object> getTimestampCreated(){
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong(){
        return (long)timestampCreated.get("timestamp");
    }

}

