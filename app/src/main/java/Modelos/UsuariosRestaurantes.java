package Modelos;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class UsuariosRestaurantes {

    public String Nombre;
    public String Domicilio;
    public long Telefono;
    public String Correo;
    public  String Password;
    public  String HoraInicio;
    public  String HoraFin;
    public  String keyNotificaciones;
    HashMap<String, Object> timestampCreated;

    public UsuariosRestaurantes()
    {

    }

    public UsuariosRestaurantes(String sNombre,String sDomicilio,long lTele, String sCorreo,
                                String sPassword,String shoraInicio,String sHoraFin,String sKey)
    {
        Nombre = sNombre;

        Domicilio= sDomicilio;
        Telefono=lTele;
        Correo=sCorreo;
        Password=sPassword;
        HoraInicio=shoraInicio;
        HoraFin=sHoraFin;
        keyNotificaciones=sKey;

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
