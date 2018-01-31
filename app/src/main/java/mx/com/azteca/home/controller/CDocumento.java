package mx.com.azteca.home.controller;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mx.com.azteca.home.model.ipati.dao.DAODocumentInstance;
import mx.com.azteca.home.model.ipati.model.MConfiguracion;
import mx.com.azteca.home.model.ipati.model.MDataset;
import mx.com.azteca.home.model.ipati.model.MDocumentInstance;
import mx.com.azteca.home.model.ipati.model.MDocumento;
import mx.com.azteca.home.model.ipati.model.MDocumentoAsignado;
import mx.com.azteca.home.model.ipati.model.MField;
import mx.com.azteca.home.model.ipati.model.MFormulario;
import mx.com.azteca.home.model.ipati.model.MPersona;
import mx.com.azteca.home.model.ipati.model.MUsuario;
import mx.com.azteca.home.model.ipati.model.MVersionInfo;
import mx.com.azteca.home.model.ipati.model.MWorkflow;
import mx.com.azteca.home.model.ipati.pojo.Column;
import mx.com.azteca.home.model.ipati.pojo.Configuracion;
import mx.com.azteca.home.model.ipati.pojo.DataField;
import mx.com.azteca.home.model.ipati.pojo.DataNamePath;
import mx.com.azteca.home.model.ipati.pojo.Dataset;
import mx.com.azteca.home.model.ipati.pojo.DocumentInstance;
import mx.com.azteca.home.model.ipati.pojo.Documento;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.Field;
import mx.com.azteca.home.model.ipati.pojo.Formulario;
import mx.com.azteca.home.model.ipati.pojo.Persona;
import mx.com.azteca.home.model.ipati.pojo.Usuario;
import mx.com.azteca.home.model.ipati.pojo.VersionInfo;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.ui.Gallery;
import mx.com.azteca.home.util.Componente;
import mx.com.azteca.home.util.IconProvider;
import mx.com.azteca.home.util.JsonHandler;
import mx.com.azteca.home.util.XmlHandler;
import mx.com.azteca.home.view.ipati.FormularioActivity;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CDocumento extends BaseController {

    private Listener listener;
    private List<Componente.ComboBox> listCorrelacion;
    private HashMap<String, String> paramFilters;
    private List<Object> listControls;

    private XmlHandler xmlHandler;
    private JsonHandler jsonHandler;

    public CDocumento(Listener listener) {
        this.listener           = listener;
        this.listCorrelacion    = new ArrayList<>();
        this.paramFilters       = new HashMap<>();
        this.listControls       = new ArrayList<>();
        this.xmlHandler         = new XmlHandler();
        this.jsonHandler        = new JsonHandler();
    }

    public void loadDocumento() {
        String method = "loadDocumento";
        Object value = listener.getConfiguracion(Boolean.class, "pref_documentos");
        if (value != null && value instanceof Boolean && !((boolean) value)) {
            method = "loadDocumentoOffline";
        }
        Proceso proceso = new Proceso(method, "Cargando documentos", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o instanceof List) {
                    listener.loadDocumento((List<Documento>) o);
                }
            }
        };
        proceso.execute();
    }

    public void iniciarEdicion(final DocumentoAsignado docAsignado) {
        listener.iniciarEdicion(docAsignado);
    }

    public void loadFormulario(int idDocumento, long fecha, String folio) {
        Proceso proceso = new Proceso("loadDocumentoAsignado", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                try {
                    if (o instanceof DocumentoAsignado) {
                        DocumentoAsignado docAsignado = (DocumentoAsignado) o;
                        listener.setDataField(docAsignado);
                        Formulario formulario = docAsignado.getWorkflow().getFormulario();
                        listener.setTitle(formulario.Titulo);
                        JsonParser parser = new JsonParser();
                        JsonArray jFormulario = (JsonArray) parser.parse(formulario.Info);
                        boolean inicio = true;
                        for (JsonElement jElement : jFormulario) {
                            loadContainer(docAsignado, null, jElement.getAsJsonObject(), inicio);
                            inicio = false;
                        }
                        CDocumento.this.loadCampoParametro();
                        CDocumento.this.loadCampoCorrelacion();
                        CDocumento.this.loadValuesComponent();
                        if (docAsignado.getWorkflow().IsStart) {
                            listener.loadStartOptions();
                        }
                        else if (docAsignado.getWorkflow().IsEnd) {
                            listener.loadEndOptoons();
                        }
                        else {
                            listener.loadWorkflowOptions();
                        }
                    }
                }
                catch (Exception ex) {
                    Log.e(getClass().getName(), ex.toString());
                }
            }
        };
        proceso.execute(idDocumento, fecha, folio);
    }

    public void loadFormulario(int idFormulario, String baseInfo) {
        Proceso proceso = new Proceso("loadFormulario", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                try {
                    if (o instanceof Formulario) {
                        Formulario formulario = (Formulario)o;
                        listener.setDataField(formulario);
                        listener.setTitle(formulario.Titulo);
                        JsonParser parser = new JsonParser();
                        JsonArray jFormulario = (JsonArray) parser.parse(formulario.Info);
                        boolean inicio = true;
                        for (JsonElement jElement : jFormulario) {
                            loadContainer(formulario, null, jElement.getAsJsonObject(), inicio);
                            inicio = false;
                        }
                        CDocumento.this.loadCampoParametro();
                        CDocumento.this.loadCampoCorrelacion();
                        CDocumento.this.loadValuesComponent();
                    }
                }
                catch (Exception ex) {
                    Log.e(getClass().getName(), ex.toString());
                }
            }
        };
        proceso.execute(idFormulario, baseInfo);
    }

    public void loadContainer(final DataField dataField, View container, JsonObject jForma, boolean inicio) {
        final View viewContainer = container;
        if (jForma.get("Info").getAsJsonObject().get("TipoContenedor").getAsString().equals("A") && inicio) {
            JsonArray jChildrens =  jForma.get("Children").getAsJsonArray();
            if (jChildrens.size() == 0 && jForma.getAsJsonObject().get("Info").getAsJsonObject().has("Controles")) {
                if (container == null) {
                    container = listener.addView(null, jForma.getAsJsonObject().get("Info").getAsJsonObject().get("Nombre").getAsString());
                }
                JsonElement jsonControlers = jForma.getAsJsonObject().get("Info").getAsJsonObject().get("Controles");
                if (jsonControlers != null) {
                    for (JsonElement fields : jsonControlers.getAsJsonArray()) {
                        Object view = Componente.getInstance(new Componente.Listener() {
                            @Override
                            public Dataset getDataset(int idDataset) {
                                MDataset mDataset = new MDataset(listener.getContext());
                                return mDataset.getDetailInfo(idDataset);
                            }
                            @Override
                            public Activity getActivity() {
                                return listener.getAcivity();
                            }

                            @Override
                            public Context getContext() {
                                return listener.getContext();
                            }

                            @Override
                            public void capturarImagen(int id) {
                                listener.capturarImagen(id);
                            }

                            @Override
                            public void loadChildForm(int parent, int idForm, int row, String childPath) {
                                loadFormularioChild(parent, idForm, row, childPath);
                            }

                            @Override
                            public void deleteForm(int parent, int idForm, int row) {
                                removeRow(parent, row);
                            }

                            @Override
                            public void onChangeDataSetField(int idParent, String valueMember, String value) {
                                CDocumento.this.paramFilters.put(valueMember, value);
                                for (Componente.ComboBox tool : listCorrelacion) {
                                    if (tool.getField().Identity != idParent) {
                                        tool.notifyDataSetChange(true);
                                    }
                                }
                            }

                            @Override
                            public HashMap<String, String> getParamFilters() {
                                return CDocumento.this.paramFilters;
                            }

                            @Override
                            public String loadDatasetInfo(String table, String[] columns, String[] params) {
                                try {
                                    MDataset mDataset = newInstanceModel(MDataset.class);
                                    return mDataset.getDatasetInfo(table, columns, params);
                                }
                                catch (Exception ex) {
                                    listener.notificarUsuario("Error", ex.getMessage());
                                }
                                return "[]";
                            }

                            @Override
                            public void loadDatasetInfo(Componente.ComboBox comboBox, String table, String[] columns, String[] params) {
                                listener.mostrarProgress("Cargando información");
                                Observable.create((Observable.OnSubscribe<String>) subscriber -> {
                                    try {
                                        MDataset mDataset = newInstanceModel(MDataset.class);
                                        String info = mDataset.getDatasetInfo(table, columns, params);
                                        subscriber.onNext(info);
                                        subscriber.onCompleted();
                                    }
                                    catch (Exception e) {
                                        subscriber.onError(e);
                                    }
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<String>() {
                                    @Override
                                    public void onCompleted() {
                                        listener.ocultarProgress();
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        listener.ocultarProgress();
                                        listener.notificarUsuario("Error", e.getMessage());
                                        comboBox.loadDatasetInfo("[]");
                                    }
                                    @Override
                                    public void onNext(String info) {
                                        comboBox.loadDatasetInfo(info);
                                    }
                                });
                            }

                            @Override
                            public void previewImage(String image) {
                                listener.previewImage(image);
                            }

                            @Override
                            public LatLng getDocPosition() {
                                double lat = listener.getData("lat") != null ? (double)listener.getData("lat") : 0.0;
                                double lng = listener.getData("lng") != null ? (double)listener.getData("lng") : 0.0;
                                return new LatLng(lat, lng);
                            }

                        }).getComponente(listener.getContext(), fields.toString());

                        CDocumento.this.listControls.add(view);
                        if (view instanceof Componente.Selector) {
                            View etiqueta = ((Componente.Tool)view).getEtiqueta();
                            view = ((Componente.Selector)view).getView();
                            if (etiqueta != null) {
                                listener.addView(container, etiqueta);
                            }
                            listener.addView(container, (View)view);
                        }
                        else if (view instanceof Componente.DateTimePicker){
                            View etiqueta = ((Componente.Tool)view).getEtiqueta();
                            view = ((Componente.DateTimePicker)view).getView();
                            if (etiqueta != null) {
                                listener.addView(container, etiqueta);
                            }
                            listener.addView(container, (View)view);
                        }
                        if (view instanceof Componente.Tool && view instanceof View) {
                            View etiqueta = ((Componente.Tool)view).getEtiqueta();
                            if (etiqueta != null) {
                                listener.addView(container, etiqueta);
                            }

                            if (view instanceof Componente.ImageBox) {
                                listener.addView(container, ((Componente.ImageBox) view).getImageView());
                            }

                            if (view instanceof Componente.MapLocation) {
                                listener.addView(container, ((Componente.MapLocation) view).getImageView());
                            }

                            if (view instanceof Componente.GalleryComponent) {
                                loadValuesCarousel((Componente.GalleryComponent) view);
                                listener.addUnbindTool((Componente.GalleryComponent) view);
                            }
                            listener.addView(container, (View)view);
                        }
                        if (view instanceof Componente.Grid) {
                            Componente.Grid grid = ((Componente.Grid) view);
                            loadValuesGrid((Componente.Grid) view);
                            listener.addUnbindTool(grid);
                            listener.addView(viewContainer, ((Componente.Grid)view).getView());
                        }
                        if (view instanceof Componente.ComboBox) {
                            Componente.ComboBox cmb = (Componente.ComboBox)view;
                            if (cmb.getDataset() != null && cmb.getDataset().Tipo.equals("S")) {
                                listCorrelacion.add((Componente.ComboBox) view);
                            }
                        }
                        if (view instanceof Componente.MultiPoint) {
                            Componente.MultiPoint mp = (Componente.MultiPoint)view;
                            listener.addView(viewContainer, ((Componente.MultiPoint)view).getView());
                            mp.notifyMap();
                            loadValuesMultiPoint(mp);
                            listener.addUnbindTool(mp);
                        }
                    }
                }
            }

            for (JsonElement jChildren : jChildrens) {
                final JsonObject jContenedor = jChildren.getAsJsonObject();
                listener.addTabContainer(jContenedor.get("Info").getAsJsonObject().get("Nombre").getAsString(), new FormularioActivity.Listener(){
                    @Override
                    public void loadView(View viewTab) {
                        JsonElement jsonControlers = jContenedor.getAsJsonObject().get("Info").getAsJsonObject().get("Controles");
                        if (jsonControlers != null) {
                            for (JsonElement fields : jsonControlers.getAsJsonArray()) {
                                Object view = Componente.getInstance(new Componente.Listener() {
                                    @Override
                                    public Dataset getDataset(int idDataset) {
                                        MDataset mDataset = newInstanceModel(MDataset.class);
                                        return mDataset.getDetailInfo(idDataset);
                                    }
                                    @Override
                                    public Activity getActivity() {
                                        return listener.getAcivity();
                                    }

                                    @Override
                                    public Context getContext() {
                                        return listener.getContext();
                                    }

                                    @Override
                                    public void capturarImagen(int id) {
                                        listener.capturarImagen(id);
                                    }

                                    @Override
                                    public void loadChildForm(int parent, int idForm, int row, String childPath) {
                                        loadFormularioChild(parent, idForm, row, childPath);
                                    }

                                    @Override
                                    public void deleteForm(int parent, int idForm, int row) {
                                        removeRow(parent, row);
                                    }

                                    @Override
                                    public void onChangeDataSetField(int idParent, String valueMember, String value) {
                                        CDocumento.this.paramFilters.put(valueMember, value);
                                        for (Componente.ComboBox tool : listCorrelacion) {
                                            if (tool.getField().Identity != idParent) {
                                                tool.notifyDataSetChange(true);
                                            }
                                        }
                                    }

                                    @Override
                                    public HashMap<String, String> getParamFilters() {
                                        return CDocumento.this.paramFilters;
                                    }

                                    @Override
                                    public String loadDatasetInfo(String table, String[] columns, String[] params) {
                                        try {
                                            MDataset mDataset = newInstanceModel(MDataset.class);
                                            return mDataset.getDatasetInfo(table, columns, params);
                                        }
                                        catch (Exception ex) {
                                            listener.notificarUsuario("Error", ex.getMessage());
                                        }
                                        return "[]";
                                    }

                                    @Override
                                    public void loadDatasetInfo(Componente.ComboBox comboBox, String table, String[] columns, String[] params) {
                                        // TODO: Falta aquoi
                                        Log.e(CDocumento.this.getClass().getSimpleName(), "ERror");
                                    }

                                    @Override
                                    public void previewImage(String image) {
                                        listener.previewImage(image);
                                    }

                                    @Override
                                    public LatLng getDocPosition() {
                                        double lat = listener.getData("lat") != null ? (double)listener.getData("lat") : 0.0;
                                        double lng = listener.getData("lng") != null ? (double)listener.getData("lng") : 0.0;
                                        return new LatLng(lat, lng);
                                    }
                                }).getComponente(listener.getContext(), fields.toString());
                                CDocumento.this.listControls.add(view);
                                if (view instanceof Componente.Selector) {
                                    View etiqueta = ((Componente.Tool)view).getEtiqueta();
                                    view = ((Componente.Selector)view).getView();
                                    if (etiqueta != null) {
                                        listener.addView(viewContainer, etiqueta);
                                    }
                                    listener.addView(viewContainer, (View)view);
                                }
                                else if (view instanceof Componente.DateTimePicker){
                                    View etiqueta = ((Componente.Tool)view).getEtiqueta();
                                    view = ((Componente.DateTimePicker)view).getView();
                                    if (etiqueta != null) {
                                        listener.addView(viewContainer, etiqueta);
                                    }
                                    listener.addView(viewContainer, (View)view);
                                }
                                if (view instanceof Componente.Tool && view instanceof View) {
                                    View etiqueta = ((Componente.Tool)view).getEtiqueta();
                                    if (etiqueta != null) {
                                        listener.addView(viewContainer, etiqueta);
                                    }

                                    if (view instanceof Componente.ImageBox) {
                                        listener.addView(viewContainer, ((Componente.ImageBox) view).getImageView());
                                    }

                                    if (view instanceof Componente.MapLocation) {
                                        listener.addView(viewContainer, ((Componente.MapLocation) view).getImageView());
                                    }
                                    listener.addView(viewContainer, (View)view);
                                }
                                if (view instanceof Componente.Grid) {
                                    Componente.Grid grid = ((Componente.Grid) view);
                                    loadValuesGrid((Componente.Grid) view);
                                    listener.addUnbindTool(grid);
                                    listener.addView(viewContainer, ((Componente.Grid)view).getView());
                                }
                                if (view instanceof Componente.Tool) {
                                    if (view instanceof Componente.Grid) {
                                        loadValuesGrid((Componente.Grid) view);
                                    }
                                    else if (view instanceof Componente.GalleryComponent) {
                                        loadValuesCarousel((Componente.GalleryComponent) view);
                                        listener.addUnbindTool((Componente.GalleryComponent) view);
                                    }
                                }
                                if (view instanceof Componente.ComboBox) {
                                    Componente.ComboBox cmb = (Componente.ComboBox)view;
                                    if (cmb.getDataset() != null && cmb.getDataset().Tipo.equals("S")) {
                                        listCorrelacion.add((Componente.ComboBox)view);
                                    }
                                }
                                if (view instanceof Componente.MultiPoint) {
                                    Componente.MultiPoint mp = (Componente.MultiPoint)view;
                                    listener.addView(viewContainer, ((Componente.MultiPoint) view).getView());
                                    mp.notifyMap();
                                    loadValuesMultiPoint(mp);
                                    listener.addUnbindTool(mp);
                                }
                            }
                        }
                        JsonArray jsonChildrens = jContenedor.getAsJsonObject().get("Children").getAsJsonArray();
                        if (jsonChildrens != null) {
                            for (JsonElement jChildren : jsonChildrens) {
                                loadContainer(dataField, viewTab, jChildren.getAsJsonObject(), false);
                            }
                        }
                    }
                });
            }
        }
        else {
            if (!jForma.getAsJsonObject().get("Info").getAsJsonObject().get("TipoContenedor").getAsString().equals("T")) {
                if (!jForma.getAsJsonObject().get("Info").getAsJsonObject().get("TipoContenedor").getAsString().equals("A")) {
                    container = listener.addView(container, jForma.getAsJsonObject().get("Info").getAsJsonObject().get("Nombre").getAsString());
                }
            }
            if (container == null || inicio) {
                if (container == null){
                    container = listener.addView(null, jForma.getAsJsonObject().get("Info").getAsJsonObject().get("Nombre").getAsString());
                }
            }
            JsonElement jsonControlers = jForma.getAsJsonObject().get("Info").getAsJsonObject().get("Controles");
            if (jsonControlers != null) {
                for (JsonElement fields : jsonControlers.getAsJsonArray()) {
                    Object view = Componente.getInstance(new Componente.Listener() {
                        @Override
                        public Dataset getDataset(int idDataset) {
                            MDataset mDataset = newInstanceModel(MDataset.class);
                            return mDataset.getDetailInfo(idDataset);
                        }
                        @Override
                        public Activity getActivity() {
                            return listener.getAcivity();
                        }

                        @Override
                        public Context getContext() {
                            return listener.getContext();
                        }

                        @Override
                        public void capturarImagen(int id) {
                            listener.capturarImagen(id);
                        }

                        @Override
                        public void loadChildForm(int parent, int idForm, int row, String childPath) {
                            loadFormularioChild(parent, idForm, row, childPath);
                        }

                        @Override
                        public void deleteForm(int parent, int idForm, int row) {
                            removeRow(parent, row);
                        }

                        @Override
                        public void onChangeDataSetField(int idParent, String valueMember, String value) {
                            CDocumento.this.paramFilters.put(valueMember, value);
                            for (Componente.ComboBox tool : listCorrelacion) {
                                if (tool.getField().Identity != idParent) {
                                    tool.notifyDataSetChange(true);
                                }
                            }
                        }

                        @Override
                        public HashMap<String, String> getParamFilters() {
                            return CDocumento.this.paramFilters;
                        }

                        @Override
                        public String loadDatasetInfo(String table, String[] columns, String[] params) {
                            try {
                                MDataset mDataset = newInstanceModel(MDataset.class);
                                return mDataset.getDatasetInfo(table, columns, params);
                            }
                            catch (Exception ex) {
                                listener.notificarUsuario("Error", ex.getMessage());
                            }
                            return "[]";
                        }

                        @Override
                        public void loadDatasetInfo(Componente.ComboBox comboBox, String table, String[] columns, String[] params) {
                            Log.e(getClass().getSimpleName(), "Cargando info");
                        }

                        @Override
                        public void previewImage(String image) {
                            listener.previewImage(image);
                        }

                        @Override
                        public LatLng getDocPosition() {
                            double lat = listener.getData("lat") != null ? (double)listener.getData("lat") : 0.0;
                            double lng = listener.getData("lng") != null ? (double)listener.getData("lng") : 0.0;
                            return new LatLng(lat, lng);
                        }

                    }).getComponente(listener.getContext(), fields.toString());
                    CDocumento.this.listControls.add(view);
                    if (view instanceof Componente.Selector) {
                        View etiqueta = ((Componente.Tool)view).getEtiqueta();
                        view = ((Componente.Selector)view).getView();
                        if (etiqueta != null) {
                            listener.addView(container, etiqueta);
                        }
                        listener.addView(container, (View)view);
                    }
                    else if (view instanceof Componente.DateTimePicker){
                        View etiqueta = ((Componente.Tool)view).getEtiqueta();
                        view = ((Componente.DateTimePicker)view).getView();
                        if (etiqueta != null) {
                            listener.addView(container, etiqueta);
                        }
                        listener.addView(container, (View)view);
                    }
                    if (view instanceof Componente.Tool && view instanceof View) {
                        View etiqueta = ((Componente.Tool)view).getEtiqueta();
                        if (etiqueta != null) {
                            listener.addView(container, etiqueta);
                        }
                        if (view instanceof Componente.ImageBox) {
                            listener.addView(container, ((Componente.ImageBox) view).getImageView());
                        }
                        if (view instanceof Componente.MapLocation) {
                            listener.addView(container, ((Componente.MapLocation) view).getImageView());
                        }
                        if (view instanceof Componente.GalleryComponent) {
                            loadValuesCarousel((Componente.GalleryComponent) view);
                            listener.addUnbindTool((Componente.GalleryComponent) view);
                        }
                        listener.addView(container, (View)view);
                    }
                    if (view instanceof Componente.Grid) {
                        Componente.Grid grid = ((Componente.Grid) view);
                        if (jForma.get("Info") != null && jForma.get("Info").getAsJsonObject().get("Nombre") != null) {
                            grid.setTitle(jForma.get("Info").getAsJsonObject().get("Nombre").getAsString());
                        }
                        loadValuesGrid((Componente.Grid) view);
                        listener.addUnbindTool(grid);
                        listener.addView(viewContainer, ((Componente.Grid)view).getView());
                    }
                    if (view instanceof Componente.ComboBox) {
                        Componente.ComboBox cmb = (Componente.ComboBox)view;
                        if (cmb.getDataset() != null && cmb.getDataset().Tipo.equals("S")) {
                            listCorrelacion.add((Componente.ComboBox) view);
                        }
                    }
                    if (view instanceof Componente.MultiPoint) {
                        Componente.MultiPoint mp = (Componente.MultiPoint)view;
                        listener.addView(viewContainer, ((Componente.MultiPoint)view).getView());
                        mp.notifyMap();
                        loadValuesMultiPoint(mp);
                        listener.addUnbindTool(mp);
                    }
                }
            }
            JsonArray jChildren = jForma.get("Children").getAsJsonArray();
            for (JsonElement jsonElement : jChildren) {
                loadContainer(dataField, container, jsonElement.getAsJsonObject(), false);
            }
        }
    }

    private void loadValuesComponent() {
        for (Object object : listControls) {
            if (object instanceof Componente.Tool) {
                try {
                    Componente.Tool tool = (Componente.Tool) object;
                    tool.setValue(jsonHandler.getValue(tool.getField().NamedPath, tool.getField().TipoDato));
                }
                catch ( Exception ex ){
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
            }
        }
    }

    private void loadValuesGrid(Componente.Grid grid) {
        grid.clearInfo();
        Formulario formulario;
        if (grid.getField().getIdChilForm() > 0) {
            MFormulario mFormulario = newInstanceModel(MFormulario.class);
            formulario = mFormulario.getDetail(grid.getField().getIdChilForm());
            JsonParser parser = new JsonParser();
            JsonArray jFormulario = (JsonArray) parser.parse(formulario.Info);
            for (JsonElement jElement : jFormulario) {
                loadChildPath(jElement.toString());
            }
        }
        JsonElement jsonElement = jsonHandler.getJsonArray(grid.getField().NamedPath);
        if (jsonElement != null && jsonElement.isJsonArray()) {
            for (JsonElement jElement : jsonElement.getAsJsonArray()) {
                JsonHandler jHandler = new JsonHandler();
                jHandler.setJson(jElement.toString());
                List<Field> row = grid.getDataInfo().addNewRow();
                try {
                    for (Field field : row) {
                        String path = "";
                        for (int indice =  grid.getField().NamedPath.split("\\\\").length + 1; indice < field.NamedPath.split("\\\\").length; indice++) {
                            path += field.NamedPath.split("\\\\")[indice]  + "\\";
                        }
                        field.Value =  jHandler.getValue(path, field.TipoDato);
                    }
                }
                catch (Exception ex ) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
            }
        }
        grid.getAdapter().notifyDataSetChanged();
    }

    private void loadCampoCorrelacion() {
        for (Object object : listControls) {
            if (object instanceof Componente.Tool) {
                try {
                    Componente.Tool tool = (Componente.Tool) object;
                    if (tool.getField().CampoCorrelacion > 0) {
                        for (Object toolCorrelacion : listControls) {
                            if (toolCorrelacion instanceof Componente.Tool && ((Componente.Tool) toolCorrelacion).getField().getIdCampo() == tool.getField().CampoCorrelacion) {
                                ((Componente.Tool) toolCorrelacion).subscribe(tool);
                                break;
                            }
                        }
                    }
                }
                catch ( Exception ex ){
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
            }
        }
    }

    private void loadCampoParametro() {
        for (Object object : listControls) {
            if (object instanceof Componente.ComboBox) {
                Componente.ComboBox comboBox = (Componente.ComboBox) object;
                if (comboBox.getField().Parametros != null && comboBox.getField().Parametros.length() > 0) {
                    JsonParser parser = new JsonParser();
                    JsonArray jsonArray = parser.parse(comboBox.getField().Parametros).getAsJsonArray();
                    for (JsonElement jsonElement : jsonArray) {
                        try {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            if (jsonObject.has("IdCampoOrigen")) {
                                for (Object control : listControls) {
                                    if (control instanceof Componente.Tool && ((Componente.Tool) control).getField().getIdCampo() == jsonObject.get("IdCampoOrigen").getAsInt()) {
                                        ((Componente.Tool) control).subscribeParameter(comboBox);
                                        comboBox.addParameterComponent(jsonObject.get("Nombre").getAsString(), (Componente.Tool)control);
                                    }
                                }
                            }
                        }
                        catch ( Exception ex ){
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                    }
                }
            }
        }
    }

    private void loadValuesCarousel(Componente.GalleryComponent gallery) {
        List<DataNamePath> listData = xmlHandler.getChilds(gallery.getField().getNamedPath());
        String directory = Environment.getExternalStorageDirectory().toString().concat("/ipati/");
        List<Gallery.GalleryItem> listGallery = new ArrayList<>();
        for (DataNamePath dataNamePath : listData) {
            try {
                XmlHandler handler = new XmlHandler();
                String xmlBase = handler.createPathXml(gallery.getField().getNamedPath(), false);
                handler.loadAttributesValues(xmlBase);
                DataNamePath nodo = handler.getNamePath(gallery.getField().getNamedPath());
                nodo.addChild((DataNamePath) dataNamePath.clone());
                handler.loadAttributesValues(handler.createDataNamePathXml());

                Gallery.GalleryItem item = gallery.new GalleryItem();
                item.setTitle(handler.getValue(dataNamePath.NamePath + "\\Titulo"));
                item.setPath(directory + handler.getValue(dataNamePath.NamePath + "\\Imagen") + ".jpg");
                listGallery.add(item);
            }
            catch (Exception ex ) {
                Log.e(getClass().getSimpleName(), ex.toString());
            }
        }
        gallery.setImages(listGallery);
    }

    private void loadValuesMultiPoint(Componente.MultiPoint multiPoint) {
        List<DataNamePath> listData = xmlHandler.getChilds(multiPoint.getField().getNamedPath());
        multiPoint.getDataInfo().DataRows.clear();
        for (DataNamePath dataNamePath : listData) {
            try {
                XmlHandler handler = new XmlHandler();
                String xmlBase = handler.createPathXml(multiPoint.getField().getNamedPath(), false);
                handler.loadAttributesValues(xmlBase);
                DataNamePath nodo = handler.getNamePath(multiPoint.getField().getNamedPath());
                nodo.addChild((DataNamePath) dataNamePath.clone());
                handler.loadAttributesValues(handler.createDataNamePathXml());
                Componente.MultiPoint.DataRow item = multiPoint.getDataInfo().addNewRow();
                item.Titulo = handler.getValue(dataNamePath.NamePath + "\\Titulo");
                item.Descripcion = handler.getValue(dataNamePath.NamePath + "\\Descripcion");
                item.Posicion = handler.getValue(dataNamePath.NamePath + "\\Posicion");
                String clasificacion = handler.getValue(dataNamePath.NamePath + "\\Clasificacion");
                try {
                    item.Clasificacion = Integer.parseInt(clasificacion);
                }
                catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
                item.Clasificacion = IconProvider.getIcon(item.Clasificacion);
            }
            catch (Exception ex ) {
                Log.e(getClass().getSimpleName(), ex.toString());
            }
        }
        multiPoint.notifyMap();
    }


    private String mapXmlTag    = null;
    private String clasXmlTag   = null;
    private void loadChildPath(String info) {
        JsonParser parser = new JsonParser();
        JsonObject jData = parser.parse(info).getAsJsonObject();
        if (jData.has("Info")) {
            JsonObject jInfo = jData.get("Info").getAsJsonObject();
            if (jInfo.has("Controles")) {
                JsonArray jControles = jInfo.get("Controles").getAsJsonArray();
                for (JsonElement jElement : jControles) {
                    JsonObject jControl = jElement.getAsJsonObject();
                    if (jControl.has("Control")) {
                        String tag = jControl.get("Control").getAsJsonObject().get("Tag").getAsString();
                        if (tag.equals("MAP")) {
                            if (jControl.has("Field")) {
                                mapXmlTag = jControl.get("Field").getAsJsonObject().get("XmlTag").getAsString();
                            }
                        }
                        else if (jControl.has("Field")) {
                            if (jControl.get("Field").getAsJsonObject().get("IdDataset").getAsInt() == 104) {
                                clasXmlTag = jControl.get("Field").getAsJsonObject().get("XmlTag").getAsString();
                            }
                        }
                    }
                }
            }
        }
        if (jData.has("Children")) {
            JsonArray jsonArray = jData.get("Children").getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                loadChildPath(jsonElement.toString());
            }
        }
    }

    public void loadWorflowSteps(int idDocumento, int version) {
        Proceso proceso = new Proceso("loadWorflowSteps", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o instanceof List) {
                    try {
                        listener.loadWorflowSteps((List<String>) o);
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getName(), ex.toString());
                    }
                }
            }
        };
        proceso.execute(idDocumento, version);
    }

    public void loadWorkFlowWithInstances(int idDocumento, int version) {
        Proceso proceso = new Proceso("loadWorkFlowWithInstances", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o instanceof List) {
                    try {
                        listener.loadWorkflow((List<WorkFlowStep>) o);
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getName(), ex.toString());
                    }
                }
            }
        };
        proceso.execute(idDocumento, version);
    }

    public void loadDocumentoAsignado(int idDocumento, int idWorkFlowStep) {
        Proceso proceso = new Proceso("loadDocumentoAsignado", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o instanceof List) {
                    try {
                        listener.loadDocumentoAsignado((List<DocumentoAsignado>) o);
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getName(), ex.toString());
                    }
                }
            }
        };
        proceso.execute(idDocumento, idWorkFlowStep);
    }

    public void loadWorkflow(int idDocumento, int version) {
        Proceso proceso = new Proceso("loadWorkFlow", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o instanceof List) {
                    try {
                        listener.loadWorkflow((List<WorkFlowStep>) o);
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getName(), ex.toString());
                    }
                }
            }
        };
        proceso.execute(idDocumento, version);
    }

    public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        if (Componente.SUBVIEW != null) {
            Componente.SUBVIEW.loadLocation(latitude, longitude, accuracy, altitude);
        }
    }

    public void updateInfo(int idNextStep, HashMap<String, Field> info, boolean nextStep) {
        Proceso proceso = new Proceso("updateInfo", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (o == null) {
                    if (nextStep) {
                        listener.formularaioGuardado();
                    }
                    else {
                        listener.getAcivity().finish();
                    }
                }
                else if (o instanceof String) {
                    listener.notificarUsuario("Error", o.toString());
                }
            }
        };
        proceso.execute(idNextStep, info);
    }

    public void loadInfoChildFormulario(HashMap<String, Field> info) {
        Proceso proceso = new Proceso("loadInfoFormulario", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                listener.formularioCompletado((String) o);
            }
        };
        proceso.execute(info);
    }

    public void sincronizar() {
        TelephonyManager telephonyManager= (TelephonyManager) listener.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("HardwareIds") String workStration = telephonyManager.getDeviceId();
        Proceso proceso = new Proceso("uploadDocumentInstance", "Sincronizar dispositivo", true) {
            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                if (result == null) {
                    listener.notificarUsuario("Informaci\u00f3n", "Dispositivo sicronizado");
                }
            }
        };
        proceso.execute(workStration);
    }

    public void loadInfoCollection(final int idField, String info, int row, String namedPath) {
        Proceso proceso = new Proceso("loadInfoGrid", "Actualizando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                if (listener.getComponente(idField) instanceof Componente.Grid) {
                    Componente.Grid grid = (Componente.Grid) listener.getComponente(idField);
                    if (grid != null) {
                        loadValuesGrid(grid);
                    }
                }
                else if (listener.getComponente(idField) instanceof Componente.GalleryComponent) {
                    Componente.GalleryComponent gallery = (Componente.GalleryComponent) listener.getComponente(idField);
                    if (gallery != null) {
                        loadValuesCarousel(gallery);
                    }
                }
                else if (listener.getComponente(idField) instanceof Componente.MultiPoint) {
                    Componente.MultiPoint multiPoint = (Componente.MultiPoint) listener.getComponente(idField);
                    if (multiPoint != null) {
                        loadValuesMultiPoint(multiPoint);
                    }
                }

            }
        };
        proceso.execute(idField, info, row, namedPath);
    }

    private void loadFormularioChild(final int parent, final int idForm, final int row, final String childPath) {
        Proceso proceso = new Proceso("loadFormularioChild", "Cargando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object result) {
                super.onPostExecute(result);
                listener.loadFormulario(parent, idForm, result.toString(), row, childPath);
            }
        };
        proceso.execute(parent, row, childPath);
    }

    public void nextWorkFlowStep(final HashMap<String, Field> info, final boolean nextStep) {
        Proceso proceso = new Proceso("nextWorkFlowStep", "Actualizando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                WorkFlowStep step = (WorkFlowStep) o;
                if (nextStep) {
                    if (step.IsEnd) {
                        listener.printReporte();
                    }
                    else {
                        if (step.NextStep > 0) {
                            updateInfo(step.getWorkFlowNext().ID, info, true);
                        }
                        else {
                            if (step.getOptions().size() > 0) {
                                listener.selectOption(step, info);
                            }
                            else {
                                updateInfo(step.ID, info, true);
                            }
                        }
                    }
                }
                else {
                    updateInfo(step.ID, info, false);
                }
            }
        };
        proceso.execute(nextStep);
    }

    private void removeRow(final int idField, int row) {
        Proceso proceso = new Proceso("removeRow", "Actualizando informaci\u00f3n", true) {
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener.getComponente(idField) instanceof Componente.Grid) {
                    Componente.Grid grid = (Componente.Grid) listener.getComponente(idField);
                    if (grid != null) {
                        loadValuesGrid(grid);
                    }
                }
                else if (listener.getComponente(idField) instanceof Componente.GalleryComponent) {
                    Componente.GalleryComponent grid = (Componente.GalleryComponent) listener.getComponente(idField);
                    if (grid != null) {
                        loadValuesCarousel(grid);
                    }
                }
                else if (listener.getComponente(idField) instanceof Componente.MultiPoint) {
                    Componente.MultiPoint grid = (Componente.MultiPoint) listener.getComponente(idField);
                    if (grid != null) {
                        loadValuesMultiPoint(grid);
                    }
                }
            }
        };
        proceso.execute(idField, row);
    }

    @Override
    public void subscribe(BaseController.Listener listener) {
    }

    private class Proceso extends AsyncTask<Object, Object, Object> {

        private String operation;
        private String mensaje;
        private boolean progressDialog;

        public Proceso(String operation, String mensaje, boolean progressDialog) {
            this.operation = operation;
            this.mensaje = mensaje;
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog)
                listener.mostrarProgress(mensaje);
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                if (operation.equals("loadDocumento") && params.length == 0) {
                    return loadDocumento();
                }
                else if (operation.equals("loadDocumentoOffline")) {
                    return loadDocumentosOffline();
                }
                else if (operation.equals("loadDocumento") && params.length == 1) {
                    return loadDocumento((int) params[0]);
                }
                else if (operation.equals("loadFormulario") && params.length == 2) {
                    return loadFormulario((int) params[0], params[1].toString());
                }
                else if (operation.equals("loadFormulario")) {
                    return loadFormulario((int) params[0], (int) params[1], (int) params[2]);
                }
                else if (operation.equals("cargarUsuario")) {
                    return cargarUsuario();
                }
                else if (operation.equals("loadWorflowSteps")) {
                    return loadWorflowSteps((int) params[0], (int) params[1]);
                } else if (operation.equals("loadDocumentoAsignado") && params.length == 2) {
                    return loadDocumentoAsignado((int)params[0], (int) params[1]);
                }
                else if (operation.equals("loadDocumentoAsignado")) {
                    return loadDocumentoAsignado((int) params[0], (long) params[1], (String) params[2]);
                }
                else if (operation.equals("updateInfo")) {
                    updateInfo((HashMap<String, Field>) params[1]);
                    updateDocAsignado((int) params[0]);
                }
                else if (operation.equals("uploadDocumentInstance")) {
                    sincronizar(params[0].toString());
                }
                else if (operation.equals("getWorkflow")) {
                    return getWorkflow((int)params[0], (int)params[1], (int)params[2]);
                }
                else if (operation.equals("loadInfoFormulario")) {
                    return loadInfoFormulario((HashMap<String, Field>) params[0]);
                }
                else if (operation.equals("loadInfoGrid")) {
                    loadInfoGrid((int)params[0],(String) params[1],(int)params[2], params[3].toString());
                }
                else if (operation.equals("loadFormularioChild")) {
                    return loadFormularioChild((int)params[0],(int) params[1], params[2].toString());
                }
                else if (operation.equals("nextWorkFlowStep")) {
                    return nextWorkFlowStep();
                }
                else if (operation.equals("removeRow")) {
                    removeRow((int)params[0], (int)params[1]);
                }
                else if (operation.equals("loadWorkFlow")) {
                    return loadWorkFlow((int)params[0], (int)params[1]);
                }
                else if (operation.equals("loadWorkFlowWithInstances")) {
                    return loadWorkFlowSteps();
                }
            }
            catch (Exception ex) {
                return ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (progressDialog)
                listener.ocultarProgress();
            if (o instanceof String && o.toString().startsWith("{\"Error\":")) {
                JsonParser parser = new JsonParser();
                String error = parser.parse(o.toString()).getAsJsonObject().get("Error").getAsString();
                listener.notificarUsuario("Error", error);
            }
        }

        private WorkFlowStep getWorkflow(int idWorkflowStep, int idDocumento, int version) {
            MWorkflow mWorkflow = newInstanceModel(MWorkflow.class);
            return mWorkflow.getDetail(idWorkflowStep);
        }

        private List<Documento> loadDocumento() {
            MDocumento mDocumento = newInstanceModel(MDocumento.class);
            return mDocumento.loadDocumento();
        }

        private List<Documento> loadDocumentosOffline() {
            MDocumento mDocumento = newInstanceModel(MDocumento.class);
            return mDocumento.loadDocumentoOffline();
        }

        private Documento loadDocumento(int idDocumento) {
            MDocumento mDocumento = newInstanceModel(MDocumento.class);
            return mDocumento.getDetail(idDocumento);
        }

        private Formulario loadFormulario(int idDocumento, int version, int idWorkflowStep) {
            MWorkflow mWorkflow = newInstanceModel(MWorkflow.class);
            WorkFlowStep workflow = mWorkflow.getDetail(idWorkflowStep, idDocumento, version);
            MFormulario mFormulario = newInstanceModel(MFormulario.class);
            return mFormulario.getDetail(workflow.IdFormulario);
        }

        private Formulario loadFormulario(int idFormulario, String contenido) {
            MFormulario mFormulario = newInstanceModel(MFormulario.class);
            Formulario formulario = mFormulario.getDetail(idFormulario);
            formulario.setDataValue(contenido);
            xmlHandler.loadAttributesValues(formulario.getDataValue());
            return formulario;
        }

        private DocumentoAsignado loadDocumentoAsignado(int idDocumento, long fecha, String folio) {
            MDocumentoAsignado mDocAsignado = newInstanceModel(MDocumentoAsignado.class);
            MWorkflow mWorkflow = newInstanceModel(MWorkflow.class);
            MFormulario mFormulario = newInstanceModel(MFormulario.class);
            MDocumentInstance mInstance = newInstanceModel(MDocumentInstance.class);
            DocumentoAsignado docAsignado = mDocAsignado.getDetail(idDocumento, fecha, folio);
            docAsignado.setWorkflow(mWorkflow.getDetail(docAsignado.IdWorkflowStep));
            docAsignado.getWorkflow().setFormulario(mFormulario.getDetail(docAsignado.getWorkflow().IdFormulario));
            docAsignado.setDocumentInstance(mInstance.getDocumentInstance(docAsignado.IdDocumento, docAsignado.Version, docAsignado.Folio));
            xmlHandler.loadAttributesValues(docAsignado.getDataValue());
            jsonHandler.setJson(docAsignado.getDocumentInstance().ContenidoJson);
            return docAsignado;
        }

        private Usuario cargarUsuario() {
            MConfiguracion mConfiguracion = newInstanceModel(MConfiguracion.class);
            int idUsuario = mConfiguracion.getUsuarioLogIn();
            MUsuario mUsuario = newInstanceModel(MUsuario.class);
            Usuario usuario = mUsuario.getDetail(idUsuario);
            MPersona mPersona = newInstanceModel(MPersona.class);
            Persona persona = mPersona.getDetail(usuario.IdPersona);
            usuario.setPersona(persona);
            return usuario;
        }

        private List<String> loadWorflowSteps(int idDocumento, int version) {
            MWorkflow mWorkflow = newInstanceModel(MWorkflow.class);
            return mWorkflow.loadWorflowSteps(idDocumento, version);
        }

        private List<DocumentoAsignado> loadDocumentoAsignado(int idDocumento, int idWorkFlowStep) {
            MDocumentoAsignado mDocAsignado = newInstanceModel(MDocumentoAsignado.class);
            MVersionInfo mVersionInfo = newInstanceModel(MVersionInfo.class);
            List<DocumentoAsignado> listDocAsignado = mDocAsignado.loadDocumentoAsignado(idDocumento, idWorkFlowStep);
            for (DocumentoAsignado docAsignado : listDocAsignado) {
                VersionInfo versionInfo = mVersionInfo.getDetail(docAsignado.IdDocumento, docAsignado.Version);
                DAODocumentInstance daoDocumentInstance = new DAODocumentInstance(listener.getContext());
                DocumentInstance docInstance = daoDocumentInstance.getDetail(docAsignado.IdDocumento, docAsignado.Version, docAsignado.Folio);
                JsonHandler jsonHandler = new JsonHandler();
                jsonHandler.setJson(docInstance.ContenidoJson);
                for (Column column : versionInfo.getListColumns()) {
                    if (column != null && column.xmlPath != null)
                        column.value = jsonHandler.getValue(column.xmlPath.replace("/", "\\").replace("@", "")).toString();
                }
                docAsignado.setVersionInfo(versionInfo);
            }
            return listDocAsignado;
        }

        private List<WorkFlowStep> loadWorkFlowSteps() {
            MWorkflow mWorkflow = newInstanceModel(MWorkflow.class);
            MDocumentoAsignado mDocAsignado = newInstanceModel(MDocumentoAsignado.class);
            MVersionInfo mVersionInfo = newInstanceModel(MVersionInfo.class);
            List<WorkFlowStep> steps = new ArrayList<>();
            WorkFlowStep step = mWorkflow.getStartStep(9, 1);
            if (step != null && step.NextStep > 0) {
                steps.add(step);
                int next = step.NextStep;
                do  {
                    WorkFlowStep nextStep = mWorkflow.getDetail(next, 9, 1);
                    if (nextStep != null && !(next == nextStep.NextStep)) {
                        next = nextStep.NextStep;
                        steps.add(nextStep);
                    }
                    else {
                        next = 0;
                    }
                } while (next > 0);
            }
            for (WorkFlowStep workFlowStep : steps) {
                List<DocumentoAsignado> listDocAsignado = mDocAsignado.loadDocumentoAsignado(9, 1, workFlowStep.ID);
                for (DocumentoAsignado docAsignado : listDocAsignado) {
                    VersionInfo versionInfo = mVersionInfo.getDetail(docAsignado.IdDocumento, docAsignado.Version);
                    DAODocumentInstance daoDocumentInstance = new DAODocumentInstance(listener.getContext());
                    DocumentInstance docInstance = daoDocumentInstance.getDetail(docAsignado.IdDocumento, docAsignado.Version, docAsignado.Folio);
                    JsonHandler jsonHandler = new JsonHandler();
                    jsonHandler.setJson(docInstance.ContenidoJson);
                    for (Column column : versionInfo.getListColumns()) {
                        if (column != null && column.xmlPath != null)
                            column.value = jsonHandler.getValue(column.xmlPath.replace("/", "\\").replace("@", "")).toString();
                    }
                    docAsignado.setVersionInfo(versionInfo);
                    workFlowStep.getDocsAsignados().add(docAsignado);
                }
            }
            return steps;
        }

        private List<WorkFlowStep> loadWorkFlow(int idDocumento, int version) {
            Object value = listener.getConfiguracion(boolean.class, "pref_documentos");
            MWorkflow mWorkflow = newInstanceModel(MWorkflow.class);
            MDocumentInstance mDocInstance = newInstanceModel(MDocumentInstance.class);
            List<WorkFlowStep> listWorkFlowStep = mWorkflow.getWorkflowByDocumento(idDocumento, version);
            for (WorkFlowStep step : listWorkFlowStep) {
                step.getDocsAsignados().addAll(loadDocumentoAsignado(step.IdDocumento, step.IdWorkflowStep));
            }
            if (!(Boolean) value) {
                for (Iterator<WorkFlowStep> iterator = listWorkFlowStep.iterator(); iterator.hasNext();) {
                    WorkFlowStep step = iterator.next();
                    if (step.getDocsAsignados().size() == 0) {
                        iterator.remove();
                    }
                }
            }
            for (WorkFlowStep workFlowStep : listWorkFlowStep) {
                for (DocumentoAsignado docAsignado : workFlowStep.getDocsAsignados()) {
                    docAsignado.setDocumentInstance(mDocInstance.getDocumentInstance(docAsignado.IdDocumento, docAsignado.Version, docAsignado.Folio));
                }
            }
            return listWorkFlowStep;
        }

        private void updateInfo(HashMap<String, Field> fields) {
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                if (entry.getValue().ReadOnly) {
                    continue;
                }
                if (entry.getValue().Tag.equals("LOCATION")) {
                    if (entry.getValue().getValueString().equals("")) {
                        JsonObject jEmpty = new JsonObject();
                        jEmpty.addProperty("lat", 0);
                        jEmpty.addProperty("lng", 0);
                        jsonHandler.setValue(entry.getValue().NamedPath, jEmpty.toString());
                        xmlHandler.setValue(entry.getValue().getNamedPath(), jEmpty.toString());
                    }
                    else {
                        jsonHandler.setValue(entry.getValue().NamedPath, new JsonParser().parse(entry.getValue().getValueString()));
                        xmlHandler.setValue(entry.getValue().getNamedPath(), entry.getValue().getValueString());
                    }
                }
                else {
                    jsonHandler.setValue(entry.getValue().NamedPath, entry.getValue().getValueString(), entry.getValue().TipoDato);
                    xmlHandler.setValue(entry.getValue().getNamedPath(), entry.getValue().getValueString());
                }
            }
            String info = xmlHandler.createDataNamePathXml();
            listener.getDataField().setDataValue(info);
            if (listener.getDataField() instanceof  DocumentoAsignado) {
                DocumentInstance docInstance = ((DocumentoAsignado) listener.getDataField()).getDocumentInstance();
                docInstance.ContenidoJson = jsonHandler.getJson();
            }
        }

        private void updateDocAsignado(int idNextStep) throws Exception {
            if (listener.getDataField() instanceof DocumentoAsignado) {
                MDocumentoAsignado mDocumentoAsignado = newInstanceModel(MDocumentoAsignado.class);
                mDocumentoAsignado.applyWorkflow((DocumentoAsignado)listener.getDataField(), idNextStep);
            }
        }

        private String loadFormularioChild(int parent, int row, String childPath) {
            if (row == 0) {
                return xmlHandler.createPathXml(childPath, false);
            }
            try {
                XmlHandler handler = new XmlHandler();
                String xml = handler.createPathXml(childPath, false);
                handler.loadAttributesValues(xml);
                List<DataNamePath> datas = xmlHandler.getChilds(childPath);
                DataNamePath dataNamePath = datas.get(row - 1);
                DataNamePath parentData = handler.getNamePath(childPath);
                parentData.addChild((DataNamePath)dataNamePath.clone());
                return handler.createDataNamePathXml();
            }
            catch (Exception ex) {
                Log.e(getClass().getSimpleName(), ex.toString());
            }
            return null;
        }

        private String loadInfoFormulario(HashMap<String, Field> fields) {
            for (Map.Entry<String, Field> entry : fields.entrySet()) {
                xmlHandler.setValue(entry.getValue().getNamedPath(), entry.getValue().getValueString());
            }
            return xmlHandler.createDataNamePathXml();
        }

        private void loadInfoGrid(int idField, String info, int row, String namedPath) {
            XmlHandler handler = new XmlHandler();
            handler.loadAttributesValues(info);
            List<DataNamePath> listChilds = handler.getChilds(namedPath);
            DataNamePath dataNamePath = xmlHandler.getNamePath(namedPath);
            if (listChilds.size() == 1) {
                if (row == 0) {
                    dataNamePath.getChildNamePath().add(row, listChilds.get(0));
                }
                else {
                    dataNamePath.getChildNamePath().set(row - 1, listChilds.get(0));
                }
            }
        }

        private void removeRow(int idField, int row) {
            MField mField = new MField(listener.getContext());
            Field field = mField.getDetail(idField);
            List<DataNamePath> listChilds = xmlHandler.getChilds(field.getNamedPath());
            listChilds.remove(row - 1);
            String xml = xmlHandler.createDataNamePathXml();
            xmlHandler.loadAttributesValues(xml);
        }

        private void sincronizar(String workStation) throws Exception  {
            Object value = listener.getConfiguracion(int.class, "pref_image_size");
            MConfiguracion mConfiguracion = newInstanceModel(MConfiguracion.class);
            int idUsuario = mConfiguracion.getUsuarioLogIn();
            MDocumentInstance mDocInstance = newInstanceModel(MDocumentInstance.class);
            mDocInstance.uploadDocumentInstance();
            // Actualizar la última fecha de sincronización
            Configuracion configuracion = new Configuracion();
            configuracion.Path  = "Device";
            configuracion.Name  = "Sincronizacion";
            configuracion.Value = String.valueOf(new Date().getTime());
            mConfiguracion.updateConfiguracion(configuracion);
            try {
                // TODO: Verificar si eliminar la información
                mConfiguracion.cleanInfo();
            }
            catch (Exception ex) {
                Log.e(getClass().getSimpleName(), ex.toString());
            }
        }

        private WorkFlowStep nextWorkFlowStep() throws Exception {
            DocumentoAsignado docAsignado = (DocumentoAsignado) listener.getDataField();
            MDocumentoAsignado mDocAsignado = newInstanceModel(MDocumentoAsignado.class);
            return mDocAsignado.loadWorkFlowOptions(docAsignado.IdWorkflowStep, docAsignado.IdDocumento, docAsignado.Version);
        }
    }

    public interface Listener extends IControlador {
        void loadDocumento(List<Documento> listDocumento);
        View addView(View parent, String title);
        View addView(View parent, View view);
        void setTitle(String title);
        View addTabContainer(String title, final FormularioActivity.Listener listener);
        Activity getAcivity();
        Context getContext();
        void loadWorflowSteps(List<String> listInfo);
        void loadWorkflow(List<WorkFlowStep> list);
        void loadDocumentoAsignado(List<DocumentoAsignado> list);
        void capturarImagen(int id);
        void formularaioGuardado();
        void iniciarEdicion(DocumentoAsignado documentoAsignado);
        void loadFormulario(int parent, int idForm, String baseInfo, int row, String namedPath);
        void formularioCompletado(String formulario);
        void setDataField(DataField dataField);
        void addUnbindTool(Componente.Tool tool);
        DataField getDataField();
        Componente.Tool getComponente(int idField);
        void selectOption(WorkFlowStep workFlowStep, HashMap<String, Field> info);
        Object getConfiguracion(Class<?> type, String key);
        void previewImage(String image);
        Object getData(String key);
        void printReporte();
        void loadStartOptions();
        void loadWorkflowOptions();
        void loadEndOptoons();
    }

}
