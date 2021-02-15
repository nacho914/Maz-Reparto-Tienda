package Modelos;

import java.util.HashMap;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

public class Usuarios {

    public String Nombre;
    public String Correo;
    public String Password;
    public String TipoUsuario;
    public int IdTipoUsuario;
    public String keyNotificaciones;
    HashMap<String, Object> timestampCreated;

    public Usuarios()
    {

    }

    public Usuarios(String sNombre, String sCorreo, String sPassword, String sTipoUsuario, int iTipoUsuario, String sKey) {
        Nombre = sNombre;
        Correo = sCorreo;
        Password = sPassword;
        TipoUsuario = sTipoUsuario;
        IdTipoUsuario = iTipoUsuario;
        keyNotificaciones = sKey;

        HashMap<String, Object> timestampNow = new HashMap<>();
        timestampNow.put("timestamp", ServerValue.TIMESTAMP);
        this.timestampCreated = timestampNow;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong() {
        return (long) timestampCreated.get("timestamp");
    }
}