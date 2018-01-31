package mx.com.azteca.home.model.ipati.model;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAODocumentInstance;
import mx.com.azteca.home.model.ipati.dao.DAODocumentoAsignado;
import mx.com.azteca.home.model.ipati.dao.DAODocumentoWorkflow;
import mx.com.azteca.home.model.ipati.dao.DAOEventLog;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;
import mx.com.azteca.home.model.ipati.dao.DAOWorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.DataNamePath;
import mx.com.azteca.home.model.ipati.pojo.DocumentInstance;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.DocumentoWorkflow;
import mx.com.azteca.home.model.ipati.pojo.Field;
import mx.com.azteca.home.model.ipati.pojo.Formulario;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.util.Functions;
import mx.com.azteca.home.util.IpatiServer;
import mx.com.azteca.home.util.XmlHandler;

public class MDocumentInstance extends BaseModel {

    private DAODocumentoAsignado daoDocAsignado;
    private DAODocumentInstance daoDocInstance;
    private DAOWorkFlowStep daoWorkFlowStep;
    private DAODocumentoWorkflow daoDocumentoWorkflow;
    private DAOIpati daoIpati;

    public MDocumentInstance(Context context) {
        super(context);
    }

    public MDocumentInstance(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        this.daoDocAsignado         = new DAODocumentoAsignado(getContext());
        this.daoDocInstance         = new DAODocumentInstance(getContext());
        this.daoWorkFlowStep        = new DAOWorkFlowStep(getContext());
        this.daoDocumentoWorkflow   = new DAODocumentoWorkflow(getContext());
        this.daoIpati               = new DAOIpati();
    }

    public DocumentInstance getDocumentInstance(int idDocumento, int version, String folio) {
        return daoDocInstance.getDetail(idDocumento, version, folio);
    }

    public void update(DocumentInstance documentInstance) {
        daoDocInstance.update(documentInstance);
    }

    public void updateNodes(String name, String info) throws Exception {
        try {
            File file = new File(getContext().getFilesDir(), name);
            if (file.exists()) {
                boolean result = file.delete();
                if (!result) {
                    throw new Exception("El documento " + name + " no pudo ser elimiando.");
                }
            }
            FileOutputStream outputStream = getContext().openFileOutput(name, Context.MODE_PRIVATE);
            outputStream.write(info.getBytes());
            outputStream.close();
        }
        catch (Exception ex) {
            throw new Exception("Ocurrió un problema al guardar los nodos del documento.", ex);
        }
    }

    public String getNodes(String name) throws Exception {
        try {
            File file = new File(getContext().getFilesDir(), name);
            if (file.exists()) {
                InputStream inputStream = getContext().openFileInput(name);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedreader = new BufferedReader(inputStreamReader);
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while (( line = bufferedreader.readLine()) != null)
                {
                    stringBuilder.append(line);
                    stringBuilder.append('\n');
                }
                return stringBuilder.toString();
            }
        }
        catch (Exception ex) {
            throw new Exception("Ocurrió un problema al guardar los nodos del documento.", ex);
        }
        return null;
    }

    public void downloadInstance(String folio) throws Exception {

        String instance = daoIpati.loadInstance(getSession().sessionID, folio);
        if (instance == null) {
            throw new Exception("Document instance does not found. Document " + folio);
        }

        JsonParser parser = new JsonParser();
        JsonObject jInstance = parser.parse(instance).getAsJsonObject();

        DocumentInstance docInstance = new DocumentInstance();

        docInstance.ID                  = jInstance.get("ID").getAsString();
        docInstance.IdDocumento         = jInstance.get("IdDocumento").getAsInt();
        docInstance.TipoDocumento       = jInstance.get("TipoDocumento").getAsString();
        docInstance.Version             = jInstance.get("Version").getAsInt();
        docInstance.Fecha               = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jInstance.get("Fecha").getAsString());
        docInstance.FechaStr            = jInstance.get("FechaStr").getAsString();
        docInstance.Folio               = jInstance.get("Folio").getAsString();
        if (jInstance.has("Contenido")) {
            docInstance.Contenido = jInstance.get("Contenido").getAsString();
        }
        else {
            docInstance.Contenido = "";
        }

