package mx.com.azteca.home.model.provider.pojo;


import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;

@IEntity(
    table = "Colonias",
    primaryKey = {"IdColonia"}
)
public class ColoniaInfo {

    @IColumn(name = "IdColonia", dataType="int")
    public int idColonia;

    @IColumn(name = "nombre", dataType="text")
    public String nombre;

    @IColumn(name = "idMunicipio", dataType="text")
    public int idMunicipio;

    @IColumn(name = "codigoPostal", dataType="text")
    public String codigoPostal;

}
