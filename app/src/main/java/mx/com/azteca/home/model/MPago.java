package mx.com.azteca.home.model;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.com.azteca.home.entity.generator.DaoObject;
import mx.com.azteca.home.model.provider.ICliente;
import mx.com.azteca.home.model.provider.pojo.MetodoPago;
import mx.com.azteca.home.model.provider.pojo.Pago;
import mx.com.azteca.home.util.BusinessException;
import retrofit2.Call;
import retrofit2.Response;


public class MPago extends BaseModel {

    public MPago(Context context) {
        super(context);
    }

    public MPago(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    public List<Pago> cargarMetodosPago() throws Exception {
        DaoObject<Pago> daoObject = new DaoObject<>();
        return daoObject.select(Pago.class);
    }

    public Pago getMetodoPago(String numTarjeta) throws Exception {
        try {
            DaoObject<Pago> daoObject = new DaoObject<>();
            daoObject.where("NumTarjeta", numTarjeta);
            return daoObject.single(Pago.class);
        }
        catch (Exception ex) {
            throw handledError("Ocurrió un problema al consultar la informaci\u00f3n", ex);
        }
    }

    public boolean exists(Pago pago) throws Exception {
        DaoObject<Pago> daoObject = new DaoObject<>();
        daoObject.where("NumTarjeta", pago.numTarjeta);
        return daoObject.single(Pago.class) != null;
    }

    public void updatePago(Pago pago) throws Exception {
        DaoObject<Pago> daoObject = new DaoObject<>();
        daoObject.where("NumTarjeta", pago.numTarjeta);
        daoObject.update(pago);
    }

    public void savePago(Pago pago) throws Exception {
        DaoObject<Pago> daoObject = new DaoObject<>();
        daoObject.insert(pago);
    }

    public void delete(String numTarjeta) throws Exception {
        DaoObject<Pago> daoObject = new DaoObject<>();
        daoObject.where("NumTarjeta", numTarjeta);
        daoObject.delete(Pago.class);
    }

    public void deleteClienteTarjeta(String sessionID, int idCliente, String numTarjeta) throws Exception {
        Map<String, String> mapHeader = new HashMap<>();
        mapHeader.put("Azteca-sessionID", sessionID);
        ICliente apiCliente = super.getRetrofit().create(ICliente.class);
        Call<Boolean> call = apiCliente.deleteClienteTarjeta(mapHeader, idCliente, numTarjeta);
        Response<Boolean> response = call.execute();
        if (!response.isSuccessful()) {
            throw handledError(response.errorBody().string());
        }
    }

    public void createConektaCard(String sessionID, int idCliente, int idUsuario, String token, Pago pago) throws Exception {
        try {
            MetodoPago metodoPago = new MetodoPago();
            metodoPago.idCliente = idCliente;
            metodoPago.numTarjeta = pago.numTarjeta;
            metodoPago.mesExpira = pago.mesExpira;
            metodoPago.anioExpira = pago.anioExpira;
            metodoPago.cVV = pago.cvv;
            metodoPago.activo = true;

            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", sessionID);
            ICliente apiCliente = super.getRetrofit().create(ICliente.class);
            Call<Boolean> call = apiCliente.createConektaCard(mapHeader, idCliente, idUsuario, token, metodoPago);
            Response<Boolean> response = call.execute();
            if (response.isSuccessful()) {
                DaoObject<Pago> daoPago = new DaoObject<>();
                daoPago.where("NumTarjeta", pago.numTarjeta);
                Pago info = daoPago.single(Pago.class);
                if (info != null)
                    updatePago(pago);
                else
                    savePago(pago);
            }
            else {
                throw new Exception(response.errorBody().string());
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError(ex.getMessage());
        }
    }

    public void loadMetodosPago(int idCliente) throws Exception {
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", getSession().sessionID);
            ICliente apiCliente = super.getRetrofit().create(ICliente.class);
            Call<List<MetodoPago>> call = apiCliente.clienteMetodoPago(mapHeader, idCliente);
            Response<List<MetodoPago>> response = call.execute();
            if (response.isSuccessful()) {
                List<MetodoPago> listMetodoPago = response.body();
                for (MetodoPago metodoPago : listMetodoPago) {
                    Pago pago = new Pago();
                    pago.numTarjeta = metodoPago.numTarjeta;
                    pago.mesExpira  = metodoPago.mesExpira;
                    pago.anioExpira = metodoPago.anioExpira;
                    pago.cvv        = metodoPago.cVV;

                    DaoObject<Pago> daoPago = new DaoObject<>();
                    daoPago.where("NumTarjeta", pago.numTarjeta);
                    Pago info = daoPago.single(Pago.class);
                    if (info != null)
                        updatePago(pago);
                    else
                        savePago(pago);
                }
            }
            else {
                throw new Exception(response.errorBody().string());
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError("Ocurrió un problema al consultar la informaci\u00f3n", ex);
        }
    }
}
