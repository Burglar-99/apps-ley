package mx.com.azteca.home.model.provider.pojo;

import java.util.List;

import mx.com.azteca.home.entity.BaseEntity;
import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;
import mx.com.azteca.home.entity.orm.IOneToMany;
import mx.com.azteca.home.entity.orm.IOneToOne;


@IEntity(
    table = "WorkflowStepHome",
    primaryKey = {"ID"}
)
public class WorkflowStep  {

    @IColumn(name="ID", dataType="text")
    public int id;

    @IColumn(name="Stado", dataType="text")
    public String state;

    @IColumn(name="IdWorkflowStep", dataType="text")
    public int idWorkflowStep;

    @IColumn(name="Tag", dataType="text")
    public String tag;

    @IColumn(name="ActionText", dataType="text")
    public String actionText;

    @IColumn(name="StatusText", dataType="text")
    public String statusText;

    @IColumn(name="Opcional", dataType="text")
    public boolean optional;

    @IColumn(name="Orden", dataType="text")
    public int order;

    @IColumn(name="Icono", dataType="text")
    public String icon;

    @IColumn(name="IdFormulario", dataType="text")
    public int idFormulario;

    @IColumn(name="Duracion", dataType="text")
    public double duracion;

    @IColumn(name="Offline", dataType="text")
    public boolean offline;

    @IColumn(name="IsStart", dataType="text")
    public boolean isStart;

    @IColumn(name="IsEnd", dataType="text")
    public boolean isEnd;

    @IOneToOne(cascade = true)
    public WorkflowStep nextStep;

    @IColumn(name="LinkVertices", dataType="text")
    public String linkVertices;

    @IColumn(name="HasOptions", dataType="text")
    public boolean hasOptions;

    @IOneToMany(cascade = true)
    public List<WorkflowOption> options;

}