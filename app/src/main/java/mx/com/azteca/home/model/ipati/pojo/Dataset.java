package mx.com.azteca.home.model.ipati.pojo;


import java.util.ArrayList;
import java.util.List;

public class Dataset extends BaseInfo {

    public String Tipo;
    public String Expresion;
    public String Dataset; // Json
    public long LastUpdate;
    public String Parametros;
    public String Columnas;

    private List<String> QueryColumnas; // Columnas especificas conforme al query del server.
    private List<String> NameColumns;   // Columnas conforme a la tabla creada en Sql.
    private List<String> NameParametro; // Parametros conforme al server.

    public Dataset() {
        this.Tipo        = "";
        this.Expresion   = "";
        this.Dataset     = "";
        this.Parametros  = "";
        this.Columnas    = "";
        this.QueryColumnas = new ArrayList<>();
        this.NameColumns = new ArrayList<>();
        this.NameParametro = new ArrayList<>();
    }

    public String getNameTableInfo() {
        return this.Nombre.replace(" ", "_").replace(".", "_");
    }


    public List<String> getNameColumns() {
        return NameColumns;
    }

    public List<String> getNameParametro() {
        return NameParametro;
    }

    public List<String> getQueryColumnas() {
        return QueryColumnas;
    }
}
