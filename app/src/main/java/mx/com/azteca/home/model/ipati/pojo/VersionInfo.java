package mx.com.azteca.home.model.ipati.pojo;


import java.util.ArrayList;
import java.util.List;

public class VersionInfo {

    public int IdDocumento;
    public int Version;
    public long Fecha;
    public String ClaseBusiness;
    public int IdEnsamblado;
    public int IdFormaBorrador;
    public int UserUpdate;
    public String Fields;
    public String Columns;

    private List<Column> listColumns;

    public VersionInfo() {
        this.listColumns = new ArrayList<>();
    }

    public List<Column> getListColumns() {
        return listColumns;
    }
}
