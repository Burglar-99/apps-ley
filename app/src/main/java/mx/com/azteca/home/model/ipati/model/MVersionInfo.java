package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;
import mx.com.azteca.home.model.ipati.dao.DAOVersionInfo;
import mx.com.azteca.home.model.ipati.pojo.Column;
import mx.com.azteca.home.model.ipati.pojo.VersionInfo;

public class MVersionInfo extends BaseModel {

    private DAOVersionInfo daoVersionInfo;
    private DAOIpati daoIpati;

    public MVersionInfo(Context context) {
        super(context);
    }

    public MVersionInfo(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    public void initialize() {
        this.daoVersionInfo = new DAOVersionInfo(getContext());
        this.daoIpati = new DAOIpati();
    }

    public VersionInfo getDetail(int idDocumento, int version) {
        VersionInfo versionInfo = daoVersionInfo.getDetail(idDocumento, version);
        if (versionInfo.Columns != null &&  versionInfo.Columns.length() > 0) {
            Type listType = new TypeToken<ArrayList<Column>>(){}.getType();
            List<Column> Columns = new Gson().fromJson(versionInfo.Columns, listType);
            versionInfo.getListColumns().addAll(Columns);
        }
        return versionInfo;
    }

    public List<VersionInfo> downloadVersion(int idDocumento) throws Exception {
        List<VersionInfo> listVersion = new ArrayList<>();
        String activeVersion = daoIpati.loadActiveVersion(getSession().sessionID, idDocumento);
        if (activeVersion != null && !activeVersion.equals("null")) {
            JsonParser parser = new JsonParser();
            JsonObject jActiveVersion = parser.parse(activeVersion).getAsJsonObject();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            VersionInfo version = new VersionInfo();

            version.IdDocumento         = idDocumento;
            version.Version             = jActiveVersion.get("version").getAsInt();
            version.Fecha               = dateFormat.parse(jActiveVersion.get("fecha").getAsString()).getTime();
            version.ClaseBusiness       = jActiveVersion.get("claseBusiness").getAsString();
            version.IdEnsamblado        = jActiveVersion.get("idEnsamblado").getAsInt();
            version.IdFormaBorrador     = jActiveVersion.get("idFormaBorrador").getAsInt();
            version.UserUpdate          = jActiveVersion.get("userUpdate").getAsInt();
            //version.Fields            = jActiveVersion.get("fields").isJsonNull() ? new JsonArray().toString() : jActiveVersion.get("Fields").getAsJsonArray().toString();
            version.Fields              = new JsonArray().toString();
            version.Columns             = jActiveVersion.get("columns").getAsJsonArray().toString();

            daoVersionInfo.delete(idDocumento, version.Version, version.Fecha);
            daoVersionInfo.guardar(version);

            listVersion.add(version);
        }
        return listVersion;
    }

}
