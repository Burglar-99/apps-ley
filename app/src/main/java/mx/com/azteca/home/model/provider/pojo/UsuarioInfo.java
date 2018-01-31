package mx.com.azteca.home.model.provider.pojo;

import mx.com.azteca.home.entity.BaseEntity;
import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;
import mx.com.azteca.home.entity.orm.IOneToOne;

@IEntity(
    table = "UsuarioInfoHome",
    primaryKey = {"ID"}
)
public class UsuarioInfo extends BaseEntity {

    @IColumn(name="ID", dataType="int")
    public int identity;

    @IColumn(name="Codigo", dataType="text")
    public String code;

    @IColumn(name="UserName", dataType="text")
    public String userName;

    @IColumn(name="Correo", dataType="text")
    public String correo;

    @IColumn(name="IdRol", dataType="int")
    public int idRol;

    @IColumn(name="Password", dataType="text")
    public String password;

    @IColumn(name="IdPersona", dataType="int")
    public int idPersona;

    @IColumn(name="IdBranch", dataType="int")
    public int idBranch;

    @IColumn(name="IdCompany", dataType="int")
    public int idCompany;

    @IOneToOne(cascade = true, joinColumn = {"IdPersona"})
    public PersonaInfo persona;

    private int idCliente;

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
}
