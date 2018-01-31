package mx.com.azteca.home.model.ipati.model;


import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mx.com.azteca.home.entity.generator.DaoObject;
import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAODocumentInstance;
import mx.com.azteca.home.model.ipati.dao.DAODocumentoAsignado;
import mx.com.azteca.home.model.ipati.dao.DAODocumentoWorkflow;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;
import mx.com.azteca.home.model.ipati.dao.DAOWorkFlowOption;
import mx.com.azteca.home.model.ipati.dao.DAOWorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.DocumentInstance;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.DocumentoWorkflow;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStepOption;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.model.provider.pojo.WorkflowStep;
import mx.com.azteca.home.util.BusinessException;

public class MDocumentoAsignado extends BaseModel {

    private DAODocumentoAsignado daoDocAsignado;
    private DAOWorkFlowStep daoWorkflow;
    private DAOWorkFlowOption daoWorkFlowOption;
    private DAODocumentInstance daoDocumentInstance;
    private DAODocumentoWorkflow daoDocumentoWorkflow;
    private DAOIpati daoIpati;


    public MDocumentoAsignado(Context context) {
        super(context);
    }

    public MDocumentoAsignado(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    public void initialize() {
        this.daoDocAsignado         = new DAODocumentoAsignado(getContext());
        this.daoWorkflow            = new DAOWorkFlowStep(getContext());
        this.daoWorkFlowOption      = new DAOWorkFlowOption(getContext());
        this.daoDocumentInstance    = new DAODocumentInstance(getContext());
        this.daoDocumentoWorkflow   = new DAODocumentoWorkflow(getContext());
        this.daoIpati               = new DAOIpati();
    }

    public List<DocumentoAsignado> loadDocumentoAsignado(int idDocumento, int idWorkFlowStep) {
        return daoDocAsignado.loadDocAsignado(idDocumento, idWorkFlowStep);
    }

    public List<DocumentoAsignado> loadDocumentoAsignado(int idDocumento, int version, int idWorkFlowStep) {
        return daoDocAsignado.loadDocAsignado(idDocumento, version, idWorkFlowStep);
    }

    public DocumentoAsignado getDetail(int idDocumento, long fecha, String folio) {
        return daoDocAsignado.getDetail(idDocumento, fecha, folio);
    }

    public void applyWorkflow(DocumentoAsignado docAsignado, int idWorkFlowStep) throws Exception {
        try {
            mx.com.azteca.home.model.MDocumentInstance mDocumentInstance = new mx.com.azteca.home.model.MDocumentInstance(getContext(), getUserName(), getPasswd(), getWorkStation());
            DaoObject<mx.com.azteca.home.model.provider.pojo.DocumentInstance> daoDocInstance = new DaoObject<>();
            daoDocInstance.where("ID", docAsignado.getDocumentInstance().ID);
            mx.com.azteca.home.model.provider.pojo.DocumentInstance documentInstance = daoDocInstance.single(mx.com.azteca.home.model.provider.pojo.DocumentInstance.class);
            documentInstance.contenidoJson = new JsonParser().parse(docAsignado.getDocumentInstance().ContenidoJson).getAsJsonObject();
            DaoObject<WorkflowStep> daoWorkflow = new DaoObject<>();
            daoWorkflow.where("ID", documentInstance.idWorkflowStep);
            documentInstance.currentStep = daoWorkflow.single(WorkflowStep.class);
            if (documentInstance.idWorkflowStep != idWorkFlowStep) {
                documentInstance.idWorkflowStep = idWorkFlowStep;
                documentInstance = mDocumentInstance.nextStep(documentInstance, idWorkFlowStep);
            }
            else {
                documentInstance = mDocumentInstance.saveInstanceJson(documentInstance);
            }
            daoDocInstance.update(documentInstance);
            // Conservamos el último paso modificado
            DocumentoWorkflow docWorkflow = new DocumentoWorkflow();
            docWorkflow.IdDocumento = docAsignado.IdDocumento;
            docWorkflow.Version = docAsignado.Version;
            docWorkflow.IdWorkflowStep = docAsignado.IdWorkflowStep;
            daoDocumentoWorkflow.delete(docAsignado.IdDocumento, docAsignado.Version);
            daoDocumentoWorkflow.guardar(docWorkflow);
            //
            docAsignado.IdWorkflowStep = idWorkFlowStep;
            docAsignado.LastUpdate = Calendar.getInstance().getTimeInMillis();
            daoDocAsignado.update(docAsignado);
            DocumentInstance docInstance = docAsignado.getDocumentInstance();
            docInstance.IdWorkflowStep = idWorkFlowStep;
            docInstance.ContenidoJson = documentInstance.contenidoJson.toString();
            daoDocumentInstance.update(docInstance);
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError(ex.getMessage());
        }
    }

    public WorkFlowStep loadWorkFlowOptions(int idWorkflowStep, int idDocumento, int version) throws Exception {
        try {
            WorkFlowStep workFlowStep = daoWorkflow.getDetail(idWorkflowStep, idDocumento, version);
            if (workFlowStep == null) {
                // TODO: Verificar esta parte es un parche mal aplicacdo
                workFlowStep = daoWorkflow.getDetail(idWorkflowStep);
            }
            if (workFlowStep.HasOptions == 1) {
                List<WorkFlowStepOption> options = daoWorkFlowOption.getOptions(workFlowStep.IdWorkflowStep, idDocumento, version);
                if (options != null) {
                    workFlowStep.getOptions().addAll(daoWorkFlowOption.getOptions(workFlowStep.IdWorkflowStep, idDocumento, version));
                    for (WorkFlowStepOption option : workFlowStep.getOptions()) {
                        option.setOption(daoWorkflow.getDetail(option.IdWorkFlowOption, option.IdDocumento, option.Version));
                    }
                }
            }
            else {
                WorkFlowStep nextStep = daoWorkflow.getDetail(workFlowStep.NextStep, workFlowStep.IdDocumento, workFlowStep.Version);
                if (nextStep == null) {
                    nextStep = daoWorkflow.getDetail(workFlowStep.NextStep);
                }
                workFlowStep.setWorkFlowNext(nextStep);
            }
            return workFlowStep;
        }
        catch (Exception ex) {
            throw handledError("Ocurri\u00f3 un problema al consultar la informacu\u00f3n", ex);
        }
    }

    public List<DocumentoAsignado> downloadDocumentoAsignado(int idDocumento, int idUsuario) throws Exception {
        try {
            List<DocumentoAsignado> listDocAsignado = new ArrayList<>();
            Calendar fechaIni = Calendar.getInstance();
            fechaIni.add(Calendar.MONTH, -6);

            String asignados = daoIpati.loadListAsignadosOffline(getSession().sessionID, fechaIni.getTime(), Calendar.getInstance().getTime(), idDocumento, idUsuario);
            JsonParser parser = new JsonParser();
            JsonArray jAsignados = parser.parse(asignados).getAsJsonArray();

            for (JsonElement jElementAsigado : jAsignados) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                JsonObject jAsignado = jElementAsigado.getAsJsonObject();

                DocumentoAsignado docAsignado = new DocumentoAsignado();
                docAsignado.IdAlmacen       = jAsignado.get("id").getAsString();
                docAsignado.Folio           = jAsignado.get("folio").getAsString();
                docAsignado.IdDocumento     = jAsignado.get("idDocumento").getAsInt();
                docAsignado.Version         = jAsignado.get("version").getAsInt();
                docAsignado.TipoDocumento   = jAsignado.get("tipoDocumento").getAsString();
                docAsignado.Fecha           = dateFormat.parse(jAsignado.get("fecha").getAsString()).getTime();
                docAsignado.Hora            = dateFormat.parse(jAsignado.get("hora").getAsString()).getTime();
                docAsignado.Referencia      = jAsignado.get("referencia").getAsString();
                docAsignado.Contenido       = jAsignado.get("contenido").getAsString();
                docAsignado.IdWorkflowStep  = jAsignado.get("idWorkflowStep").getAsInt();
                docAsignado.IdStatus        = jAsignado.get("idStatus").getAsInt();
                docAsignado.WorkflowStep    = jAsignado.get("workflowStep").isJsonNull() ? "" : jAsignado.get("workflowStep").getAsString();
                docAsignado.Tag             = jAsignado.get("tag").isJsonNull() ? "" : jAsignado.get("tag").getAsString();
                docAsignado.Estatus         = jAsignado.get("estatus").getAsString();

                if (daoDocAsignado.exist(docAsignado.IdAlmacen)) {
                    daoDocAsignado.delete(docAsignado.IdAlmacen);
                }
                daoDocAsignado.guardar(docAsignado);

                listDocAsignado.add(docAsignado);
            }
            return listDocAsignado;
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError("Ocurri\u00f3 un problema al consultar la informacu\u00f3n", ex);
        }
    }

}
