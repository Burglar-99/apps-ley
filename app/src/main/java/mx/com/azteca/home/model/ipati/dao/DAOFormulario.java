package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.model.ipati.pojo.Formulario;
import mx.com.azteca.home.util.Formula;

public class DAOFormulario extends DAOObject {

    public DAOFormulario(Context context) {
        super(context);
    }

    public void guardar(Formulario formulario) {
        try {
            super.insert(formulario);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public List<Formulario> loadFormulario(int idDocumento) {
        List<Formulario> listFormulario = new ArrayList<>();
        try {
            List formularios = (List) super.select(Formulario.class, null, "IdDocumento = " + idDocumento, null, null);
            if (formularios != null) {
                listFormulario.addAll(formularios);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listFormulario;
    }

    public Formulario getDetail(int idFormulario) {
        try {
            Formulario formulario = new Formulario();
            formulario = (Formulario) super.select(formulario, null, "Identity = ?", new String[]{String.valueOf(idFormulario)}, null);
            return formulario;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public boolean exist(int idFormulario) {
        try {
            return super.count(Formulario.class, null, "Identity = ?", new String[]{String.valueOf(idFormulario)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void delete(int idFormulario) {
        try {
            super.delete(Formulario.class, "Identity = ?", String.valueOf(idFormulario));
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public void truncate() {
        try {
            super.delete(Formulario.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}
