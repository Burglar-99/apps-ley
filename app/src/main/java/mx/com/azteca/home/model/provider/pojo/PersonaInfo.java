package mx.com.azteca.home.model.provider.pojo;


import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;

@IEntity(
    table = "PersonasHome",
    primaryKey = {"Id"}
)
public class PersonaInfo extends CatalogInfo {

    @IColumn(name = "Nombre", dataType="text")
    public String nombre;

    @IColumn(name = "ApellidoPaterno", dataType="text")
    public String apellidoPaterno;

    @IColumn(name = "ApellidoMaterno", dataType="text")
    public String apellidoMaterno;

    @IColumn(name = "TelefonoCasa", dataType="text")
    public String telefonoCasa;

    @IColumn(name = "TelefonoOficina", dataType="text")
    public String telefonoOficina;

    @IColumn(name = "Celular", dataType="text")
    public String celular;

    @IColumn(name = "Correo", dataType="text")
    public String correo;


    public String getFullName() {
        return this.nombre + " " + this.apellidoPaterno + " " + this.apellidoMaterno;
    }
}
