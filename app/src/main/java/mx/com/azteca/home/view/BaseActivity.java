package mx.com.azteca.home.view;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.HomeApplication;
import mx.com.azteca.home.controller.BaseController;
import mx.com.azteca.home.controller.CComparable;
import mx.com.azteca.home.controller.CDocumentInstance;
import mx.com.azteca.home.model.ipati.pojo.DocumentInstance;
import mx.com.azteca.home.service.LocalizacionServices;
import mx.com.azteca.home.view.ipati.FormularioActivity;
import mx.com.azteca.home.view.ipati.util.ProgressDialog;
import mx.com.azteca.home.view.util.DialogResponse;
import rx.Observable;

public abstract class BaseActivity<Controller extends BaseController>  extends AppCompatActivity implements ServiceConnection {

    private BaseController controller;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    protected DocumentInstance documentInstance;

    private Intent intService;
    private Messenger messengerService;
    private Messenger messengerCustomer;
    String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messengerCustomer = new Messenger(new IncomingMessageHandler());
        intService = new Intent(BaseActivity.this, LocalizacionServices.class);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        startService(intService);
    }

    public void onCreateActivity(final Class<? extends BaseController> controller, boolean checkPermisos)  {
        this.sharedPreferences = this.getSharedPreferences(HomeApplication.class.getPackage().getName(), Context.MODE_PRIVATE);

        Observable<BaseController> observable = Observable.create(subscriber -> {
            try {
                BaseController baseController = controller.newInstance();
                baseController.setBaseActivity(BaseActivity.this);
                subscriber.onNext(baseController);
                subscriber.onCompleted();
            }
            catch (InstantiationException ie) {
                subscriber.onError(new Exception("Ocurri\u00f3 un problema al instanciar el controlador", ie));
            }
            catch (IllegalAccessException iae) {
                subscriber.onError(new Exception("Ocurri\u00f3 un problema al acceder al controlador", iae));
            }
        });

        observable.subscribe(
                value -> BaseActivity.this.controller = value,
                error -> notificarUsuario("Error", error.getMessage())
        );

        if (checkPermisos) {
            checkPermissions();
        }
    }

    public void onCreateActivity(final Class<? extends BaseController> controller) {
        onCreateActivity(controller, true);
    }

    public void iniciarNewThread(Class<? extends AppCompatActivity> activity) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(getApplication(), activity);
            startActivity(intent);
            finish();
        }, 0);
    }

    public void notificarUsuario(String titulo, String mensaje) {
        DialogResponse.createDialogResponse(BaseActivity.this, titulo, mensaje, "Aceptar", null).show();
    }

    @SuppressLint("HardwareIds")
    public Object getData(String key) {
        if (key.equals("IMEI")) {
            TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            return mngr.getDeviceId();
        }
        else if (getIntent().hasExtra(key)) {
            return getIntent().getExtras().get(key);
        }
        else if (sharedPreferences.contains(key)) {
            return sharedPreferences.getAll().get(key);
        }
        return null;
    }

    public Object getData(String key, Object defaul) {
        Object object = getData(key);
        return object == null ? defaul : object;
    }

    public void setData(String key, Object value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if (value instanceof  String) {
            edit.putString(key, value.toString());
        }
        else if (value.getClass().getSimpleName().equals("int") || value instanceof Integer) {
            edit.putInt(key, (int)value);
        }
        else if (value.getClass().getSimpleName().equals("boolean") || value instanceof Boolean) {
            edit.putBoolean(key, (boolean)value);
        }
        else if (value.getClass().getSimpleName().equals("float") || value instanceof Float) {
            edit.putFloat(key, (float)value);
        }
        else if (value.getClass().getSimpleName().equals("long")  || value instanceof Long) {
            edit.putLong(key, (long)value);
        }
        boolean commit = edit.commit();
        if (commit) {
            edit.apply();
        }
    }

    public void mostrarProgress(String mensaje) {
        progressDialog = ProgressDialog.cargarProgressDialog(BaseActivity.this, null, mensaje).mostrar();
    }

    public void ocultarProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void iniciarEdicion(DocumentInstance documentInstance) {
        this.documentInstance = documentInstance;
        iniciarEdicion(documentInstance.IdDocumento, documentInstance.Fecha, documentInstance.Folio);
    }

    public void iniciarEdicion(int idDocumento, long fecha, String folio) {
        Intent intent = new Intent(BaseActivity.this, FormularioActivity.class);
        intent.putExtra("IdDocumento"       , idDocumento);
        intent.putExtra("Fecha"             , fecha);
        intent.putExtra("Folio"             , folio);
        intent.putExtra("lat"               , 0.0);
        intent.putExtra("lng"               , 0.0);
        startActivityForResult(intent, idDocumento);
    }

    public Controller getController() {
        return (Controller) controller;
    }

    protected boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(BaseActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), permissions.length );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == permissions.length) {
            if (!permisosCompletos()) {
                notificarUsuario("Permisos", "Para el correcto funcionamiento de la aplicaci\u00f3n es necesario conceder los permisos requeridos.");
            }
            else {
                notificarUsuario("Permisos", "Los permisos han sido concedidos, a continuaci\u00f3nn puede continuar utilizando la aplicaci\u00f3n");
            }
        }
    }

    protected boolean permisosCompletos() {
        for (String permiso : permissions ) {
            if (ActivityCompat.checkSelfPermission(BaseActivity.this, permiso) == PackageManager.PERMISSION_DENIED ) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(intService);
        bindService(   intService, this, Context.BIND_AUTO_CREATE);
        sendMessageToService(LocalizacionServices.LOAD_LOCATION);
    }

    public Drawable getIconDrawable(int resource) {
        Drawable mDrawable;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            mDrawable = ContextCompat.getDrawable(getContext(), resource);
        } else {
            mDrawable = getResources().getDrawable(resource);
        }
        return mDrawable;
    }

    public Context getContext() {
        return getBaseContext();
    }

    @Override
    protected void onPause() {
        try {
            doUnbindService();
        }
        catch (Exception ex) {
            Log.e("LocalizacionActivity", "Error:" + ex.getMessage());
        }
        super.onPause();
    }

    private void doUnbindService() {
        if (messengerService != null) {
            try {
                Message msg = Message.obtain(null, LocalizacionServices.CLIENT_UNREGISTER);
                msg.replyTo = messengerCustomer;
                messengerService.send(msg);
            }
            catch (RemoteException e) {
                Log.e("LocalizacionActivity", "Error:" + e.getMessage());
            }
        }
        unbindService(this);
    }
    private void sendMessageToService(int value) {
        if (messengerService != null) {
            try {
                Message msg = Message.obtain(null, value);
                msg.replyTo = messengerCustomer;
                messengerService.send(msg);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.e("LocalizacionActivity", "EL messengerService NULL");
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        messengerService = new Messenger(service);
        try {
            Message msg = Message.obtain(null, LocalizacionServices.CLIENT_REGISTER);
            msg.replyTo = messengerCustomer;
            messengerService.send(msg);
            Log.e("LocalizacionActivity", "Servicio conectado");
            sendMessageToService(LocalizacionServices.LOAD_LOCATION);
        }
        catch (RemoteException e) {
            e.printStackTrace();
            Log.e("LocalizacionActivity", "Error al conectar con el servicio:" + e.getMessage());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        messengerService = null;
    }

    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case LocalizacionServices.LOAD_LOCATION:

                    double lat = msg.getData().getDouble("latitude");
                    double lng = msg.getData().getDouble("longitude");
                    onCambioPosicion(lat, lng);

                    break;
                case LocalizacionServices.GPS_DISABLED:
                    break;
                case LocalizacionServices.GPS_ENABLE:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void onCambioPosicion(double lat, double lng) {
    }

    public void loadComparables(CDocumentInstance.MarkerInfo markerInfo) {
    }

    public void loadComparableInfo(CComparable.ComparableInfo comparableInfo) {
    }

    public void suscribe(BaseController.Listener listener) {
        controller.subscribe(listener);
    }

}
