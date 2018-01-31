package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import mx.com.azteca.home.model.ipati.pojo.Configuracion;

public class DAOConfiguracion extends DAOObject{

    public DAOConfiguracion(Context context) {
        super(context);
    }

    public boolean isDispositivoBloqueado() {
        try {
            Configuracion configuracion = new Configuracion();
            configuracion = (Configuracion) select(configuracion, null, "Path = 'Device' AND Name = 'Bloqueado'", null, null);
            return configuracion == null || Boolean.parseBoolean(configuracion.Value);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return true;
    }

    public void updateConfiguracion(Configuracion configuracion) {
        try {
            super.update(configuracion, "Path = ? AND Name = ?", configuracion.Path, configuracion.Name);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public int usuarioLogIn() {
        try {
            Configuracion configuracion = new Configuracion();
            configuracion = (Configuracion) select(configuracion, null, "Path = 'Device' AND Name = 'Usuario'", null, null);
            return Integer.parseInt(configuracion.Value);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return 0;
    }

    public int getEmpresa() {
        try {
            Configuracion configuracion = new Configuracion();
            configuracion = (Configuracion) select(configuracion, null, "Path = 'Device' AND Name = 'Empresa'", null, null);
            return Integer.parseInt(configuracion.Value);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return 0;
    }

    public int getSucursal() {
        try {
            Configuracion configuracion = new Configuracion();
            configuracion = (Configuracion) select(configuracion, null, "Path = 'Device' AND Name = 'Sucursal'", null, null);
            return Integer.parseInt(configuracion.Value);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return 0;
    }

    public void truncate() {
        try {
            super.delete(Configuration.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}
