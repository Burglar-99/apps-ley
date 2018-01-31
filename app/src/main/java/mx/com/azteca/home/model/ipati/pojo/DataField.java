package mx.com.azteca.home.model.ipati.pojo;


import java.util.HashMap;

public interface DataField {

    HashMap<String, String> getValues();
    String getDataValue();
    void setDataValue(String contenido);

}
