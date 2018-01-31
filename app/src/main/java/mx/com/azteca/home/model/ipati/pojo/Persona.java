package mx.com.azteca.home.model.ipati.pojo;


public class Persona extends BaseInfo {

    public String ApellidoPaterno;
    public String ApellidoMaterno;
    public String TelefonoCasa;
    public String TelefonoOficina;
    public String Celular;
    public String Correo;

    public Persona() {
        this.ApellidoPaterno = "";
        this.ApellidoMaterno = "";
        this.TelefonoCasa = "";
        this.TelefonoOficina = "";
        this.Celular = "";
        this.Correo = "";
    }

    public String getNombreCompleto() {
        return Nombre + " " + ApellidoPaterno + " " + ApellidoMaterno;
    }

}
