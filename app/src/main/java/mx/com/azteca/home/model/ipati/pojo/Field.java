package mx.com.azteca.home.model.ipati.pojo;


import java.util.ArrayList;
import java.util.List;

public class Field extends BaseInfo {

    // ---- Tipos datos -----//

    // System.String	STRING
    // System.Int32	    INT
    // System.Int64	    BIGINT
    // System.Decimal	DECIMAL
    // System.DateTime	DATE
    // System.DateTime	TIME
    // System.DateTime	DATETIME
    // System.String	LOCATION
    // System.String	POLYGON
    // System.Guid	    FILE
    // System.Guid	    IMAGE
    // System.Byte[]	BINARY
    // System.Boolean	BOOLEAN

    // ---- Tipos campo -----//

    // Etiqueta	                LABEL
    // Caja de Texto	        TEXTBOX
    // Cuadro de Verificación	CHECKBOX
    // Lista Desplegable	    COMBOBOX
    // Selector	                SELECTOR
    // Tabla	                GRID
    // Imágen	                IMAGEBOX
    // Archivo	                FILEBOX
    // Memo	                    MEMO
    // Selector de Fecha    	DATEPICKER
    // Selector de Hora	        TIMEPICKER
    // Selector Fecha y Hora	DATETIMEPICKER

    public int IdDocumento;
    public int Version;
    public String Etiqueta;
    public String XmlTag;
    public String NombreLargo;
    public int IdTipoDato;
    public int Longitud;
    public int IdDataset;
    public String DisplayMember;
    public String ValueMember;
    public boolean ComplexType;
    public String XmlPath;
    public String ClassPath;
    public String MemberPath;
    public boolean Coleccion;
    public int OcurrenciaMin;
    public int OcurrenciaMax;
    public double LimiteInferior;
    public double LimiteSuperior;
    public String RegularExpression;
    public int CampoCorrelacion;
    public String ExpresionCorrelacion;
    public String ExpresionValidacion;
    public int Nivel;
    public String Path;
    public int Orden;
    public String NamedPath;
    public Object Value;
    public boolean Required;
    public boolean ReadOnly;
    public String ValidationRule;
    public String TipoControl;
    public int IdPadre;
    public String Referencia;
    public String TipoDato;
    public String Parametros;
    public String Tag;
    public long LastUpdate;
    public long CreateDate;
    public String PCUpdate;
    public long ActivateDate;
    public long DeactivateDate;
    public int UserCreate;
    public int UserUpdate;
    public boolean Active;
    public String SessionID;
    public String InfoJson;
    private Dataset dataset;
    private List<Field> listField;
    private String valueString;
    private int IdChilForm = -2;
    private TipoDato TipoDatoInfo;
    private String Location;
    private String Clasificacion;
    private int IdCampo;


    public Field() {
        this.listField = new ArrayList<>();
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setFields(List<Field> listField) {
        this.listField = listField;
    }

    public List<Field> getFields() {
        return this.listField;
    }

    public String getValueString() {
        if (valueString == null) {
            valueString = "";
        }
        if (this.Value != null && this.Value instanceof String) {
            valueString = this.Value.toString();
        }
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public Object getFieldValue() {
        if (this.Tag.equals("BOOLEAN")) {
            return this.Value;
        }
        return getValueString();
    }

    public int getIdChilForm() {
        return IdChilForm;
    }

    public void setIdChilForm(int idChilForm) {
        IdChilForm = idChilForm;
    }

    public TipoDato getTipoDatoInfo() {
        return TipoDatoInfo;
    }

    public void setTipoDatoInfo(TipoDato tipoDatoInfo) {
        this.TipoDatoInfo = tipoDatoInfo;
    }


    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getClasificacion() {
        return Clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        Clasificacion = clasificacion;
    }

    public int getIdCampo() {
        return IdCampo;
    }

    public void setIdCampo(int idCampo) {
        IdCampo = idCampo;
    }

    public Object cloneField() throws CloneNotSupportedException{
        Field field = new Field();
        field.Identity = this.Identity;
        field.Code = this.Code;
        field.Nombre = this.Nombre;
        field.IdDocumento = this.IdDocumento;
        field.Version = this.Version;
        field.Etiqueta = this.Etiqueta;
        field.Orden = this.Orden;
        field.Required = this.Required;
        field.ValidationRule = this.ValidationRule;
        field.TipoDato = this.TipoDato;
        field.TipoControl = this.TipoControl;
        field.IdDataset = this.IdDataset;
        field.DisplayMember = this.DisplayMember;
        field.ValueMember = this.ValueMember;
        field.XmlTag = this.XmlTag;
        field.NombreLargo = this.NombreLargo;
        field.IdTipoDato = this.IdTipoDato;
        field.Longitud = this.Longitud;
        field.ComplexType = this.ComplexType;
        field.Coleccion = this.Coleccion;
        field.ClassPath = this.ClassPath;
        field.MemberPath = this.MemberPath;
        field.XmlPath = this.XmlPath;
        field.OcurrenciaMax = this.OcurrenciaMax;
        field.OcurrenciaMin = this.OcurrenciaMin;
        field.RegularExpression = this.RegularExpression;
        field.CampoCorrelacion = this.CampoCorrelacion;
        field.ExpresionCorrelacion = this.ExpresionCorrelacion;
        field.ExpresionValidacion = this.ExpresionValidacion;
        field.IdPadre = this.IdPadre;
        field.Nivel = this.Nivel;
        field.Path = this.Path;
        field.NamedPath = this.NamedPath;
        field.Value = this.Value;
        field.Tag  = this.Tag;
        field.Referencia = this.Referencia;
        field.ReadOnly = this.ReadOnly;
        field.LastUpdate = this.LastUpdate;
        field.CreateDate = this.CreateDate;
        field.PCUpdate = this.PCUpdate;
        field.ActivateDate = this.ActivateDate;
        field.DeactivateDate = this.DeactivateDate;
        field.UserCreate = this.UserCreate;
        field.UserUpdate = this.UserUpdate;
        field.Active = this.Active;
        field.SessionID = this.SessionID;
        field.setIdCampo(this.getIdCampo());
        return field;
    }

    public String getNamedPath() {
        return "Root\\".concat(NamedPath);
    }
}
