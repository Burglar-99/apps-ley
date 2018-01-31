package mx.com.azteca.home.util;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHandler {

    public String json;
    public JsonObject jObject;

    public Object getValue(String path) {
        return getValue(path, "System.String");
    }

    public void setJson(String json) {
        this.json = json;
        JsonParser jsonParser = new JsonParser();
        this.jObject = jsonParser.parse(json).getAsJsonObject();
    }

    public String getJson() {
        return jObject.toString();
    }

    public Object getValue(String path, String type) {
        String key = getKey(path);
        if (key != null) {
            JsonObject jsonObject = getJsonObject(path);
            switch(type) {
                case "System.Int32": {

                    int value = 0;
                    try {
                        value = jsonObject.get(key).getAsInt();
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }

                    return value;
                }
                case "System.Int64": {

                    long value = 0;
                    try {
                        value = jsonObject.get(key).getAsLong();
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }

                    return value;
                }
                case "System.Decimal": {
                    double value = 0;
                    try {
                        value = jsonObject.get(key).getAsDouble();
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }
                    return value;
                }
                case "System.Boolean": {
                    boolean value = false;
                    try {
                        value = jsonObject.get(key).getAsBoolean();
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }
                    return value;
                }
                default:
                    JsonElement jElement = jsonObject.get(key);
                    if (jElement.isJsonObject()) {
                        return jElement.toString();
                    }
                    else {
                        return jsonObject.get(key).getAsString();
                    }

            }
        }
        return null;
    }

    public void setValue(String path, Object value) {
        this.setValue(path, value, "System.String");
    }

    public void setValue(String path, Object value, String type) {
        String key = getKey(path);
        JsonObject jsonObject = getJsonObject(path);
        if (key != null && jsonObject != null) {

            if (value instanceof JsonElement) {
                jsonObject.add(key, (JsonElement)value);
            }
            else {
                switch(type) {
                    case "System.Int32": {
                        int valueConverted = 0;
                        try {
                            valueConverted = Integer.parseInt(value.toString());
                        }
                        catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                        finally {
                            jsonObject.addProperty(key, valueConverted);
                        }
                        break;
                    }
                    case "System.Int64": {
                        long valueConverted = 0;
                        try {
                            valueConverted = Long.parseLong(value.toString());
                        }
                        catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                        finally {
                            jsonObject.addProperty(key, valueConverted);
                        }
                        break;
                    }
                    case "System.Decimal": {

                        double valueConverted = 0;
                        try {
                            valueConverted = Double.parseDouble(value.toString());
                        }
                        catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                        finally {
                            jsonObject.addProperty(key, valueConverted);
                        }
                        break;
                    }
                    case "System.Boolean": {
                        boolean valueConverted = false;
                        try {
                            valueConverted = Boolean.parseBoolean(value.toString());
                        }
                        catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                        finally {
                            jsonObject.addProperty(key, valueConverted);
                        }
                        break;
                    }
                    default:
                        jsonObject.addProperty(key, value.toString());
                        break;
                }
            }
        }
    }

    public void setJsonRow(String path, int row, String info) {
        JsonParser parser = new JsonParser();
        this.setJsonRow(path, row, parser.parse(info));
    }

    public void setJsonRow(String path, int row, JsonElement jInfo) {
        String key = getKey(path);
        JsonElement jsonElement = getJsonObject(path);
        if (jsonElement != null && jsonElement.isJsonObject() &&  jsonElement.getAsJsonObject().has(key)) {
            JsonArray jsonArray = jsonElement.getAsJsonObject().get(key).getAsJsonArray();
            if (jsonArray.size() > row)
                jsonArray.set(row, jInfo);
        }
    }

    public void addRow(String path, String info) {
        JsonParser parser = new JsonParser();
        this.addRow(path, parser.parse(info));
    }

    public void addRow(String path, JsonElement jInfo) {
        String key = getKey(path);
        JsonElement jsonElement = getJsonObject(path);
        if (jsonElement != null && jsonElement.isJsonObject() &&  jsonElement.getAsJsonObject().has(key)) {
            jsonElement.getAsJsonObject().get(key).getAsJsonArray().add(jInfo);
        }
    }

    public void removeRow(String path, int row) {
        String key = getKey(path);
        JsonElement jsonElement = getJsonObject(path);
        if (jsonElement != null && jsonElement.isJsonObject() &&  jsonElement.getAsJsonObject().has(key)) {
            jsonElement.getAsJsonObject().get(key).getAsJsonArray().remove(row);
        }
    }

    public JsonObject getJsonObjectArray(String path, int row) {
        String key = getKey(path);
        JsonElement jsonElement = getJsonObject(path);
        if (jsonElement != null && jsonElement.isJsonObject() &&  jsonElement.getAsJsonObject().has(key)) {
            JsonArray jsonArray = jsonElement.getAsJsonObject().get(key).getAsJsonArray();
            if (jsonArray.size() > row)
                return jsonArray.get(row).getAsJsonObject();
        }
        return null;
    }

    public JsonArray getJsonArray(String path) {
        String key = getKey(path);
        JsonElement jsonElement = getJsonObject(path);
        if (jsonElement != null && jsonElement.isJsonObject() &&  jsonElement.getAsJsonObject().has(key)) {
            return jsonElement.getAsJsonObject().get(key).getAsJsonArray();
        }
        return null;
    }

    public JsonObject getJsonObject(String path) {
        JsonElement jsonElement = getJsonElement(path);
        if (jsonElement != null && jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        }
        return null;
    }

    public JsonElement getJsonElement(String path) {
        String[] keys = path.split("\\\\");
        JsonElement jsonElement = jObject;
        for ( int index = 0; index < keys.length; index++) {
            if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has(keys[index])) {
                if (index + 1 < keys.length)
                    jsonElement = jsonElement.getAsJsonObject().get(keys[index]);
                else
                    return jsonElement;
            }
            else {
                if (!(index + 1 < keys.length))
                    return jsonElement;
                else
                    return null;
            }
        }
        return null;
    }

    private String getKey(String path) {
        String[] keys = path.split("\\\\");
        return keys.length > 0 ?  keys[keys.length - 1] : null;
    }

}

