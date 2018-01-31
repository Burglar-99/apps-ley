package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.provider.pojo.SessionInfo;

public class DAOSessionInfo extends DAOObject {

    public DAOSessionInfo(Context context) {
        super(context);
    }

    public void guardar(SessionInfo sessionInfo) {
        try {
            super.insert(sessionInfo);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public SessionInfo getDetail(String sessionID) {
        try {
            SessionInfo sessionInfo = new SessionInfo();
            sessionInfo = (SessionInfo) super.select(sessionInfo, null, "sessionID = ?", new String[]{ sessionID } , null);
            return sessionInfo;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }


    public boolean existSessionUsuario(int idUsuario) {
        try {
            return super.count(SessionInfo.class, null, "idUsuario = ?", new String[]{String.valueOf(idUsuario)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void delete(String sessionID) {
        try {
            super.delete(SessionInfo.class, "sessionID = ?", sessionID);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void truncate() {
        try {
            super.delete(SessionInfo.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}