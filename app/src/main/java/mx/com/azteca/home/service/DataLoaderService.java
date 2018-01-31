package mx.com.azteca.home.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mx.com.azteca.home.HomeApplication;
import mx.com.azteca.home.model.ipati.dao.DAODataset;
import mx.com.azteca.home.util.PreferenceDevice;

public class DataLoaderService extends IntentService {

    public static boolean DATA_LOADING = false;
    public static boolean DATA_LOADED = false;

    private SharedPreferences sharedPreferences;

    public DataLoaderService() {
        this("DataLoader");
    }

    public DataLoaderService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DataLoaderService.DATA_LOADING = true;
        try {
            if (this.sharedPreferences == null) {
                this.sharedPreferences = this.getSharedPreferences(HomeApplication.class.getPackage().getName(), Context.MODE_PRIVATE);
            }
            DataLoaderService.DATA_LOADED = (boolean) getData(PreferenceDevice.DEFAULT_DATA_LOADED.Key, false);
            if (!DataLoaderService.DATA_LOADED) {
                ExecutorService executor = Executors.newFixedThreadPool(4);
                // Estados
                executor.submit(() ->{
                    try {
                        DAODataset daoDataset = new DAODataset(getBaseContext());
                        daoDataset.executeSQL("DELETE FROM Estados");
                        loadDefaultInfo(daoDataset, "data-estados");
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                // Municipios
                executor.submit(() ->{
                    try {
                        DAODataset daoDataset = new DAODataset(getBaseContext());
                        daoDataset.executeSQL("DELETE FROM Municipios");
                        loadDefaultInfo(daoDataset, "data-municipios");
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                // Poblaciones
                executor.submit(() ->{
                    try {
                        DAODataset daoDataset = new DAODataset(getBaseContext());
                        daoDataset.executeSQL("DELETE FROM Poblaciones");
                        loadDefaultInfo(daoDataset, "data-poblaciones");
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                // Colonias
                executor.submit(() ->{
                    try {
                        DAODataset daoDataset = new DAODataset(getBaseContext());
                        daoDataset.executeSQL("DELETE FROM Colonias");
                        loadDefaultInfo(daoDataset, "data-colonias");
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                executor.shutdown();
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                setData(PreferenceDevice.DEFAULT_DATA_LOADED.Key, true);
                DataLoaderService.DATA_LOADED = true;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            DATA_LOADING = false;
        }
    }

    private void loadDefaultInfo(DAODataset dataSet, String directory) throws Exception {
        String[] dataInfo = getBaseContext().getAssets().list(directory);
        for (String info : dataInfo) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getBaseContext().getAssets().open(directory + "/" + info)));
            String line = reader.readLine();
            while (line != null) {
                dataSet.executeSQL(line);
                line = reader.readLine();
            }
            reader.close();
        }
    }

    public Object getData(String key) {
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getAll().get(key);
        }
        return null;
    }

    public Object getData(String key, Object defaul) {
        Object object = getData(key);
        return object == null ? defaul : object;
    }

    public void setData(String key, Object value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        if (value instanceof  String) {
            edit.putString(key, value.toString());
        }
        else if (value.getClass().getSimpleName().equals("int") || value instanceof Integer) {
            edit.putInt(key, (int)value);
        }
        else if (value.getClass().getSimpleName().equals("boolean") || value instanceof Boolean) {
            edit.putBoolean(key, (boolean)value);
        }
        else if (value.getClass().getSimpleName().equals("float") || value instanceof Float) {
            edit.putFloat(key, (float)value);
        }
        else if (value.getClass().getSimpleName().equals("long")  || value instanceof Long) {
            edit.putLong(key, (long)value);
        }
        boolean commit = edit.commit();
        if (commit) {
            edit.apply();
        }
    }
}
