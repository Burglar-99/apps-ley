package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.Date;

import mx.com.azteca.home.model.ipati.pojo.EventLog;

public class DAOEventLog extends DAOObject {

    public DAOEventLog(Context context) {
        super(context);
    }

    public void guardar(EventLog eventLog) {
        try {
            insert(eventLog);
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    @Override
    protected void truncate() {
        try {
            delete(EventLog.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public void deleteOldValues(long fecha) {
        try {
            delete(EventLog.class, "Fecha <= " + fecha);
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public static void HandledException(Context context, Exception ex, String userMessage) throws Exception {
        EventLog eventLog = new EventLog();
        eventLog.Fecha  = new Date().getTime();
        eventLog.Type   = EventLog.EventType.ERROR.numType;
        eventLog.Info   = ex.getMessage() == null? ex.toString() : ex.getMessage();
        DAOEventLog daoEventLog = new DAOEventLog(context);
        daoEventLog.guardar(eventLog);
        if (ex.getMessage() == null || !ex.getMessage().startsWith("{\"Error\":")) {
            JsonObject jError = new JsonObject();
            jError.addProperty("Error", userMessage);
            throw new Exception(jError.toString());
        }
        throw ex;
    }

}
