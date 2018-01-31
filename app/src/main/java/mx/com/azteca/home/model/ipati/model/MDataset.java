package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAODataset;
import mx.com.azteca.home.model.ipati.pojo.Dataset;
import mx.com.azteca.home.service.DataLoaderService;

public class MDataset extends BaseModel {

    private DAODataset daoDataset;

    public MDataset(Context context) {
        super(context);
    }

    public MDataset(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.daoDataset = new DAODataset(getContext());
    }

    public Dataset getDetail(int idDataset) {
        return daoDataset.getDetail(idDataset);
    }

    public Dataset getDetailInfo(int idDataset) {
        Dataset dataset = daoDataset.getDetail(idDataset);
        for (String column : daoDataset.getDatasetInfoColumns(dataset.getNameTableInfo())) {
            dataset.getNameColumns().add(column);
        }
        JsonParser parser = new JsonParser();
        for (JsonElement element :  parser.parse(dataset.Columnas).getAsJsonArray()) {
            dataset.getQueryColumnas().add(element.getAsString());
        }
        JsonArray jParametros = parser.parse(dataset.Parametros).getAsJsonArray();
        if (jParametros.size() == 0) {
            dataset.Dataset = daoDataset.getDatasetInfo(dataset.getNameTableInfo());
        }
        else {
            for (JsonElement jsonElement : jParametros) {
                String parametro = jsonElement.getAsJsonObject().get("nombre").getAsString();
                for (String column : dataset.getNameColumns()) {
                    if (parametro.toUpperCase().equals(column.toUpperCase())) {
                        dataset.getNameParametro().add(jsonElement.getAsJsonObject().get("nombre").getAsString());
                        break;
                    }
                }
            }
        }
        return dataset;
    }

    public String getDatasetInfo(String table, String[] columns, String[] parameters) throws Exception {
        try {
            if (!DataLoaderService.DATA_LOADED && (table.equals("Estados") || table.equals("Municipios") || table.equals("Poblaciones") || table.equals("Colonias"))) {
                int idDataset = 0;
                switch (table) {
                    case "Estados":
                        idDataset = 19;
                        break;
                    case "Municipios":
                        idDataset = 21;
                        break;
                    case "Poblaciones":
                        idDataset = 22;
                        break;
                    case "Colonias":
                        idDataset = 23;
                        break;
                }
                HashMap<String, String> mapParamentro = new HashMap<>();
                for (int index = 0; index < columns.length; index++) {
                    if (parameters[index].equals("Null")) {
                        return "[]";
                    }
                    mapParamentro.put(columns[index], parameters[index]);
                }
                mx.com.azteca.home.model.MDataset mDataset = new mx.com.azteca.home.model.MDataset(getContext(), getUserName(), getPasswd(), getWorkStation());
                JsonArray jsonArray = mDataset.getDataset(idDataset, mapParamentro);
                return jsonArray.toString();
            }
            else {
                return daoDataset.getDatasetInfo(table, columns, parameters);
            }
        }
        catch (Exception ex) {
            throw handledError(ex.getMessage());
        }
    }

}
