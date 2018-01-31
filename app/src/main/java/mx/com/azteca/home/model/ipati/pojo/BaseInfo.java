package mx.com.azteca.home.model.ipati.pojo;


import java.util.ArrayList;
import java.util.List;

public class BaseInfo {

    public int Identity;
    public String Code;
    public String Nombre;
    private List<BaseInfo> childs;

    public BaseInfo() {
        this.Code = "";
        this.Nombre = "";
        this.childs = new ArrayList<>();
    }

    public List<BaseInfo> getChilds() {
        return childs;
    }

    @Override
    public String toString() {
        return this.Nombre;
    }
}
