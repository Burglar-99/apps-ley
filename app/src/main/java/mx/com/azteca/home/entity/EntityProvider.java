package mx.com.azteca.home.entity;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mx.com.azteca.home.entity.generator.DaoGenerator;
import mx.com.azteca.home.entity.generator.DatabaseAccess;


public class EntityProvider {

    private int version = 1;
    private String databaseName = "DataBase";

    private static EntityProvider PROVIDER;

    private HashMap<Class<?>, String> mapInsert;
    private Set<Class<?>> entities;

    private DaoGenerator daoGenerator;
    private SQLiteDatabase database;


    private DatabaseAccess databaseAccess;

    private EntityProvider() {
        this.daoGenerator = new DaoGenerator();
        this.mapInsert = new HashMap<>();
        this.entities = new HashSet<>();
    }

    private synchronized static EntityProvider initialize() {
        if (EntityProvider.PROVIDER == null) {
            EntityProvider.PROVIDER = new EntityProvider();
        }
        return EntityProvider.PROVIDER;
    }

    public static synchronized int getVersion() {
        return EntityProvider.initialize().version;
    }

    public static void setVersion(int version) {
        EntityProvider.initialize().version = version;
    }

    public static synchronized String getDatabaseName() {
        return EntityProvider.initialize().databaseName;
    }

    public static synchronized void setDatabaseName(String databaseName) {
        EntityProvider.initialize().databaseName = databaseName;
    }

    public static synchronized void setDatabase(SQLiteDatabase database) {
        EntityProvider.initialize().database = database;
    }

    public static synchronized Set<Class<?>> getEntities() {
        return EntityProvider.initialize().entities;
    }

    public static synchronized DaoGenerator getDaoGenerator() {
        return EntityProvider.initialize().daoGenerator;
    }

    public static void newSession(Context context) {
        DatabaseAccess access = new DatabaseAccess(context, null);
        EntityProvider.initialize().database = access.getWritableDatabase();
    }

    public static SQLiteDatabase getDatabase() {
        return EntityProvider.initialize().database;
    }
}
