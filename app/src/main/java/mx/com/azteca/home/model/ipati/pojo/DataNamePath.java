package mx.com.azteca.home.model.ipati.pojo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataNamePath implements Cloneable {

    public String NamePath;
    public String Nodo;
    public boolean IsLeft;
    public int Level;

    private DataNamePath Parent;

    private HashMap<String, String> MapAtrributes;
    private List<DataNamePath> ChildNamePath;

    public DataNamePath() {
        this.MapAtrributes = new HashMap<>();
        this.ChildNamePath = new ArrayList<>();
    }

    public DataNamePath addChild() {
        DataNamePath namePath = new DataNamePath();
        namePath.Parent = this;
        ChildNamePath.add(namePath);
        return namePath;
    }

    public DataNamePath addChild(DataNamePath namePath) {
        namePath.Parent = this;
        ChildNamePath.add(namePath);
        return namePath;
    }


    public HashMap<String, String> getMapAtrributes() {
        return MapAtrributes;
    }

    public List<DataNamePath> getChildNamePath() {
        return ChildNamePath;
    }

    public void addAtrribute(String key, String value) {
        this.MapAtrributes.put(key, value);
    }


    public DataNamePath getParent() {
        return Parent;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}