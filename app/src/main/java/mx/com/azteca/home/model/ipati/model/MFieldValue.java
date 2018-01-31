package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOFieldValue;
import mx.com.azteca.home.model.ipati.pojo.FieldValue;

public class MFieldValue extends BaseModel {

    private DAOFieldValue daoFieldValue;

    public MFieldValue(Context context) {
        super(context);
    }

    public MFieldValue(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        this.daoFieldValue = new DAOFieldValue(getContext());
    }

    public void guardar(FieldValue fieldValue) {
        this.daoFieldValue.guardar(fieldValue);
    }

    public void delete(String idAlmacen, int idField) {
        this.daoFieldValue.delete(idAlmacen, idField);
    }
}
