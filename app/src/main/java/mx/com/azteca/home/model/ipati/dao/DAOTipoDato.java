package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.ipati.pojo.TipoDato;


public class DAOTipoDato extends DAOObject {

    public DAOTipoDato(Context context) {
        super(context);
    }

    public void guardar(TipoDato instance) {
        try {
            super.insert(instance);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void update(TipoDato tipoDato) {
        try {
            super.update(tipoDato, "Identity = ?", String.valueOf(tipoDato.Identity));
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public TipoDato getDetail(int identity) {
        try {
            TipoDato tipoDato = new TipoDato();
            tipoDato = (TipoDato) super.select(tipoDato, null, "Identity = ?", new String[]{String.valueOf(identity)}, null);
            return tipoDato;
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return null;
    }

    public boolean exist(int idTipoDato) {
        try {
            return super.count(TipoDato.class, null, "Identity = ? ", new String[]{String.valueOf(idTipoDato)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void delete(int idTipoDato) {
        try {
            super.delete(TipoDato.class, "Identity = ?", String.valueOf(idTipoDato));
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    @Override
    public void truncate() {
        try {
            super.delete(TipoDato.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}
