package mx.com.azteca.home.model.ipati.pojo;


public class Usuario extends BaseInfo {

    public String Password;
    public String UserName;
    public int IdPersona;
    public int IdBranch;
    public int IdCompany;
    public int IdUsuario;

    private String sessionID;
    private Persona Persona;


    public Usuario() {
        this.Persona = new Persona();
    }

    public Persona getPersona() {
        return this.Persona;
    }

    public void setPersona(Persona persona) {
        this.Persona = persona;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
