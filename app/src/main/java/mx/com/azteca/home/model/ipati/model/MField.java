package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOField;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;
import mx.com.azteca.home.model.ipati.dao.DAOTipoDato;
import mx.com.azteca.home.model.ipati.pojo.Field;
import mx.com.azteca.home.model.ipati.pojo.TipoDato;
import mx.com.azteca.home.util.Functions;

public class MField extends BaseModel {

    private DAOField daoField;
    private DAOTipoDato daoTipoDato;
    private DAOIpati daoIpati;

    public MField(Context context) {
        super(context);
    }

    public MField(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        this.daoField       = new DAOField(getContext());
        this.daoTipoDato    = new DAOTipoDato(getContext());
        this.daoIpati       = new DAOIpati();
    }

    public Field getDetail(int idField) {
        return daoField.getDetail(idField);
    }

    public Field getFieldByPadre(int idPadre) {
        return daoField.getFieldByPadre(idPadre);
    }

    public void update(Field field) {
        daoField.update(field);
    }

    public List<Field> downloadComplexType(int idDocumento, int version) throws Exception {
        List<Field> listField = new ArrayList<>();
        String docCampos = daoIpati.loadFieldsComplexType(getSession().sessionID, idDocumento, version);
        JsonParser parser = new JsonParser();
        JsonArray jCampos = parser.parse(docCampos).getAsJsonArray();
        for (JsonElement jElementCampos : jCampos) {
            Field field = saveDocumentoCampos(jElementCampos.getAsJsonObject());
            if (field != null) {
                listField.add(field);
            }
        }
        return listField;
    }

