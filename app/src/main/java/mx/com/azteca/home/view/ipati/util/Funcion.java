package mx.com.azteca.home.view.ipati.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Funcion {

    public static Funcion FUNCION;

    public static synchronized  Funcion getInstace() {
        if (Funcion.FUNCION == null) {
            Funcion.FUNCION = new Funcion();
        }
        return Funcion.FUNCION;
    }

    public static boolean isNetworkAvailiable(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        return i != null && i.isConnected() && i.isAvailable();
    }

}
