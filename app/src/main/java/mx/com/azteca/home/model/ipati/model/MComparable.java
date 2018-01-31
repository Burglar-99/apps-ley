package mx.com.azteca.home.model.ipati.model;


import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;

public class MComparable extends BaseModel {

    private DAOIpati daoIpati;

    public MComparable(Context context) {
        super(context);
    }

    public MComparable(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        this.daoIpati = new DAOIpati();
    }

    public String loadComparablesExpress(String sessionID, String inmueble, int radio, String tipo, String lat, String lng) {
        try {
            return this.daoIpati.loadComparableExpress(sessionID, inmueble, radio, tipo, lat, lng);
        }
        catch (Exception ex){
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return null;
    }

    public String loadComparablesImagenesSeleccion(String sessionID, String idRegistro) {
        try {
            return this.daoIpati.loadComparableSeleccion(sessionID, idRegistro);
        }
        catch (Exception ex){
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return null;
    }

}