    private Field saveDocumentoCampos(JsonObject jField) {
        if (jField.get("complexType").getAsBoolean()) {

            JsonObject jInfoResult = new JsonObject();
            createUpperCamelCase(jInfoResult, jField);

            Field field = new Field();
            field.InfoJson              = jInfoResult.toString();
            field.IdDocumento           = jField.get("idDocumento").getAsInt();
            field.Version               = jField.get("version").getAsInt();
            field.Etiqueta              = jField.get("etiqueta").getAsString();
            field.XmlTag                = jField.get("xmlTag").getAsString();
            field.NombreLargo           = jField.get("nombreLargo").getAsString();
            field.IdTipoDato            = jField.get("idTipoDato").getAsInt();
            field.Longitud              = jField.get("longitud").getAsInt();
            field.IdDataset             = jField.get("idDataset").getAsInt();
            field.DisplayMember         = jField.get("displayMember").getAsString();
            field.ValueMember           = jField.get("valueMember").getAsString();
            field.ComplexType           = jField.get("complexType").getAsBoolean();
            field.XmlPath               = jField.get("xmlPath").getAsString();
            field.ClassPath             = jField.get("classPath").getAsString();
            field.MemberPath            = jField.get("memberPath").getAsString();
            field.Coleccion             = jField.get("coleccion").getAsBoolean();
            field.OcurrenciaMin         = jField.get("ocurrenciaMinima").getAsInt();
            field.OcurrenciaMax         = jField.get("ocurrenciaMaxima").getAsInt();
            field.RegularExpression     = jField.get("regularExpression").getAsString();
            field.CampoCorrelacion      = jField.get("campoCorrelacion").getAsInt();
            field.ExpresionCorrelacion  = jField.get("expresionCorrelacion").getAsString();
            field.ExpresionValidacion   = jField.get("expresionValidacion").getAsString();
            field.Referencia            = jField.get("referencia").getAsString();
            field.Nombre                = jField.get("nombre").getAsString();
            field.IdPadre               = jField.get("idPadre").getAsInt();
            field.Orden                 = jField.get("orden").getAsInt();
            field.Nivel                 = jField.get("nivel").getAsInt();
            field.Path                  = jField.get("path").getAsString();
            field.NamedPath             = jField.get("namedPath").getAsString();
            field.Identity              = jField.get("identity").getAsInt();
            field.Code                  = jField.get("code").getAsString();
            field.TipoDato              = jField.get("tipoDato").getAsJsonObject().get("type").getAsString();
            field.Tag                   = jField.get("tipoDato").getAsJsonObject().get("tag").getAsString();

            JsonObject jTipoDato = jField.get("tipoDato").getAsJsonObject();
            TipoDato tipoDato = new TipoDato();
            tipoDato.Identity       = jTipoDato.get("identity").getAsInt();
            tipoDato.Code           = jTipoDato.get("code").getAsString();
            tipoDato.Nombre         = jTipoDato.get("nombre").getAsString();
            tipoDato.Type           = jTipoDato.get("type").getAsString();
            tipoDato.Tag            = jTipoDato.get("tag").getAsString();
            tipoDato.Icon           = jTipoDato.get("icon").getAsString();
            tipoDato.Active         = jTipoDato.get("active").getAsBoolean();
            tipoDato.IdBranch       = jTipoDato.get("idBranch").getAsInt();
            tipoDato.IdCompany      = jTipoDato.get("idCompany").getAsInt();
            tipoDato.CreateDate     = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jTipoDato.get("createDate").getAsString());
            tipoDato.LastUpdate     = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jTipoDato.get("lastUpdate").getAsString());
            tipoDato.PCUpdate       = jTipoDato.get("pcUpdate").getAsString();
            tipoDato.UserCreate     = jTipoDato.get("userCreate").getAsInt();
            tipoDato.UserUpdate     = jTipoDato.get("userUpdate").getAsInt();
            tipoDato.ActivateDate   = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jTipoDato.get("activateDate").getAsString());
            tipoDato.DeactivateDate = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jTipoDato.get("deactivateDate").getAsString());

            if (jTipoDato.get("sessionID") != null) {
                tipoDato.SessionID      = jTipoDato.get("sessionID").getAsString();
            }

            if (!daoTipoDato.exist(tipoDato.Identity)) {
                daoTipoDato.guardar(tipoDato);
            }

            field.LastUpdate            = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jField.get("lastUpdate").getAsString());
            field.CreateDate            = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jField.get("createDate").getAsString());
            field.PCUpdate              = jField.get("pcUpdate").getAsString();
            field.ActivateDate          = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jField.get("activateDate").getAsString());
            field.DeactivateDate        = Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).getTime(jField.get("deactivateDate").getAsString());
            field.UserCreate            = jField.get("userCreate").getAsInt();
            field.UserUpdate            = jField.get("userUpdate").getAsInt();
            field.Active                = jField.get("active").getAsBoolean();
            // TODO: El sessionID desaparece en el nuevo framework
            if (jField.get("sessionID") != null) {
                field.SessionID = jField.get("sessionID").getAsString();
            }

            if (!daoField.exist(field.Identity))
                daoField.guardar(field);

            return field;
        }
        return null;
    }

    private void createUpperCamelCase(JsonObject jsonObject, JsonObject jInfo) {
        for (Map.Entry<String, JsonElement> entry : jInfo.entrySet()) {
            String key = entry.getKey().equals("pcUpdate")? "PCUpdate" : Character.toUpperCase(entry.getKey().charAt(0)) + entry.getKey().substring(1, entry.getKey().length());
            if (entry.getValue().isJsonPrimitive()) {
                jsonObject.add(key, entry.getValue());
            }
            else if (entry.getValue().isJsonObject()) {
                JsonObject jValue = new JsonObject();
                jsonObject.add(key, jValue);
                createUpperCamelCase(jValue, entry.getValue().getAsJsonObject());
            }
            else if (entry.getValue().isJsonArray()) {
                JsonArray jValueArray = new JsonArray();
                jsonObject.add(key, jValueArray);
                for (JsonElement element : entry.getValue().getAsJsonArray()) {
                    JsonObject jValue = new JsonObject();
                    jValueArray.add(jValue);
                    createUpperCamelCase(jValue, element.getAsJsonObject());
                }
            }
        }
    }

}
