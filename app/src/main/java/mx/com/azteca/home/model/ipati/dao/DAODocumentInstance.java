package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.model.ipati.pojo.DocumentInstance;


public class DAODocumentInstance extends DAOObject {

    public DAODocumentInstance(Context context) {
        super(context);
    }

    public void guardar(DocumentInstance instance) {
        try {
            super.insert(instance);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void update(DocumentInstance documentInstance) {
        try {
            super.update(documentInstance, "IdDocumento = ? AND Version = ? AND Folio = ?",
                    String.valueOf(documentInstance.IdDocumento), String.valueOf(documentInstance.Version), documentInstance.Folio);
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public DocumentInstance getDetail(int idDocumento, int version) {
        try {
            DocumentInstance docInstance = new DocumentInstance();
            docInstance = (DocumentInstance) super.select(docInstance, null, "IdDocumento = ? AND Version = ?", new String[]{String.valueOf(idDocumento), String.valueOf(version)}, null);
            return docInstance;
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return null;
    }

    public DocumentInstance getDetail(int idDocumento, int version, String folio) {
        try {
            DocumentInstance docInstance = new DocumentInstance();
            docInstance = (DocumentInstance) super.select(docInstance, null, "IdDocumento = ? AND Version = ? AND Folio = ?", new String[]{String.valueOf(idDocumento), String.valueOf(version), String.valueOf(folio)}, null);
            return docInstance;
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return null;
    }

    public List<DocumentInstance> loadList() {
        List<DocumentInstance> listDocInstance = new ArrayList<>();
        try {
            List documentos = (List) super.select(DocumentInstance.class, null, null, null, null);
            if (documentos != null) {
                listDocInstance.addAll(documentos);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listDocInstance;
    }

    public boolean exist(int idDocumento, long fecha, String folio) {
        try {
            return super.count(DocumentInstance.class, null, "IdDocumento = ? AND Fecha = ? AND Folio = ?",
                    new String[]{String.valueOf(idDocumento), String.valueOf(fecha), String.valueOf(folio)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public boolean existByFolio(int idDocumento, int version, String folio) {
        try {
            return super.count(DocumentInstance.class, null, "IdDocumento = ? AND Version = ? AND Folio = ?",
                    new String[]{String.valueOf(idDocumento), String.valueOf(version), String.valueOf(folio)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void delete(int idDocumento, int version, String folio) {
        try {
            super.delete(DocumentInstance.class, "IdDocumento = ? AND Version = ? AND Folio = ?", String.valueOf(idDocumento), String.valueOf(version), folio);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    @Override
    public void truncate() {
        try {
            super.delete(DocumentInstance.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }
}
