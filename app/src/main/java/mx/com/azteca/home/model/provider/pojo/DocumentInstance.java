package mx.com.azteca.home.model.provider.pojo;

import com.google.gson.JsonObject;

import java.util.Date;

import mx.com.azteca.home.entity.BaseEntity;
import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;
import mx.com.azteca.home.entity.orm.IOneToOne;


@IEntity(
    table = "DocumentInstanceHome",
    primaryKey = {"ID"}
)
public class DocumentInstance {

    @IColumn(name="ID", dataType="text")
    public String id;

    @IColumn(name="IdDocumento", dataType="int")
    public int idDocumento;

    @IColumn(name="Version", dataType="int")
    public int version;

    @IColumn(name="Folio", dataType="text")
    public String folio;

    @IColumn(name="Fecha", dataType="Date")
    public Date fecha;

    @IColumn(name="FechaStr", dataType="text")
    public String fechaStr;

    @IColumn(name="Contenido", dataType="text")
    public String contenido;

    @IColumn(name="TipoDocumento", dataType="text")
    public String tipoDocumento;

    @IColumn(name="Referencia", dataType="text")
    public String referencia;

    @IColumn(name="Latitud", dataType="double")
    public double latitud;

    @IColumn(name="Longitud", dataType="double")
    public double longitud;

    @IColumn(name="Altitud", dataType="double")
    public double altitud;

    @IColumn(name="Posicion", dataType="text")
    public String posicion;

    @IColumn(name="IdUsuarioAsignado", dataType="int")
    public int idUsuarioAsignado;

    @IColumn(name="IdWorkflowStep", dataType="int")
    public int idWorkflowStep;

    @IColumn(name="IdCurrentStep", dataType="int")
    public int idCurrentStep;

    @IOneToOne(cascade = true, joinColumn = {"idCurrentStep"})
    public WorkflowStep currentStep;

    @IColumn(name="IdStatus", dataType="int")
    public int idStatus;

    @IColumn(name="Status", dataType="text")
    public String status;

    @IColumn(name="StartStep", dataType="Date")
    public Date startStep;

    @IColumn(name="EndStep", dataType="Date")
    public Date endStep;

    @IColumn(name="HasStartStep", dataType="boolean")
    public boolean hasStartStep;

    @IColumn(name="HasEndStep", dataType="boolean")
    public boolean hasEndStep;

    @IColumn(name="IdSucursal", dataType="int")
    public int idSucursal;

    @IColumn(name="IdEmpresa", dataType="int")
    public int idEmpresa;

    @IColumn(name="LastUpdate", dataType="Date")
    public Date lastUpdate;

    @IColumn(name="UserUpdate", dataType="int")
    public int userUpdate;

    @IColumn(name="PCUpdate", dataType="text")
    public String pcUpdate;

    @IColumn(name="ContenidoJson", dataType="json")
    public JsonObject contenidoJson;




}