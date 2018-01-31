package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;
import mx.com.azteca.home.model.ipati.dao.DAOWorkFlowOption;
import mx.com.azteca.home.model.ipati.dao.DAOWorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStepOption;

public class MWorkflow  extends BaseModel {

    private DAOWorkFlowStep daoWorkflow;
    private DAOWorkFlowOption daoWorkFlowOption;
    private DAOIpati daoIpati;

    public MWorkflow(Context context) {
        super(context);
    }

    public MWorkflow(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    public void initialize() {
        this.daoWorkflow        = new DAOWorkFlowStep(getContext());
        this.daoWorkFlowOption  = new DAOWorkFlowOption(getContext());
        this.daoIpati           = new DAOIpati();
    }

    public List<String> loadWorflowSteps(int idDocumento, int version) {
        return daoWorkflow.getWorkflowStatus(idDocumento, version);
    }

    public List<String> loadWorflowStepsOffline(int idDocumento, int version) {
        return daoWorkflow.getWorkflowStatusOffline(idDocumento, version);
    }

    public WorkFlowStep getDetail(int idWorkflow) {
        return daoWorkflow.getDetail(idWorkflow);
    }

    public WorkFlowStep getDetail(int idWorkflowStep, int idDocumento, int version) {
        return daoWorkflow.getDetail(idWorkflowStep, idDocumento, version);
    }

    public WorkFlowStep getStartStep(int idDocumento, int version) {
        return daoWorkflow.getStartStep(idDocumento, version);
    }

    public List<WorkFlowStep> getWorkflowByDocumento(int idDocumenot, int version) {
        return daoWorkflow.getWorkflowByDocumento(idDocumenot, version);
    }

    public List<WorkFlowStep> downloadWorkflow(int idSucursal, int idDocumento, int version) throws Exception {
        return downloadWorkflow(idSucursal, idDocumento, version, null);
    }

    public List<WorkFlowStep> downloadWorkflow(int idSucursal, int idDocumento, int version, List<Integer> formularios) throws Exception {
        return downloadWorkflow(idSucursal, idDocumento,  version, formularios, false, false);
    }

    public List<WorkFlowStep> downloadWorkflow(int idSucursal, int idDocumento, int version, List<Integer> formularios, boolean incluirAllFormularios, boolean override) throws Exception {
        List<WorkFlowStep> listStep = new ArrayList<>();
        String dataWorkflow = daoIpati.loadWorkflow(getSession().sessionID, idSucursal, idDocumento, version);
        JsonParser parser = new JsonParser();
        JsonArray jWorkflows =  parser.parse(dataWorkflow).getAsJsonArray();
        for (JsonElement element : jWorkflows) {
            WorkFlowStep workFlowStep = loadWorkflow(formularios, null, element.getAsJsonObject(), false, idDocumento, version, null, null, incluirAllFormularios);

            boolean exist = daoWorkflow.exist(workFlowStep.IdWorkflowStep, workFlowStep.IdDocumento, workFlowStep.Version);
            if (exist && override) {
                daoWorkflow.delete(workFlowStep.IdWorkflowStep, workFlowStep.IdDocumento, workFlowStep.Version);
                daoWorkflow.guardar(workFlowStep);
            }
            else if (!exist) {
                daoWorkflow.guardar(workFlowStep);
            }

            listStep.add(workFlowStep);
        }

        for (WorkFlowStep step : listStep) {
            updateWorkFlow(step);
        }

        return listStep;
    }

    private WorkFlowStep loadWorkflow(List<Integer> listFormulario, WorkFlowStep workFlowStep, JsonObject jWorkFlow, boolean next, int idDocumento, int version, String tag, String text) {
        return loadWorkflow(listFormulario, workFlowStep, jWorkFlow, next, idDocumento,  version, tag, text, false);
    }

    private WorkFlowStep loadWorkflow(List<Integer> listFormulario, WorkFlowStep workFlowStep, JsonObject jWorkFlow, boolean next, int idDocumento, int version, String tag, String text, boolean incluirAllFommularios) {
        WorkFlowStep step = new WorkFlowStep();
        step.IdWorkflowStep = jWorkFlow.get("idWorkflowStep").getAsInt();
        step.Tag            = jWorkFlow.get("tag").getAsString();
        step.ActionText     = jWorkFlow.get("actionText").getAsString();
        step.StatusText     = jWorkFlow.get("statusText").getAsString();
        step.Optional       = jWorkFlow.get("optional").getAsBoolean();
        step.Orden          = jWorkFlow.get("order").getAsInt();
        step.IdFormulario   = jWorkFlow.get("idFormulario").getAsInt();
        step.Offline        = jWorkFlow.get("offline").getAsBoolean();
        step.IsStart        = jWorkFlow.get("isStart").getAsBoolean();
        step.IsEnd          = jWorkFlow.get("isEnd").getAsBoolean();
        step.Duracion       = jWorkFlow.get("duracion").getAsDouble();
        step.HasOptions     = jWorkFlow.get("hasOptions").getAsBoolean() ? 1 : 0;
        step.IdDocumento    = idDocumento;
        step.Version        = version;
        step.ID             = jWorkFlow.get("id").getAsInt();
        if (listFormulario != null && !listFormulario.contains(step.IdFormulario) && (step.Offline || incluirAllFommularios)) {
            listFormulario.add(step.IdFormulario);
        }
        if (workFlowStep != null) {
            if (next) {
                workFlowStep.NextStep = step.IdWorkflowStep;
                workFlowStep.setWorkFlowNext(step);
            }
            else {
                WorkFlowStepOption option = new WorkFlowStepOption();
                option.IdDocumento      = workFlowStep.IdDocumento;
                option.Version          = workFlowStep.Version;
                option.IdWorkFlowStep   = workFlowStep.IdWorkflowStep;
                option.IdWorkFlowOption = step.IdWorkflowStep;
                option.Tag              = tag;
                option.Text             = text;
                option.setOption(step);
                workFlowStep.getOptions().add(option);
            }
        }
        if(jWorkFlow.has("nextStep") && !jWorkFlow.get("nextStep").isJsonNull()) {
            loadWorkflow(listFormulario, step, jWorkFlow.get("nextStep").getAsJsonObject(), true, idDocumento, version, null, null, incluirAllFommularios);
        }
        for (JsonElement element : jWorkFlow.get("options").getAsJsonArray()) {
            loadWorkflow(listFormulario, step, element.getAsJsonObject().get("nextStep").getAsJsonObject(), false, idDocumento, version, element.getAsJsonObject().get("tag").getAsString(), element.getAsJsonObject().get("text").getAsString(), incluirAllFommularios);
        }
        return step;
    }

    private void updateWorkFlow(WorkFlowStep workFlowStep) {
        if (workFlowStep != null) {
            if (!daoWorkflow.exist(workFlowStep.IdWorkflowStep, workFlowStep.IdDocumento, workFlowStep.Version)) {
                daoWorkflow.guardar(workFlowStep);
            }
            updateWorkFlow(workFlowStep.getWorkFlowNext());

            if (workFlowStep.HasOptions == 1) {
                for (WorkFlowStepOption option : workFlowStep.getOptions()) {
                    if (!daoWorkFlowOption.exist(option.IdWorkFlowStep, option.IdWorkFlowOption, option.IdDocumento, option.Version)) {
                        daoWorkFlowOption.guardar(option);
                    }
                    updateWorkFlow(option.getOption());
                }
            }
        }
    }

}
