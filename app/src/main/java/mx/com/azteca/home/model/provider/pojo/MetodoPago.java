package mx.com.azteca.home.model.provider.pojo;


public class MetodoPago {

    public int idClienteTarjeta;
    public int idCliente;
    public String numTarjeta;
    public int mesExpira;
    public int anioExpira;
    public String cVV;
    public boolean activo;
    public String tarjetaID;

    public MetodoPago() {
        this.numTarjeta = "";
        this.cVV        = "";
        this.tarjetaID  = "";
    }

}
