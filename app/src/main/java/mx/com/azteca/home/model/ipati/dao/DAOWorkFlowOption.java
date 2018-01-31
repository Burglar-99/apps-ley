package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import java.util.List;

import mx.com.azteca.home.model.ipati.pojo.WorkFlowStepOption;

public class DAOWorkFlowOption extends DAOObject {


    public DAOWorkFlowOption(Context context) {
        super(context);
    }

    public void guardar(WorkFlowStepOption option) {
        try {
            super.insert(option);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public boolean exist(int idWorkFlow, int idWorkFlowOption, int idDocumento, int version) {
        try {
            return super.count(WorkFlowStepOption.class, null, "IdWorkFlowStep = ? AND IdWorkFlowOption = ? AND IdDocumento = ? AND Version = ? ", new String[]{ String.valueOf(idWorkFlow), String.valueOf(idWorkFlowOption), String.valueOf(idDocumento), String.valueOf(version) }, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public List<WorkFlowStepOption> getOptions(int idWorkFlowStep, int idDocumento, int version) {
        try {
            return (List)super.select(WorkFlowStepOption.class, null, "IdWorkFlowStep = ? AND IdDocumento = ? AND Version = ? ", new String[]{ String.valueOf(idWorkFlowStep), String.valueOf(idDocumento), String.valueOf(version) }, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    @Override
    public void truncate() {
        try {
            super.delete(WorkFlowStepOption.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}
