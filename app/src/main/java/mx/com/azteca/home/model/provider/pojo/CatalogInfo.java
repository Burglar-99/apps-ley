package mx.com.azteca.home.model.provider.pojo;


import java.util.Date;

import mx.com.azteca.home.entity.BaseEntity;
import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IMappedSuperclass;

@IMappedSuperclass
public class CatalogInfo extends BaseEntity {

    @IColumn(name = "Id", dataType="int")
    public int identity;

    @IColumn(name = "Codigo", dataType="text")
    public String code;

    @IColumn(name = "Activo", dataType="int")
    public boolean active;

    @IColumn(name = "IdSucursal", dataType="int")
    public int idBranch;

    @IColumn(name = "IdCompania", dataType="int")
    public int idCompany;

    @IColumn(name = "CreateDate", dataType="int")
    public Date createDate;

    @IColumn(name = "LastUpdate", dataType="int")
    public Date lastUpdate;

    @IColumn(name = "PCUpdate", dataType="text")
    public String pcUpdate;

    @IColumn(name = "UserCreate", dataType="int")
    public int userCreate;

    @IColumn(name = "UserUpdate", dataType="int")
    public int userUpdate;

    @IColumn(name = "ActivateDate", dataType="int")
    public Date activateDate;

    @IColumn(name = "DeactivateDate", dataType="int")
    public Date deactivateDate;

    @IColumn(name = "SessionID", dataType="text")
    public String sessionID;

}
