package mx.com.azteca.home.model.provider.pojo;

import mx.com.azteca.home.entity.orm.IColumn;
import mx.com.azteca.home.entity.orm.IEntity;

@IEntity(
    table = "TarjetaPago",
    primaryKey = {"NumTarjeta"}
)
public class Pago {

    @IColumn(name = "NumTarjeta", dataType="text")
    public String numTarjeta;

    @IColumn(name = "MesExpira", dataType="text")
    public int mesExpira;

    @IColumn(name = "AnioExpira", dataType="text")
    public int anioExpira;

    @IColumn(name = "CVV", dataType="text")
    public String cvv;


    public String getNumTarjeta() {
        String numero = numTarjeta;
        if (numTarjeta.length() > 4) {
            String baseNumero = numTarjeta.replace(" ", "");
            numero = "";
            int index = 0;
            while (index < baseNumero.length()) {
                if (index < baseNumero.length() - 4) {
                    numero += "*" + ( ( index + 1 ) % 4 == 0 ? " " : "");
                }
                else {
                    numero += baseNumero.substring(index);
                    break;
                }
                index++;
            }
        }
        return numero;
    }
}
