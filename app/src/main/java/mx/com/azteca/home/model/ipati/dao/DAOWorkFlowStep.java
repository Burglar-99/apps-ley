package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.model.provider.pojo.WorkflowStep;

public class DAOWorkFlowStep extends DAOObject {


    public DAOWorkFlowStep(Context context) {
        super(context);
    }

    public void guardar(WorkFlowStep workflow) {
        try {
            super.insert(workflow);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public WorkFlowStep getDetail(int idWorkflow) {
        try {
            WorkFlowStep workflow = new WorkFlowStep();
            workflow = (WorkFlowStep) super.select(workflow, null, "ID = ?", new String[]{String.valueOf(idWorkflow)}, null);
            return workflow;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public WorkFlowStep getDetail( int idWorkflowStep, int idDocumento, int version) {
        try {
            WorkFlowStep workflow = new WorkFlowStep();
            workflow = (WorkFlowStep) super.select(workflow, null, "IdWorkflowStep = ? AND IdDocumento = ? AND Version = ?", new String[]{String.valueOf(idWorkflowStep), String.valueOf(idDocumento), String.valueOf(version)}, null);
            return workflow;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public List<String> getWorkflowStatus(int idDocumento,  int version) {
        List<String> listResultado = new ArrayList<>();
        try {
            List<HashMap<String, Object>> listResult = super.select(WorkFlowStep.class, "Steps", new String[]{String.valueOf(idDocumento),  String.valueOf(version)});
            for (HashMap<String, Object> mapResult : listResult) {
                for (Map.Entry<String, Object> entry : mapResult.entrySet()) {
                    String value = entry.getValue().toString();
                    listResultado.add(value);
                }
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listResultado;
    }

    public List<String> getWorkflowStatusOffline(int idDocumento,  int version) {
        List<String> listResultado = new ArrayList<>();
        try {
            List<HashMap<String, Object>> listResult = super.select(WorkFlowStep.class, "StepsOffline", new String[]{String.valueOf(idDocumento),  String.valueOf(version)});
            for (HashMap<String, Object> mapResult : listResult) {
                for (Map.Entry<String, Object> entry : mapResult.entrySet()) {
                    String value = entry.getValue().toString();
                    listResultado.add(value);
                }
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listResultado;
    }

    public List<WorkFlowStep> getWorkflowByDocumento(int idDocumento, int version) {
        try {

            List<WorkFlowStep> listWorkFlow = (List) super.select(WorkFlowStep.class, null, "IdDocumento = ? AND Version = ?", new String[]{String.valueOf(idDocumento), String.valueOf(version)}, "Orden");
            return listWorkFlow;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public WorkFlowStep getStartStep(int idDocumento, int version) {
        try {
            WorkFlowStep workflow = new WorkFlowStep();
            return (WorkFlowStep) super.select(workflow, null, "IdDocumento = ? AND Version = ? AND IsStart = 1", new String[]{String.valueOf(idDocumento), String.valueOf(version)}, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public boolean exist(int idWorkFlow, int idDocumento, int version) {
        try {
            return super.count(WorkFlowStep.class, null, "IdWorkflowStep = ? AND IdDocumento = ? AND Version = ?", new String[]{String.valueOf(idWorkFlow), String.valueOf(idDocumento), String.valueOf(version)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void delete(int idWorkFlow, int idDocumento, int version) {
        try {
            super.delete(WorkFlowStep.class, "IdWorkflowStep = ? AND IdDocumento = ? AND Version = ?",  String.valueOf(idWorkFlow), String.valueOf(idDocumento), String.valueOf(version));
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    @Override
    public void truncate() {
        try {
            super.delete(WorkFlowStep.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }
}
