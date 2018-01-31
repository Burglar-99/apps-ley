package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import mx.com.azteca.home.model.ipati.pojo.Usuario;

public class DAOUsuario extends DAOObject {

    public DAOUsuario(Context context) {
        super(context);
    }

    public void guardar(Usuario usuario) {
        try {
            super.insert(usuario);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public Usuario getDetail(int idUsuario) {
        try {
            Usuario usuario = new Usuario();
            usuario = (Usuario) super.select(usuario, null, "Identity = " + idUsuario, null, null);
            return usuario;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public void truncate() {
        try {
            super.delete(Usuario.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public HashMap<String, Object> getDataLoginUsuario(int idUsuario) {
        try {
            List<HashMap<String, Object>> listResult = super.select(Usuario.class, "DataUserLogin", new String[]{String.valueOf(idUsuario)});
            if (listResult.size() > 0) {
                return listResult.get(0);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }
}
