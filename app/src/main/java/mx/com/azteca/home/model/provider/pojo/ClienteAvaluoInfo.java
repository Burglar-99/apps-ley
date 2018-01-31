package mx.com.azteca.home.model.provider.pojo;


import java.util.Date;

import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;

@IEntity(
    table = "ClienteAvaluo",
    primaryKey = {"Id"}
)
public class ClienteAvaluoInfo extends CatalogInfo {

    @IColumn(name = "Fecha", dataType="Date")
    public Date fecha;

    @IColumn(name = "IdServicio", dataType="int")
    public int idServicio;

    @IColumn(name = "Precio", dataType="double")
    public double precio;

    @IColumn(name = "IdCliente", dataType="int")
    public int idCliente;

    @IColumn(name = "Folio", dataType="text")
    public String folio;

    @IColumn(name = "IdOrden", dataType="text")
    public String idOrden;

    @IColumn(name = "IdMetodoPagoServicio", dataType="int")
    public int idMetodoPagoServicio;

    @IColumn(name = "IdStatus", dataType="int")
    public int idStatus;

    @IColumn(name = "Referencia", dataType="text")
    public String referencia;

}
