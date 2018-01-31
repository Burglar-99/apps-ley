package mx.com.azteca.home.model.ipati.dao;

import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.ipati.pojo.DocumentoWorkflow;
import mx.com.azteca.home.model.ipati.pojo.EventLog;

public class DAODocumentoWorkflow extends DAOObject {

    public DAODocumentoWorkflow(Context context) {
        super(context);
    }

    public void guardar(DocumentoWorkflow documentoWorkflow) {
        try {
            super.insert(documentoWorkflow);
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public void delete(int idDocumento, int version) {
        try {
            super.delete(DocumentoWorkflow.class, "IdDocumento = ? AND Version = ?", String.valueOf(idDocumento), String.valueOf(version));
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public DocumentoWorkflow getDetail(int idDocumento, int version) {
        try {
            DocumentoWorkflow documentoWorkflow = new DocumentoWorkflow();
            documentoWorkflow = (DocumentoWorkflow) super.select(documentoWorkflow, null, "IdDocumento = ? AND Version = ?", new String[]{String.valueOf(idDocumento), String.valueOf(version)}, null);
            return documentoWorkflow;
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return null;
    }

    @Override
    protected void truncate() {
        try {
            delete(EventLog.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }
}
