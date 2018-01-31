package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.ipati.pojo.Persona;

public class DAOPersona extends DAOObject {

    public DAOPersona(Context context) {
        super(context);
    }

    public void guardar(Persona persona) {
        try {
            super.insert(persona);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public Persona getDetail(int idPersona) {
        try {
            Persona persona = new Persona();
            persona = (Persona) super.select(persona, null, "Identity = " + idPersona, null, null);
            return persona;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public void truncate() {
        try {
            super.delete(Persona.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}
