package mx.com.azteca.home.util;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import mx.com.azteca.home.model.ipati.pojo.Field;
import mx.com.azteca.home.model.ipati.pojo.Dataset;
import mx.com.azteca.home.service.DataLoaderService;
import mx.com.azteca.home.service.LocalizacionServices;
import mx.com.azteca.home.view.ipati.FormularioActivity;
import mx.com.azteca.home.view.ipati.util.ContentView;
import mx.com.azteca.home.view.ipati.util.DialogResponse;
import mx.com.azteca.home.ui.Gallery;
import mx.com.azteca.home.ui.MapDialog;
import mx.com.azteca.home.R;

public class Componente {

    public static Tool SUBVIEW;

    private Listener listener;

    private Componente(Listener listener) {
        this.listener = listener;
    }

    public static synchronized Componente getInstance(Listener listener) {
        return new Componente(listener);
    }

    public Object getComponente(Context context, String defincion) {
        Field field = getField(defincion);
        return getComponente(context, field);
    }

    public Object getComponente(Context context, Field field) {
        switch (field.TipoControl) {

            case "MEMO":
            case "TEXTBOX":
            case "NUMERICINPUT":
                TextBox textBox = new TextBox(context);
                textBox.setField(field);
                textBox.prepare();
                return textBox;

            case "COMBOBOX":
                ComboBox comboBox = new ComboBox(context);
                comboBox.setField(field);
                comboBox.prepare();
                return comboBox;

            case "IMAGEBOX":
                ImageBox imgBox = new ImageBox(context);
                imgBox.setField(field);
                imgBox.prepare();
                return imgBox;

            case "CHECKBOX":
                CheckBoxInput chkInput = new CheckBoxInput(context);
                chkInput.setField(field);
                chkInput.prepare();
                return chkInput;

            case "MAP":
                MapLocation mapLocation = new MapLocation(context);
                mapLocation.setField(field);
                mapLocation.prepare();
                return mapLocation;

            case "DATEPICKER":
                DateTimePicker datePicker = new DateTimePicker(context);
                datePicker.setField(field);
                datePicker.prepare(true, false);
                return datePicker;

            case "TIMEPICKER":
                DateTimePicker timePicker = new DateTimePicker(context);
                timePicker.setField(field);
                timePicker.prepare(false, true);
                return timePicker;

            case "DATETIMEPICKER":
                DateTimePicker dateTimePicker = new DateTimePicker(context);
                dateTimePicker.setField(field);
                dateTimePicker.prepare(true, true);
                return dateTimePicker;

            case "SELECTOR":
                field.Etiqueta = "Enfoque de mercado:";
                Selector selector2 = new Selector(context);
                selector2.setField(field);
                selector2.prepare(false, true, true);
                return selector2;

            case "SELECTOR_MULTIPLE":
                field.Etiqueta = "Infraestructura disponible en la zona:";
                Selector selector = new Selector(context);
                selector.setField(field);
                selector.prepare(true, false, false);
                return selector;

            case "POLYGON":
                MapPolygon mapPolygon = new MapPolygon(context);
                mapPolygon.setField(field);
                mapPolygon.prepare();
                return mapPolygon;

            case "GRID":
                Grid grid = new Grid(context);
                grid.setField(field);
                grid.prepare();
                return grid;

            case "LABEL":
                Label label = new Label(context);
                label.setField(field);
                label.prepare();
                return label;

            case "CAROUSEL":
                GalleryComponent gallery = new GalleryComponent(context);
                gallery.setField(field);
                gallery.prepare();
                return gallery;


            case "MULTIPOINTMAP":
                MultiPoint multiPoint = new MultiPoint(context);
                multiPoint.setField(field);
                multiPoint.prepare();
                return multiPoint;
        }
        return null;
    }

    private Field getField(String data) {
        JsonParser parser = new JsonParser();
        JsonObject jControl = parser.parse(data).getAsJsonObject();
        JsonObject jField = jControl.get("Field").getAsJsonObject();

        Field field = new Field();

        field.Identity              = jField.get("Identity").getAsInt();
        field.Code                  = jField.get("Code").getAsString();
        field.Nombre                = jField.get("Nombre").getAsString();
        field.IdDocumento           = jField.get("IdDocumento").getAsInt();
        field.Version               = jField.get("Version").getAsInt();
        field.Etiqueta              = jField.get("Etiqueta").getAsString();
        field.Orden                 = jField.get("Orden").getAsInt();
        field.TipoDato              = jField.get("TipoDato").getAsJsonObject().get("Type").getAsString();
        field.Tag                   = jField.get("TipoDato").getAsJsonObject().get("Tag").getAsString();
        field.IdDataset             = jField.get("IdDataset").getAsInt();
        field.DisplayMember         = jField.get("DisplayMember").getAsString();
        field.ValueMember           = jField.get("ValueMember").getAsString();
        field.XmlTag                = jField.get("XmlTag").getAsString();
        field.XmlPath               = jField.get("XmlPath").getAsString();
        field.NamedPath             = jField.get("NamedPath").getAsString();
        field.OcurrenciaMin         = jField.get("OcurrenciaMinima").getAsInt();
        field.OcurrenciaMax         = jField.get("OcurrenciaMaxima").getAsInt();
        field.Nivel                 = jField.get("Nivel").getAsInt();
        field.Coleccion             = jField.get("Coleccion").getAsBoolean();
        field.CampoCorrelacion      = jField.get("CampoCorrelacion").getAsInt();
        field.ExpresionCorrelacion  = jField.get("ExpresionCorrelacion").getAsString();
        field.Parametros            = jField.get("Parametros").toString();

        if (jControl.has("ReadOnly")) {
            field.ReadOnly = jControl.get("ReadOnly").getAsBoolean();
        }

        if (jControl.has("IdCampo")) {
            field.setIdCampo(jControl.get("IdCampo").getAsInt());
        }

        if (jControl.has("IdChildForm")) {
            field.setIdChilForm(jControl.get("IdChildForm").getAsInt());
        }

        if (jControl.has("Control")) {
            field.TipoControl = jControl.get("Control").getAsJsonObject().get("Tag").getAsString();
        }

        if (jControl.has("Columns")) {
            for (JsonElement jColumn : jControl.get("Columns").getAsJsonArray()) {
                field.getFields().add(getField(jColumn.toString()));
            }
        }

        return field;
    }

    public interface Tool {

        public Field getField();

        public void setField(Field field);

        public boolean isValid();

        public void prepare();

        public View getEtiqueta();

        Object getValue();

        View getView();

        void setValue(Object value);

        void loadLocation(double latitude, double longitude, double accuracy, double altitude);

        void subscribe(Componente.Tool tool);

        // Notifica el cambio de valor al campo correlacionado
        void notifyObserver(Componente.Tool tool, Object value);

        // Notifica el campo parametrizado
        void notifyParameter();

        // CampoOriteg
        void subscribeParameter(Componente.Tool tool);

        // Campo DataSet
        void addParameterComponent(String key, Componente.Tool tool);


    }

    public interface Listener {
        Dataset getDataset(int idDataset);

        Activity getActivity();

        Context getContext();

        void capturarImagen(int id);

        void loadChildForm(int parent, int idForm, int row, String childPath);

        void deleteForm(int parent, int idForm, int row);

        void onChangeDataSetField(int idParent, String valueMember, String value);

        HashMap<String, String> getParamFilters();

        String loadDatasetInfo(String table, String[] columns, String[] params);

        void loadDatasetInfo(ComboBox comboBox, String table, String[] columns, String[] params);

        void previewImage(String image);

        LatLng getDocPosition();
    }

    public class Label implements Tool {

        private Context context;
        private Field field;
        private View view;

        public Label(Context context) {
            this.context = context;
            this.view = View.inflate(context, R.layout.widget_label, null);
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void prepare() {
            ((TextView) view.findViewById(R.id.lblEtiqueta)).setText(field.Etiqueta);

            if (field.TipoDato != null && field.TipoDato.equals("System.Decimal")) {
                try {
                    double valor  = Double.parseDouble(field.Value != null ? field.Value.toString() : "0");
                    switch (field.TipoDato) {
                        case "System.Int32":
                        case "System.Int64":
                            ((TextView) view.findViewById(R.id.lblInfo)).setText(Functions.getInstance(Functions.Format.INTEGER).applyNumber(valor));
                        case "System.Decimal":
                            ((TextView) view.findViewById(R.id.lblInfo)).setText(Functions.getInstance(Functions.Format.DECIMAL).applyNumber(valor));
                            break;
                    }
                }
                catch (Exception ex) {
                    ((TextView) view.findViewById(R.id.lblInfo)).setText(field.Value != null ? field.Value.toString() : "");
                }
            }
            else {
                ((TextView) view.findViewById(R.id.lblInfo)).setText(field.Value != null ? field.Value.toString() : "");
            }
        }

        @Override
        public View getEtiqueta() {
            return null;
        }

        @Override
        public Object getValue() {
            return field.Value;
        }

        @Override
        public void setValue(Object value) {
            field.Value = value;
            ((TextView) view.findViewById(R.id.lblInfo)).setText(field.Value.toString());
            notifyObserver();
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }

        public View getView() {
            return view;
        }

        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {

        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }
    }

    public class TextBox extends EditText implements Tool {

        private Field field;
        private TextView textField;

