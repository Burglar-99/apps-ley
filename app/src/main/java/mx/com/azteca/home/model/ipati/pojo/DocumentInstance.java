package mx.com.azteca.home.model.ipati.pojo;


public class DocumentInstance {

    public String ID;
    public int IdDocumento;
    public int Version;
    public String Folio;
    public String TipoDocumento;
    public long Fecha;
    public String FechaStr;
    public String Contenido;
    public String Referencia;
    public double Latitud;
    public double Longitud;
    public double Altitud;
    public String Posicion;
    public int IdUsuarioAsignado;
    public int IdWorkflowStep;
    public int IdStatus;
    public String Status;
    public long StartStep;
    public long EndStep;
    public boolean HasStartStep;
    public boolean HasEndStep;
    public int IdSucursal;
    public int IdEmpresa;
    public long LastUpdate;
    public int UserUpdate;
    public String PCUpdate;
    public String Nodes;
    public String ContenidoJson;

    private String NodesInfo;

    public String getNodesInfo() {
        return NodesInfo;
    }

    public void setNodesInfo(String nodesInfo) {
        NodesInfo = nodesInfo;
    }
}
