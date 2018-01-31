package mx.com.azteca.home.util;


import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.com.azteca.home.model.ipati.pojo.DataNamePath;


public class XmlHandler {

    private XmlPullParser Parser;
    private List<String> Tags;
    private DataNamePath DataPath;
    private HashMap<String, DataNamePath> HashDataNamePath;

    public XmlHandler() {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            Parser = factory.newPullParser();
            HashDataNamePath = new HashMap<>();
        }
        catch (Exception ex) {
            this.handleException(new Exception("Error al crear una instancia de XmlPullParserFactory", ex));
        }

        this.Tags = new ArrayList<>();
    }

    public void loadAttributesValues(String xmlInfo) {
        try {
            DataNamePath DataPath = null;
            Tags.clear();
            Parser.setInput(new StringReader(xmlInfo));
            int eventType = Parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG /*&& !Parser.getName().equals("Root")*/) {

                    Tags.add(Parser.getName());

                    String target = "";
                    for (String tag : Tags) {
                        target +=  (target.length() > 0 ? "\\" + tag  : tag);
                    }

                    if (DataPath == null) {
                        DataPath = new DataNamePath();
                        DataPath.IsLeft = true;
                    }
                    else {
                        DataPath = DataPath.addChild();
                    }

                    DataPath.NamePath   = target;
                    DataPath.Nodo       = Parser.getName();
                    DataPath.Level      = Parser.getDepth();

                    HashDataNamePath.put(target, DataPath);

                    if (Parser.getAttributeCount() > 0) {
                        for (int indice = 0; indice < Parser.getAttributeCount(); indice++) {
                            DataPath.addAtrribute(Parser.getAttributeName(indice), Parser.getAttributeValue(indice));
                        }
                    }
                }
                else if(eventType == XmlPullParser.END_TAG /*&& !Parser.getName().equals("Root")*/) {
                    if (Tags.size() > 0) {
                        Tags.remove(Tags.size() - 1);
                        if (DataPath != null)
                            DataPath = DataPath.IsLeft ? DataPath : DataPath.getParent();
                    }
                }
                eventType = Parser.next();
            }
            XmlHandler.this.DataPath = DataPath;
        }
        catch (Exception ex) {
            this.handleException(new Exception("Error al cargar los valores del xml", ex));
        }
    }

    public String createDataNamePathXml() {
        return createDataNamePathXml(this.DataPath);
    }

    public String createDataNamePathXml(DataNamePath dataNamePath) {
        try {
            StringWriter writer = new StringWriter();
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(writer);
            serializer.startDocument(null, true);
            writerNamePath(serializer, dataNamePath);
            serializer.endDocument();
            return writer.toString();
        }
        catch (Exception ex) {
            this.handleException(new Exception("Error al crear el xml del data name path", ex));
        }
        return null;
    }

    public void createPath(String path) {
        DataNamePath dataNamePath = null;
        DataNamePath lastDataNamePath = null;
        String target = "";
        String[] tags = path.split("\\\\");
        for (String tag : tags) {
            target += (target.length() > 0 ? "\\" + tag  : tag);
            if (dataNamePath == null) {
                dataNamePath = new DataNamePath();
                dataNamePath.NamePath   = target;
                dataNamePath.Nodo       = tag;
                lastDataNamePath = dataNamePath;
            }
            else {
                lastDataNamePath = lastDataNamePath.addChild();
                lastDataNamePath.NamePath   = target;
                lastDataNamePath.Nodo       = tag;
            }
        }

        this.DataPath = dataNamePath;
    }

    public String createPathXml(String path, boolean attribute) {
        DataNamePath dataNamePath = null;
        DataNamePath lastDataNamePath = null;
        String target = "";
        String[] tags = path.split("\\\\");
        for (int indice = 0; indice < tags.length - (attribute ? 1 : 0) ; indice++) {
            target += (target.length() > 0 ? "\\" + tags[indice]  : tags[indice]);
            if (dataNamePath == null) {
                dataNamePath = new DataNamePath();
                dataNamePath.NamePath   = target;
                dataNamePath.Nodo       = tags[indice];
                lastDataNamePath = dataNamePath;
            }
            else {
                lastDataNamePath = lastDataNamePath.addChild();
                lastDataNamePath.NamePath   = target;
                lastDataNamePath.Nodo       = tags[indice];
            }
        }
        if (attribute && lastDataNamePath != null) {
            lastDataNamePath.addAtrribute(tags[tags.length - 1], "");
        }
        return createDataNamePathXml(dataNamePath);
    }

    public String getValue(String path) {
        String target = "";
        String[] tags = path.split("\\\\");
        for (int indice = 0; indice < tags.length - 1; indice++) {
            target += (target.length() > 0 ? "\\" + tags[indice]  : tags[indice]);
        }

        if (this.DataPath == null) {
            String xml = createPathXml(path, true);
            loadAttributesValues(xml);
        }

        if (HashDataNamePath.containsKey(target)) {
            if (!HashDataNamePath.get(target).getMapAtrributes().containsKey(tags[tags.length - 1])) {
                HashDataNamePath.get(target).getMapAtrributes().put(tags[tags.length - 1], "");
            }
            return HashDataNamePath.get(target).getMapAtrributes().get(tags[tags.length - 1]);
        }
        else {
            DataNamePath lastMatch = null;
            for (Map.Entry<String, DataNamePath> entry : HashDataNamePath.entrySet()) {
                if (target.startsWith(entry.getValue().NamePath)) {
                    if (lastMatch == null) {
                        lastMatch = entry.getValue();
                    }
                    else if (lastMatch.Level < entry.getValue().Level) {
                        lastMatch = entry.getValue();
                    }
                }
            }

            DataNamePath newDataNamePath = null;
            if (lastMatch != null) {
                String newTarget = lastMatch.NamePath;
                String[] tagsMatch = lastMatch.NamePath.split("\\\\");
                for (int indice = tagsMatch.length; indice < tags.length - 1 ; indice++) {
                    newTarget += (target.length() > 0 ? "\\" + tags[indice]  : tags[indice]);

                    newDataNamePath = newDataNamePath == null ? lastMatch.addChild() : newDataNamePath.addChild();
                    newDataNamePath.NamePath = newTarget;
                    newDataNamePath.Nodo = tags[indice];
                    newDataNamePath.Level = indice + 1;

                    HashDataNamePath.put(newTarget, newDataNamePath);
                }
            }

            if (newDataNamePath != null) {
                newDataNamePath.addAtrribute(tags[tags.length - 1], "");
            }
        }
        return "";
    }



    public void setValue(String path, String value) {
        String target = "";
        String[] tags = path.split("\\\\");
        for (int indice = 0; indice < tags.length - 1; indice++) {
            target += (target.length() > 0 ? "\\" + tags[indice]  : tags[indice]);
        }

        if (this.DataPath == null) {
            String xml = createPathXml(path, true);
            loadAttributesValues(xml);
        }

        if (HashDataNamePath.containsKey(target)) {
            HashDataNamePath.get(target).getMapAtrributes().put(tags[tags.length - 1], value);
        }
        else {
            DataNamePath lastMatch = null;
            for (Map.Entry<String, DataNamePath> entry : HashDataNamePath.entrySet()) {
                if (target.startsWith(entry.getValue().NamePath)) {
                    if (lastMatch == null) {
                        lastMatch = entry.getValue();
                    }
                    else if (lastMatch.Level < entry.getValue().Level) {
                        lastMatch = entry.getValue();
                    }
                }
            }

            DataNamePath newDataNamePath = null;
            if (lastMatch != null) {
                String newTarget = lastMatch.NamePath;
                String[] tagsMatch = lastMatch.NamePath.split("\\\\");
                for (int indice = tagsMatch.length; indice < tags.length - 1 ; indice++) {
                    newTarget += (target.length() > 0 ? "\\" + tags[indice]  : tags[indice]);

                    newDataNamePath = newDataNamePath == null ? lastMatch.addChild() : newDataNamePath.addChild();
                    newDataNamePath.NamePath = newTarget;
                    newDataNamePath.Nodo = tags[indice];
                    newDataNamePath.Level = indice + 1;

                    HashDataNamePath.put(newTarget, newDataNamePath);
                }
            }

            if (newDataNamePath != null) {
                newDataNamePath.addAtrribute(tags[tags.length - 1], value);
            }
        }
    }

    public DataNamePath getNamePath(String path) {
        String target = "";
        String[] tags = path.split("\\\\");
        for (String tag : tags) {
            target += (target.length() > 0 ? "\\" + tag  : tag);
        }

        if (this.DataPath == null) {
            String xml = createPathXml(path, false);
            loadAttributesValues(xml);
        }

        if (HashDataNamePath.containsKey(target)) {
            return HashDataNamePath.get(target);
        }
        else {
            DataNamePath lastMatch = null;
            for (Map.Entry<String, DataNamePath> entry : HashDataNamePath.entrySet()) {
                if (target.startsWith(entry.getValue().NamePath)) {
                    if (lastMatch == null) {
                        lastMatch = entry.getValue();
                    }
                    else if (lastMatch.Level < entry.getValue().Level) {
                        lastMatch = entry.getValue();
                    }
                }
            }
            DataNamePath newDataNamePath = null;
            if (lastMatch != null) {
                String newTarget = lastMatch.NamePath;
                String[] tagsMatch = lastMatch.NamePath.split("\\\\");
                for (int indice = tagsMatch.length; indice < tags.length ; indice++) {
                    newTarget += (target.length() > 0 ? "\\" + tags[indice]  : tags[indice]);
                    newDataNamePath = newDataNamePath == null ? lastMatch.addChild() : newDataNamePath.addChild();
                    newDataNamePath.NamePath = newTarget;
                    newDataNamePath.Nodo = tags[indice];
                    newDataNamePath.Level = indice + 1;
                    HashDataNamePath.put(newTarget, newDataNamePath);
                }
            }
            return newDataNamePath;
        }
    }

    public List<DataNamePath> getChilds(String path) {
        String target = "";
        String[] tags = path.split("\\\\");
        for (String tag : tags) {
            target += (target.length() > 0 ? "\\" + tag  : tag);
        }

        if (this.DataPath == null) {
            String xml = createPathXml(path, false);
            loadAttributesValues(xml);
        }

        if (HashDataNamePath.containsKey(target)) {
            return HashDataNamePath.get(target).getChildNamePath();
        }
        else {
            DataNamePath lastMatch = null;
            for (Map.Entry<String, DataNamePath> entry : HashDataNamePath.entrySet()) {
                if (target.startsWith(entry.getValue().NamePath)) {
                    if (lastMatch == null) {
                        lastMatch = entry.getValue();
                    }
                    else if (lastMatch.Level < entry.getValue().Level) {
                        lastMatch = entry.getValue();
                    }
                }
            }

            DataNamePath newDataNamePath = null;
            if (lastMatch != null) {
                String newTarget = lastMatch.NamePath;
                String[] tagsMatch = lastMatch.NamePath.split("\\\\");
                for (int indice = tagsMatch.length; indice < tags.length ; indice++) {
                    newTarget += (target.length() > 0 ? "\\" + tags[indice]  : tags[indice]);

                    newDataNamePath = newDataNamePath == null ? lastMatch.addChild() : newDataNamePath.addChild();
                    newDataNamePath.NamePath = newTarget;
                    newDataNamePath.Nodo = tags[indice];
                    newDataNamePath.Level = indice + 1;

                    HashDataNamePath.put(newTarget, newDataNamePath);
                }
            }

        }
        return new ArrayList<>();
    }

    private void writerNamePath(XmlSerializer serializer, DataNamePath dataNamePath) throws Exception {
        serializer.startTag(null, dataNamePath.Nodo);
        for (Map.Entry<String, String> entry : dataNamePath.getMapAtrributes().entrySet()) {
            serializer.attribute(null, entry.getKey(), entry.getValue());
        }
        for (DataNamePath child : dataNamePath.getChildNamePath()) {
            writerNamePath(serializer, child);
        }
        serializer.endTag(null, dataNamePath.Nodo);
    }

    public DataNamePath getKeyValue() {
        return DataPath;
    }

    public void handleException(Exception ex) {
    }

}
