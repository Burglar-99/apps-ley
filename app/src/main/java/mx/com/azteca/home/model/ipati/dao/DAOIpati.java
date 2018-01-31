package mx.com.azteca.home.model.ipati.dao;


import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import mx.com.azteca.home.util.IpatiServer;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DAOIpati {

    public String iniciarSesion(String usuario, String contrasena, String workStation, int timeZone) throws Exception{
        JsonObject parameters = new JsonObject();
        parameters.addProperty("userName", usuario);
        parameters.addProperty("password", contrasena);
        parameters.addProperty("workStation", workStation);
        parameters.addProperty("timezoneOffset", timeZone);
        return executeRequest(IpatiServer.LOGIN.getUrl(), parameters, true);
    }

    public String getDataSet(String sessionID, int idDataset, boolean checkStatus) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("itemID", idDataset);
        parameters.addProperty("checkStatus", checkStatus);
        return executeRequest(IpatiServer.GET_DETAIL_DATASET_BY_ID.getUrl(), parameters, true);
    }

    public String getDetailFormulario(String sessionID, int idFormulario, boolean checkStatus) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("itemID", idFormulario);
        parameters.addProperty("checkStatus", checkStatus);
        return executeRequest(IpatiServer.GET_DETAIL_FORMULARIO.getUrl(), parameters, true);
    }


    public String loadFormulario(String sessionID, int idFormulario) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idFormulario", idFormulario);
        return executeRequest(IpatiServer.FORM_LOAD_JOSON_UI.getUrl(), parameters, true);
    }

    public String loadDataSet(String sessionID, int idDataSet, boolean json) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idDataset", idDataSet);
        return executeRequest(IpatiServer.GET_OFFLINE_DATASET.getUrl(), parameters, true);
    }

    public String loadDocumentoByUser(String sessionID, int idBranch, int idUsuario) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idSucursal", idBranch);
        parameters.addProperty("idUsuario", idUsuario);
        return executeRequest(IpatiServer.DOCUMENTS_USER.getUrl(), parameters, true);
    }

    public String loadDocumentoDetail(String sessionID, int idDocumento) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("itemID", idDocumento);
        return executeRequest(IpatiServer.GET_DETAIL_DOCUMENTO.getUrl(), parameters, true);
    }

    public String loadListAsignadosOffline(String sessionID, Date fechaIni, Date fechaFin, int idDocumento, int idUsuarioAsignado) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("fechaInicial", fechaIni.getTime());
        parameters.addProperty("fechaFinal", fechaFin.getTime());
        parameters.addProperty("idDocumento", idDocumento);
        parameters.addProperty("idUsuarioAsignado", idUsuarioAsignado);
        return executeRequest(IpatiServer.LOAD_ASIGNADOS_OFFLINE.getUrl(), parameters, true);
    }


    public String loadWorkflow(String sessionID, int idSucursal, int idDocumento, int version) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idSucursal", idSucursal);
        parameters.addProperty("idDocumento", idDocumento);
        parameters.addProperty("version", version);
        return executeRequest(IpatiServer.LOAD_WORKFLOW.getUrl(), parameters, true);
    }

    public String loadInstance(String sessionID, String folio) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("folio", folio);
        return executeRequest(IpatiServer.LOAD_INSTANCE.getUrl(), parameters, true);
    }

    public String loadEmpresas(String sessionID, int idUsuario) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idUsuario", idUsuario);
        return executeRequest(IpatiServer.LOAD_EMPRESAS.getUrl(), parameters, true);
    }

    public String loadSucursales(String sessionID, int idEmpresa, int idUsuario) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idEmpresa", idEmpresa);
        parameters.addProperty("idUsuario", idUsuario);
        return executeRequest(IpatiServer.LOAD_SUCURSALES.getUrl(), parameters, true);
    }

    public String loadActiveVersion(String sessionID, int idDocumento) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idDocumento", idDocumento);
        return executeRequest(IpatiServer.LOAD_ACTIVE_VERSION.getUrl(), parameters, true);
    }

    public String sincronizar(String sessionID, String instance, int idWorkflowStep) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("instance", instance);
        parameters.addProperty("idWorkflowStep", idWorkflowStep);
        return executeRequest(IpatiServer.SAVE_INSTANCE.getUrl(), parameters, false);
    }

    public String loadFieldsComplexType(String sessionID, int idDocumento, int version) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idDocumento", idDocumento);
        parameters.addProperty("version", version);
        return executeRequest(IpatiServer.LOAD_FIELDS_COMPLEX_TYPE.getUrl(), parameters, true);
    }


    public String loadComparableExpress(String sessionID, String inmueble, int radio, String tipo, String lat, String lng) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("inmueble", inmueble);
        parameters.addProperty("radio", radio);
        parameters.addProperty("tipo", tipo);
        parameters.addProperty("lat", lat);
        parameters.addProperty("lng", lng);
        return executeRequest(IpatiServer.LOAD_COMPARABLE_EXPRESS.getUrl(), parameters, true);
    }

    public String loadComparableSeleccion(String sessionID, String inmueble, int radio, String tipo, String lat, String lng) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("inmueble", inmueble);
        parameters.addProperty("radio", radio);
        parameters.addProperty("tipo", tipo);
        parameters.addProperty("lat", lat);
        parameters.addProperty("lng", lng);
        return executeRequest(IpatiServer.LOAD_COMPARABLE_SELECCION.getUrl(), parameters, true);
    }

    public String loadComparableSeleccion(String sessionID, String idRegistro) throws Exception {
        JsonObject parameters = new JsonObject();
        parameters.addProperty("sessionID", sessionID);
        parameters.addProperty("idRegistro", idRegistro);
        return executeRequest(IpatiServer.LOAD_COMPARABLE_IMAGENES_SELECCION.getUrl(), parameters, true);
    }

    private String executeRequest(String url, JsonObject parametes, boolean get) throws Exception {
        String data;
        try {
            Request request;
            if (get) {
                request = getRequestGet(url, parametes);
            }
            else {
                request = getRequestPost(url, parametes);
            }
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(360, TimeUnit.SECONDS)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                data = response.body().string();
                Log.e(getClass().getName(), url + data);
            }
            else {
                throw new Exception(response.networkResponse().toString());
            }

        }
        catch (SocketTimeoutException sex) {
            JsonObject jResult = new JsonObject();
            jResult.addProperty("Error", "Tiempo de solicitud agotado.");
            jResult.addProperty("Class", getClass().getName());
            jResult.addProperty("Exception", sex.toString());
            Log.e(getClass().getName(), sex.toString());
            throw new Exception(jResult.toString());
        }
        catch (Exception ex){
            JsonObject jResult = new JsonObject();
            jResult.addProperty("Error", "Error al establecer una comunicaci\u00f3n con el servidor.");
            jResult.addProperty("Class", getClass().getName());
            jResult.addProperty("Exception", ex.toString());
            Log.e(getClass().getName(), ex.toString());
            throw new Exception(jResult.toString());
        }
        return data;
    }

    private Request getRequestGet(String url, JsonObject parametes) throws Exception {
        String sessionID = "";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, JsonElement> entry : parametes.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().getAsString());

            if (entry.getKey().equals("sessionID")) {
                sessionID = entry.getValue().getAsString();
            }
        }
        String urlData = urlBuilder.build().toString();
        return new Request  .Builder()
                            .url(urlData)
                            .addHeader("Azteca-sessionID", sessionID)
                            .get()
                            .build();
    }

    private Request getRequestPost(String url, JsonObject parametes) throws Exception {
        String sessionID = "";
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, JsonElement> entry : parametes.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue().getAsString());
            if (entry.getKey().equals("sessionID")) {
                sessionID = entry.getValue().getAsString();
            }
        }
        RequestBody requestBody = builder.build();
        return new Request  .Builder()
                            .url(url)
                            .addHeader("Azteca-sessionID", sessionID)
                            .method("POST", RequestBody.create(null, new byte[0]))
                            .post(requestBody)
                            .build();
    }



}
