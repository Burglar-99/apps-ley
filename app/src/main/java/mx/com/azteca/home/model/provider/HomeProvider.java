package mx.com.azteca.home.model.provider;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.com.azteca.home.HomeApplication;
import mx.com.azteca.home.model.ipati.pojo.Configuracion;
import mx.com.azteca.home.model.ipati.pojo.Dataset;
import mx.com.azteca.home.model.ipati.pojo.DocumentInstance;
import mx.com.azteca.home.model.ipati.pojo.Documento;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.DocumentoWorkflow;
import mx.com.azteca.home.model.ipati.pojo.EventLog;
import mx.com.azteca.home.model.ipati.pojo.FieldValue;
import mx.com.azteca.home.model.ipati.pojo.Forma;
import mx.com.azteca.home.model.ipati.pojo.Formulario;
import mx.com.azteca.home.model.ipati.pojo.Persona;
import mx.com.azteca.home.model.ipati.pojo.Session;
import mx.com.azteca.home.model.ipati.pojo.TipoDato;
import mx.com.azteca.home.model.ipati.pojo.Usuario;
import mx.com.azteca.home.model.ipati.pojo.VersionInfo;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStepOption;
import mx.com.azteca.home.model.ipati.pojo.Workflow;
import mx.com.azteca.home.model.ipati.dao.DAOConfiguracion;
import mx.com.azteca.home.model.ipati.dao.DAOObject;

public class HomeProvider extends ContentProvider {

    private static final String DATABASE_NAME = "HomeBook.db";
    public static int DATABASE_VERSION = 6;
    private static final UriMatcher uriMatcher;
    private SQLiteDatabase dataBase;
    private static List<Class<?>> listEntidad;

    public static final String PATH;

    static  {
        listEntidad = new ArrayList<>();
        listEntidad.add(Session.class);
        listEntidad.add(Persona.class);
        listEntidad.add(Usuario.class);
        listEntidad.add(Configuracion.class);
        listEntidad.add(Documento.class);
        listEntidad.add(Formulario.class);
        listEntidad.add(Dataset.class);
        listEntidad.add(Forma.class);
        listEntidad.add(DocumentoAsignado.class);
        listEntidad.add(Workflow.class);
        listEntidad.add(mx.com.azteca.home.model.ipati.pojo.Field.class);
        listEntidad.add(DocumentInstance.class);
        listEntidad.add(FieldValue.class);
        listEntidad.add(EventLog.class);
        listEntidad.add(WorkFlowStep.class);
        listEntidad.add(WorkFlowStepOption.class);
        listEntidad.add(TipoDato.class);
        listEntidad.add(VersionInfo.class);
        listEntidad.add(DocumentoWorkflow.class);

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HomeApplication.class.getPackage().getName();
        PATH = "content://" + authority + "/";

        int contador = 0;
        for (int indice = 0; indice < listEntidad.size(); indice++) {
            uriMatcher.addURI(authority, listEntidad.get(indice).getSimpleName(), contador);
            contador ++;
        }

        for (Map.Entry<String, DAOObject.Selector> entry : DAOObject.SELECTORS.entrySet()) {
            DAOObject.Selector value = entry.getValue();
            uriMatcher.addURI(authority, value.getKey(), contador);
            contador++;
        }

        uriMatcher.addURI(authority, "STATEMENT", 100);
        uriMatcher.addURI(authority, "QUERY", 101);

    }