        public TextBox(Context context) {
            super(context);
            setBackgroundResource(R.drawable.edit_text_green);
            setTextColor(getResources().getColor(R.color.text_black_100));

            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    field.Value = editable.toString();
                }
            });
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public boolean isValid() {
            switch (field.TipoDato) {
                case "System.Decimal":
                    setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    try {
                        double valor = Double.parseDouble(getValue().toString());
                    } catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                        return false;
                    }
                    break;
                case "System.Int32":
                    try {
                        int valor = Integer.parseInt(getValue().toString());
                    } catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                        return false;
                    }
                    break;
            }
            return (field.ReadOnly || field.OcurrenciaMin == 0 || getValue().toString().trim().length() >= field.OcurrenciaMin);
        }

        @Override
        public View getEtiqueta() {
            if (field.Etiqueta != null) {
                if (field.Etiqueta.trim().length() > 0) {
                    if (textField == null) {
                        textField = new TextView(getContext());
                        textField.setText(field.Etiqueta);
                        if (field.OcurrenciaMin > 0) {
                            textField.setTextColor(getResources().getColor(R.color.md_red_400));
                        } else {
                            textField.setTextColor(getResources().getColor(R.color.black_alpha_50));
                        }
                    }
                }
            }
            return textField;
        }

        public void prepare() {
            setEnabled(!field.ReadOnly);
            if (field.TipoControl.equals("MEMO")) {
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                setLines(6);
                setGravity(Gravity.TOP | Gravity.START | Gravity.LEFT);
            } else {
                switch (field.TipoDato) {
                    case "System.String":
                        setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case "System.Decimal":
                        setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                    case "System.Int32":
                        setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;
                    case "System.DateTime":
                        setInputType(InputType.TYPE_CLASS_DATETIME);
                        break;
                }
            }
        }

        @Override
        public Object getValue() {
            return super.getText().toString();
        }

        @Override
        public void setValue(Object value) {
            if (value != null) {
                setText(value.toString());
            }
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }

        @Override
        public View getView() {
            return this;
        }

        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        @Override
        protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
            notifyObserver();
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
            if (value != null && value instanceof String) {
                String expresiones = getField().ExpresionCorrelacion;
                for (String expresion : expresiones.split("\\|")) {
                    if (expresion.equals(value))  {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.VISIBLE);
                        this.setVisibility(View.VISIBLE);
                        break;
                    }
                    else {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.GONE);
                        this.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (getEtiqueta() != null) {
                    getEtiqueta().setVisibility(View.GONE);
                    this.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void notifyParameter() {
        }


        List<Componente.Tool> observableParametros;
        @Override
        public void subscribeParameter(Tool tool) {
            if (observableParametros == null)
                observableParametros = new ArrayList<>();
            observableParametros.add(tool);
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
            if (observableParametros != null) {
                for (Componente.Tool tool :  observableParametros) {
                    tool.notifyParameter();
                }
            }
        }

    }

    public class ComboBox extends Spinner implements Tool {

        private Object value;

        public class Info {
            Object DisplayMember;
            Object ValueMember;

            @Override
            public String toString() {
                return DisplayMember.toString();
            }
        }

        private Field field;
        private TextView textField;
        private Dataset dataset;
        private JsonArray jData;

        private String lastValueFilter;

        public ComboBox(Context context) {
            super(context);
        }

        @Override
        public Field getField() {
            return this.field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return (field.ReadOnly || field.OcurrenciaMin == 0 || field.Value != null);
        }

        @Override
        public void prepare() {
            if (listener != null) {
                try {
                    setEnabled(!field.ReadOnly);
                    if (field.ReadOnly) {
                        this.setBackground(listener.getActivity().getDrawable(R.drawable.field_disabled));
                    }
                    else {
                        this.setBackground(listener.getActivity().getDrawable(R.drawable.spinner_bg));
                    }

                    setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            Info info = (Info) adapterView.getAdapter().getItem(i);
                            if (field.Value == null || !field.Value.toString().equals(info.ValueMember.toString())) {
                                field.Value = info.ValueMember;
                                listener.onChangeDataSetField(field.Identity, field.ValueMember.toUpperCase(), field.Value.toString());

                            }
                            notifyObserver();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    this.dataset = listener.getDataset(field.IdDataset);
                    JsonParser parser = new JsonParser();
                    if (this.dataset.Dataset.length() > 0 && !this.dataset.Dataset.equals("null")) {
                        jData = parser.parse(this.dataset.Dataset).getAsJsonArray();
                    }

                    notifyDataSetChange(false);
                } catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
            }
        }

        public Dataset getDataset() {
            return dataset;
        }

        public void notifyDataSetChange(boolean refresch) {
            try {
                if (this.dataset != null && (jData != null || this.dataset.Tipo.equals("S"))) {
                    if (this.dataset.Tipo.equals("S")) {
                        HashMap<String, String> params = new HashMap<>();
                        if (toolParametros == null) {
                            toolParametros = new HashMap<>();
                        }
                        for (Map.Entry<String, Componente.Tool> entry : toolParametros.entrySet()) {
                            if (!(this.dataset.Identity == 22 && entry.getKey().equals("CodigoPostal")) || !DataLoaderService.DATA_LOADED) {
                                params.put(entry.getKey(), entry.getValue().getValue() != null ? entry.getValue().getValue().toString() :  "Null");
                            }
                        }
                        if (this.dataset.Identity == 22 && DataLoaderService.DATA_LOADED) {
                            this.dataset.getNameParametro().remove("CodigoPostal");
                        }
                        if (params.size() >= this.dataset.getNameParametro().size()) {
                            String[] keys = new String[params.size()];
                            String[] values = new String[params.size()];
                            //
                            params.keySet().toArray(keys);
                            params.values().toArray(values);
                            // El componente solicita la información al controlador
                            listener.loadDatasetInfo(ComboBox.this, this.dataset.getNameTableInfo(), keys, values);
                        }
                        else {
                            setAdapter(null);
                        }
                    }
                    else {
                        HashMap<String, String> params = new HashMap<>();
                        if (toolParametros == null)
                            toolParametros = new HashMap<>();

                        for (Map.Entry<String, Componente.Tool> entry : toolParametros.entrySet()) {
                            params.put(entry.getValue().getField().Nombre, entry.getValue().getValue() != null ? entry.getValue().getValue().toString() :  "Null");
                        }

                        List<Info> listAdapter = new ArrayList<>();
                        String valueFilter = null;
                        String valueMember = null;

                        if (jData.size() > 0) {
                            JsonObject jInfo = jData.get(0).getAsJsonObject();
                            for (Map.Entry<String, String> entry : params.entrySet()) {
                                if (!entry.getKey().equals(field.ValueMember.toUpperCase()) && jInfo.get(entry.getKey()) != null) {
                                    valueMember = entry.getKey();
                                    valueFilter = entry.getValue();
                                    break;
                                }
                            }
                        }
                        if (refresch) {
                            if (valueMember == null) {
                                return;
                            } else if (lastValueFilter != null && lastValueFilter.equals(valueFilter)) {
                                return;
                            } else {
                                lastValueFilter = valueFilter;
                            }
                        }
                        for (JsonElement jElement : jData) {
                            JsonObject jObject = jElement.getAsJsonObject();
                            Info info = new Info();

                            String displayDataSet;
                            String valueDataSet;

                            if (jObject.has(field.DisplayMember))
                                displayDataSet = field.DisplayMember;
                            else
                                displayDataSet = field.DisplayMember.substring(0, 1).toLowerCase() + field.DisplayMember.substring(1, field.DisplayMember.length());

                            if (jObject.has(field.ValueMember))
                                valueDataSet = field.ValueMember;
                            else
                                valueDataSet = field.ValueMember.substring(0, 1).toLowerCase() + field.ValueMember.substring(1, field.ValueMember.length());

                            info.DisplayMember = jObject.get(displayDataSet).getAsString();
                            info.ValueMember = jObject.get(valueDataSet).getAsString();

                            if (valueMember != null) {
                                if (!jObject.get(valueMember).getAsString().equals(valueFilter)) {
                                    continue;
                                }
                            }

                            listAdapter.add(info);
                        }

                        final ListAdapter adapter = new ListAdapter(getContext(), R.layout.list_item_view, listAdapter) {
                            @Override
                            public void onEntry(Object entry, View view, int position) {
                                Info info = (Info) entry;
                                ((TextView) view.findViewById(R.id.lblNombre)).setText(info.DisplayMember.toString());
                            }

                            @Override
                            public void notifyDataSetChanged() {
                                super.notifyDataSetChanged();
                                setValue(value);
                            }
                        };

                        setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                    setValue(field.Value);

                }
            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(), ex.toString());
            }
            finally {
                notifyObserver();
            }
        }

        public void loadDatasetInfo(String dataInfo) {
            JsonParser parser = new JsonParser();
            List<Info> listAdapter = new ArrayList<>();
            //
            for (JsonElement jElement : parser.parse(dataInfo).getAsJsonArray()) {
                JsonObject jObject = jElement.getAsJsonObject();
                Info info = new Info();
                if (this.dataset.Identity == 19) {
                    // 19: Estados
                    if (jObject.has("IdEstado")) {
                        info.ValueMember = jObject.get("IdEstado").getAsString();
                    }
                    else if (jObject.has("idEstado")) {
                        info.ValueMember = jObject.get("idEstado").getAsString();
                    }
                    if (jObject.has("Nombre")) {
                        info.DisplayMember = jObject.get("Nombre").getAsString();
                    }
                    else if (jObject.has("nombre")) {
                        info.DisplayMember = jObject.get("nombre").getAsString();
                    }
                }
                else if (this.dataset.Identity == 21) {
                    // 21: Municipios
                    if (jObject.has("IdMunicipio")) {
                        info.ValueMember = jObject.get("IdMunicipio").getAsString();
                    }
                    else if (jObject.has("idMunicipio")) {
                        info.ValueMember = jObject.get("idMunicipio").getAsString();
                    }
                    if (jObject.has("Nombre")) {
                        info.DisplayMember = jObject.get("Nombre").getAsString();
                    }
                    else if (jObject.has("nombre")) {
                        info.DisplayMember = jObject.get("nombre").getAsString();
                    }
                }
                else if (this.dataset.Identity == 22) {
                    // 22: Poblaciones
                    if (jObject.has("IdPoblacon")) {
                        info.ValueMember = jObject.get("IdPoblacon").getAsString();
                    }
                    else if (jObject.has("idPoblacon")) {
                        info.ValueMember = jObject.get("idPoblacon").getAsString();
                    }
                    if (jObject.has("Nombre")) {
                        info.DisplayMember = jObject.get("Nombre").getAsString();
                    }
                    else if (jObject.has("nombre")) {
                        info.DisplayMember = jObject.get("nombre").getAsString();
                    }
                }
                else if (this.dataset.Identity == 23) {
                    // 23: Colonias
                    if (jObject.has("IdColonia")) {
                        info.ValueMember = jObject.get("IdColonia").getAsString();
                    }
                    else if (jObject.has("idColonia")) {
                        info.ValueMember = jObject.get("idColonia").getAsString();
                    }
                    if (jObject.has("Nombre")) {
                        info.DisplayMember = jObject.get("Nombre").getAsString();
                    }
                    else if (jObject.has("nombre")) {
                        info.DisplayMember = jObject.get("nombre").getAsString();
                    }
                }
                else {
                    String displayDataSet;
                    String valueDataSet;
                    //
                    if (jObject.has(this.dataset.getQueryColumnas().get(0)))
                        displayDataSet = this.dataset.getQueryColumnas().get(0);
                    else
                        displayDataSet = this.dataset.getQueryColumnas().get(0).substring(0, 1).toLowerCase() + this.dataset.getQueryColumnas().get(0).substring(1, this.dataset.getQueryColumnas().get(0).length());
                    //
                    if (jObject.has(this.dataset.getQueryColumnas().get(1)))
                        valueDataSet = this.dataset.getQueryColumnas().get(1);
                    else
                        valueDataSet = this.dataset.getQueryColumnas().get(1).substring(0, 1).toLowerCase() + this.dataset.getQueryColumnas().get(1).substring(1, this.dataset.getQueryColumnas().get(1).length());
                    //
                    info.ValueMember = jObject.get(displayDataSet).getAsString();
                    info.DisplayMember = jObject.get(valueDataSet).getAsString();
                }
                listAdapter.add(info);
            }
            final ListAdapter adapter = new ListAdapter(getContext(), R.layout.list_item_view, listAdapter) {
                @Override
                public void onEntry(Object entry, View view, int position) {
                    Info info = (Info) entry;
                    ((TextView) view.findViewById(R.id.lblNombre)).setText(info.DisplayMember.toString());
                }

                @Override
                public void notifyDataSetChanged() {
                    super.notifyDataSetChanged();
                    setValue(value);
                }
            };
            setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        @Override
        public View getEtiqueta() {
            if (field.Etiqueta != null) {
                if (field.Etiqueta.trim().length() > 0) {
                    if (textField == null) {
                        textField = new TextView(getContext());
                        textField.setText(field.Etiqueta);
                        textField.setTextColor(getResources().getColor(R.color.black_alpha_50));
                    }
                }
            }
            return textField;
        }

        @Override
        public Object getValue() {
            Object object = getSelectedItem();
            if (object instanceof Info) {
                return ((Info) object).ValueMember;
            }
            return value;
        }

        @Override
        public void setValue(Object value) {
            if (value != null) {
                this.value = value;
                SpinnerAdapter adapter = getAdapter();
                if (adapter != null && !adapter.isEmpty()) {
                    for (int indice = 0; indice < adapter.getCount(); indice++) {
                        String valueMember = value instanceof  Info ? ((Info)value).ValueMember.toString() : value.toString();
                        Info info = (Info) adapter.getItem(indice);
                        if (info.ValueMember.toString().equals(valueMember)) {
                            setSelection(indice);
                            break;
                        }
                    }
                }
            }
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }

        @Override
        public View getView() {
            return this;
        }


        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
            if (value != null)
                value = value.toString();

            if (value != null) {
                String expresiones = getField().ExpresionCorrelacion;
                for (String expresion : expresiones.split("\\|")) {
                    if (expresion.equals(value))  {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.VISIBLE);
                        this.setVisibility(View.VISIBLE);
                        break;
                    }
                    else {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.GONE);
                        this.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (getEtiqueta() != null) {
                    getEtiqueta().setVisibility(View.GONE);
                    this.setVisibility(View.GONE);
                }
            }

            if (observableParametros != null) {
                for (Componente.Tool toolParameter :  observableParametros) {
                    toolParameter.notifyParameter();
                }
            }
        }


        List<Componente.Tool> observableParametros;
        @Override
        public void subscribeParameter(Tool tool) {
            if (observableParametros == null)
                observableParametros = new ArrayList<>();
            observableParametros.add(tool);
        }

        @Override
        public void notifyParameter() {
            notifyDataSetChange(true);
        }

        HashMap<String, Componente.Tool> toolParametros;

        @Override
        public void addParameterComponent(String key, Tool tool) {
            if (toolParametros == null)
                toolParametros = new HashMap<>();
            toolParametros.put(key, tool);
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    Object value = this.getValue();
                    if (value == null) {
                        tool.notifyObserver(this, null);
                    }
                    else {
                        tool.notifyObserver(this, value instanceof Info ? ((Info) value).ValueMember.toString() : value.toString());
                    }
                }
            }
            if (observableParametros != null) {
                for (Componente.Tool toolParameter :  observableParametros) {
                    toolParameter.notifyParameter();
                }
            }
        }
    }

    public class ImageBox extends AppCompatImageButton implements Tool {

        private Field field;
        private TextView textField;
        private ImageView imageView;
        private String fileName;

        public ImageBox(Context context) {
            super(context);
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return (field.ReadOnly || field.OcurrenciaMin == 0 || field.Value != null);
        }

        @Override
        public void prepare() {
            if (field.Value != null) {
                setImageDrawable(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_image).color(Color.WHITE).actionBar());
            }
            else {
                setImageDrawable(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_camera).color(Color.WHITE).actionBar());
            }
            setEnabled(!field.ReadOnly);
            setOnClickListener(view -> {
                listener.capturarImagen(getField().Identity);
            });
            imageView = new ImageBox(getContext());
            imageView.setVisibility(View.GONE);
            imageView.setMaxHeight(20);
            imageView.setMaxWidth(20);
            imageView.setBackgroundColor(getResources().getColor(R.color.white));
            imageView.setOnClickListener(view -> {
                openImage();
            });
            imageView.setOnLongClickListener(view -> {
                openImage();
                return true;
            });
            if (getValue() != null) {
                setValue(getValue());
            }
        }

        @Override
        public View getEtiqueta() {
            if (field.Etiqueta != null) {
                if (field.Etiqueta.trim().length() > 0) {
                    if (textField == null) {
                        textField = new TextView(getContext());
                        textField.setText(field.Etiqueta);
                        textField.setTextColor(getResources().getColor(R.color.black_alpha_50));
                    }
                }
            }
            return textField;
        }

        @Override
        public Object getValue() {
            return field.Value;
        }

        @Override
        public void setValue(Object value) {
            if (value != null) {
                if (value instanceof String) {
                    field.Value = value;
                    setImageDrawable(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_image).color(Color.WHITE).actionBar());
                    File imgFile;
                    imgFile = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(getFileName()).concat(".JPEG"));
                    if (imgFile.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageView.setImageBitmap(bm);
                        imageView.setVisibility(View.VISIBLE);
                    }
                    imgFile = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(getFileName()).concat(".jpg"));
                    if (imgFile.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageView.setImageBitmap(bm);
                        imageView.setVisibility(View.VISIBLE);
                    }
                    if (!imgFile.exists()) {
                        try {
                            JsonParser jsonParser = new JsonParser();
                            JsonObject jData = jsonParser.parse(field.Value.toString()).getAsJsonObject();
                            Glide.with(getContext())
                                    .load(IpatiServer.FILE_SERVER.getUrl() + jData.get("fileUri").getAsString())
                                    .asBitmap()
                                    .toBytes(Bitmap.CompressFormat.JPEG, 100)
                                    .into(new SimpleTarget<byte[]>() {
                                        @Override
                                        public void onResourceReady(final byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                            new AsyncTask<String, Void, Void>() {
                                                @Override
                                                protected Void doInBackground(String... params) {
                                                    if (params.length > 0) {
                                                        String name = params[0];
                                                        File file = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(name).concat(".jpg"));
                                                        File dir = file.getParentFile();
                                                        try {
                                                            if (!dir.mkdirs() && (!dir.exists() || !dir.isDirectory())) {
                                                                throw new IOException("Cannot ensure parent directory for file " + file);
                                                            }
                                                            BufferedOutputStream s = new BufferedOutputStream(new FileOutputStream(file));
                                                            s.write(resource);
                                                            s.flush();
                                                            s.close();
                                                        }
                                                        catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    return null;
                                                }

                                                @Override
                                                protected void onPostExecute(Void aVoid) {
                                                    super.onPostExecute(aVoid);
                                                    File imgFile = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(getFileName()).concat(".jpg"));
                                                    if (imgFile.exists()) {
                                                        Bitmap bm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                                        imageView.setImageBitmap(bm);
                                                        imageView.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            }.execute(getFileName());
                                        }
                                    });
                        }
                        catch (Exception ex) {
                            Log.e(getClass().getSimpleName(), ex.toString());
                        }
                    }
                }
            }
            else {
                imageView.setImageBitmap(null);
                imageView.setVisibility(View.GONE);
            }
            notifyObserver();
        }

        public ImageView getImageView() {
            return imageView;
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }

        @Override
        public View getView() {
            return this;
        }

        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }

        private String getFileName() {
            if (fileName == null && getValue() != null && getValue() instanceof String) {
                String value = (String)getValue();
                JsonParser parser  = new JsonParser();
                JsonObject jValue = parser.parse(value).getAsJsonObject();
                if (jValue.has("_id")) {
                    fileName = jValue.get("_id").getAsString();
                }
            }
            return fileName;
        }

        public void openImage() {
            if (field.Value != null) {
                File imgFile;
                imgFile = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(getFileName()).concat(".JPEG"));
                if (imgFile.exists()) {
                    listener.previewImage(imgFile.getPath());
                }

                imgFile = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(getFileName()).concat(".jpg"));
                if (imgFile.exists()) {
                    listener.previewImage(imgFile.getPath());
                }
            }
        }
    }

    public class MapLocation extends AppCompatImageButton implements Tool {

        private Field field;
        private TextView textField;
        private ImageView imageView;

        private Marker marker;
        private GoogleMap gMap;
        private LatLng latLng;
        private Marker position;
        private Dialog dialog;

        public MapLocation(Context context) {
            super(context);
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return (field.OcurrenciaMin == 0 || field.getValueString() != null);
        }

        @Override
        public void prepare() {
            if (field.Value != null) {
                setImageDrawable(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_edit).color(Color.WHITE).actionBar());
            } else {
                setImageDrawable(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_map).color(Color.WHITE).actionBar());
            }

            setEnabled(!field.ReadOnly);

            imageView = new ImageBox(getContext());
            imageView.setVisibility(View.GONE);
            imageView.setMinimumHeight(100);
            imageView.setMaxHeight(110);
            //imageView.setMaxWidth(40);
            imageView.setBackgroundColor(getResources().getColor(R.color.white));
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            if (params != null) {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }

            imageView.setOnLongClickListener(view -> {
                showImage();
                return false;
            });

            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImage();
                }
            });

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    setEnabled(false);
                    String title = "Localizaci\u00f3n";
                    if (field.Etiqueta != null) {
                        if (field.Etiqueta.trim().length() > 0) {
                            title = field.Etiqueta;
                        }
                    }
                    Fragment fragment = ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);
                    if (fragment != null) {
                        setEnabled(true);
                        return;
                    }
                    MapLocation.this.dialog = new Dialog(listener.getActivity());
                    dialog.setContentView(R.layout.dialog_maps);
                    dialog.setTitle("Captura ".concat(title));
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Componente.SUBVIEW = null;
                            if (listener.getActivity() instanceof FormularioActivity) {
                                Fragment fragmentMap = ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);
                                ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().beginTransaction().remove(fragmentMap).commit();

                                android.app.Fragment fragmentPlace = listener.getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                                if (fragmentPlace != null)
                                    listener.getActivity().getFragmentManager().beginTransaction().remove(fragmentPlace).commit();

                                ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().executePendingTransactions();
                                listener.getActivity().getFragmentManager().executePendingTransactions();
                            }
                        }
                    });
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Componente.SUBVIEW = MapLocation.this;
                            ((SupportMapFragment) ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    gMap = googleMap;
                                    if (ActivityCompat.checkSelfPermission(listener.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(listener.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        gMap.setMyLocationEnabled(true);
                                    }
                                    gMap.getUiSettings().setZoomControlsEnabled(true);
                                    gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                        @Override
                                        public void onMapLongClick(LatLng latLng) {
                                            if (marker == null) {
                                                marker = gMap.addMarker(new MarkerOptions().position(latLng).title(""));
                                                marker.setDraggable(true);
                                            } else {
                                                marker.setPosition(latLng);
                                            }

                                            setValue(latLng);
                                        }
                                    });

                                    gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                        @Override
                                        public void onMapClick(LatLng latLng) {
                                            if (marker == null) {
                                                marker = gMap.addMarker(new MarkerOptions().position(latLng).title(""));
                                                marker.setDraggable(true);
                                            } else {
                                                marker.setPosition(latLng);
                                            }
                                            setValue(latLng);
                                        }
                                    });

                                    if (latLng != null) {
                                        marker = gMap.addMarker(new MarkerOptions().position(latLng).title(""));
                                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                                    }
                                    else {
                                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.662571, -103.348732), 16));
                                    }
                                    if (LocalizacionServices.LOCATION != null) {
                                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocalizacionServices.LOCATION.getLatitude(), LocalizacionServices.LOCATION.getLongitude()), 17));
                                        if (LocalizacionServices.LOCATION.hasAccuracy())
                                            ((TextView) MapLocation.this.dialog.findViewById(R.id.lblDetalle)).setText("Precisi\u00f3n " +
                                                    ((int) LocalizacionServices.LOCATION.getAccuracy()) + " m " + (LocalizacionServices.LOCATION.hasAltitude() ? " Alt. " + (int) LocalizacionServices.LOCATION.getAltitude() : ""));
                                    }
                                }
                            });

                        }
                    });

                    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                            listener.getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(Place place) {
                            try {
                                setValue(place.getLatLng());
                                if (marker == null) {
                                    marker = gMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                                    marker.setDraggable(true);
                                } else {
                                    marker.setPosition(latLng);
                                    marker.setTitle(place.getName().toString());
                                }
                                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17));
                            }
                            catch (Exception ex) {
                                Log.e(getClass().getSimpleName(), ex.toString());
                            }
                        }

                        @Override
                        public void onError(Status status) {
                        }
                    });


                    dialog.findViewById(R.id.place_autocomplete_fragment);

                    dialog.findViewById(R.id.btnCancelar).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.btnCaptura).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (marker != null) {
                                v.setEnabled(false);
                                if (gMap != null) {
                                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 17));
                                    GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                                        @Override
                                        public void onSnapshotReady(Bitmap snapshot) {
                                            String fileName = UUID.randomUUID().toString();
                                            JsonObject jResult = new JsonObject();

                                            try
                                            {
                                                File file = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/"));
                                                if (!file.exists()) {
                                                    if (!file.mkdir())
                                                        throw new Exception("Directory not exists");
                                                }
                                            }
                                            catch (Exception ex)
                                            {
                                                Log.e(getClass().getSimpleName(), ex.toString());
                                            }

                                            try {
                                                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(fileName).concat(".JPEG"));
                                                snapshot.compress(Bitmap.CompressFormat.PNG, 90, out);
                                            } catch (Exception ex) {
                                                Log.e(getClass().getSimpleName(), ex.toString());
                                            }
                                            jResult.addProperty("lat", marker.getPosition().latitude);
                                            jResult.addProperty("lng", marker.getPosition().longitude);
                                            jResult.addProperty("Captura", fileName);
                                            setValue(jResult.toString());
                                            dialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(listener.getActivity());
                                            builder.setMessage("Posici\u00f3n registrada");
                                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builder.show();
                                        }
                                    };
                                    gMap.snapshot(callback);
                                }
                            } else {
                                Toast.makeText(getContext(), "Agregue un marcador en el mapa", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    dialog.show();

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setAttributes(lp);

                    setEnabled(true);
                }
            });
        }

        @Override
        public View getEtiqueta() {
            if (field.Etiqueta != null) {
                if (field.Etiqueta.trim().length() > 0) {
                    if (textField == null) {
                        textField = new TextView(getContext());
                        textField.setText(field.Etiqueta);
                        textField.setTextColor(getResources().getColor(R.color.black_alpha_50));
                    }
                }
            }
            return textField;
        }

        @Override
        public Object getValue() {
            return latLng;
        }

        @Override
        public void setValue(Object value) {
            try {
                if (value instanceof LatLng) {
                    latLng = (LatLng) value;
                    field.setValueString("{\"lat\":" + latLng.latitude + ",\"lng\":" + latLng.longitude + "}");
                }
                if (value instanceof String) {
                    if (!value.equals("")) {
                        JsonParser parser = new JsonParser();
                        JsonObject jValue = parser.parse(value.toString()).getAsJsonObject();
                        double lat = jValue.get("lat").getAsDouble();
                        double lng = jValue.get("lng").getAsDouble();
                        latLng = new LatLng(lat, lng);
                        if (jValue.has("Captura")) {
                            File imgFile = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(jValue.get("Captura").getAsString()).concat(".JPEG"));
                            if (imgFile.exists()) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                imageView.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 300, 300, true));
                                imageView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            imageView.setVisibility(View.GONE);
                        }
                        field.setValueString(value.toString());
                    }
                }
                if (value != null) {
                    setImageDrawable(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_map).color(Color.WHITE).actionBar());
                }

                notifyObserver();
            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(), ex.toString());
            }
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
            if (gMap == null)
                return;
            if (latLng == null)
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
            ((TextView) MapLocation.this.dialog.findViewById(R.id.lblDetalle)).setText("Precisi\u00f3n " + ((int) accuracy) + " m " + (altitude > 0 ? " Alt. " + (int) altitude : ""));
        }

        @Override
        public View getView() {
            return this;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {

        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }

        public void showImage() {
            String value = field.getValueString();
            if (value != null && !value.equals("")) {
                JsonParser parser = new JsonParser();
                JsonObject jValue = parser.parse(value).getAsJsonObject();
                if (jValue.has("Captura")) {
                    File imgFile = new File(Environment.getExternalStorageDirectory().toString().concat("/ipati/").concat(jValue.get("Captura").getAsString()).concat(".JPEG"));
                    if (imgFile.exists()) {
                        Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryIntent.setDataAndType(Uri.fromFile(imgFile), "image/*");
                        galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        listener.getActivity().startActivity(galleryIntent);
                    }
                }
            }
        }
    }

    public class MapPolygon extends ImageButton implements Tool {

        private Field field;
        private TextView textField;
        private GoogleMap gMap;
        private Marker position;
        private Dialog dialog;

        private List<Info> listInfo;

        private class Info {

            public Marker Marker;
            public Polyline Polyline;
            public Polyline PolylineStarted;

        }

        public MapPolygon(Context context) {
            super(context);
            listInfo = new ArrayList<>();
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return (field.OcurrenciaMin == 0 || field.Value != null);
        }

        @Override
        public void prepare() {
            if (field.Value != null) {
                setImageDrawable(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_edit).color(Color.WHITE).actionBar());
            } else {
                setImageDrawable(new IconicsDrawable(getContext(), GoogleMaterial.Icon.gmd_map).color(Color.WHITE).actionBar());
            }

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = "Localizaci\u00f3n";
                    if (field.Etiqueta != null) {
                        if (field.Etiqueta.trim().length() > 0) {
                            title = field.Etiqueta;
                        }
                    }

                    Fragment fragment = ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);
                    if (fragment != null) {
                        setEnabled(true);
                        return;
                    }

                    MapPolygon.this.dialog = new Dialog(listener.getActivity());
                    dialog.setContentView(R.layout.dialog_maps);
                    dialog.setTitle("Captura ".concat(title));
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Componente.SUBVIEW = null;
                            if (listener.getActivity() instanceof FormularioActivity) {
                                Fragment fragment = ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);
                                ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().executePendingTransactions();
                            }
                        }
                    });

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Componente.SUBVIEW = MapPolygon.this;
                            ((SupportMapFragment) ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    gMap = googleMap;

                                    gMap.getUiSettings().setZoomControlsEnabled(true);
                                    gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                        @Override
                                        public void onMapLongClick(LatLng latLng) {

                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions.draggable(true);
                                            markerOptions.position(latLng);

                                            if (listInfo.size() > 0) {

                                                Info info = new Info();
                                                info.Marker = gMap.addMarker(markerOptions);

                                                PolylineOptions polylineOptions = new PolylineOptions();
                                                polylineOptions.add(listInfo.get(listInfo.size() - 1).Marker.getPosition(), info.Marker.getPosition());
                                                polylineOptions.geodesic(true);
                                                polylineOptions.width(5);

                                                info.Polyline = gMap.addPolyline(polylineOptions);

                                                listInfo.add(info);
                                                int lastPosition = listInfo.size() - 1;

                                                if (listInfo.get(lastPosition - 1).PolylineStarted != null) {
                                                    listInfo.get(lastPosition - 1).PolylineStarted.remove();
                                                }

                                                PolylineOptions polylineOptionsStarted = new PolylineOptions();
                                                polylineOptionsStarted.add(listInfo.get(0).Marker.getPosition(), info.Marker.getPosition());
                                                polylineOptionsStarted.geodesic(true);
                                                polylineOptionsStarted.width(5);
                                                info.PolylineStarted = gMap.addPolyline(polylineOptionsStarted);
                                            } else {
                                                Info info = new Info();
                                                info.Marker = gMap.addMarker(markerOptions);
                                                listInfo.add(info);
                                            }
                                        }
                                    });

                                    gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                        @Override
                                        public void onMarkerDragStart(Marker marker) {
                                        }

                                        @Override
                                        public void onMarkerDrag(Marker marker) {
                                        }

                                        @Override
                                        public void onMarkerDragEnd(Marker marker) {

                                            Info lastInfo = null;
                                            for (Iterator<Info> iterator = listInfo.iterator(); iterator.hasNext(); ) {
                                                Info info = iterator.next();
                                                if (info.Marker.getId().equals(marker.getId())) {
                                                    if (lastInfo != null) {

                                                        info.Polyline.remove();

                                                        if (info.PolylineStarted != null) {
                                                            info.PolylineStarted.remove();
                                                        }

                                                        PolylineOptions polylineOptions = new PolylineOptions();
                                                        polylineOptions.add(lastInfo.Marker.getPosition(), info.Marker.getPosition());
                                                        polylineOptions.geodesic(true);
                                                        polylineOptions.width(5);
                                                        info.Polyline = gMap.addPolyline(polylineOptions);


                                                        if (iterator.hasNext()) {
                                                            Info nextInfo = iterator.next();

                                                            nextInfo.Polyline.remove();

                                                            PolylineOptions polylineOptionsStarted = new PolylineOptions();
                                                            polylineOptionsStarted.add(info.Marker.getPosition(), nextInfo.Marker.getPosition());
                                                            polylineOptionsStarted.geodesic(true);
                                                            polylineOptionsStarted.width(5);
                                                            nextInfo.Polyline = gMap.addPolyline(polylineOptionsStarted);
                                                        } else {

                                                            info.PolylineStarted.remove();

                                                            PolylineOptions polylineOptionsStarted = new PolylineOptions();
                                                            polylineOptionsStarted.add(info.Marker.getPosition(), listInfo.get(0).Marker.getPosition());
                                                            polylineOptionsStarted.geodesic(true);
                                                            polylineOptionsStarted.width(5);
                                                            info.PolylineStarted = gMap.addPolyline(polylineOptionsStarted);
                                                        }

                                                    } else {
                                                        if (listInfo.size() > 1) {
                                                            Info endInfo = listInfo.get(listInfo.size() - 1);
                                                            if (endInfo.PolylineStarted != null) {
                                                                endInfo.PolylineStarted.remove();
                                                            }
                                                            PolylineOptions polylineOptions = new PolylineOptions();
                                                            polylineOptions.add(endInfo.Marker.getPosition(), info.Marker.getPosition());
                                                            polylineOptions.geodesic(true);
                                                            polylineOptions.width(5);
                                                            endInfo.PolylineStarted = gMap.addPolyline(polylineOptions);

                                                            if (iterator.hasNext()) {
                                                                Info nextInfo = iterator.next();

                                                                nextInfo.Polyline.remove();

                                                                PolylineOptions polylineOptionsStarted = new PolylineOptions();
                                                                polylineOptionsStarted.add(info.Marker.getPosition(), nextInfo.Marker.getPosition());
                                                                polylineOptionsStarted.geodesic(true);
                                                                polylineOptionsStarted.width(5);
                                                                nextInfo.Polyline = gMap.addPolyline(polylineOptionsStarted);
                                                            }

                                                        }
                                                    }

                                                }

                                                lastInfo = info;
                                            }
                                        }
                                    });

                                    gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                        @Override
                                        public View getInfoWindow(Marker marker) {
                                            return null;
                                        }

                                        @Override
                                        public View getInfoContents(Marker marker) {
                                            if (marker.getId().equals(MapPolygon.this.position.getId()))
                                                return null;
                                            return listener.getActivity().getLayoutInflater().inflate(R.layout.item_point, null);
                                        }
                                    });

                                    gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(final Marker marker) {
                                            DialogResponse.createDialogResponse(listener.getActivity(), "Punto", "¿Desea eliminar el market?", "Aceptar", "Cancelar").show(new DialogResponse.CallBack() {
                                                @Override
                                                public void onClickListener(int which) {
                                                    if (which == Dialog.BUTTON_POSITIVE) {

                                                        Info lastInfo = null;

                                                        for (Iterator<Info> iterator = listInfo.iterator(); iterator.hasNext(); ) {
                                                            Info info = iterator.next();

                                                            if (marker.getId().equals(info.Marker.getId())) {
                                                                info.Marker.remove();

                                                                if (info.Polyline != null) {
                                                                    info.Polyline.remove();
                                                                }

                                                                if (info.PolylineStarted != null) {
                                                                    info.PolylineStarted.remove();
                                                                }

                                                                if (lastInfo != null) {
                                                                    if (iterator.hasNext()) {
                                                                        iterator.remove();
                                                                        Info infoNext = iterator.next();
                                                                        PolylineOptions polylineOptions = new PolylineOptions();
                                                                        polylineOptions.add(lastInfo.Marker.getPosition(), infoNext.Marker.getPosition());
                                                                        polylineOptions.geodesic(true);
                                                                        polylineOptions.width(5);

                                                                        if (infoNext.Polyline != null) {
                                                                            infoNext.Polyline.remove();
                                                                        }

                                                                        infoNext.Polyline = gMap.addPolyline(polylineOptions);
                                                                        continue;
                                                                    } else {
                                                                        PolylineOptions polylineOptions = new PolylineOptions();
                                                                        polylineOptions.add(lastInfo.Marker.getPosition(), listInfo.get(0).Marker.getPosition());
                                                                        polylineOptions.geodesic(true);
                                                                        polylineOptions.width(5);
                                                                        lastInfo.PolylineStarted = gMap.addPolyline(polylineOptions);
                                                                    }
                                                                    iterator.remove();
                                                                } else {
                                                                    iterator.remove();
                                                                    if (iterator.hasNext()) {
                                                                        Info infoNext = iterator.next();
                                                                        infoNext.Polyline.remove();
                                                                        infoNext.Polyline = null;

                                                                        if (infoNext.PolylineStarted != null) {
                                                                            infoNext.PolylineStarted.remove();
                                                                        }

                                                                        if (iterator.hasNext()) {
                                                                            Info endInfo = listInfo.get(listInfo.size() - 1);
                                                                            if (endInfo.PolylineStarted != null) {
                                                                                endInfo.PolylineStarted.remove();
                                                                            }
                                                                            PolylineOptions polylineOptions = new PolylineOptions();
                                                                            polylineOptions.add(endInfo.Marker.getPosition(), infoNext.Marker.getPosition());
                                                                            polylineOptions.geodesic(true);
                                                                            polylineOptions.width(5);
                                                                            endInfo.PolylineStarted = gMap.addPolyline(polylineOptions);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            lastInfo = info;
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    if (LocalizacionServices.LOCATION != null) {
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(new LatLng(LocalizacionServices.LOCATION.getLatitude(), LocalizacionServices.LOCATION.getLongitude()));
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_position));
                                        MapPolygon.this.position = gMap.addMarker(markerOptions);
                                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 17));
                                        if (LocalizacionServices.LOCATION.hasAccuracy())
                                            ((TextView) MapPolygon.this.dialog.findViewById(R.id.lblDetalle)).setText("Precisi\u00f3n " +
                                                    ((int) LocalizacionServices.LOCATION.getAccuracy()) + " m " + (LocalizacionServices.LOCATION.hasAltitude() ? " Alt. " + (int) LocalizacionServices.LOCATION.getAltitude() : ""));
                                    }

                                }
                            });
                        }
                    });
                    dialog.findViewById(R.id.btnCancelar).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.btnCaptura).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setAttributes(lp);
                }
            });
        }

        @Override
        public View getEtiqueta() {
            if (field.Etiqueta != null) {
                if (field.Etiqueta.trim().length() > 0) {
                    if (textField == null) {
                        textField = new TextView(getContext());
                        textField.setText(field.Etiqueta);
                        textField.setTextColor(getResources().getColor(R.color.black_alpha_50));
                    }
                }
            }
            return textField;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object value) {
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
            if (gMap == null)
                return;
            if (position != null) {
                position.setPosition(new LatLng(latitude, longitude));
            } else {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_position));
                MapPolygon.this.position = gMap.addMarker(markerOptions);
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
            }
            ((TextView) MapPolygon.this.dialog.findViewById(R.id.lblDetalle)).setText("Precisi\u00f3n " + ((int) accuracy) + " m " + (altitude > 0 ? " Alt. " + (int) altitude : ""));
        }

        @Override
        public View getView() {
            return this;
        }

        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {

        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }
    }

    public class CheckBoxInput extends CheckBox implements Tool {

        private Field field;

        public CheckBoxInput(Context context) {
            super(context);
            setTextColor(getResources().getColor(R.color.black_alpha_50));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                setButtonTintList(new ColorStateList(new int[][]{new int[0]}, new int[]{Color.rgb(117, 117, 117)}));
            }
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void prepare() {
            setEnabled(!field.ReadOnly);

            if (field.Value != null) {
                try {
                    setValue(field.Value);
                }
                catch (Exception ex) {
                    Log.e(getClass().getSimpleName(),ex.toString());
                }
            }

            if (field.Etiqueta != null) {
                if (field.Etiqueta.trim().length() > 0) {
                    super.setText(field.Etiqueta);
                }
            }
            super.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    field.Value = b;
                    field.setValueString(String.valueOf(b));
                    setValue(b);
                }
            });
        }

        @Override
        public View getEtiqueta() {
            return null;
        }

        @Override
        public Object getValue() {
            return field.Value == null ? false : field.Value;
        }

        @Override
        public void setValue(Object value) {
            if (value instanceof Boolean) {
                super.setChecked((boolean) value);
                field.Value = value;
            } else if (value instanceof String) {
                try {
                    super.setChecked(Boolean.parseBoolean(value.toString()));
                    field.Value = Boolean.parseBoolean(value.toString());
                } catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
            }
            field.setValueString(value.toString());
            notifyObserver();
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }

        @Override
        public View getView() {
            return this;
        }


        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
            tool.notifyObserver(this, this.getValue());
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
            if (value != null)
                value = value.toString();

            if (value != null) {
                String expresiones = getField().ExpresionCorrelacion;
                for (String expresion : expresiones.split("\\|")) {
                    if (expresion.equals(value))  {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.VISIBLE);
                        this.setVisibility(View.VISIBLE);
                        break;
                    }
                    else {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.GONE);
                        this.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (getEtiqueta() != null) {
                    getEtiqueta().setVisibility(View.GONE);
                    this.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }
    }

    public class Selector implements Tool {

        private Field field;
        private Context context;
        private ArrayAdapter<Object> adapter;
        private LinearLayout view;

        public class Info {

            public boolean Selected;
            public Object DisplayMember;
            public Object ValueMember;

            public String InfoDataset;

            private JsonParser parser;

            @Override
            public String toString() {
                return DisplayMember.toString();
            }

            public Object getValue(String key, Class<?> type) {
                if (parser == null) {
                    parser = new JsonParser();
                }
                if (type.equals(Double.class)) {
                    return parser.parse(InfoDataset).getAsJsonObject().get(key).getAsDouble();
                }
                return parser.parse(InfoDataset).getAsJsonObject().get(key);
            }

        }


        public Selector(Context context) {
            this.context = context;
            view = (LinearLayout) View.inflate(context, R.layout.widget_selector, null);
            view.setMinimumHeight(100);
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return (field.OcurrenciaMin == 0 || field.Value != null);
        }

        @Override
        public void prepare() {

        }

        public void prepare(final boolean multiSelector, final boolean geolocation, final boolean addElement) {
            if (multiSelector) {

                Dataset dataset = new Dataset();
                dataset.Nombre = "Caracterísitcas Urbanas";
                dataset.Tipo = "";
                dataset.Expresion = "";
                dataset.Dataset = "[{\"IdCaracteristica\" : 1, \"Nombre\" : \"AGUA POTABLE\"}," +
                        "{\"IdCaracteristica\" : 2, \"Nombre\" : \"DRENAJE Y ALCANTARILLADO\"}," +
                        "{\"IdCaracteristica\" : 3, \"Nombre\" : \"RED DE DRENAJE PLUVIAL EN LA CALLE\"}," +
                        "{\"IdCaracteristica\" : 4, \"Nombre\" : \"CON CONEXION AL INMUEBLE\"}," +
                        "{\"IdCaracteristica\" : 5, \"Nombre\" : \"FOSA SEPTICA\"}," +
                        "{\"IdCaracteristica\" : 6, \"Nombre\" : \"SISTEMA MIXTO (AGUAS RESIDUALES Y PLUVIALES)\"}" +
                        "]";

                JsonParser parser = new JsonParser();
                JsonArray jData = parser.parse(dataset.Dataset).getAsJsonArray();

                field.setDataset(dataset);

                final List<Info> listInfo = new ArrayList<>();
                for (JsonElement jElement : jData) {
                    JsonObject jObject = jElement.getAsJsonObject();
                    Info info = new Info();
                    info.DisplayMember = jObject.get("Nombre").getAsString();
                    info.ValueMember = jObject.get("IdCaracteristica").getAsString();
                    info.InfoDataset = jElement.toString();
                    listInfo.add(info);
                }

                ArrayAdapter<Info> adapter = new ArrayAdapter<Info>(context, R.layout.list_item_selector, listInfo) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final Info info = getItem(position);
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_selector, parent, false);
                        }
                        convertView.findViewById(R.id.chkElement).setVisibility(View.VISIBLE);
                        convertView.findViewById(R.id.lblInfo).setVisibility(View.GONE);
                        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.chkElement);
                        checkBox.setText(info.toString());
                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                info.Selected = checkBox.isSelected();
                            }
                        });
                        checkBox.setTextColor(context.getResources().getColor(R.color.black_alpha_50));
                        convertView.findViewById(R.id.btnMap).setVisibility(geolocation ? View.VISIBLE : View.GONE);
                        return convertView;
                    }
                };

                final ListView listView = (ListView) view.findViewById(R.id.listView);
                listView.setAdapter(adapter);

                if (field.Etiqueta != null) {
                    if (field.Etiqueta.trim().length() > 0) {
                        ((TextView) view.findViewById(R.id.title)).setText(field.Etiqueta);
                    }
                }
                view.findViewById(R.id.btnAdd).setVisibility(addElement ? View.VISIBLE : View.GONE);
                view.findViewById(R.id.btnMap).setVisibility(geolocation ? View.VISIBLE : View.GONE);
            } else {
                Dataset dataset = new Dataset();
                dataset.Nombre = "Caracterísitcas Urbanas";
                dataset.Tipo = "";
                dataset.Expresion = "";
                dataset.Dataset = "[\n" +
                        "  {\n" +
                        "    \"No\": 1,\n" +
                        "    \"Ubicacion\": \"Hacienda Real Col. Hacienda Real\",\n" +
                        "    \"Edad\": 10,\n" +
                        "    \"Conservacion\": \"Buena\",\n" +
                        "    \"Fecha\": \"25/07/2014\",\n" +
                        "    \"Telefono\": 33426019,\n" +
                        "    \"Fuente\": \"MIGUEL CASTANEDA\",\n" +
                        "    \"Latitud\": 20.6287208,\n" +
                        "    \"Longitud\": -103.24679909999998\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"No\": 2,\n" +
                        "    \"Ubicacion\": \"Real de camichines No. S/N Col Real Camichines\",\n" +
                        "    \"Edad\": 10,\n" +
                        "    \"Conservacion\": \"Buena\",\n" +
                        "    \"Fecha\": \"25/07/2014\",\n" +
                        "    \"Telefono\": 3331283797,\n" +
                        "    \"Fuente\": \"Particular\",\n" +
                        "    \"Latitud\": 20.6286741,\n" +
                        "    \"Longitud\": -103.24659710000003\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"No\": 3,\n" +
                        "    \"Ubicacion\": \"REAL DEL VALLE Colonia REAL DEL VALLE\",\n" +
                        "    \"Edad\": 10,\n" +
                        "    \"Conservacion\": \"Buena\",\n" +
                        "    \"Fecha\": \"25/07/2014\",\n" +
                        "    \"Telefono\": 3336022125,\n" +
                        "    \"Fuente\": \"MIGUEL CASTANEDA\",\n" +
                        "    \"Latitud\": 20.6287851,\n" +
                        "    \"Longitud\": -103.24702580000002\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"No\": 4,\n" +
                        "    \"Ubicacion\": \"8 DE JULIO No. 1220-2 Colonia LOS OLIVOS TLAQUEPAQUE\",\n" +
                        "    \"Edad\": 4,\n" +
                        "    \"Conservacion\": \"Buena\",\n" +
                        "    \"Fecha\": \"25/07/2014\",\n" +
                        "    \"Telefono\": 15928297,\n" +
                        "    \"Fuente\": \"CARLA AZNAR R.\",\n" +
                        "    \"Latitud\": 20.6166382,\n" +
                        "    \"Longitud\": -103.27645100000001\n" +
                        "  }\n" +
                        "]";

                JsonParser parser = new JsonParser();
                JsonArray jData = parser.parse(dataset.Dataset).getAsJsonArray();

                field.setDataset(dataset);

                final List<Info> listInfo = new ArrayList<>();
                for (JsonElement jElement : jData) {
                    JsonObject jObject = jElement.getAsJsonObject();
                    Info info = new Info();
                    info.DisplayMember = jObject.get("Ubicacion").getAsString();
                    info.ValueMember = jObject.get("No").getAsString();
                    info.InfoDataset = jElement.toString();
                    listInfo.add(info);
                }

                ArrayAdapter<Info> adapter = new ArrayAdapter<Info>(context, R.layout.list_item_selector, listInfo) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        final Info info = getItem(position);
                        if (convertView == null) {
                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_selector, parent, false);
                        }
                        convertView.findViewById(R.id.chkElement).setVisibility(View.GONE);
                        convertView.findViewById(R.id.lblInfo).setVisibility(View.VISIBLE);
                        TextView textView = (TextView) convertView.findViewById(R.id.lblInfo);
                        textView.setText(info.toString());
                        if (geolocation) {
                            convertView.findViewById(R.id.btnMap).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String title = "Localizaci\u00f3n";
                                    if (field.Etiqueta != null) {
                                        if (field.Etiqueta.trim().length() > 0) {
                                            title = field.Etiqueta;
                                        }
                                    }

                                    final Dialog dialog = new Dialog(listener.getActivity());
                                    dialog.setContentView(R.layout.dialog_maps);
                                    dialog.setTitle(title);
                                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            if (listener.getActivity() instanceof FormularioActivity) {
                                                Fragment fragment = ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);
                                                ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                                ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().executePendingTransactions();
                                            }
                                        }
                                    });

                                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            ((SupportMapFragment) ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                                                @Override
                                                public void onMapReady(GoogleMap gMap) {
                                                    if (ActivityCompat.checkSelfPermission(listener.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(listener.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                        gMap.setMyLocationEnabled(true);
                                                    }
                                                    gMap.getUiSettings().setZoomControlsEnabled(true);
                                                    double lat = (double) info.getValue("Latitud", Double.class);
                                                    double lon = (double) info.getValue("Longitud", Double.class);
                                                    Marker marker = gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(info.DisplayMember.toString()));
                                                }
                                            });
                                        }
                                    });
                                    dialog.findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });


                                    dialog.show();


                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                                    dialog.getWindow().setAttributes(lp);

                                }
                            });
                        }
                        convertView.findViewById(R.id.btnMap).setVisibility(geolocation ? View.VISIBLE : View.GONE);
                        return convertView;
                    }
                };

                final ListView listView = (ListView) view.findViewById(R.id.listView);
                listView.setAdapter(adapter);

                if (field.Etiqueta != null) {
                    if (field.Etiqueta.trim().length() > 0) {
                        ((TextView) view.findViewById(R.id.title)).setText(field.Etiqueta);
                    }
                }
                view.findViewById(R.id.btnAdd).setVisibility(addElement ? View.VISIBLE : View.GONE);
                view.findViewById(R.id.btnMap).setVisibility(geolocation ? View.VISIBLE : View.GONE);

                if (geolocation) {
                    view.findViewById(R.id.btnMap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String title = "Localizaci\u00f3n";
                            if (field.Etiqueta != null) {
                                if (field.Etiqueta.trim().length() > 0) {
                                    title = field.Etiqueta;
                                }
                            }

                            final Dialog dialog = new Dialog(listener.getActivity());
                            dialog.setContentView(R.layout.dialog_maps);
                            dialog.setTitle(title);
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    if (listener.getActivity() instanceof FormularioActivity) {
                                        Fragment fragment = ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map);
                                        ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                        ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().executePendingTransactions();
                                    }
                                }
                            });

                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    ((SupportMapFragment) ((FormularioActivity) listener.getActivity()).getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap gMap) {
                                            if (ActivityCompat.checkSelfPermission(listener.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(listener.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                                gMap.setMyLocationEnabled(true);
                                            }
                                            gMap.getUiSettings().setZoomControlsEnabled(true);

                                            for (Info data : listInfo) {
                                                double lat = (double) data.getValue("Latitud", Double.class);
                                                double lon = (double) data.getValue("Longitud", Double.class);
                                                gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(data.DisplayMember.toString()));
                                            }
                                        }
                                    });
                                }
                            });
                            dialog.findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                            dialog.getWindow().setAttributes(lp);
                        }
                    });
                }

            }

        }

        @Override
        public View getEtiqueta() {
            return null;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object value) {
            notifyObserver();
        }

        public View getView() {
            return view;
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }


        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
            if (value != null)
                value = value.toString();

            if (value != null) {
                String expresiones = getField().ExpresionCorrelacion;
                for (String expresion : expresiones.split("\\|")) {
                    if (expresion.equals(value))  {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.VISIBLE);
                        if (this.view != null)
                            this.view.setVisibility(View.VISIBLE);
                        break;
                    }
                    else {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.GONE);
                        if (this.view != null)
                            this.view.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (getEtiqueta() != null) {
                    getEtiqueta().setVisibility(View.GONE);
                    if (this.view != null)
                        this.view.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }
    }

    public class DateTimePicker implements Tool {

        private Field field;
        private ContentView view;
        private TextView textField;
        private Context context;

        private SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        private SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        private SimpleDateFormat formatDateTime = new SimpleDateFormat("dd-MM-yyy HH:mm", Locale.ENGLISH);
        private SimpleDateFormat formatWindow = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

        public DateTimePicker(Context context) {
            this.context = context;
            this.view = (ContentView) View.inflate(context, R.layout.widget_datetime, null);
            this.view.setMinimumHeight(100);
            this.view.setTool(this);
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
            if (this.field.Value == null) {
                setValue(new Date().getTime());
            }
        }

        @Override
        public boolean isValid() {
            return (field.OcurrenciaMin == 0 || field.Value != null);
        }

        @Override
        public void prepare() {
        }

        public void prepare(boolean date, boolean time) {
            final Button btnDate = (Button) view.findViewById(R.id.btnDate);
            final Button btnTime = (Button) view.findViewById(R.id.btnTime);

            if (date) {
                btnDate.setVisibility(View.VISIBLE);
            } else {
                btnDate.setVisibility(View.GONE);
            }

            if (time) {
                btnTime.setVisibility(View.VISIBLE);
            } else {
                btnTime.setVisibility(View.GONE);
            }

            btnDate.setText(formatDate.format(field.Value));
            btnTime.setText(formatTime.format(field.Value));

            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog dialog = new Dialog(listener.getActivity());
                    dialog.setContentView(R.layout.dialog_date);
                    dialog.setTitle("Fecha");

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Calendar fecha = Calendar.getInstance();
                            fecha.setTimeInMillis((long) field.Value);
                            DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.dtpFecha);
                            datePicker.updateDate(fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_YEAR));
                        }
                    });
                    dialog.findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.btnAceptar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.dtpFecha);

                            int year = datePicker.getYear();
                            int month = datePicker.getMonth();
                            int day = datePicker.getDayOfMonth();

                            String value = day + "-" + month + "-" + year;
                            Date date = new Date();
                            try {
                                date = formatDateTime.parse(value + " " + formatTime.format(field.Value));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            setValue(date.getTime());
                            btnDate.setText(formatDate.format(field.Value));
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setAttributes(lp);
                }
            });

            btnTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(listener.getActivity());
                    dialog.setContentView(R.layout.dialog_time);
                    dialog.setTitle("Fecha");

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Calendar fecha = Calendar.getInstance();
                            fecha.setTimeInMillis((long) field.Value);
                            //TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.dtpTime);
                            //timePicker.setCurrentHour(fecha.get(Calendar.HOUR_OF_DAY));
                            //timePicker.setCurrentMinute(fecha.get(Calendar.MINUTE));
                        }
                    });
                    dialog.findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.findViewById(R.id.btnAceptar).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.dtpTime);
                            Date date = new Date();
                            try {
                                date = formatDateTime.parse(formatDate.format(field.Value) + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            setValue(date.getTime());
                            btnTime.setText(formatTime.format(field.Value));
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setAttributes(lp);
                }
            });
        }

        @Override
        public View getEtiqueta() {
            if (field.Etiqueta != null) {
                if (field.Etiqueta.trim().length() > 0) {
                    if (textField == null) {
                        textField = new TextView(context);
                        textField.setText(field.Etiqueta);
                        textField.setTextColor(context.getResources().getColor(R.color.black_alpha_50));
                    }
                }
            }
            return textField;
        }

        @Override
        public Object getValue() {
            return field.Value;
        }

        @Override
        public void setValue(Object value) {
            field.Value = value;
            if (value != null) {
                field.setValueString(formatWindow.format(new Date((long) value)));
            }
            notifyObserver();
        }

        public View getView() {
            return view;
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }

        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
            if (value != null)
                value = value.toString();

            if (value != null) {
                String expresiones = getField().ExpresionCorrelacion;
                for (String expresion : expresiones.split("\\|")) {
                    if (expresion.equals(value))  {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.VISIBLE);
                        if (this.view != null)
                            this.view.setVisibility(View.VISIBLE);
                        break;
                    }
                    else {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.GONE);
                        if (this.view != null)
                            this.view.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (getEtiqueta() != null) {
                    getEtiqueta().setVisibility(View.GONE);
                    if (this.view != null)
                        this.view.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {

        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }
    }

    public class Grid implements Tool {

        private Field field;
        private LinearLayout view;
        private Context context;
        private ArrayAdapter<Object> adapter;
        private DataTable DataInfo;

        private JsonParser parser;

        public class DataTable {

            public List<Field> DataColumns;
            public List<Object> DataRows;

            public DataTable() {
                this.DataColumns = new ArrayList<>();
                this.DataRows = new ArrayList<>();
            }

            public List<Field> addNewRow() {
                List<Field> row = new ArrayList<>();
                try {
                    for (Field field : this.DataColumns) {
                        row.add((Field) field.cloneField());
                    }
                    this.DataRows.add(row);
                } catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
                return row;
            }
        }

        public Grid(Context context) {
            this.context = context;
            this.DataInfo = new DataTable();
            this.parser = new JsonParser();
            this.view = (LinearLayout) View.inflate(context, R.layout.widget_grid, null);
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return (field.OcurrenciaMin == 0 || field.Value != null);
        }

        @Override
        public void prepare() {
            view.findViewById(R.id.btnAdd).setVisibility(field.getIdChilForm() > 0 ? View.VISIBLE : View.INVISIBLE);
            view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        listener.loadChildForm(field.Identity, field.getIdChilForm(), 0, field.getNamedPath());
                    } catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }

                }
            });

            this.DataInfo.DataColumns.addAll(field.getFields());

            this.adapter = new ArrayAdapter<Object>(context, R.layout.list_item_grid, this.DataInfo.DataRows) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_grid, parent, false);
                    List<Field> row = (List<Field>) getItem(position);

                    boolean locationProcesada = false;

                    for (final Field field : row) {

                        // TODO: Evarluar si es readonly
                        field.ReadOnly = true;

                        if (field.TipoDato.equals("System.Boolean")) {
                            field.TipoControl = "CHECKBOX";
                        } else {
                            field.TipoControl = "LABEL";
                        }
                        Object component = getComponente(context, field);
                        if (component != null && component instanceof Tool) {
                            ((ViewGroup) convertView.findViewById(R.id.layout_container)).addView(((Tool) component).getView());
                            convertView.findViewById(R.id.layout_container).setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    return false;
                                }
                            });
                        }

                        if (!locationProcesada && field.getLocation() != null) {
                            try {
                                convertView.findViewById(R.id.imgLocaion).setVisibility(View.VISIBLE);
                                ((ImageView) convertView.findViewById(R.id.imgLocaion)).setImageDrawable(
                                        listener.getActivity().getDrawable(IconProvider.getIcon(field.getIdChilForm()))
                                );

                                convertView.findViewById(R.id.imgLocaion).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View view) {

                                        JsonObject jInfo = parser.parse(field.getLocation()).getAsJsonObject();
                                        MapDialog mapDialog = new MapDialog(listener.getActivity(), "") {
                                            public AppCompatActivity getAppCompactActivity() throws Exception {
                                                return (AppCompatActivity) listener.getActivity();
                                            }

                                            @Override
                                            public void setOnDismissListener(OnDismissListener listener) {
                                                super.setOnDismissListener(listener);
                                                view.setEnabled(true);
                                            }
                                        };

                                        MapDialog.Point point = mapDialog.new Point();
                                        point.setLatitude(Double.parseDouble(jInfo.get("lat").toString()));
                                        point.setLongitude(Double.parseDouble(jInfo.get("lng").toString()));
                                        point.setClasificacion(IconProvider.getIcon(field.getIdChilForm()));

                                        LatLng location = listener.getDocPosition();
                                        MapDialog.Point pointVisita = mapDialog.new Point();
                                        pointVisita.setLatitude(location.latitude);
                                        pointVisita.setLongitude(location.longitude);
                                        pointVisita.setClasificacion(IconProvider.getIcon(-1));

                                        mapDialog.show();
                                        mapDialog.setLocation(null, point, pointVisita);
                                        mapDialog.setCenter(location);
                                    }
                                });

                                locationProcesada = true;
                            } catch (Exception ex) {
                                Log.e(getClass().getSimpleName(), ex.toString());
                            }
                        }
                    }
                    return convertView;
                }
            };
            final ListView listView = (ListView) view.findViewById(R.id.listView);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(listener.getActivity());
                    builder.setTitle(field.Etiqueta)
                            .setItems(new String[]{"Editar", "Eliminar"}, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0) {
                                        try {
                                            listener.loadChildForm(field.Identity, field.getIdChilForm(), i + 1, field.getNamedPath());
                                        } catch (Exception ex) {
                                            Log.e(getClass().getSimpleName(), ex.toString());
                                        }

                                    } else if (which == 1) {
                                        listener.deleteForm(field.Identity, field.getIdChilForm(), i + 1);
                                    }
                                }
                            });
                    builder.create();
                    builder.show();
                    return false;
                }
            });

            listView.setAdapter(adapter);
        }


        public void clearInfo() {
            if (this.DataInfo != null && this.DataInfo.DataRows != null)
                this.DataInfo.DataRows.clear();
        }

        @Override
        public View getEtiqueta() {
            return null;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object value) {
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }

        public View getView() {
            return view;
        }

        public DataTable getDataInfo() {
            return DataInfo;
        }

        public ArrayAdapter<Object> getAdapter() {
            return adapter;
        }

        public void notifyPosition() {
            view.findViewById(R.id.btnLocation).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.btnLocation)).setImageDrawable(
                    new IconicsDrawable(listener.getActivity(), GoogleMaterial.Icon.gmd_location_on).color(Color.parseColor("#ffab40")).actionBar()
            );
            view.findViewById(R.id.btnLocation).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    view.setEnabled(false);
                    MapDialog mapDialog = new MapDialog(listener.getActivity(), "") {
                        public AppCompatActivity getAppCompactActivity() throws Exception {
                            return (AppCompatActivity) listener.getActivity();
                        }

                        @Override
                        public void setOnDismissListener(OnDismissListener listener) {
                            super.setOnDismissListener(listener);
                            view.setEnabled(true);
                        }
                    };
                    List<MapDialog.Point> listPoints = new ArrayList<>();
                    for (Object row : DataInfo.DataRows) {
                        List<Field> fields = (List) row;
                        if (fields.size() > 0) {
                            Field field = fields.get(0);
                            if (field.getLocation() != null) {

                                JsonObject jInfo = parser.parse(field.getLocation()).getAsJsonObject();
                                MapDialog.Point point = mapDialog.new Point();
                                point.setLatitude(Double.parseDouble(jInfo.get("lat").toString()));
                                point.setLongitude(Double.parseDouble(jInfo.get("lng").toString()));
                                point.setClasificacion(IconProvider.getIcon(field.getIdChilForm()));

                                listPoints.add(point);

                            }
                        }
                    }

                    LatLng location = listener.getDocPosition();
                    MapDialog.Point pointVisita = mapDialog.new Point();
                    pointVisita.setLatitude(location.latitude);
                    pointVisita.setLongitude(location.longitude);
                    pointVisita.setClasificacion(IconProvider.getIcon(-1));
                    listPoints.add(pointVisita);

                    MapDialog.Point[] points = new MapDialog.Point[listPoints.size()];
                    listPoints.toArray(points);

                    mapDialog.show();
                    mapDialog.setLocation(null, points);
                    mapDialog.setCenter(location);
                }
            });
        }

        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
            if (value != null)
                value = value.toString();

            if (value != null) {
                String expresiones = getField().ExpresionCorrelacion;
                for (String expresion : expresiones.split("\\|")) {
                    if (expresion.equals(value))  {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.VISIBLE);
                        if (this.view != null)
                            this.view.setVisibility(View.VISIBLE);
                        break;
                    }
                    else {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.GONE);
                        if (this.view != null)
                            this.view.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (getEtiqueta() != null) {
                    getEtiqueta().setVisibility(View.GONE);
                    if (this.view != null)
                        this.view.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }

        public void setTitle(String title) {
            if (this.view != null) {
                if (this.view.findViewById(R.id.title)!= null) {
                    ((TextView)this.view.findViewById(R.id.title)).setText(title);
                }
            }

            if (this.view != null) {
                if (this.view.findViewById(R.id.btnAdd)!= null) {
                    this.view.findViewById(R.id.btnAdd).setVisibility(View.GONE);
                }
            }
        }

    }


    public class GalleryComponent extends Gallery implements Tool {

        private Field field;

        public GalleryComponent(Context context) {
            super(context, ((AppCompatActivity) listener.getActivity()).getSupportFragmentManager());
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void prepare() {

        }

        @Override
        public View getEtiqueta() {
            return null;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public View getView() {
            return this;
        }

        @Override
        public void setValue(Object value) {
            notifyObserver();
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {

        }

        @Override
        protected void addItem() {
            try {
                listener.loadChildForm(field.Identity, field.getIdChilForm(), 0, field.getNamedPath());
            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(), ex.toString());
            }
        }

        public void modificar(final int index) {
            AlertDialog.Builder builder = new AlertDialog.Builder(listener.getActivity());
            builder.setTitle(field.Etiqueta)
                    .setItems(new String[]{"Editar", "Eliminar"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                try {
                                    listener.loadChildForm(field.Identity, field.getIdChilForm(), index + 1, field.getNamedPath());

                                } catch (Exception ex) {
                                    Log.e(getClass().getSimpleName(), ex.toString());
                                }
                            } else if (which == 1) {
                                listener.deleteForm(field.Identity, field.getIdChilForm(), index + 1);
                            }
                        }
                    });
            builder.create();
            builder.show();

        }

        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
            if (value != null)
                value = value.toString();

            if (value != null) {
                String expresiones = getField().ExpresionCorrelacion;
                for (String expresion : expresiones.split("\\|")) {
                    if (expresion.equals(value))  {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.VISIBLE);
                        this.setVisibility(View.VISIBLE);
                        break;
                    }
                    else {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.GONE);
                        this.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (getEtiqueta() != null) {
                    getEtiqueta().setVisibility(View.GONE);
                    this.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }

    }


    public class MultiPoint implements Tool {

        private Field field;
        private LinearLayout view;
        private Context context;
        private ArrayAdapter<Object> adapter;
        private DataTable DataInfo;
        private JsonParser parser;
        private GoogleMap gMap;

        public class DataRow {

            public String Titulo;
            public String Descripcion;
            public String Posicion;
            public int Clasificacion = -2;
            public int Index;
            public String IdMarker;
            private LatLng Location;

            public LatLng getLocation() {
                if (this.Location == null) {
                    double lat = 0;
                    double lng = 0;
                    try {
                        JsonObject jPosicion = parser.parse(Posicion).getAsJsonObject();
                        lat = jPosicion.get("lat").getAsDouble();
                        lng = jPosicion.get("lng").getAsDouble();
                    } catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }
                    this.Location = new LatLng(lat, lng);
                }
                return Location;
            }
        }

        public class DataTable {

            public List<DataRow> DataRows;

            public DataTable() {
                this.DataRows = new ArrayList<>();
            }

            public DataRow addNewRow() {
                DataRow dataRow = new DataRow();
                try {
                    this.DataRows.add(dataRow);
                } catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
                return dataRow;
            }
        }

        public MultiPoint(Context context) {
            this.context = context;
            this.DataInfo = new DataTable();
            this.parser = new JsonParser();
            this.view = (LinearLayout) View.inflate(context, R.layout.widget_multi_point, null);
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public void setField(Field field) {
            this.field = field;
        }

        @Override
        public boolean isValid() {
            return (field.OcurrenciaMin == 0 || field.Value != null);
        }

        @Override
        public void prepare() {
            view.findViewById(R.id.btnAdd).setVisibility(field.getIdChilForm() > 0 ? View.VISIBLE : View.INVISIBLE);
            view.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        listener.loadChildForm(field.Identity, field.getIdChilForm(), 0, field.getNamedPath());
                    } catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }
                }
            });
        }

        @Override
        public View getEtiqueta() {
            return null;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public void setValue(Object value) {
            notifyObserver();
        }

        @Override
        public void loadLocation(double latitude, double longitude, double accuracy, double altitude) {
        }

        @Override
        public View getView() {
            return view;
        }

        public void notifyMap() {
            try {
                if (gMap == null) {
                    int id = view.findViewById(R.id.gMap).getId();

                    FragmentManager fm = ((AppCompatActivity) listener.getActivity()).getSupportFragmentManager();
                    SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
                    fm.beginTransaction().replace(id, supportMapFragment).commit();
                    fm.executePendingTransactions();
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            gMap = googleMap;
                            if (ActivityCompat.checkSelfPermission(listener.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(listener.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                gMap.setMyLocationEnabled(true);
                            }
                            gMap.getUiSettings().setZoomControlsEnabled(true);
                            gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(final Marker marker) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(listener.getActivity());
                                    builder .setTitle(field.Etiqueta)
                                            .setItems(new String[]{"Editar", "Eliminar"}, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {
                                                        try {
                                                            for (DataRow dr : DataInfo.DataRows) {
                                                                if (dr.IdMarker.equals(marker.getId())) {
                                                                    listener.loadChildForm(field.Identity, field.getIdChilForm(), dr.Index + 1, field.getNamedPath());
                                                                }
                                                            }
                                                        } catch (Exception ex) {
                                                            Log.e(getClass().getSimpleName(), ex.toString());
                                                        }
                                                    }
                                                    else if (which == 1) {
                                                        for (DataRow dr : DataInfo.DataRows) {
                                                            if (dr.IdMarker.equals(marker.getId())) {
                                                                listener.deleteForm(field.Identity, field.getIdChilForm(), dr.Index + 1);
                                                            }
                                                        }

                                                    }
                                                }
                                            });
                                    builder.create();
                                    builder.show();



                                }
                            });

                            gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                @Override
                                public View getInfoWindow(Marker arg0) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {

                                    LinearLayout info = new LinearLayout(listener.getActivity());
                                    info.setOrientation(LinearLayout.VERTICAL);

                                    TextView title = new TextView(listener.getActivity());
                                    title.setTextColor(Color.BLACK);
                                    title.setGravity(Gravity.CENTER);
                                    title.setTypeface(null, Typeface.BOLD);
                                    title.setText(marker.getTitle());

                                    TextView snippet = new TextView(listener.getActivity());
                                    snippet.setTextColor(Color.GRAY);
                                    snippet.setText(marker.getSnippet());

                                    info.addView(title);
                                    info.addView(snippet);

                                    return info;
                                }
                            });

                            if (LocalizacionServices.LOCATION != null) {
                                double lat = LocalizacionServices.LOCATION.getLatitude();
                                double lng = LocalizacionServices.LOCATION.getLongitude();
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14));
                            }

                            notifyMap();
                        }
                    });

                    if (supportMapFragment .getView() != null) {
                        ViewGroup.LayoutParams params = supportMapFragment.getView().getLayoutParams();
                        if (params != null) {
                            params.height = 750;
                            supportMapFragment.getView().setLayoutParams(params);
                        }

                        supportMapFragment.onResume();
                        supportMapFragment.getView().invalidate();
                    }
                }
                else {
                    gMap.clear();
                    int index = 0;
                    for (DataRow dr : DataInfo.DataRows) {
                        Marker marker = gMap.addMarker(new MarkerOptions().position(dr.getLocation()).title(dr.Titulo).snippet(dr.Descripcion).icon(BitmapDescriptorFactory.fromResource(dr.Clasificacion)));
                        dr.Index = index;
                        dr.IdMarker = marker.getId();
                    }
                }
            }
            catch (Exception ex) {
                Log.e(getClass().getName(), ex.toString());
            }
        }

        public DataTable getDataInfo() {
            return DataInfo;
        }


        public List<Componente.Tool> observables;

        public void subscribe(Componente.Tool tool) {
            if (observables == null)
                this.observables = new ArrayList<>();
            this.observables.add(tool);
        }

        public void notifyObserver(Componente.Tool tool, Object value) {
            if (value != null)
                value = value.toString();

            if (value != null) {
                String expresiones = getField().ExpresionCorrelacion;
                for (String expresion : expresiones.split("\\|")) {
                    if (expresion.equals(value))  {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.VISIBLE);
                        if (this.view != null)
                            this.view.setVisibility(View.VISIBLE);
                        break;
                    }
                    else {
                        if (getEtiqueta() != null)
                            getEtiqueta().setVisibility(View.GONE);
                        if (this.view != null)
                            this.view.setVisibility(View.GONE);
                    }
                }
            }
            else {
                if (getEtiqueta() != null) {
                    getEtiqueta().setVisibility(View.GONE);
                    if (this.view != null)
                        this.view.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void notifyParameter() {
        }

        @Override
        public void subscribeParameter(Tool tool) {
        }

        @Override
        public void addParameterComponent(String key, Tool tool) {
        }

        public void notifyObserver() {
            if (observables != null) {
                for (Componente.Tool  tool  : observables) {
                    tool.notifyObserver(this, this.getValue());
                }
            }
        }
    }


}