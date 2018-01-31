package mx.com.azteca.home.model.ipati.pojo;


import java.util.HashMap;

import mx.com.azteca.home.model.provider.pojo.ClienteAvaluoInfo;

public class DocumentoAsignado implements DataField {

    public String IdAlmacen;
    public int IdDocumento;
    public int Version;
    public long Fecha;
    public String Folio;
    public String Referencia;
    public double Latitud;
    public double Longitud;
    public double Altitud;
    public String Posicion;
    public int IdWorkflowStep;
    public int IdStatus;
    public int IdEmpresa;
    public int IdSucursal;
    public long LastUpdate;
    public int UserUpdate;
    public String PCUpdate;
    public long Hora;
    public String TipoDocumento;
    public String Contenido;
    public String WorkflowStep;
    public String Tag;
    public String Estatus;

    private WorkFlowStep workflow;
    private DocumentInstance DocumentInstance;
    private VersionInfo VersionInfo;
    private HashMap<String, String> values;
    private ClienteAvaluoInfo ClienteAvaluo;

    public void setWorkflow(WorkFlowStep workflow) {
        this.workflow = workflow;
        if (values == null)
            this.values = new HashMap<>();
        if (this.DocumentInstance == null)
            this.DocumentInstance = new DocumentInstance();
    }

    public WorkFlowStep getWorkflow() {
        return workflow;
    }

    @Override
    public HashMap<String, String> getValues() {
        return values;
    }

    @Override
    public String getDataValue() {
        return this.Contenido;
    }

    @Override
    public void setDataValue(String contenido) {
        this.Contenido = contenido;
    }

    public DocumentInstance getDocumentInstance() {
        return DocumentInstance;
    }

    public void setDocumentInstance(DocumentInstance documentInstance) {
        DocumentInstance = documentInstance;
    }

    public VersionInfo getVersionInfo() {
        return VersionInfo;
    }

    public void setVersionInfo(VersionInfo versionInfo) {
        VersionInfo = versionInfo;
    }

    public ClienteAvaluoInfo getClienteAvaluo() {
        return ClienteAvaluo;
    }

    public void setClienteAvaluo(ClienteAvaluoInfo clienteAvaluo) {
        ClienteAvaluo = clienteAvaluo;
    }
}