    @Override
    public boolean onCreate() {
        LocationDbHelper dbHelper = new LocationDbHelper(getContext(), DATABASE_NAME, null);
        this.dataBase = dbHelper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException(this.getClass().getName() + "Undefined uri");
        }
        else {
            if (uri.getPathSegments().size() == 1) {

                switch (match) {
                    case 100:
                        dataBase.execSQL(selection);
                        return null;
                    case 101: {
                        Cursor cursor = dataBase.rawQuery(selection, selectionArgs);
                        cursor.setNotificationUri(getContext().getContentResolver(), uri);
                        return cursor;
                    }
                    default:
                        Class<?> entity = listEntidad.get(match);
                        Cursor cursor = dataBase.query(entity.getSimpleName(), projection, selection, selectionArgs, null, null, sortOrder);
                        cursor.setNotificationUri(getContext().getContentResolver(), uri);
                        return cursor;
                }
            }
            else if (uri.getPathSegments().size() == 2) {
                String key = uri.getPathSegments().get(0).concat("/").concat(uri.getPathSegments().get(1));
                DAOObject.Selector selector = DAOConfiguracion.SELECTORS.get(key);
                Cursor cursor = dataBase.rawQuery(selector.Statement, selectionArgs);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            else {
                throw new IllegalArgumentException(this.getClass().getName() + "Undefined uri");
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException(this.getClass().getName() + "Undefined uri");
        }
        else {
            Class<?> entity = listEntidad.get(match);
            return entity.getName();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException(this.getClass().getName() + "Undefined uri");
        }
        else {
            Class<?> entity = listEntidad.get(match);
            long identity = dataBase.insert(entity.getSimpleName(), null, values);
            if (identity > 0) {
                Uri inserted = ContentUris.withAppendedId(uri, identity);
                getContext().getContentResolver().notifyChange(inserted, null);
                return inserted;
            }
            else {
                throw new SQLiteException(this.getClass().getName() + " Error inserting " + uri);
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException(this.getClass().getName() + " Undefined uri");
        }
        else {
            Class<?> entity = listEntidad.get(match);
            ContentResolver contentResolver = getContext().getContentResolver();
            int updated = dataBase.delete(entity.getSimpleName(), selection, selectionArgs);
            contentResolver.notifyChange(uri, null);
            return updated;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException(this.getClass().getName() + "Undefined uri");
        }
        else {
            ContentResolver contentResolver = getContext().getContentResolver();
            if (uri.getPathSegments().size() == 1) {
                Class<?> entity = listEntidad.get(match);
                int updated = dataBase.update(entity.getSimpleName(), values, selection, selectionArgs);
                contentResolver.notifyChange(uri, null);
                return updated;
            }
            else if (uri.getPathSegments().size() == 2) {
                String key = uri.getPathSegments().get(0).concat("/").concat(uri.getPathSegments().get(1));
                DAOObject.Selector selector = DAOConfiguracion.SELECTORS.get(key);
                dataBase.execSQL(selector.Statement, selectionArgs);
                contentResolver.notifyChange(uri, null);
                return 0;
            }
            else {
                throw new IllegalArgumentException(this.getClass().getName() + "Undefined uri");
            }
        }
    }

    private static class LocationDbHelper extends SQLiteOpenHelper {

        private Context context;

        private LocationDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            for (Class<?> entity : listEntidad) {
                database.execSQL("DROP TABLE IF EXISTS " + entity.getSimpleName() + ";");
                createTable(database, entity);
            }
            database.execSQL("DROP TABLE IF EXISTS Estados;");
            database.execSQL("DROP TABLE IF EXISTS Colonias;");
            database.execSQL("DROP TABLE IF EXISTS Municipios;");
            database.execSQL("DROP TABLE IF EXISTS Poblaciones;");
            defaulInsert(database);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            for (Class<?> entity : listEntidad) {
                createTable(database, entity);
            }
            defaulInsert(database);
        }

        private void createTable(SQLiteDatabase db, Class<?> entity) {
            String statement = "CREATE TABLE " + entity.getSimpleName() + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,";
            for (Field field : entity.getFields()) {
                statement  += field.getName() + " " + getDataType(field.getType().getSimpleName()) + ",";
            }
            statement = (statement.substring(0, statement.length() - 1)) + ");";
            db.execSQL(statement);
        }

        private void defaulInsert(SQLiteDatabase db) {

            db.execSQL( "CREATE TABLE Estados (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " IdEstado INT, " +
                    " Nombre TEXT, " +
                    " IdPais INT, " +
                    " CodigoPostal TEXT);"
            );

            db.execSQL( "CREATE TABLE Colonias (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " IdColonia INT," +
                    " Nombre TEXT," +
                    " IdMunicipio INT," +
                    " CodigoPostal TEXT);"
            );

            db.execSQL("CREATE TABLE Municipios (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " IdMunicipio INT," +
                    " Nombre TEXT," +
                    " IdEstado INT," +
                    " CodigoPostal TEXT );"
            );

            db.execSQL("CREATE TABLE Poblaciones (" +
                    " _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " IdPoblacon INT," +
                    " Nombre TEXT," +
                    " IdMunicipio INT);"
            );

            for (Object object : DAOObject.INSERTS) {
                try {
                    String insert = DAOObject.createInsert(object);
                    db.execSQL(insert);
                }
                catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
            }
        }

        private String getDataType(String type) {
            switch (type) {
                case "int":
                    return type;
                case "long":
                    return "int";
                case "double":
                    return "real";
                case "boolean":
                    return "int";
                default:
                    return "TEXT";
            }
        }

        private void loadDefaultInfo(SQLiteDatabase db, String directory) throws Exception {
            String[] dataColonias = context.getAssets().list(directory);
            for (String colonias : dataColonias) {
                if (directory.equals("data-estados")) {
                    Log.e("Assests", colonias);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(directory + "/" + colonias)));
                String line = reader.readLine();
                while (line != null) {
                    db.execSQL(line);
                    line = reader.readLine();
                }
                reader.close();
            }
        }

    }
}
