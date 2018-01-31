package mx.com.azteca.home.model.ipati.dao;

import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.ipati.pojo.Workflow;


public class DAOWorkflow  extends DAOObject {

    public DAOWorkflow(Context context) {
        super(context);
    }

    public void guardar(Workflow workflow) {
        try {
            super.insert(workflow);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public Workflow getDetail(int idDocumento, int version, int idWorkflowStep) {
        try {
            Workflow workflow = new Workflow();
            workflow = (Workflow) super.select(workflow, null, "IdDocumento = ? AND Version = ? AND IdWorkflowStep = ?", new String[]{ String.valueOf(idDocumento), String.valueOf(version), String.valueOf(idWorkflowStep) }, null);
            return workflow;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public Workflow getNextStepDocument(int idDocumento, int idWorkFlowStep) {
        try {
            Workflow workflow = new Workflow();
            workflow = (Workflow) super.selectWithStatement(workflow, "NextStep", new String[]{String.valueOf(idDocumento), String.valueOf(idWorkFlowStep)});
            return workflow;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public boolean exist(int idDocumento, int version, int idWorkFlow) {
        try {
            return super.count(Workflow.class, null, "IdDocumento = ? AND Version = ? AND IdWorkflowStep = ?",
                    new String[]{String.valueOf(idDocumento), String.valueOf(version), String.valueOf(idWorkFlow)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void truncate() {
        try {
            super.delete(Workflow.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}

