package mx.com.azteca.home.controller;


import android.content.Context;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;

import com.google.gson.JsonParser;

import java.util.List;

import mx.com.azteca.home.model.ipati.model.MConfiguracion;
import mx.com.azteca.home.model.ipati.pojo.BaseInfo;


public class CConfiguracion extends BaseController {

    private Context context;
    private Listener listener;


    public enum Operacion {
        CONFIGURAR_DISPOSITIVO,
        INICAR_DISPOSITIVO
    }

    public CConfiguracion(Context context) {
        this.context = context;
    }

    public CConfiguracion(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void isDispositivoBloqueado() {
        Proceso proceso = new Proceso("isDispositivoBloqueado", null, false) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o instanceof Boolean) {
                    if ((boolean) o) {
                        if (listener != null)
                            listener.iniciarSession();
                    }
                    else {
                        if (listener != null)
                            listener.iniciarDispositivo();
                    }
                }
            }
        };
        proceso.execute();
    }

    public void cerrarSesion() {
        Proceso proceso = new Proceso("cerrarSesion", "Cerrando sesi\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null)
                    listener.cerrarSesion();
            }
        };
        proceso.execute();
    }

    public void loadEmpresas() {
        TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String workStration = telephonyManager.getDeviceId();
        Proceso proceso = new Proceso("loadEmpresas", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o != null) {
                    if (o instanceof List) {
                        if (listener != null) {
                            listener.loadEmpresas((List<BaseInfo>) o);
                        }
                    }
                }
                else {
                    listener.notificarUsuario("Error", "No se encontraron empresas al que usuario tenga acceso");
                }
            }
        };
        proceso.execute(workStration);
    }

    public void updateEmpSuc(Object empresa, Object sucursal) {
        if (empresa == null) {
            listener.notificarUsuario("Configuraci\u00f3n", "Seleccione la empresa");
            return;
        }
        if (sucursal == null) {
            listener.notificarUsuario("Configuraci\u00f3n", "Seleccione la sucursal");
            return;
        }
        Proceso proceso = new Proceso("updateEmpSuc", "Actualizando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.confActualizada();
                }
            }
        };
        proceso.execute(((BaseInfo)empresa).Identity, ((BaseInfo)sucursal).Identity);
    }

    public class Proceso extends AsyncTask<Object, Object, Object> {

        private String operation;
        private String mensaje;
        private boolean progressDialog;

        public Proceso(String operation, String mensaje, boolean progressDialog) {
            this.operation = operation;
            this.mensaje = mensaje;
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog)
                if (listener != null)
                    listener.mostrarProgress(mensaje);
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                switch (operation) {
                    case "isDispositivoBloqueado":
                        return isDispositivoBloqueado();
                    case "cerrarSesion":
                        cerrarSesion();
                        break;
                    case "updateEmpSuc":
                        updateEmpSuc((int)params[0], (int)params[1]);
                        break;
                }
            }
            catch (Exception ex) {
                return ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (progressDialog)
                if (listener != null)
                    listener.ocultarProgress();
            if (o instanceof String && o.toString().startsWith("{\"Error\":")) {
                JsonParser parser = new JsonParser();
                String error = parser.parse(o.toString()).getAsJsonObject().get("Error").getAsString();
                listener.notificarUsuario("Error", error);
            }
        }

        private boolean isDispositivoBloqueado() throws Exception {
            MConfiguracion mConfiguracion = new MConfiguracion(context);
            return mConfiguracion.isDispositivoBloqueado();
        }


        private void cerrarSesion() {
            MConfiguracion mConfiguracion = new MConfiguracion(context);
            mConfiguracion.cerrarSesion();
        }


        private void updateEmpSuc(int idEmpresa, int idSucursal) {
            MConfiguracion mConfiguracion = new MConfiguracion(context);
            mConfiguracion.updateEmpresa(idEmpresa);
            mConfiguracion.updateSucursal(idSucursal);
            mConfiguracion.iniciarSesion();
        }

    }

    @Override
    public void subscribe(BaseController.Listener listener) {
    }

    public interface Listener extends IControlador {
        void iniciarDispositivo();
        void iniciarSession();
        void cerrarSesion();
        void cuentaIncorrecta();
        void loadSessionID(int idUsuario, String sessionID);
        void loadEmpresas(List<BaseInfo> info);
        void inicarConfiguracion();
        void confActualizada();
        Object getConfiguracion(Class<?> type, String key);
    }

}
