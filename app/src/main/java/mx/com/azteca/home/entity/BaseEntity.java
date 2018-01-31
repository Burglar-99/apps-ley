package mx.com.azteca.home.entity;

import java.util.UUID;

import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IMappedSuperclass;

@IMappedSuperclass
public class BaseEntity {

    @IColumn(name = "_EntityID", dataType="text")
    public String _EntityID;

    @IColumn(name = "_RefenrenceEntityID", dataType="text")
    public String _RefenrenceEntityID;


    public BaseEntity() {
        this._EntityID = UUID.randomUUID().toString();
    }

}
