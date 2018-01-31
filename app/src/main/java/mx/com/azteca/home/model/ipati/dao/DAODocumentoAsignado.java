package mx.com.azteca.home.model.ipati.dao;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;

public class DAODocumentoAsignado extends DAOObject {

    public DAODocumentoAsignado(Context context) {
        super(context);
    }

    public void guardar(DocumentoAsignado documento) {
        try {
            super.insert(documento);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public DocumentoAsignado getDetail(int idDocumento, long fecha, String folio) {
        try {
            DocumentoAsignado docAsignado = new DocumentoAsignado();
            docAsignado = (DocumentoAsignado) super.select(docAsignado, null, "IdDocumento = ? AND Fecha = ? AND Folio = ?", new String[] {String.valueOf(idDocumento), String.valueOf(fecha), folio} , null);
            return docAsignado;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public DocumentoAsignado getDetail(String idAlmacen) {
        try {
            DocumentoAsignado docAsignado = new DocumentoAsignado();
            docAsignado = (DocumentoAsignado) super.select(docAsignado, null, "IdAlmacen = ?", new String[]{ idAlmacen } , null);
            return docAsignado;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public List<DocumentoAsignado> loadDocAsignado(int idDocumento, int idWorkFlowStep) {
        List<DocumentoAsignado> listDocAsignado = new ArrayList<>();
        try {
            List documentos = (List) super.select(DocumentoAsignado.class, null, "IdDocumento = ? AND IdWorkflowStep = ?", new String[]{String.valueOf(idDocumento), String.valueOf(idWorkFlowStep)}, null);
            if (documentos != null) {
                listDocAsignado.addAll(documentos);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listDocAsignado;
    }

    public List<DocumentoAsignado> loadDocAsignado(int idDocumento, int version, int idWorkFlowStep) {
        List<DocumentoAsignado> listDocAsignado = new ArrayList<>();
        try {
            List documentos = (List) super.select(DocumentoAsignado.class, null, "IdDocumento = ? AND Version = ? AND IdWorkflowStep = ?", new String[]{String.valueOf(idDocumento), String.valueOf(version), String.valueOf(idWorkFlowStep)}, null);
            if (documentos != null) {
                listDocAsignado.addAll(documentos);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listDocAsignado;
    }

    public int update(DocumentoAsignado docAsignado) {
        try {
            return super.update(docAsignado, "Folio = ?  AND Fecha = ? AND IdDocumento = ?", String.valueOf(docAsignado.Folio), String.valueOf(docAsignado.Fecha), String.valueOf(docAsignado.IdDocumento));
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return -1;
    }

    public boolean exist(String idAlmacen) {
        try {
            return super.count(DocumentoAsignado.class, null, "IdAlmacen = ?", new String[]{String.valueOf(idAlmacen)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public List<DocumentoAsignado> loadDocumentoAsignado() {
        List<DocumentoAsignado> listDocAsignado = new ArrayList<>();
        try {
            List documentos = (List) super.select(DocumentoAsignado.class, null, null, null, null);
            if (documentos != null) {
                listDocAsignado.addAll(documentos);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listDocAsignado;
    }

    public List<DocumentoAsignado> loadDocFinalizado() {
        try {
            return (List)super.selectWithStatement(DocumentoAsignado.class, "DocCompleted", null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public List<DocumentoAsignado> loadDocModificado() {
        try {
            return (List)super.selectWithStatement(DocumentoAsignado.class, "DocUpdated", null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public List<DocumentoAsignado> loadDocAsignadoVersion(int idDocumento, int version) {
        List<DocumentoAsignado> listDocAsignado = new ArrayList<>();
        try {
            List documentos = (List) super.select(DocumentoAsignado.class, null, "IdDocumento = ? AND Version = ?", new String[]{String.valueOf(idDocumento), String.valueOf(version)}, null);
            if (documentos != null) {
                listDocAsignado.addAll(documentos);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listDocAsignado;
    }

    public void delete(String idAlmacen) {
        try {
            super.delete(DocumentoAsignado.class, "IdAlmacen = ?", idAlmacen);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void truncate() {
        try {
            super.delete(DocumentoAsignado.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}
