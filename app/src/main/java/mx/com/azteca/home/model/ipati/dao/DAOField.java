package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.model.ipati.pojo.Field;

public class DAOField  extends DAOObject {

    public DAOField(Context context) {
        super(context);
    }

    public void guardar(Field field) {
        try {
            super.insert(field);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public Field getDetail(int idField) {
        try {
            Field field = new Field();
            field = (Field) super.select(field, null, "Identity = " + idField, null, null);
            return field;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public Field getFieldByPadre(int idPadre) {
        try {
            Field field = new Field();
            field = (Field) super.select(field, null, "IdPadre = " + idPadre, null, null);
            return field;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public List<Field> loadFieldByDocumento(int idDocumento, int version) {
        List<Field> listField = new ArrayList<>();
        try {
            List fields = (List) super.select(Field.class, null, "IdDocumento = ? AND Version = ?", new String[] {String.valueOf(idDocumento), String.valueOf(version)}, null);
            if (fields != null) {
                listField.addAll(fields);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listField;
    }

    public void update(Field field) {
        try {
            super.update(field, "Identity = ?", String.valueOf(field.Identity));
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
    }

    public boolean exist(int idField) {
        try {
            return super.count(Field.class, null, "Identity = ?", new String[]{String.valueOf(idField)}, null) > 0;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return false;
    }

    public void truncate() {
        try {
            super.delete(Field.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public List<Field> getCollecciones() {
        List<Field> listField = new ArrayList<>();
        try {
            List fields = (List) super.select(Field.class, null, "Coleccion = 1", null, null);
            if (fields != null) {
                listField.addAll(fields);
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return listField;
    }

}
