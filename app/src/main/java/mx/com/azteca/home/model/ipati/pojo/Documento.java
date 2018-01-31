package mx.com.azteca.home.model.ipati.pojo;

import java.util.ArrayList;
import java.util.List;

public class Documento extends BaseInfo {

    public int Version;

    private int DocsOffline;

    private List<DocumentoAsignado> DocAsignados;

    public Documento() {
        this.DocAsignados = new ArrayList<>();
    }

    public List<DocumentoAsignado> getDocAsignados() {
        return DocAsignados;
    }


    public int getDocsOffline() {
        return DocsOffline;
    }

    public void setDocsOffline(int docsOffline) {
        DocsOffline = docsOffline;
    }
}
