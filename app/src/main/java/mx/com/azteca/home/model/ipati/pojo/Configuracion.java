package mx.com.azteca.home.model.ipati.pojo;


public class Configuracion {

    public String Path;
    public String Name;
    public String Value;

    public Configuracion() {
    }

    public Configuracion(String path, String name, String value) {
        this.Path = path;
        this.Name = name;
        this.Value = value;
    }
}

