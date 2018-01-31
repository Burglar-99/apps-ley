package mx.com.azteca.home.model.provider.pojo;


import mx.com.azteca.home.entity.BaseEntity;
import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;
import mx.com.azteca.home.entity.orm.IOneToOne;

@IEntity(
    table = "WorkflowOptionHome",
    primaryKey = {"identity"}
)
public class WorkflowOption extends BaseEntity {

    @IColumn(name="Tag", dataType="text")
    public String tag;

    @IColumn(name="Text", dataType="text")
    public String text;

    @IColumn(name="LinkVertices", dataType="text")
    public String linkVertices;

    @IOneToOne(cascade = true)
    public WorkflowStep nextStep;

}