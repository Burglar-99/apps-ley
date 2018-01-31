package mx.com.azteca.home.entity.generator;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.entity.BaseEntity;
import mx.com.azteca.home.entity.EntityProvider;
import mx.com.azteca.home.entity.orm.IEntity;
import mx.com.azteca.home.entity.orm.IMappedSuperclass;
import mx.com.azteca.home.entity.orm.IOneToOne;


public class DaoObject<T> {

    private QueryBuilder queryWhere;


    public void insert(T t) throws Exception {
        if (t.getClass().isAnnotationPresent(IEntity.class)) {
            ContentValues contentValues = new ContentValues();
            if (t.getClass().getSuperclass().isAnnotationPresent(IMappedSuperclass.class)) {
                EntityProvider.getDaoGenerator().loadContentValues(t, t.getClass().getSuperclass(), contentValues);
            }
            EntityProvider.getDaoGenerator().loadContentValues(t, t.getClass(), contentValues);
            IEntity entity = t.getClass().getAnnotation(IEntity.class);
            long insert = EntityProvider.getDatabase().insertOrThrow(entity.table(), null, contentValues);
            if (insert == -1)
                throw new Exception("Error to insert row in table");
        }
    }

    /*
    public void insert(T parent, T child, String fieldName) throws Exception {
        if (t.getClass().isAnnotationPresent(IEntity.class)) {
            ContentValues contentValues = new ContentValues();
            if (t.getClass().getSuperclass().isAnnotationPresent(IMappedSuperclass.class)) {
                EntityProvider.getDaoGenerator().loadContentValues(t, t.getClass().getSuperclass(), contentValues);
            }
            EntityProvider.getDaoGenerator().loadContentValues(t, t.getClass(), contentValues);
            if (parent != null) {
                if (parentID != null && parent.getClass().getField("fieldName").isAnnotationPresent(IOneToOne.class)) {
                    if (! (parent.getClass().getField("fieldName").getAnnotation(IOneToOne.class).joinColumn().length  > 0) ) {
                        contentValues.put("_RefenrenceEntityID", parentID.toString());
                    }
                }
            }
            IEntity entity = child.getClass().getAnnotation(IEntity.class);
            long insert = EntityProvider.getDatabase().insertOrThrow(entity.table(), null, contentValues);
            if (insert == -1)
                throw new Exception("Error to insert row in table");
        }
    }
    */

    public void update(T t) throws Exception {
        if (t.getClass().isAnnotationPresent(IEntity.class)) {
            ContentValues contentValues = new ContentValues();
            if (t.getClass().getSuperclass().isAnnotationPresent(IMappedSuperclass.class)) {
                EntityProvider.getDaoGenerator().loadContentValues(t, t.getClass().getSuperclass(), contentValues);
            }
            EntityProvider.getDaoGenerator().loadContentValues(t, t.getClass(), contentValues);
            IEntity entity = t.getClass().getAnnotation(IEntity.class);
            int rows = EntityProvider.getDatabase().update(entity.table(), contentValues, getWhereKeys(), getWhereValues());
            Log.i(getClass().getSimpleName(), "Number of rows affected " + rows);
        }
    }

    public void delete(Class<?> entidad) throws Exception {
        if (entidad.isAnnotationPresent(IEntity.class)) {
            IEntity entity = entidad.getAnnotation(IEntity.class);
            int rows = EntityProvider.getDatabase().delete(entity.table(), getWhereKeys(), getWhereValues());
            Log.i(getClass().getSimpleName(), "Number of rows affected " + rows);
        }
    }

    public List<T> select(Class<?> type) throws Exception {
        return select(type, false);
    }

    public List<T> select(Class<?> type, boolean single) throws Exception {
        if (type.isAnnotationPresent(IEntity.class)) {
            List<T> entities = new ArrayList<>();
            IEntity entity = type.getAnnotation(IEntity.class);
            Cursor cursor = EntityProvider.getDatabase().query(entity.table(), null, getWhereKeys(), getWhereValues(), null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    Object object = type.newInstance();
                    if (type.getSuperclass().isAnnotationPresent(IMappedSuperclass.class)) {
                        EntityProvider.getDaoGenerator().loadEntitiesValies(cursor, type.getSuperclass(), object);
                    }
                    EntityProvider.getDaoGenerator().loadEntitiesValies(cursor, type, object);
                    entities.add((T) object);
                    if (single)
                        break;
                } while (cursor.moveToNext());
            }
            cursor.close();
            return entities;
        }
        return null;
    }

    public T single(Class<?> type) throws Exception {
        List<T> rows = select(type, true);
        if (rows.size() > 1)
            throw new Exception("Number of rows is not unit single");
        else if (rows.size() == 0)
            return null;
        return rows.get(0);
    }

    public QueryBuilder where(String key, Object value) {
        this.queryWhere = new QueryBuilder();
        this.queryWhere.and(key, value);
        return queryWhere;
    }

    private String getWhereKeys() throws Exception {
        if (queryWhere != null && queryWhere.getQueryConditions().size() > 0) {
            StringBuilder stringBuilder = null;
            for (QueryBuilder.QueryField queryField : queryWhere.getQueryConditions()) {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(queryField.key);
                    stringBuilder.append(" =  ? ");
                }
                else {
                    switch (queryField.condition) {
                        case AND:
                            stringBuilder.append(" AND ");
                            stringBuilder.append(queryField.key);
                            stringBuilder.append(" =  ? ");
                            break;
                        case OR:
                            stringBuilder.append(" OR ");
                            stringBuilder.append(queryField.key);
                            stringBuilder.append(" =  ? ");
                            break;
                        case IN:
                            throw new Exception("Undefined condition");
                        default:
                            throw new Exception("Undefined condition");
                    }
                }
            }
            if (stringBuilder != null)
                return stringBuilder.toString();
        }
        return null;
    }

    private String[] getWhereValues() throws Exception {
        if (queryWhere != null && queryWhere.getQueryConditions().size() > 0) {
            if (queryWhere != null && queryWhere.getQueryConditions().size() > 0) {
                List<String> listValues = new ArrayList<>();
                for (QueryBuilder.QueryField queryField : queryWhere.getQueryConditions()) {
                    switch (queryField.condition) {
                        case AND:
                        case OR:
                            listValues.add(queryField.value.toString());
                            break;
                        case IN:
                            throw new Exception("Undefined condition");
                        default:
                            throw new Exception("Undefined condition");
                    }
                }
                String[] values = new String[listValues.size()];
                return listValues.toArray(values);
            }
        }
        return null;
    }

    public void clearConditions() {
        this.queryWhere = null;
    }

}
