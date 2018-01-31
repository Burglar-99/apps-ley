package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAODocumento;
import mx.com.azteca.home.model.ipati.dao.DAODocumentoAsignado;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;
import mx.com.azteca.home.model.ipati.dao.DAOWorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.Documento;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;

public class MDocumento extends BaseModel {

    private DAODocumento daoDocumento;
    private DAODocumentoAsignado daoDocAsignado;
    private DAOWorkFlowStep daoWorkFlowStep;
    private DAOIpati daoIpati;

    public MDocumento(Context context) {
        super(context);
    }

    public MDocumento(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        this.daoDocumento       = new DAODocumento(getContext());
        this.daoDocAsignado     = new DAODocumentoAsignado(getContext());
        this.daoWorkFlowStep    = new DAOWorkFlowStep(getContext());
        this.daoIpati           = new DAOIpati();
    }

    public List<Documento> loadDocumento() {
        List<Documento> listDocumento = daoDocumento.loadDocumento();
        for (Documento documento : listDocumento) {
            documento.getDocAsignados().addAll(daoDocAsignado.loadDocAsignadoVersion(documento.Identity, documento.Version));
            documento.setDocsOffline(0);
            for (DocumentoAsignado docAsignado : documento.getDocAsignados()) {
                WorkFlowStep workFlowStep = daoWorkFlowStep.getDetail(docAsignado.IdWorkflowStep, docAsignado.IdDocumento, docAsignado.Version);
                docAsignado.setWorkflow(workFlowStep);
                if (workFlowStep != null) {
                    documento.setDocsOffline(documento.getDocsOffline() + (workFlowStep.Offline ? 1 : 0));
                }
            }
        }
        return listDocumento;
    }

    public List<Documento> loadDocumentoOffline() {
        List<Documento> listDocumento = daoDocumento.loadDocumentoOffline();
        for (Documento documento : listDocumento) {
            documento.getDocAsignados().addAll(daoDocAsignado.loadDocAsignadoVersion(documento.Identity, documento.Version));
            documento.setDocsOffline(0);
            for (Iterator<DocumentoAsignado> iterator = documento.getDocAsignados().iterator(); iterator.hasNext();) {
                DocumentoAsignado docAsignado = iterator.next();
                WorkFlowStep workFlowStep = daoWorkFlowStep.getDetail(docAsignado.IdWorkflowStep, docAsignado.IdDocumento, docAsignado.Version);
                if (workFlowStep != null && workFlowStep.Offline) {
                    docAsignado.setWorkflow(workFlowStep);
                    documento.setDocsOffline(documento.getDocsOffline() + (workFlowStep.Offline ? 1 : 0));
                }
                else {
                    iterator.remove();
                }
            }
        }
        return listDocumento;
    }

    public Documento getDetail(int idDocumento) {
        return daoDocumento.getDetail(idDocumento);
    }

    public List<Documento> downloadDocumentos(int idSucursal, int idUsuario) throws Exception {
        List<Documento> listDocumentos = new ArrayList<>();
        String dataDocumentos = daoIpati.loadDocumentoByUser(getSession().sessionID, idSucursal, idUsuario);
        JsonParser parser = new JsonParser();
        JsonArray jDocumentos = parser.parse(dataDocumentos).getAsJsonArray();
        for (JsonElement jElement : jDocumentos) {
            JsonObject jDocumento = jElement.getAsJsonObject();
            Documento documento = new Documento();
            documento.Identity  = jDocumento.get("id").getAsInt();
            documento.Code      = jDocumento.get("codigo").getAsString();
            documento.Nombre    = jDocumento.get("nombre").getAsString();
            documento.Version   = jDocumento.get("version").getAsInt();
            if (!daoDocumento.exist(documento.Identity)) {
                daoDocumento.guardar(documento);
            }
            listDocumentos.add(documento);
        }
        return listDocumentos;
    }

    public Documento getServerDetail(int idDocumento) throws Exception {
        String dataDocumentos = daoIpati.loadDocumentoDetail(getSession().sessionID, idDocumento);
        JsonParser parser = new JsonParser();
        JsonObject jDocumento = parser.parse(dataDocumentos).getAsJsonObject();
        Documento documento = new Documento();
        documento.Identity  = jDocumento.get("identity").getAsInt();
        documento.Code      = jDocumento.get("code").getAsString();
        documento.Nombre    = jDocumento.get("nombre").getAsString();
        documento.Version   = jDocumento.get("version").getAsInt();
        if (!daoDocumento.exist(documento.Identity)) {
            daoDocumento.guardar(documento);
        }
        return  documento;
    }

}
