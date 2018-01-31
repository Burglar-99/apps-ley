package mx.com.azteca.home.model.provider.pojo;


import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;

@IEntity(
    table = "Poblaciones",
    primaryKey = {"IdPoblacon"}
)
public class PoblacionInfo {

    @IColumn(name = "IdPoblacon", dataType="int")
    public int IdPoblacon;

    @IColumn(name = "Nombre", dataType="text")
    public String Nombre;

    @IColumn(name = "IdMunicipio", dataType="int")
    public int IdMunicipio;

}
