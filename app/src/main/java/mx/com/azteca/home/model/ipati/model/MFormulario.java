package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAODataset;
import mx.com.azteca.home.model.ipati.dao.DAOEventLog;
import mx.com.azteca.home.model.ipati.dao.DAOFormulario;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;
import mx.com.azteca.home.model.ipati.pojo.Dataset;
import mx.com.azteca.home.model.ipati.pojo.Formulario;
import mx.com.azteca.home.util.Functions;

public class MFormulario extends BaseModel {

    private DAOFormulario daoFormulario;
    private DAODataset daoDataset;
    private DAOIpati daoIpati;

    public MFormulario(Context context) {
        super(context);
    }

    public MFormulario(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.daoFormulario  = new DAOFormulario(getContext());
        this.daoDataset     = new DAODataset(getContext());
        this.daoIpati       = new DAOIpati();
    }

    public Formulario getDetail(int idFormulario) {
        return daoFormulario.getDetail(idFormulario);
    }

    public Formulario downloadFormulario(int idForma, int idUsuario) throws Exception {
        return downloadFormulario(idForma, false, idUsuario);
    }

    public Formulario downloadFormulario(int idForma, boolean deleteAndNew, int idUsuario) throws Exception {
        String dataForma = daoIpati.getDetailFormulario(getSession().sessionID, idForma, false);
        JsonParser parser = new JsonParser();
        JsonObject jFormulario = parser.parse(dataForma).getAsJsonObject();
        Formulario formulario = new Formulario();
        formulario.Identity     = jFormulario.get("identity").getAsInt();
        formulario.Code         = jFormulario.get("code").getAsString();
        formulario.Nombre       = jFormulario.get("nombre").getAsString();
        formulario.IdDocumento  = jFormulario.get("idDocumento").getAsInt();
        formulario.Version      = jFormulario.get("version").getAsInt();
        formulario.Titulo       = jFormulario.get("titulo").getAsString();
        String dataFormulario = daoIpati.loadFormulario(getSession().sessionID, formulario.Identity);
        JsonParser parserFormulario = new JsonParser();
        JsonElement jElementFormulario = parserFormulario.parse(dataFormulario);
        formulario.Info = jElementFormulario.toString();
        boolean exist = daoFormulario.exist(formulario.Identity);
        if (deleteAndNew) {
            if (exist)
                daoFormulario.delete(formulario.Identity);
            daoFormulario.guardar(formulario);
        }
        else if (!exist) {
            daoFormulario.guardar(formulario);
        }
        for (JsonElement jInfo : jElementFormulario.getAsJsonArray()) {
            getControlDataSet(jInfo.toString(), idUsuario);
        }
        return formulario;
    }

