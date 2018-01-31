package mx.com.azteca.home.model.provider.pojo;


import java.util.Date;

import mx.com.azteca.home.entity.BaseEntity;
import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;
import mx.com.azteca.home.entity.orm.IOneToOne;

@IEntity(
    table = "SessionInfo",
    primaryKey = {"SessionID"}
)
public class SessionInfo extends BaseEntity {

    @IColumn(name="SessionID", dataType="text")
    public String sessionID;

    @IColumn(name="WorkStation", dataType="text")
    public String workStation;

    @IColumn(name="BusinessDate", dataType="Date")
    public Date businessDate;

    @IColumn(name="LoginDate", dataType="Date")
    public Date loginDate;

    @IColumn(name="TimezoneOffset", dataType="int")
    public int timezoneOffset;

    @IOneToOne(cascade = true)
    public UsuarioInfo user;

}
