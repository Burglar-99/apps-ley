package mx.com.azteca.home.model.ipati.pojo;


import java.util.HashMap;

public class Formulario extends BaseInfo implements DataField {

    public int IdDocumento;
    public int Version;
    public String Info;
    public String Titulo;


    private String Contenido;
    private HashMap<String, String> values;

    public Formulario() {
        this.Contenido = "";
    }

    @Override
    public HashMap<String, String> getValues() {
        if (values == null) {
            values = new HashMap<>();
        }
        return values;
    }

    @Override
    public String getDataValue() {
        return Contenido;
    }

    @Override
    public void setDataValue(String contenido) {
        this.Contenido = contenido;
    }


}
