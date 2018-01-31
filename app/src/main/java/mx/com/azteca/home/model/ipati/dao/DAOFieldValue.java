package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import mx.com.azteca.home.model.ipati.pojo.FieldValue;

public class DAOFieldValue extends DAOObject {


    public DAOFieldValue(Context context) {
        super(context);
    }


    public void guardar(FieldValue fieldValue) {
        try {
            super.insert(fieldValue);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public FieldValue getDetail(String idAlmacen, int idField) {
        try {
            FieldValue fieldValue = new FieldValue();
            fieldValue = (FieldValue) super.select(fieldValue, null, "IdAlmacen = ? AND IdField = ?", new String[]{idAlmacen, String.valueOf(idField)}, null);
            return fieldValue;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public void delete(String idAlmacen, int idField) {
        try {
            delete(FieldValue.class, "IdAlmacen = '" + idAlmacen + "' AND IdField = " + idField);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    @Override
    protected void truncate() {
        try {
            delete(FieldValue.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

}
