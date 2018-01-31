package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.ipati.pojo.Session;


public class DAOSession extends DAOObject {

    public DAOSession(Context context) {
        super(context);
    }

    public void guardar(Session session) {
        try {
            super.insert(session);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void update(Session session) {
        try {
            super.update(session, "IdUsuario = ?", String.valueOf(session.idUsuario));
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public Session getDetail(int idUsuario) {
        try {
            Session session = new Session();
            session = (Session) super.select(session, null, "IdUsuario = " + idUsuario, null, null);
            return session;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public boolean exist(int idUsuario) {
        try {
            return super.count(Session.class, null, "IdUsuario = ?", new String[]{String.valueOf(idUsuario)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void truncate() {
        try {
            super.delete(Session.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}

