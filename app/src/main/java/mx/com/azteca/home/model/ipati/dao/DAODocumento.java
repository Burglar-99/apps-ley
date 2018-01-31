package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.model.ipati.pojo.Documento;

public class DAODocumento extends DAOObject {

    public DAODocumento(Context context) {
        super(context);
    }

    public void guardar(Documento documento) {
        try {
            super.insert(documento);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public List<Documento> loadDocumento() {
        List<Documento> listDocumento = new ArrayList<>();
        try {
            List documentos = (List) super.select(Documento.class, null, null, null, null);
            if (documentos != null) {
                listDocumento.addAll(documentos);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listDocumento;
    }

    public List<Documento> loadDocumentoOffline() {
        List<Documento> listDocumento = new ArrayList<>();
        try {
            listDocumento = (List) super.selectWithStatement(Documento.class, "DocOffline", null);
        }
        catch (Exception ex ) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listDocumento;
    }

    public Documento getDetail(int idDocumento) {
        try {
            Documento documento = new Documento();
            documento = (Documento) super.select(documento, null, "Identity = ?", new String[]{String.valueOf(idDocumento)}, null);
            return documento;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public boolean exist(int idDocumento) {
        try {
            return super.count(Documento.class, null, "Identity = ?", new String[]{String.valueOf(idDocumento)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void truncate() {
        try {
            super.delete(Documento.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

}
