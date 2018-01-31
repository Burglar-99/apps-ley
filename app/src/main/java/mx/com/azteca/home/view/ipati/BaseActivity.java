package mx.com.azteca.home.view.ipati;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import mx.com.azteca.home.HomeApplication;
import mx.com.azteca.home.R;
import mx.com.azteca.home.controller.CConfiguracion;
import mx.com.azteca.home.controller.CDocumento;
import mx.com.azteca.home.model.ipati.pojo.BaseInfo;
import mx.com.azteca.home.model.ipati.pojo.DataField;
import mx.com.azteca.home.model.ipati.pojo.Documento;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.Field;
import mx.com.azteca.home.model.ipati.pojo.Usuario;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStepOption;
import mx.com.azteca.home.service.LocalizacionServices;
import mx.com.azteca.home.util.Componente;
import mx.com.azteca.home.util.Formula;
import mx.com.azteca.home.util.GPS;
import mx.com.azteca.home.util.ListAdapter;
import mx.com.azteca.home.util.PreferenceDevice;
import mx.com.azteca.home.view.MapActivity;
import mx.com.azteca.home.view.ipati.util.DialogResponse;
import mx.com.azteca.home.view.ipati.util.ProgressDialog;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    private ProgressDialog progressDialog;
    private Object controlador;
    private Bundle bundle;
    private Messenger messengerService;
    private Messenger messengerCustomer;
    private Intent intService;
    private Uri uriCapturedImage;
    private DataField dataField;
    private Class<?> activity;
    private List<Componente.Tool> UnbindTool;
    private SharedPreferences sharedPreferences;

    String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };


    protected Componente.Tool ToolSelected;

    public int requestImageCapture = -1;
    public static final int CONFIGURACION_ACTIVITY  = 1000;
    public static final int MULTIPLE_PERMISSIONS    = 10;

    public BaseActivity() {
        this.UnbindTool = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bundle = savedInstanceState;
        messengerCustomer = new Messenger(new IncomingMessageHandler());
        intService = new Intent(BaseActivity.this, LocalizacionServices.class);
        this.sharedPreferences = this.getSharedPreferences(HomeApplication.class.getPackage().getName(), Context.MODE_PRIVATE);
        startService(intService);
    }

    public void onCreateActivity(Class<?> activity) {
        this.activity = activity;
        if (activity.getName().equals(PresentacionActivity.class.getName()) ||
                activity.getName().equals(ConfiguracionActivity.class.getName())) {
            this.controlador = new CConfiguracion(getContext(), new CConfiguracion.Listener() {
                @Override
                public void notificarUsuario(String titulo, String mensaje) {
                    BaseActivity.this.notificarUsuario(titulo, mensaje);
                }

                @Override
                public void mostrarProgress(String mensaje) {
                    progressDialog = ProgressDialog.cargarProgressDialog(BaseActivity.this, null, mensaje).mostrar();
                }

                @Override
                public void ocultarProgress() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void iniciarDispositivo() {
                    BaseActivity.this.iniciarDispositivo();
                }

                @Override
                public void iniciarSession() {
                }

                @Override
                public void cuentaIncorrecta() {
                    if (getSupportFragmentManager().getFragments().size() == 1 && getSupportFragmentManager().getFragments().get(0) != null) {
                        View view = getSupportFragmentManager().getFragments().get(0).getView();
                        if (view != null) {
                            view.findViewById(R.id.lblError).setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void cerrarSesion() {
                    BaseActivity.this.finish();
                }

                @Override
                public void loadSessionID(int idUsuario, String sessionID) {
                }

                @Override
                public void loadEmpresas(List<BaseInfo> info) {
                    BaseActivity.this.loadEmpresas(info);
                }

                @Override
                public void confActualizada() {
                    setResult(RESULT_OK);
                    BaseActivity.this.finish();
                }

                @Override
                public void inicarConfiguracion() {
                    Intent intent = new Intent(BaseActivity.this, ConfiguracionActivity.class);
                    startActivityForResult(intent, BaseActivity.CONFIGURACION_ACTIVITY);
                }

                @Override
                public Object getConfiguracion(Class<?> type, String key) {
                    if (sharedPreferences.contains(key)) {
                        return sharedPreferences.getAll().get(key);
                    }
                    return null;
                }

            });

            String userName = (String)getData(PreferenceDevice.USER_NAME.Key, "");
            String passwd   = (String)getData(PreferenceDevice.USER_PASSWD.Key, "");
            String imei     = (String)getData("IMEI", "");
            ((CConfiguracion)controlador).setContext(BaseActivity.this);
            ((CConfiguracion)controlador).setUserName(userName);
            ((CConfiguracion)controlador).setPasswd(passwd);
            ((CConfiguracion)controlador).setDeviceID(imei);

            if (activity.getName().equals(PresentacionActivity.class.getName())) {
                ((CConfiguracion)controlador).isDispositivoBloqueado();
            }
            else if (activity.getName().equals(ConfiguracionActivity.class.getName())) {
                ((CConfiguracion) controlador).loadEmpresas();
            }

        }
        else if ( activity.getName().equals(FormularioActivity.class.getName()) || activity.getName().equals(DocManagerActivity.class.getName())|| activity.getName().equals(MapActivity.class.getName()) ) {
            this.controlador = new CDocumento(new CDocumento.Listener() {

                @Override
                public void loadDocumento(List<Documento> listDocumento) {
                    BaseActivity.this.loadDocumento(listDocumento);
                }

                @Override
                public void notificarUsuario(String titulo, String mensaje) {
                    BaseActivity.this.notificarUsuario(titulo, mensaje);
                }

                @Override
                public void mostrarProgress(String mensaje) {
                    progressDialog = ProgressDialog.cargarProgressDialog(BaseActivity.this, null, mensaje).mostrar();
                }

                @Override
                public void ocultarProgress() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public View addView(View parent, String title) {
                    return BaseActivity.this.addView(parent, title);
                }

                @Override
                public View addView(View parent, View view) {
                    return BaseActivity.this.addView(parent, view);
                }

                @Override
                public void setTitle(String title) {
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(title);
                    }
                }

                @Override
                public View addTabContainer(String title, final FormularioActivity.Listener listener) {
                    return BaseActivity.this.addTabContainer(title, listener);
                }

                @Override
                public Activity getAcivity() {
                    return BaseActivity.this.getAcivity();
                }

                @Override
                public Context getContext() {
                    return BaseActivity.this.getContext();
                }

                @Override
                public void loadWorflowSteps(List<String> listInfo) {
                    BaseActivity.this.loadWorflowSteps(listInfo);
                }

                @Override
                public void loadWorkflow(List<WorkFlowStep> list) {
                    BaseActivity.this.loadWorkflow(list);
                }

                @Override
                public void loadDocumentoAsignado(List<DocumentoAsignado> list) {
                    BaseActivity.this.loadDocumentoAsignado(list);
                }

                @Override
                public void capturarImagen(int id) {
                    BaseActivity.this.capturarImagen(id);
                }

                @Override
                public void formularaioGuardado() {
                    BaseActivity.this.formularaioGuardado();
                }

                @Override
                public void iniciarEdicion(DocumentoAsignado docAsignado) {
                    if (!permisosCompletos()) {
                        checkPermissions();
                    }
                    else {
                        Intent intent = new Intent(BaseActivity.this, FormularioActivity.class);
                        intent.putExtra("documento"         , docAsignado.IdDocumento);
                        intent.putExtra("version"           , docAsignado.Version);
                        intent.putExtra("idWorkflowStep"    , docAsignado.IdWorkflowStep);
                        intent.putExtra("IdDocumento"       , docAsignado.IdDocumento);
                        intent.putExtra("Fecha"             , docAsignado.Fecha);
                        intent.putExtra("Folio"             , docAsignado.Folio);

                        if (docAsignado.getDocumentInstance() != null) {
                            intent.putExtra("lat", docAsignado.getDocumentInstance().Latitud);
                            intent.putExtra("lng", docAsignado.getDocumentInstance().Longitud);
                        }
                        else {
                            intent.putExtra("lat", 0.0);
                            intent.putExtra("lng", 0.0);
                        }
                        startActivityForResult(intent, docAsignado.IdDocumento);
                    }
                }

                @Override
                public void loadFormulario(int parent, int idForm, String baseInfo, int row, String namedPath) {
                    if (!permisosCompletos()) {
                        checkPermissions();
                    }
                    else {
                        double lat = getData("lat") != null ? (double)getData("lat") : 0.0;
                        double lng = getData("lng") != null ? (double)getData("lng") : 0.0;

                        Intent intent = new Intent(BaseActivity.this, FormularioActivity.class);
                        intent.putExtra("IdParent"      , parent);
                        intent.putExtra("IdForm"        , idForm);
                        intent.putExtra("Row"           , row);
                        intent.putExtra("BaseInfo"      , baseInfo);
                        intent.putExtra("NamedPath"     , namedPath);
                        intent.putExtra("lat"           , lat);
                        intent.putExtra("lng"           , lng);
                        startActivityForResult(intent, parent);
                    }
                }

                @Override
                public void formularioCompletado(String formulario) {
                    BaseActivity.this.formularioCompletado(formulario);
                }

                @Override
                public void setDataField(DataField dataField) {
                    BaseActivity.this.dataField = dataField;
                }

                @Override
                public void addUnbindTool(Componente.Tool tool) {
                    BaseActivity.this.UnbindTool.add(tool);
                }

                @Override
                public DataField getDataField() {
                    return BaseActivity.this.dataField;
                }

                @Override
                public Componente.Tool getComponente(int idField) {
                    BaseActivity.this.selectTool(null, idField);
                    return BaseActivity.this.ToolSelected;
                }

                @Override
                public void selectOption(WorkFlowStep workFlowStep, HashMap<String, Field> info) {
                    BaseActivity.this.selectOption(workFlowStep, info);
                }

                @Override
                public Object getConfiguracion(Class<?> type, String key) {
                    if (sharedPreferences.contains(key)) {
                        return sharedPreferences.getAll().get(key);
                    }
                    return null;
                }

                @Override
                public void previewImage(String image) {

                    Intent galleryIntent = new Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.setDataAndType(Uri.fromFile(new File(image)), "image/*");
                    galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(galleryIntent);

                }

                @Override
                public Object getData(String key) {
                    return BaseActivity.this.getData(key);
                }

                @Override
                public void printReporte() {
                    BaseActivity.this.printReporte();
                }

                @Override
                public void loadStartOptions() {
                    BaseActivity.this.loadStartOptions();
                }

                @Override
                public void loadWorkflowOptions() {
                    BaseActivity.this.loadWorkflowOptions();
                }

                @Override
                public void loadEndOptoons() {
                    BaseActivity.this.loadEndOptoons();
                }

            });

            String userName = (String)getData(PreferenceDevice.USER_NAME.Key, "");
            String passwd   = (String)getData(PreferenceDevice.USER_PASSWD.Key, "");
            String imei     = (String)getData("IMEI", "");
            ((CDocumento)controlador).setContext(BaseActivity.this);
            ((CDocumento)controlador).setUserName(userName);
            ((CDocumento)controlador).setPasswd(passwd);
            ((CDocumento)controlador).setDeviceID(imei);

            if (activity.getName().equals(FormularioActivity.class.getName())) {
                if (BaseActivity.this.getData("IdDocumento") != null) {
                    int idDocumento = (int) BaseActivity.this.getData("IdDocumento");
                    long fecha = (long) BaseActivity.this.getData("Fecha");
                    String folio = BaseActivity.this.getData("Folio").toString();
                    ((CDocumento)controlador).loadFormulario(idDocumento, fecha, folio);
                }
                else if (BaseActivity.this.getData("IdForm") != null) {
                    int idFormulario = (int) BaseActivity.this.getData("IdForm");
                    String baseInfo = BaseActivity.this.getData("BaseInfo").toString();
                    ((CDocumento)controlador).loadFormulario(idFormulario, baseInfo);
                }
            }
            else if (  activity.getName().equals(DocManagerActivity.class.getName())|| activity.getName().equals(MapActivity.class.getName())) {
                int idDocument      = 9;//Integer.parseInt(BaseActivity.this.getData("documento").toString());
                int version         = 1;//Integer.parseInt(BaseActivity.this.getData("version").toString());
                ((CDocumento)controlador).loadWorkFlowWithInstances(idDocument, version);
            }
        }
    }


    protected void loadDocumento(List<Documento> listDocumento) {
    }

    protected View addView(View parent, String title) {
        return null;
    }

    protected View addView(View parent, View view) {
        return null;
    }

    protected void loadDocumento() {
        ((CDocumento)controlador).loadDocumento();
    }

    protected Object getData(String name) {
        if (name.equals("IMEI")) {
            TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            return mngr.getDeviceId();
        }
        else if (getIntent().hasExtra(name)) {
            return getIntent().getExtras().get(name);
        }
        else if (sharedPreferences.contains(name)) {
            return sharedPreferences.getAll().get(name);
        }
        return null;
    }

    public Object getData(String key, Object defaul) {
        Object object = getData(key);
        return object == null ? defaul : object;
    }

    protected void cargarUsuario(Usuario usuario) {
    }

    protected View addTabContainer(String title, final FormularioActivity.Listener listener) {
        return null;
    }

    protected Activity getAcivity() {
        return BaseActivity.this;
    }

    protected Bundle getBundle() {
        return bundle;
    }

    protected void loadWorflowSteps() {
        if (controlador instanceof CDocumento) {
            int idDocumento     = Integer.parseInt(BaseActivity.this.getData("documento").toString());
            int version         = Integer.parseInt(BaseActivity.this.getData("version").toString());
            ((CDocumento)controlador).loadWorflowSteps(idDocumento, version);
        }
    }

    protected void loadWorflowSteps(List<String> listInfo) {
    }

    public void loadWorkflow(int idDocumento, int version) {
        ((CDocumento)controlador).loadWorkflow(idDocumento, version);
    }

    public void loadWorkFlowWithInstances(int idDocument, int version){
        ((CDocumento)controlador).loadWorkFlowWithInstances(idDocument, version);
    }

    protected void loadDocumentoAsignado(int idDocumento, int idWorkFlowStep) {
        ((CDocumento)controlador).loadDocumentoAsignado(idDocumento, idWorkFlowStep);
    }

    protected void loadDocumentoAsignado(List<DocumentoAsignado> listDocAsignados) {
    }

    protected void setValue(ViewGroup viewGroup, int id, Object value) {
    }

    public void updateInfo(boolean nextStep) {
        try {
            HashMap<String, Field> info = new HashMap<>();
            if (BaseActivity.this.getData("IdDocumento") != null) {
                ((CDocumento) controlador).nextWorkFlowStep(loadField(info, null), nextStep);
            }
            else if (BaseActivity.this.getData("IdForm") != null) {
                ((CDocumento) controlador).loadInfoChildFormulario(loadField(info, null));
            }
        }
        catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg.startsWith("Informaci\u00f3n incompleta")) {
                notificarUsuario("Error", msg);
            }
        }
    }

    protected HashMap<String, Field> loadField(HashMap<String, Field> info, ViewGroup view) throws Exception {
        return null;
    }


    protected void loadEmpresas(List<BaseInfo> info) {
        if (controlador instanceof CConfiguracion) {
            ((CConfiguracion)controlador).cerrarSesion();
        }
    }

    protected void saveConfiguracion() {
        BaseInfo empresa = (BaseInfo) getData("Empresa");
        Object sucursal = getData("Sucursal");
        ((CConfiguracion)controlador).updateEmpSuc(empresa, sucursal);
    }

    protected void capturarImagen(int id) {
        this.requestImageCapture = id;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, UUID.randomUUID().toString());
        uriCapturedImage = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCapturedImage);
        startActivityForResult(intent, requestImageCapture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestImageCapture != -1 && requestImageCapture == requestCode) {
                String[] projection = { MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uriCapturedImage, projection, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        String capturedImageFilePath = cursor.getString(column_index_data);
                        Log.e(getClass().getSimpleName(), capturedImageFilePath);
                        try {
                            String name = UUID.randomUUID().toString();
                            String fileName = Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(name).concat(".jpg");
                            String directory = Environment.getExternalStorageDirectory().toString().concat("/ipati/");
                            File file = new File(directory);
                            if (!file.exists()) {
                                boolean result = file.mkdir();
                                if (!result) {
                                    throw new Exception("No fue posible crear el directorio " + directory);
                                }
                            }
                            File source = new File(capturedImageFilePath);
                            File destination = new File(fileName);
                            InputStream in = new FileInputStream(source);
                            OutputStream out = new FileOutputStream(destination);
                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            in.close();
                            out.close();
                            SimpleDateFormat fmt_Exif = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.ENGLISH);
                            ExifInterface exifInterface = new ExifInterface(fileName);
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE       , GPS.convert(LocalizacionServices.LOCATION.getLatitude()));
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF   , GPS.latitudeRef(LocalizacionServices.LOCATION.getLatitude()));
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE      , GPS.convert(LocalizacionServices.LOCATION.getLongitude()));
                            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF  , GPS.longitudeRef(LocalizacionServices.LOCATION.getLongitude()));
                            exifInterface.setAttribute(ExifInterface.TAG_DATETIME           , fmt_Exif.format(new Date()));
                            exifInterface.saveAttributes();
                            setValue(null, requestCode, name);
                            cursor.close();
                        }
                        catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                    }
                    cursor.close();
                }
            }
            else if (requestCode == CONFIGURACION_ACTIVITY) {
                if (this.activity.getName().equals(PresentacionActivity.class.getName())) {
                    BaseActivity.this.iniciarDispositivo();
                }
                else {
                    BaseActivity.this.loadDocumento();
                }
            }
            else if (data.getExtras().containsKey("Formulario")) {
                Log.e(getClass().getSimpleName(), data.getExtras().getString("Formulario"));
                ((CDocumento) controlador).loadInfoCollection(data.getExtras().getInt("IdParent"), data.getExtras().getString("Formulario"), data.getExtras().getInt("Row"), data.getExtras().getString("NamedPath"));
            }
        }
    }

    protected void notificarUsuario(String titulo, String mensaje) {
        DialogResponse.createDialogResponse(BaseActivity.this, titulo, mensaje, "Aceptar", null).show();
    }

    protected void formularaioGuardado() {
    }

    protected void formularioCompletado(String formulario) {
    }

    protected void iniciarDispositivo() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplication(), HomeApplication.class);
                startActivity(intent);
                finish();
            }
        }, 0);
    }

    protected void iniciarEdicion(DocumentoAsignado docAsignado) {
        if (controlador instanceof CDocumento) {
            ((CDocumento) controlador).iniciarEdicion(docAsignado);
        }
    }

    protected void selectTool(ViewGroup view, int idField) {
        for (Componente.Tool tool : BaseActivity.this.UnbindTool) {
            if (tool.getField().Identity == idField) {
                BaseActivity.this.ToolSelected = tool;
            }
        }
    }

    protected void selectOption(WorkFlowStep workFlowStep, final HashMap<String, Field> info) {
        final ListAdapter adapter = new ListAdapter(getContext(), R.layout.list_item_view, workFlowStep.getOptions()) {
            @Override
            public void onEntry(Object entry, View view, int position) {
                WorkFlowStepOption option = (WorkFlowStepOption) entry;
                ((TextView)view.findViewById(R.id.lblNombre)).setText(option.Text);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Flujo de trabajo")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WorkFlowStepOption option = (WorkFlowStepOption) adapter.getItem(i);
                        ((CDocumento)controlador).updateInfo(option.IdWorkFlowOption, info, true);
                    }
                });
        builder.create();
        builder.show();
    }

    public void loadWorkflow(List<WorkFlowStep> list) {
    }

    public double getZoom() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return Formula.getZoom(2000, width);
    }

    public void printReporte() {
    }

    public void loadStartOptions() {
    }

    public void loadWorkflowOptions() {
    }

    public void loadEndOptoons() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(intService);
        bindService(intService, this, Context.BIND_AUTO_CREATE);
        sendMessageToService(LocalizacionServices.LOAD_LOCATION);
    }

    public void mostrarProgress(String mensaje) {
        progressDialog = ProgressDialog.cargarProgressDialog(BaseActivity.this, null, mensaje).mostrar();
    }

    public void ocultarProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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
                    Bundle bundleLoadLocation = msg.getData();
                    if (controlador instanceof CDocumento) {
                        ((CDocumento)controlador).loadLocation(bundleLoadLocation.getDouble("latitude"),
                                bundleLoadLocation.getDouble("longitude"), bundleLoadLocation.getDouble("accuracy"),bundleLoadLocation.getDouble("altitude"));
                    }
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
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (!permisosCompletos()) {
                    notificarUsuario("Permisos", "Para el correcto funcionamiento de la aplicaci\u00f3n es necesario conceder los permisos requeridos.");
                }
                else {
                    notificarUsuario("Permisos", "Los permisos han sido concedidos, a continuaci\u00f3nn puede continuar utilizando la aplicaci\u00f3n");
                }
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

}