        docInstance.Referencia          = jInstance.get("Referencia").getAsString();
        docInstance.Latitud             = jInstance.get("Latitud").getAsDouble();
        docInstance.Longitud            = jInstance.get("Longitud").getAsDouble();
        docInstance.Altitud             = jInstance.get("Altitud").getAsDouble();
        docInstance.Posicion            = jInstance.get("Posicion").getAsString();
        docInstance.IdUsuarioAsignado   = jInstance.get("IdUsuarioAsignado").getAsInt();
        docInstance.IdWorkflowStep      = jInstance.get("IdWorkflowStep").getAsInt();
        docInstance.IdStatus            = jInstance.get("IdStatus").getAsInt();
        docInstance.Status              = jInstance.get("Status").getAsString();
        docInstance.StartStep           = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jInstance.get("StartStep").getAsString());
        docInstance.EndStep             = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jInstance.get("EndStep").getAsString());
        docInstance.HasStartStep        = jInstance.get("HasStartStep").getAsBoolean();
        docInstance.HasEndStep          = jInstance.get("HasEndStep").getAsBoolean();
        docInstance.IdSucursal          = jInstance.get("IdSucursal").getAsInt();
        docInstance.IdEmpresa           = jInstance.get("IdEmpresa").getAsInt();
        docInstance.LastUpdate          = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jInstance.get("LastUpdate").getAsString());
        docInstance.UserUpdate          = jInstance.get("UserUpdate").getAsInt();
        docInstance.PCUpdate            = jInstance.get("PCUpdate").getAsString();
        docInstance.Nodes               = UUID.randomUUID().toString();

        JsonArray jNodes = jInstance.get("Nodes").getAsJsonArray();
        updateNodes(docInstance.Nodes, jNodes.toString());

        for (JsonElement jNode : jNodes) {
            downloadFile(jNode.getAsJsonObject());
        }

        if (daoDocInstance.exist(docInstance.IdDocumento, docInstance.Fecha, docInstance.Folio) ||
                daoDocInstance.existByFolio(docInstance.IdDocumento, docInstance.Version, docInstance.Folio)) {
            daoDocInstance.delete(docInstance.IdDocumento, docInstance.Version, docInstance.Folio);
        }

        daoDocInstance.guardar(docInstance);
    }

    public void downloadFile(JsonObject jField) {
        try {
            if (jField.has("FieldValue") && !jField.get("FieldValue").isJsonNull() &&
                    jField.get("FieldValue").getAsJsonObject().has("File") && !jField.get("FieldValue").getAsJsonObject().get("File").isJsonNull() &&
                    jField.get("FieldValue").getAsJsonObject().get("File").getAsJsonObject().has("FileUri") &&
                    !jField.get("FieldValue").getAsJsonObject().get("File").getAsJsonObject().get("FileUri").isJsonNull()) {

                if (jField.get("FieldValue").getAsJsonObject().has("Value") &&
                        !jField.get("FieldValue").getAsJsonObject().get("Value").isJsonNull() &&
                        !jField.get("FieldValue").getAsJsonObject().get("File").getAsJsonObject().get("Extension").isJsonNull() &&
                        !jField.get("FieldValue").getAsJsonObject().get("File").getAsJsonObject().get("FileUri").isJsonNull() &&
                        !jField.get("FieldValue").getAsJsonObject().get("File").getAsJsonObject().get("ContentType").isJsonNull()) {
                    String name          = jField.get("FieldValue").getAsJsonObject().get("Value").getAsString();
                    String extesion      = jField.get("FieldValue").getAsJsonObject().get("File").getAsJsonObject().get("Extension").getAsString();
                    String fileUri       = jField.get("FieldValue").getAsJsonObject().get("File").getAsJsonObject().get("FileUri").getAsString();
                    String contentType   = jField.get("FieldValue").getAsJsonObject().get("File").getAsJsonObject().get("ContentType").getAsString();
                    if (contentType.equals("images")) {
                        try {
                            downloadImage(name, extesion, fileUri, 500);
                        }
                        catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                    }
                }
            }
            if (jField.has("Children") && jField.get("Children") != null) {
                for (JsonElement jsonElement : jField.get("Children").getAsJsonArray()) {
                    downloadFile(jsonElement.getAsJsonObject());
                }
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public void downloadImage(String name, String extension, String fileUri, int imageWidth) throws Exception {
        String url = IpatiServer.FILE_SERVER.getImageUrl(fileUri, imageWidth);
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(IpatiServer.FILE_SERVER.getImageUrl(fileUri, imageWidth));
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("HTTP 500. Error al acceder al url " + url);
            }
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                String directory = Environment.getExternalStorageDirectory().toString().concat("/ipati/");
                String fileName = directory + name + "." + extension;

                File fileDir = new File(directory);
                if (!fileDir.exists()) {
                    boolean result = fileDir.mkdir();
                    if (!result) {
                        throw new Exception("Directorio " + directory + " no pudo ser creado");
                    }
                }

                File fileImg = new File(fileName);
                if (fileImg.exists()) {
                    boolean result = fileImg.delete();
                    if (!result) {
                        throw new Exception("Archivo " + fileName + " no pudo ser eliminado");
                    }
                }

                File pictureFile = new File(directory + name + "." + extension);
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            }
        } catch (Exception ex) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            DAOEventLog.HandledException(getContext(), ex, "Error al descargar la informaci\u00f3n");
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public void updateDocumentInstance(DocumentoAsignado docAsignado) throws Exception {
        Formulario formulario = docAsignado.getWorkflow().getFormulario();
        JsonParser parser = new JsonParser();
        JsonArray jFormulario = (JsonArray) parser.parse(formulario.Info);
        XmlHandler handler = new XmlHandler();
        handler.loadAttributesValues(docAsignado.getDataValue());
        JsonArray jFieldInfo = new JsonArray();

        for (JsonElement jElement : jFormulario) {
            procesarNodos(handler, jFieldInfo, jElement.getAsJsonObject());
        }

        DocumentInstance docInstance = daoDocInstance.getDetail(docAsignado.IdDocumento, docAsignado.Version, docAsignado.Folio);
        docInstance.setNodesInfo(getNodes(docInstance.Nodes));
        if (docInstance.getNodesInfo() != null) {
            JsonArray jElementNodes = parser.parse(docInstance.getNodesInfo()).getAsJsonArray();
            for (JsonElement jElement : jFieldInfo ) {
                updateNodes(jElement.getAsJsonObject(), jElementNodes);
            }
            updateNodes(docInstance.Nodes, jElementNodes.toString());
        }
    }


    public void uploadDocumentInstance() throws Exception {
        try {
            List<DocumentoAsignado> listDocFinalizado = daoDocAsignado.loadDocModificado();
            for (DocumentoAsignado docAsignado : listDocFinalizado) {
                DocumentInstance docInstance = daoDocInstance.getDetail(docAsignado.IdDocumento, docAsignado.Version, docAsignado.Folio);
                docInstance.setNodesInfo(getNodes(docInstance.Nodes));
                DocumentoWorkflow docWorkflowStep = daoDocumentoWorkflow.getDetail(docAsignado.IdDocumento, docAsignado.Version);
                MWorkflow mWorkflow = new MWorkflow(getContext());
                MFormulario mFormulario = new MFormulario(getContext());
                MDocumentInstance mInstance = new MDocumentInstance(getContext());
                docAsignado.setWorkflow(mWorkflow.getDetail(docWorkflowStep.IdWorkflowStep, docAsignado.IdDocumento, docAsignado.Version));
                docAsignado.getWorkflow().setFormulario(mFormulario.getDetail(docAsignado.getWorkflow().IdFormulario));
                docAsignado.setDocumentInstance(mInstance.getDocumentInstance(docAsignado.IdDocumento, docAsignado.Version, docAsignado.Folio));
                JsonObject jDocAsignado = new JsonObject();
                jDocAsignado.addProperty("ID"                   , docInstance.ID);
                jDocAsignado.addProperty("IdDocumento"          , docInstance.IdDocumento);
                jDocAsignado.addProperty("TipoDocumento"        , docInstance.TipoDocumento);
                jDocAsignado.addProperty("Version"              , docInstance.Version);
                jDocAsignado.addProperty("Fecha"                , docInstance.Fecha);
                jDocAsignado.addProperty("FechaStr"             , docInstance.FechaStr);
                jDocAsignado.addProperty("Folio"                , docInstance.Folio);
                jDocAsignado.addProperty("Contenido"            , docAsignado.Contenido);
                jDocAsignado.addProperty("Referencia"           , docAsignado.Referencia);
                jDocAsignado.addProperty("Latitud"              , docAsignado.Latitud);
                jDocAsignado.addProperty("Longitud"             , docAsignado.Longitud);
                jDocAsignado.addProperty("Altitud"              , docAsignado.Altitud);
                jDocAsignado.addProperty("Posicion"             , docAsignado.Posicion);
                jDocAsignado.addProperty("IdUsuarioAsignado"    , docInstance.IdUsuarioAsignado);
                jDocAsignado.addProperty("IdWorkflowStep"       , docAsignado.IdWorkflowStep);
                WorkFlowStep step = daoWorkFlowStep.getDetail(docAsignado.IdWorkflowStep, docAsignado.IdDocumento, docAsignado.Version);
                WorkFlowStep nextStep = daoWorkFlowStep.getDetail(step.NextStep, docAsignado.IdDocumento, docAsignado.Version);
                JsonObject jNextStep = null;
                if (nextStep != null) {
                    jNextStep = new JsonObject();
                    jNextStep.addProperty("IdWorkflowStep"  , nextStep.IdWorkflowStep);
                    jNextStep.addProperty("Tag"             , nextStep.Tag);
                    jNextStep.addProperty("ActionText"      , nextStep.ActionText);
                    jNextStep.addProperty("StatusText"      , nextStep.StatusText);
                    jNextStep.addProperty("Optional"        , nextStep.Optional);
                    jNextStep.addProperty("Order"           , nextStep.Orden);
                    jNextStep.addProperty("Icon"            , nextStep.Icon);
                    jNextStep.addProperty("IdFormulario"    , nextStep.IdFormulario);
                    jNextStep.addProperty("Duracion"        , nextStep.Duracion);
                    jNextStep.addProperty("Offline"         , nextStep.Offline);
                    jNextStep.addProperty("IsStart"         , nextStep.IsStart);
                    jNextStep.addProperty("IsEnd"           , nextStep.IsEnd);
                    jNextStep.add("Options", new JsonArray());
                    jNextStep.add("NextStep", null);
                }
                JsonObject jStep = new JsonObject();
                jStep.addProperty("IdWorkflowStep"      , step.IdWorkflowStep);
                jStep.addProperty("Tag"                 , step.Tag);
                jStep.addProperty("ActionText"          , step.ActionText);
                jStep.addProperty("StatusText"          , step.StatusText);
                jStep.addProperty("Optional"            , step.Optional);
                jStep.addProperty("Order"               , step.Orden);
                jStep.addProperty("Icon"                , step.Icon);
                jStep.addProperty("IdFormulario"        , step.IdFormulario);
                jStep.addProperty("Duracion"            , step.Duracion);
                jStep.addProperty("Offline"             , step.Offline);
                jStep.addProperty("IsStart"             , step.IsStart);
                jStep.addProperty("IsEnd"               , step.IsEnd);
                jStep.add("NextStep"                    , jNextStep);
                jStep.add("Options"                     , new JsonArray());
                if (jNextStep == null) {
                    String workflowStep = jStep.toString();
                    jStep.add("NextStep", new JsonParser().parse(workflowStep));
                }
                jDocAsignado.add("CurrentStep", jStep);
                jDocAsignado.addProperty("IdStatus"     , docInstance.IdStatus);
                jDocAsignado.addProperty("Status"       , docInstance.Status);
                jDocAsignado.addProperty("StartStep"    , docInstance.StartStep);
                jDocAsignado.addProperty("EndStep"      , docInstance.EndStep);
                jDocAsignado.addProperty("HasStartStep" , docInstance.HasStartStep);
                jDocAsignado.addProperty("HasEndStep"   , docInstance.HasEndStep);
                jDocAsignado.addProperty("IdEmpresa"    , docInstance.IdEmpresa);
                jDocAsignado.addProperty("IdSucursal"   , docInstance.IdSucursal);
                jDocAsignado.addProperty("LastUpdate"   , docInstance.LastUpdate);
                jDocAsignado.addProperty("UserUpdate"   , docInstance.UserUpdate);
                jDocAsignado.addProperty("PCUpdate", docInstance.PCUpdate);
                JsonParser parser = new JsonParser();
                JsonArray jsonNodes = parser.parse(docInstance.getNodesInfo()).getAsJsonArray();
                jDocAsignado.add("Nodes", jsonNodes);
                String info = jDocAsignado.toString();
                Log.e(getClass().getSimpleName(), "Sincronizar:" + info);
                String result = daoIpati.sincronizar(getSession().sessionID, info, docInstance.IdWorkflowStep);
                if (result.startsWith("Error")) {
                    throw new Exception(result);
                }
                Log.e(getClass().getSimpleName(), result);
            }
        }
        catch (Exception ex) {
            DAOEventLog.HandledException(getContext(), ex, "Ocurri\u00f3 un error al uploadDocumentInstance el dispositivo");
        }
    }

    private void procesarNodos(XmlHandler handler, JsonArray jsonArray, JsonObject jField) {
        JsonObject jFieldInfo = null;
        if (jField.has("Info") && jField.get("Info").getAsJsonObject().has("Controles")) {
            JsonArray jControls = jField.get("Info").getAsJsonObject().get("Controles").getAsJsonArray();
            for (JsonElement jElement : jControls) {
                JsonObject jValue = new JsonObject();
                loadValue(jValue, jElement.getAsJsonObject().get("Field").getAsJsonObject().get("TipoDato").getAsJsonObject().get("Type").getAsString(), handler.getValue("Root\\" + jElement.getAsJsonObject().get("Field").getAsJsonObject().get("NamedPath").getAsString()));
                jValue.addProperty("State", "MODIFIED");
                jValue.addProperty("DisplayValue", "");
                if (jElement.getAsJsonObject().get("Field").getAsJsonObject().get("TipoDato").getAsJsonObject().get("Tag").getAsString().equals("IMAGE")) {
                    String fileName = handler.getValue("Root\\" + jElement.getAsJsonObject().get("Field").getAsJsonObject().get("NamedPath").getAsString());
                    File imgFile = new  File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(fileName).concat(".jpg"));
                    String encodedImage = null;
                    if (imgFile.exists()) {
                        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        encodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                        try {
                            stream.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        jValue.addProperty("State", "UNCHANGED");
                    }
                    JsonObject jFileInfo = new JsonObject();
                    jFileInfo.addProperty("_ID"         , "");
                    jFileInfo.addProperty("Owner"       , "bpm-document");
                    jFileInfo.addProperty("ContentType" , "images");
                    jFileInfo.addProperty("Extension"   , "jpg");
                    jFileInfo.addProperty("FileUri"     , "");
                    jFileInfo.addProperty("Content"     , encodedImage);
                    jValue.add("File", jFileInfo);
                }
                jFieldInfo = new JsonObject();
                jFieldInfo.add("FieldInfo", jElement.getAsJsonObject().get("Field").getAsJsonObject());
                if (jElement.getAsJsonObject().get("Field").getAsJsonObject().has("Coleccion") &&
                        jElement.getAsJsonObject().get("Field").getAsJsonObject().get("Coleccion").getAsBoolean()) {
                    int idChildForm = jElement.getAsJsonObject().get("IdChildForm").getAsInt();
                    if (idChildForm > 0) {
                        int idPadre = -1;
                        try {
                            idPadre = jElement.getAsJsonObject().get("Columns").getAsJsonArray().get(0).getAsJsonObject().get("Field").getAsJsonObject().get("IdPadre").getAsInt();
                        }
                        catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                        String namePath = "Root\\" + jElement.getAsJsonObject().get("Field").getAsJsonObject().get("NamedPath").getAsString();
                        List<DataNamePath> listData = handler.getChilds(namePath);
                        JsonArray jChildren = new JsonArray();
                        jFieldInfo.add("Children", jChildren);
                        for (DataNamePath dataNamePath : listData) {
                            try {
                                XmlHandler xmlHandler = new XmlHandler();
                                String xmlBase = xmlHandler.createPathXml(namePath, false);
                                xmlHandler.loadAttributesValues(xmlBase);
                                DataNamePath nodo = xmlHandler.getNamePath(namePath);
                                nodo.addChild((DataNamePath)dataNamePath.clone());
                                xmlHandler.loadAttributesValues(xmlHandler.createDataNamePathXml());
                                MFormulario mFormulario = new MFormulario(getContext());
                                Formulario formulario = mFormulario.getDetail(idChildForm);
                                JsonParser parser = new JsonParser();
                                JsonArray jFormulario = (JsonArray) parser.parse(formulario.Info);
                                for (JsonElement jElementFormulario : jFormulario) {
                                    if (idPadre > 0) {
                                        MField mField = new MField(getContext());
                                        Field field = mField.getDetail(idPadre);
                                        JsonObject jFieldObject = parser.parse(field.InfoJson).getAsJsonObject();
                                        JsonArray jsonPadre = new JsonArray();
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.add("FieldInfo"  , jFieldObject);
                                        jsonObject.add("Children"   , jsonPadre);
                                        jsonObject.add("FieldValue" , null);
                                        jChildren.add(jsonObject);
                                        procesarNodos(xmlHandler, jsonPadre, jElementFormulario.getAsJsonObject());
                                    }
                                }
                            }
                            catch (Exception ex ) {
                                Log.e(getClass().getSimpleName(), ex.toString());
                            }
                        }
                    }
                    jFieldInfo.add("FieldValue", null);
                }
                else {
                    jFieldInfo.add("FieldValue", jValue);
                    jFieldInfo.add("Children", new JsonArray());
                }
                jsonArray.add(jFieldInfo);
            }
        }

        if (jField.get("Children") != null) {
            if (jFieldInfo != null && !jFieldInfo.has("Children")) {
                JsonArray jChildren = new JsonArray();
                jFieldInfo.add("Children", jChildren);
                for (JsonElement jsonElement : jField.get("Children").getAsJsonArray()) {
                    procesarNodos(handler, jChildren, jsonElement.getAsJsonObject());
                }
            }
            else {
                for (JsonElement jsonElement : jField.get("Children").getAsJsonArray()) {
                    procesarNodos(handler, jsonArray, jsonElement.getAsJsonObject());
                }
            }
        }
        else {
            if (jFieldInfo != null)
                jFieldInfo.add("Children", new JsonArray());
        }
    }

    private void updateNodes(JsonObject jsonObject, JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            if (jsonElement.getAsJsonObject().has("FieldInfo")) {
                if (jsonElement.getAsJsonObject().get("FieldInfo").getAsJsonObject().get("Identity").getAsInt() ==
                        jsonObject.get("FieldInfo").getAsJsonObject().get("Identity").getAsInt()) {

                    JsonObject jsonNode = jsonElement.getAsJsonObject();
                    jsonNode.add("FieldValue", jsonObject.get("FieldValue"));
                    jsonNode.add("Children", jsonObject.get("Children"));
                    return;
                }
            }
            if (jsonElement.getAsJsonObject().has("Children")) {
                updateNodes(jsonObject, jsonElement.getAsJsonObject().get("Children").getAsJsonArray());
            }
        }
        if (jsonElement.isJsonArray()) {
            for (JsonElement jsonArray : jsonElement.getAsJsonArray()) {
                updateNodes(jsonObject, jsonArray);
            }
        }
    }

    private void loadValue(JsonObject jsonObject, String type, String value) {
        switch (type) {
            case "System.Int32":
                jsonObject.addProperty("Value", Integer.parseInt(value.equals("") ? "0" : value));
                jsonObject.addProperty("OldValue", Integer.parseInt(value.equals("") ? "0" : value));
                return;
            case "System.Int64":
                jsonObject.addProperty("Value", Long.parseLong(value.equals("") ? "0" : value));
                jsonObject.addProperty("OldValue", Long.parseLong(value.equals("") ? "0" : value));
                return;
            case "System.Decimal":
                jsonObject.addProperty("Value", Double.parseDouble(value.equals("") ? "0" : value));
                jsonObject.addProperty("OldValue", Double.parseDouble(value.equals("") ? "0" : value));
                return;
            case "System.DateTime":
                try {
                    try {
                        long time = Long.parseLong(value);
                        jsonObject.addProperty("Value", time);
                        jsonObject.addProperty("OldValue", time);
                        return;
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }

                }
                catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
                jsonObject.addProperty("Value", value);
                jsonObject.addProperty("OldValue", value);
                return;
            case "System.Boolean":
                jsonObject.addProperty("Value", Boolean.parseBoolean(value.equals("") ? "false" : value));
                jsonObject.addProperty("OldValue", Boolean.parseBoolean(value.equals("") ? "false" : value));
                return;
            default:
                jsonObject.addProperty("Value", value);
                jsonObject.addProperty("OldValue", value);
        }
    }

}
