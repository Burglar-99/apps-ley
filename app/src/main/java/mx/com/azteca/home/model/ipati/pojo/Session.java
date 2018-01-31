package mx.com.azteca.home.model.ipati.pojo;


public class Session {

    public String sessionID;
    public String workStation;
    public long businessDate;
    public long loginDate;
    public int timezoneOffset;
    public int idUsuario;

    public Session() {
        this.sessionID = "";
        this.workStation = "";
    }

}
