package mx.com.azteca.home.model.ipati.pojo;


public class Forma {

    public int IdDocumento;
    public int IdVersion;
    public int Folio;
    public int IdentityField;
    public String Value;
    public long CreateDate;
    public long LastUpdate;
    public int UserCreate;
    public long DeactivateDate;
    public boolean Activo;

    // 1: Borrador
    // 2: Gurdado
    // 3: Sincronizado
    public int Estatus;

    public double Latitud;
    public double Longitud;

}
