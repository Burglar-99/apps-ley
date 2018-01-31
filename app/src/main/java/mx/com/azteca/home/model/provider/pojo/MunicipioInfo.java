package mx.com.azteca.home.model.provider.pojo;

import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;

@IEntity(
    table = "Municipios",
    primaryKey = {"IdMunicipio"}
)
public class MunicipioInfo {

    @IColumn(name = "IdMunicipio", dataType="int")
    public int idMunicipio;

    @IColumn(name = "nombre", dataType="text")
    public String nombre;

    @IColumn(name = "idEstado", dataType="int")
    public int idEstado;

}
