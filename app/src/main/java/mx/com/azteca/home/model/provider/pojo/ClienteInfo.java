package mx.com.azteca.home.model.provider.pojo;


import java.util.Date;

import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;

@IEntity(
    table = "Cliente",
    primaryKey = {"Id"}
)
public class ClienteInfo extends CatalogInfo {

    @IColumn(name = "Nombre", dataType="text")
    public String nombre;

    @IColumn(name = "IdCuenta", dataType="int")
    public int idCuenta;

    @IColumn(name = "Latitud", dataType="double")
    public double latitud;

    @IColumn(name = "Longitud", dataType="double")
    public double longitud;

    @IColumn(name = "Telefonos", dataType="text")
    public String telefonos;

    @IColumn(name = "Celular", dataType="text")
    public String celular;

    @IColumn(name = "Correo", dataType="text")
    public String correo;

    @IColumn(name = "PagWeb", dataType="text")
    public String pagWeb;

    @IColumn(name = "IdListaPrecios", dataType="int")
    public int idListaPrecios;

    @IColumn(name = "IdGrupoDescuentos", dataType="int")
    public int idGrupoDescuentos;

    @IColumn(name = "CondicionesPago", dataType="text")
    public String condicionesPago;

    @IColumn(name = "PrimerCompra", dataType="Date")
    public Date primerCompra;

    @IColumn(name = "UltimaCompra", dataType="Date")
    public Date ultimaCompra;

    @IColumn(name = "Facturable", dataType="boolean")
    public boolean facturable;

    @IColumn(name = "IdContribuyente", dataType="int")
    public int idContribuyente;

    @IColumn(name = "IdDeudor", dataType="int")
    public int idDeudor;

    @IColumn(name = "IdCFDSerie", dataType="int")
    public int idCFDSerie;

    @IColumn(name = "WebUser", dataType="text")
    public String webUser;

    @IColumn(name = "WebPassword", dataType="text")
    public String webPassword;

    @IColumn(name = "IdComprobanteAddenda", dataType="int")
    public int idComprobanteAddenda;

    @IColumn(name = "IdComprobanteDestino", dataType="int")
    public int idComprobanteDestino;

    @IColumn(name = "Gln", dataType="text")
    public String gln;

    @IColumn(name = "GlnTienda", dataType="text")
    public String glnTienda;

    @IColumn(name = "NumProveedor", dataType="text")
    public String numProveedor;

    @IColumn(name = "IdFormaPago", dataType="int")
    public int idFormaPago;

    @IColumn(name = "CuentaPago", dataType="text")
    public String cuentaPago;

    @IColumn(name = "CodigoReferencia", dataType="text")
    public String codigoReferencia;

}
