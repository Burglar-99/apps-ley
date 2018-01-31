package mx.com.azteca.home.entity.generator;


import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.JsonParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;

import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;
import mx.com.azteca.home.entity.orm.IMappedSuperclass;
import mx.com.azteca.home.entity.orm.IOneToOne;


public class DaoGenerator {

    public String createTable(Class<?> object) {
        if (object.isAnnotationPresent(IEntity.class)) {
            IEntity entity = object.getAnnotation(IEntity.class);
            StringBuilder builder = new StringBuilder("CREATE TABLE ");
            builder.append(entity.table());
            builder.append(" (");

            if (object.getSuperclass()  != Object.class) {
                if (object.getSuperclass().isAnnotationPresent(IMappedSuperclass.class)) {
                    Class<?> superClass = object.getSuperclass();
                    createColumns(builder, superClass);
                }
            }
            createColumns(builder, object);
            if (entity.primaryKey().length > 0) {
                builder.append(" PRIMARY KEY (");
                for (String key : entity.primaryKey()) {
                    builder.append(" ");
                    builder.append(key);
                    builder.append(",");
                }
                builder.replace(builder.length() - 1, builder.length(), "));");
            }
            else {
                builder.replace(builder.length() - 1, builder.length(), ");");
            }
            return builder.toString();
        }
        return null;
    }

    public void createColumns(StringBuilder builder, Class<?> object) {
        for (Field field : object.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().isAssignableFrom(IColumn.class)) {
                    IColumn column = (IColumn)annotation;
                    builder.append(" ");
                    builder.append(column.name());
                    builder.append(" ");
                    builder.append(column.dataType());
                    if (!column.allowNull()) {
                        builder.append(" NOT NULL");
                    }
                    builder.append(",");
                }
                else if (annotation.annotationType().isAssignableFrom(IOneToOne.class)) {
                    /*
                    // TODO: Terminar la funcionalidad
                    IOneToOne column = (IOneToOne)annotation;
                    for (String reference : ((IOneToOne) annotation).joinColumn()) {
                        object.getAnnotation()
                    }
                    */
                }
            }
        }
    }

    public void loadContentValues(Object object, Class<?> classObject, ContentValues values) throws IllegalAccessException {
        for (Field field : classObject.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().isAssignableFrom(IColumn.class)) {
                    IColumn column = (IColumn)annotation;
                    if (field.getType().getSimpleName().equals("String")) {
                        if (field.get(object) != null) {
                            values.put(column.name(), field.get(object).toString());
                        }
                        else {
                            values.put(column.name(), "");
                        }
                    }
                    else if (field.getType().getSimpleName().equals("int")) {
                        values.put(column.name(), field.getInt(object));
                    }
                    else if (field.getType().getSimpleName().equals("double")) {
                        values.put(column.name(), field.getDouble(object));
                    }
                    else if (field.getType().getSimpleName().equals("float")) {
                        values.put(column.name(), field.getFloat(object));
                    }
                    else if (field.getType().getSimpleName().equals("long")) {
                        values.put(column.name(), field.getLong(object));
                    }
                    else if (field.getType().getSimpleName().equals("boolean")) {
                        values.put(column.name(), (field.getBoolean(object) ? 1 : 0));
                    }
                    else if (field.getType().getSimpleName().equals("Date")) {
                        values.put(column.name(), ((Date)field.get(object)).getTime());
                    }
                    else if (field.getType().getSimpleName().equals("JsonObject")) {
                        if (field.get(object) != null) {
                            values.put(column.name(), field.get(object).toString());
                        }
                        else {
                            values.put(column.name(), "{}");
                        }
                    }
                }
            }
        }
    }

    public void loadEntitiesValies(Cursor cursor, Class<?> type, Object object) throws IllegalAccessException {
        for (Field field : type.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().isAssignableFrom(IColumn.class)) {
                    IColumn column = (IColumn)annotation;
                    if (field.getType().getSimpleName().equals("String")) {
                        field.set(object, cursor.getString(cursor.getColumnIndex(column.name())));
                    }
                    else if (field.getType().getSimpleName().equals("int")) {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(column.name())));
                    }
                    else if (field.getType().getSimpleName().equals("double")) {
                        field.set(object, cursor.getDouble(cursor.getColumnIndex(column.name())));
                    }
                    else if (field.getType().getSimpleName().equals("float")) {
                        field.set(object, cursor.getFloat(cursor.getColumnIndex(column.name())));
                    }
                    else if (field.getType().getSimpleName().equals("long")) {
                        field.set(object, cursor.getLong(cursor.getColumnIndex(column.name())));
                    }
                    else if (field.getType().getSimpleName().equals("boolean")) {
                        field.set(object, cursor.getInt(cursor.getColumnIndex(column.name())) == 1);
                    }
                    else if (field.getType().getSimpleName().equals("Date")) {
                        long time = cursor.getLong(cursor.getColumnIndex(column.name()));
                        field.set(object, new Date(time));
                    }
                    else if (field.getType().getSimpleName().equals("JsonObject")) {
                        field.set(object, new JsonParser().parse(cursor.getString(cursor.getColumnIndex(column.name()))));
                    }
                }
            }
        }
    }

}
