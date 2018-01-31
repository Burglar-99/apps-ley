package mx.com.azteca.home.model.ipati.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mx.com.azteca.home.model.provider.HomeProvider;
import mx.com.azteca.home.model.ipati.pojo.Configuracion;
import mx.com.azteca.home.model.ipati.pojo.Documento;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.Usuario;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.Workflow;


public abstract class DAOObject {

    public static final HashMap<String, Selector> SELECTORS;
    public static final List<Object> INSERTS;

    static {

        Selector selLogin = new Selector(Usuario.class,
                "LogIn",
                "SELECT * FROM Usuario WHERE UserName = ? AND Password = ?");

        Selector selSteps = new Selector(WorkFlowStep.class,
                "Steps",
                " SELECT W.ID AS IdWorkflowStep, W.Tag, COUNT(DISTINCT A.Folio) Cant" +
                        " FROM WorkFlowStep W" +
                        " LEFT JOIN DocumentoAsignado A" +
                        " ON A.IdWorkflowStep = W.ID " +
                        " WHERE W.IdDocumento = ? AND W.Version = ? " +
                        " GROUP BY W.ID, W.Orden, W.Tag" +
                        " ORDER BY W.Orden");

        Selector selStepsOffline = new Selector(WorkFlowStep.class,
                "StepsOffline",
                " SELECT W.IdWorkflowStep, W.Tag, COUNT(DISTINCT A.Folio) Cant" +
                        " FROM WorkFlowStep W" +
                        " INNER JOIN DocumentoAsignado A" +
                        " ON A.IdWorkflowStep = W.IdWorkflowStep AND A.IdDocumento = W.IdDocumento" +
                        " where W.IdDocumento = ? AND W.Version = ? AND W.Offline = 1 " +
                        " GROUP BY W.IdWorkflowStep, W.Orden, W.Tag" +
                        " ORDER BY W.Orden");


        Selector nextStep = new Selector(Workflow.class,
                "NextStep",
                " SELECT * " +
                        " FROM Workflow " +
                        " WHERE IdDocumento = ? AND IdWorkflowStep > ? ORDER BY Orden LIMIT 1 ");

        Selector dataUserLogin = new Selector(Usuario.class, "DataUserLogin",
                "SELECT UserName, Password FROM Usuario WHERE Identity = ?");

        Selector documentosCompletos = new Selector(DocumentoAsignado.class,
                "DocCompleted",
                " SELECT * " +
                        " FROM DocumentoAsignado D " +
                        " WHERE IdWorkflowStep = " +
                        " ( " +
                        "   SELECT IdWorkflowStep " +
                        "   FROM Workflow " +
                        "   WHERE IdDocumento = D.IdDocumento AND Offline = 0 AND Version = D.Version AND IdWorkflowStep >= D.IdWorkflowStep ORDER BY Orden LIMIT 1 " +
                        " ) "
        );

        Selector documentosModificados = new Selector(DocumentoAsignado.class,
                "DocUpdated",
                " SELECT * " +
                        " FROM DocumentoAsignado D " +
                        " WHERE LastUpdate > 0 AND LastUpdate >  (SELECT CAST(Value AS INTEGER) FROM Configuracion WHERE Path = 'Device' AND Name = 'Sincronizacion') "
        );

        Selector documentosOffline = new Selector(Documento.class,
                "DocOffline",
                " SELECT DISTINCT D.* " +
                        " FROM Documento D " +
                        " INNER JOIN DocumentoAsignado DA " +
                        " ON DA.IdDocumento = D.Identity AND D.Version = DA.Version " +
                        " INNER JOIN WorkFlowStep W " +
                        " ON W.IdDocumento = DA.IdDocumento AND W.Version = DA.Version AND W.IdWorkflowStep = DA.IdWorkflowStep " +
                        " WHERE W.Offline = 1"
        );

        SELECTORS = new HashMap<>();
        SELECTORS.put(selLogin.getKey(), selLogin);
        SELECTORS.put(selSteps.getKey(), selSteps);
        SELECTORS.put(selStepsOffline.getKey(), selStepsOffline);
        SELECTORS.put(nextStep.getKey(), nextStep);
        SELECTORS.put(dataUserLogin.getKey(), dataUserLogin);
        SELECTORS.put(documentosCompletos.getKey(), documentosCompletos);
        SELECTORS.put(documentosModificados.getKey(), documentosModificados);
        SELECTORS.put(documentosOffline.getKey(), documentosOffline);

        INSERTS = new ArrayList<>();
        INSERTS.add(new Configuracion("Device", "Bloqueado", "true"));
        INSERTS.add(new Configuracion("Device", "Sincronizacion", "0"));
        INSERTS.add(new Configuracion("Device", "Usuario", "0"));
        INSERTS.add(new Configuracion("Device", "Empresa", "0"));
        INSERTS.add(new Configuracion("Device", "Sucursal", "0"));
        INSERTS.add(new Configuracion("Device", "NomEmpresa", ""));
        INSERTS.add(new Configuracion("Device", "NomSucursal", ""));
    }