    private void getControlDataSet(String data, int idUsuario) throws Exception {
        try {
            JsonParser parser = new JsonParser();
            JsonObject jData = parser.parse(data).getAsJsonObject();
            JsonArray jControles = jData.get("Info").getAsJsonObject().get("Controles").getAsJsonArray();
            for (JsonElement jElement : jControles) {
                JsonObject jControl = jElement.getAsJsonObject();
                int idDataSet = jControl.get("Field").getAsJsonObject().get("IdDataset").getAsInt();
                if (idDataSet > 0) {
                    Dataset dataSet = new Dataset();
                    String dataset = daoIpati.getDataSet(getSession().sessionID, idDataSet, false);
                    if (dataset != null) {
                        JsonObject jDataset = parser.parse(dataset).getAsJsonObject();
                        dataSet.Identity    = jDataset.get("identity").getAsInt();
                        dataSet.Code        = jDataset.get("code").getAsString();
                        dataSet.Nombre      = jDataset.get("nombre").getAsString();
                        dataSet.Expresion   = jDataset.get("expresion").getAsString();
                        dataSet.Tipo          = jDataset.get("tipo").getAsString();
                        dataSet.LastUpdate  = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jDataset.get("lastUpdate").getAsString());
                        dataSet.Parametros  = jDataset.get("parametros").toString();
                        dataSet.Columnas    = jDataset.get("columnas").toString();
                    }
                    Dataset datasetDevice = daoDataset.getDetail(idDataSet);
                    // 149: Dataset para las tarjetas de crédito
                    if (datasetDevice == null || datasetDevice.LastUpdate < dataSet.LastUpdate || idDataSet == 149) {
                        // 19: Estados
                        // 21: Municipios
                        // 22: Poblaciones
                        // 23: Colonias
                        if ( idDataSet != 19 && idDataSet != 21 && idDataSet != 22 && idDataSet != 23 ) {
                            if (idDataSet == 149) {
                                // Dataset TarjetasReferencia
                                mx.com.azteca.home.model.MDataset mDataset = new mx.com.azteca.home.model.MDataset(getContext(), getUserName(), getPasswd(), getWorkStation());
                                HashMap<String, String> mapParamentro = new HashMap<>();
                                mapParamentro.put("IdUsuario", String.valueOf(idUsuario));
                                JsonArray jsonArray = mDataset.getDataset(idDataSet, mapParamentro);
                                saveInfoDateset(dataSet.Nombre.replace(" ", "_").replace(".", "_"), jsonArray.toString());
                            }
                            else {
                                String datasetData = daoIpati.loadDataSet(getSession().sessionID, idDataSet, false);
                                saveInfoDateset(dataSet.Nombre.replace(" ", "_").replace(".", "_"), datasetData);
                            }
                        }
                        daoDataset.delete(dataSet.Identity);
                        daoDataset.guardar(dataSet);
                    }
                }
                int idChildForm = jControl.get("IdChildForm").getAsInt();
                if (idChildForm > 0) {
                    downloadFormulario(idChildForm, idUsuario);
                }
            }
            JsonArray jChildren = jData.get("Children").getAsJsonArray();
            for (JsonElement jElement : jChildren) {
                getControlDataSet(jElement.getAsJsonObject().toString(), idUsuario);
            }
        }
        catch (Exception ex) {
            DAOEventLog.HandledException(getContext(), ex, "Error al descargar la informaci\u00f3n");
        }
    }

    private void saveInfoDateset(String datasetName, String data) throws Exception {
        try {
            int start = 0;
            int end = data.length() > 1000 ?  1000 : data.length();
            JsonParser parser = new JsonParser();
            String table = null;
            while (true) {
                if (start < data.length() && start < end) {
                    String info = data.substring(start, end);
                    if (!info.startsWith("{")) {
                        start++;
                    }
                    else if (!info.endsWith("}")) {
                        end--;
                    }
                    else {
                        JsonArray jsonArray = parser.parse("[" + info  +"]").getAsJsonArray();
                        for (JsonElement jsonElement : jsonArray) {
                            if (table == null) {
                                table = "CREATE TABLE IF NOT EXISTS " + datasetName + " ( ";
                                for (Iterator<Map.Entry<String, JsonElement>> iteratoror = jsonElement.getAsJsonObject().entrySet().iterator(); iteratoror.hasNext();) {
                                    Map.Entry<String, JsonElement> entry = iteratoror.next();
                                    table +=   entry.getKey() + " TEXT" + (iteratoror.hasNext() ? "," : "");
                                }
                                table += ");";
                                // Eliminar tabla
                                daoDataset.dropTable(datasetName);
                                // Crear tabla conforme al esquema del json
                                daoDataset.createTable(table);
                            }
                            String insert   = "INSERT INTO " + datasetName + " (";
                            String columns  = "";
                            String values   = "";
                            for (Iterator<Map.Entry<String, JsonElement>> iteratoror = jsonElement.getAsJsonObject().entrySet().iterator(); iteratoror.hasNext();) {
                                Map.Entry<String, JsonElement> entry = iteratoror.next();
                                String coma  = iteratoror.hasNext() ? ","  : "";
                                columns += entry.getKey() + coma;
                                values += "'" + entry.getValue().getAsString().replace("'", "''") + "'" + coma;
                            }
                            insert += columns + ") VALUES (" + values + ")";
                            daoDataset.insertRow(insert);
                            start = end;
                            end = data.length() > end + 1000 ? end + 1000 : data.length();
                        }
                    }
                }
                else {
                    break;
                }
            }
        }
        catch (Exception ex) {
            DAOEventLog.HandledException(getContext(), ex, "Error al procesar la información del dataset");
        }
    }

}
