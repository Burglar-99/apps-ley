package mx.com.azteca.home.model.ipati.dao;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mx.com.azteca.home.model.provider.HomeProvider;
import mx.com.azteca.home.model.ipati.pojo.Dataset;

public class DAODataset extends DAOObject {

    public DAODataset(Context context) {
        super(context);
    }

    public void guardar(Dataset dataset) {
        try {
            super.insert(dataset);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public Dataset getDetail(int idDataset) {
        try {
            Dataset dataset = new Dataset();
            dataset = (Dataset) super.select(dataset, null, "Identity = " + idDataset, null, null);
            return dataset;
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        return null;
    }

    public void delete(int idDataSet) {
        try {
            super.delete(Dataset.class, "Identity = ?", String.valueOf(idDataSet));
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void truncate() {
        try {
            super.delete(Dataset.class, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void dropTable(String table) {
        try {
            getContext().getContentResolver().query(Uri.parse(HomeProvider.PATH.concat("/").concat("STATEMENT")), null, "DROP TABLE IF EXISTS ".concat(table).concat(";"), null, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void createTable(String table) {
        try {
            getContext().getContentResolver().query(Uri.parse(HomeProvider.PATH.concat("/").concat("STATEMENT")), null, table, null, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public void insertRow(String insert) {
        try {
            getContext().getContentResolver().query(Uri.parse(HomeProvider.PATH.concat("/").concat("STATEMENT")), null, insert, null, null);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    public String getDatasetInfo(String table ) {
        Cursor cursor = null;
        String info = "null";
        try {
            List<HashMap<String, String>> listInfo = new ArrayList<>();
            cursor = getContext().getContentResolver().query(Uri.parse(HomeProvider.PATH.concat("/").concat("QUERY")), null, "SELECT * FROM ".concat(table), null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<>();
                        for (String column : cursor.getColumnNames()) {
                            map.put(column, cursor.getString(cursor.getColumnIndex(column)));
                        }
                        listInfo.add(map);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            Gson gson = new Gson();
            info = gson.toJson(listInfo);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return info;
    }

    public String getDatasetInfo(String table, String[] columns, String[] params) {
        Cursor cursor = null;
        String info = "null";
        try {
            List<HashMap<String, String>> listInfo = new ArrayList<>();
            String query = "SELECT * FROM ".concat(table) ;
            int lenght, contador = 0;
            if ((lenght = columns.length) > 0) {
                query += " WHERE ";
                for (String column : columns) {
                    query +=  column + " = ? " + (++contador <  lenght ? " AND " : "") ;
                }
            }
            cursor = getContext().getContentResolver().query(Uri.parse(HomeProvider.PATH.concat("/").concat("QUERY")), null, query, params, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        HashMap<String, String> map = new HashMap<>();
                        for (String column : cursor.getColumnNames()) {
                            map.put(column, cursor.getString(cursor.getColumnIndex(column)));
                        }
                        listInfo.add(map);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            Gson gson = new Gson();
            info = gson.toJson(listInfo);
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return info;
    }

    public String[] getDatasetInfoColumns(String table) {
        String[] columns = null;
        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(Uri.parse(HomeProvider.PATH.concat("/").concat("QUERY")), null, "SELECT * FROM ".concat(table).concat(" LIMIT 1"), null, null);
            if (cursor != null)
                columns = cursor.getColumnNames();
        }
        catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
        finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return columns;
    }

}
