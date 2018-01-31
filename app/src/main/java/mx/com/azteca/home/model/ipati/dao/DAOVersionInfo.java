package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.ipati.pojo.VersionInfo;


public class DAOVersionInfo extends DAOObject {

    public DAOVersionInfo(Context context) {
        super(context);
    }

    public void guardar(VersionInfo version) {
        try {
            super.insert(version);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public VersionInfo getDetail(int idDocumento, int version) {
        try {
            VersionInfo versionInfo = new VersionInfo();
            versionInfo = (VersionInfo) super.select(versionInfo, null, "IdDocumento = ? AND Version = ?", new String[]{ String.valueOf(idDocumento), String.valueOf(version) }, null);
            return versionInfo;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public void delete(int idDocumento, int version, long fecha) {
        try {
            super.delete(VersionInfo.class, "IdDocumento = ? AND Version = ? AND Fecha = ?", String.valueOf(idDocumento), String.valueOf(version), String.valueOf(fecha));
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public boolean exist(int idDocumento, int version, long fecha) {
        try {
            return super.count(VersionInfo.class, null, "IdDocumento = ? AND Version = ? AND Fecha = ? ",new String[]{ String.valueOf(idDocumento), String.valueOf(version), String.valueOf(fecha)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void truncate() {
        try {
            super.delete(VersionInfo.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}
