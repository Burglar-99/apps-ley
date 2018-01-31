package mx.com.azteca.home.model.provider.pojo;


import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;

@IEntity(
        table = "BaseInstanceCurrentStep",
        primaryKey = {"ID"}
)
public class BaseInstanceCurrentStep {

    @IColumn(name="ID", dataType="text")
    public String idDocumentInstance;

    @IColumn(name="ID", dataType="int")
    public int idWorkflowStep;

}