    private Context context;

    public DAOObject(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public static String createInsert(Object entity) throws IllegalAccessException {
        String statement = "INSERT INTO " + entity.getClass().getSimpleName() + " (";
        for (Field field : entity.getClass().getFields()) {
            statement  += field.getName() + ",";
        }
        statement = (statement.substring(0, statement.length() - 1)) + ") VALUES (";
        for (Field field : entity.getClass().getFields()) {
            if (!(field.getType().getSimpleName().equals("int") || field.getType().getSimpleName().equals("double"))) {
                if (field.get(entity) != null) {
                    statement  += "'" + field.get(entity).toString() + "',";
                }
            }
            else {
                if (field.get(entity) != null) {
                    statement  += field.get(entity).toString() + ",";
                }
            }
        }
        statement = (statement.substring(0, statement.length() - 1)) + ");";
        return statement;
    }

    protected Uri insert(Object object) throws IllegalAccessException {
        ContentValues values = getContentValues(object);
        return context.getContentResolver().insert(Uri.parse(HomeProvider.PATH + object.getClass().getSimpleName()), values);
    }

    protected int update(Object object, String where, String ... selectionArgs) throws IllegalAccessException {
        ContentValues values = getContentValues(object);
        return context.getContentResolver().update(Uri.parse(HomeProvider.PATH + object.getClass().getSimpleName()), values, where, selectionArgs);
    }

    protected int delete(Class<?> object, String where, String ... selectionArgs) {
        return context.getContentResolver().delete(Uri.parse(HomeProvider.PATH + object.getSimpleName()), where, selectionArgs);
    }

    protected List<Object> select(Class<?> object, String[] projection, String selection, String[] selectionArgs, String sortOrder) throws IllegalAccessException, InstantiationException {
        List<Object> results = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse(HomeProvider.PATH + object.getSimpleName()), projection, selection, selectionArgs, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Object result = object.newInstance();
                for (Field field : result.getClass().getFields()) {
                    if (field.getType().getSimpleName().equals("int")) {
                        field.set(result, cursor.getInt(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("String")) {
                        field.set(result, cursor.getString(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("double")) {
                        field.set(result, cursor.getDouble(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("float")) {
                        field.set(result, cursor.getFloat(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("long")) {
                        field.set(result, cursor.getLong(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("boolean")) {
                        field.set(result, cursor.getInt(cursor.getColumnIndex(field.getName())) == 1);
                    }
                    else if (field.getType().getSimpleName().equals("Object")) {
                        field.set(result, cursor.getString(cursor.getColumnIndex(field.getName())));
                    }
                }
                results.add(result);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return results;
    }

    protected Object select(Object object, String[] projection, String selection, String[] selectionArgs, String sortOrder) throws IllegalAccessException, InstantiationException {
        Cursor cursor = context.getContentResolver().query(Uri.parse(HomeProvider.PATH + object.getClass().getSimpleName()), projection, selection, selectionArgs, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                for (Field field : object.getClass().getFields()) {
                    if (field.getType().getSimpleName().equals("int")) {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("String")) {
                        field.set(object, cursor.getString(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("double")) {
                        field.set(object, cursor.getDouble(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("float")) {
                        field.set(object, cursor.getFloat(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("long")) {
                        field.set(object, cursor.getLong(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("boolean")) {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(field.getName())) == 1);
                    }
                    else if (field.getType().getSimpleName().equals("Object")) {
                        field.set(object, cursor.getString(cursor.getColumnIndex(field.getName())));
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        else {
            object = null;
        }
        return object;
    }


    protected Object selectWithStatement(Object object, String statement, String[] selectionArgs) throws IllegalAccessException, InstantiationException {
        Cursor cursor = context.getContentResolver().query(Uri.parse(HomeProvider.PATH + object.getClass().getSimpleName().concat("/").concat(statement)), null, null, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                for (Field field : object.getClass().getFields()) {
                    if (field.getType().getSimpleName().equals("int")) {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("String")) {
                        field.set(object, cursor.getString(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("double")) {
                        field.set(object, cursor.getDouble(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("float")) {
                        field.set(object, cursor.getFloat(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("long")) {
                        field.set(object, cursor.getLong(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("boolean")) {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(field.getName())) == 1);
                    }
                    else if (field.getType().getSimpleName().equals("Object")) {
                        field.set(object, cursor.getString(cursor.getColumnIndex(field.getName())));
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        else {
            object = null;
        }

        return object;
    }

    protected List<Object> selectWithStatement(Class<?> object, String statement, String[] selectionArgs) throws IllegalAccessException, InstantiationException {
        List<Object> results = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Uri.parse(HomeProvider.PATH + object.getSimpleName().concat("/").concat(statement)), null, null, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Object result = object.newInstance();
                for (Field field : result.getClass().getFields()) {
                    if (field.getType().getSimpleName().equals("int")) {
                        field.set(result, cursor.getInt(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("String")) {
                        field.set(result, cursor.getString(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("double")) {
                        field.set(result, cursor.getDouble(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("float")) {
                        field.set(result, cursor.getFloat(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("long")) {
                        field.set(result, cursor.getLong(cursor.getColumnIndex(field.getName())));
                    }
                    else if (field.getType().getSimpleName().equals("boolean")) {
                        field.set(result, cursor.getInt(cursor.getColumnIndex(field.getName())) == 1);
                    }
                    else if (field.getType().getSimpleName().equals("Object")) {
                        field.set(result, cursor.getString(cursor.getColumnIndex(field.getName())));
                    }
                }
                results.add(result);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return results;
    }

    protected List<HashMap<String, Object>> select(Class<?> object, String statement, String[] selectionArgs) {
        Cursor cursor = context.getContentResolver().query(Uri.parse(HomeProvider.PATH + object.getSimpleName().concat("/").concat(statement)), null, null, selectionArgs, null);
        List<HashMap<String, Object>> listResult = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                HashMap<String, Object> resultKeyValue = new HashMap<>();
                for (String column : cursor.getColumnNames()) {
                    if (cursor.getType(cursor.getColumnIndex(column)) == Cursor.FIELD_TYPE_INTEGER) {
                        resultKeyValue.put(column, cursor.getInt(cursor.getColumnIndex(column)));
                    }
                    else if (cursor.getType(cursor.getColumnIndex(column)) == Cursor.FIELD_TYPE_STRING) {
                        resultKeyValue.put(column, cursor.getString(cursor.getColumnIndex(column)));
                    }
                    else if (cursor.getType(cursor.getColumnIndex(column)) == Cursor.FIELD_TYPE_FLOAT) {
                        resultKeyValue.put(column, cursor.getDouble(cursor.getColumnIndex(column)));
                    }
                }

                listResult.add(resultKeyValue);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return  listResult;
    }

    protected int count(Class<?> object, String[] projection, String selection, String[] selectionArgs, String sortOrder) throws IllegalAccessException, InstantiationException {
        Cursor cursor = context.getContentResolver().query(Uri.parse(HomeProvider.PATH + object.getSimpleName()), projection, selection, selectionArgs, sortOrder);
        int rows = cursor.getCount();
        cursor.close();
        return rows;
    }

    private ContentValues getContentValues(Object object) throws IllegalAccessException {
        ContentValues values = new ContentValues();
        for (Field field : object.getClass().getFields()) {
            if (field.getType().getSimpleName().equals("int")) {
                values.put(field.getName(), field.getInt(object));
            }
            else if (field.getType().getSimpleName().equals("String")) {
                if (field.get(object) != null) {
                    values.put(field.getName(), field.get(object).toString());
                }
                else {
                    values.put(field.getName(), "");
                }
            }
            else if (field.getType().getSimpleName().equals("double")) {
                values.put(field.getName(), field.getDouble(object));
            }
            else if (field.getType().getSimpleName().equals("float")) {
                values.put(field.getName(), field.getFloat(object));
            }
            else if (field.getType().getSimpleName().equals("long")) {
                values.put(field.getName(), field.getLong(object));
            }
            else if (field.getType().getSimpleName().equals("boolean")) {
                values.put(field.getName(), (field.getBoolean(object) ? 1 : 0));
            }
            else if (field.getType().getSimpleName().equals("Object")) {
                Object data = field.get(object);
                if (data != null && data instanceof String) {
                    values.put(field.getName(), data.toString());
                }
                else {
                    values.put(field.getName(), "");
                }
            }
        }
        return values;
    }

    public void executeSQL(String statement) {
        Cursor cursor = getContext().getContentResolver().query(Uri.parse(HomeProvider.PATH.concat("/").concat("QUERY")), null, statement, null, null);
        if (cursor != null) {
            cursor.close();
        }
    }


    protected abstract void truncate();

    public static class Selector {

        public Class<?> Enttity;
        public String Name;
        public String Statement;

        public Selector(Class<?> entity, String name, String statement) {
            this.Enttity = entity;
            this.Name = name;
            this.Statement = statement;
        }
        public String getKey() {
            return Enttity.getSimpleName().concat("/").concat(Name);
        }

    }

}
