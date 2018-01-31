package mx.com.azteca.home.model.provider;


import java.util.Map;

import mx.com.azteca.home.model.provider.pojo.ClienteAvaluoInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IClientesAvaluos {

    @GET("/api/avaluos/clientes-avaluos/create")
    Call<ClienteAvaluoInfo> create(@HeaderMap Map<String, String> headers);

    @POST("/api/avaluos/clientes-avaluos/update")
    Call<ClienteAvaluoInfo> update(@HeaderMap Map<String, String> headers, @Body ClienteAvaluoInfo catalogInfo);

    @GET("/api/avaluos/clientes-avaluos/get-precio-servicio")
    Call<Double> getPrecioServicio(@HeaderMap Map<String, String> headers, @Query("folio") String folio);

    @POST("/api/avaluos/clientes-avaluos/create-avaluo-cliente")
    Call<ClienteAvaluoInfo> createAvaluoCliente(@HeaderMap Map<String, String> headers, @Query("folio") String folio);

    @POST("/api/avaluos/clientes-avaluos/generar-cargo")
    Call<ClienteAvaluoInfo> generarCargo(@HeaderMap Map<String, String> headers, @Query("folio") String folio, @Query("numero") String token);

    @POST("/api/avaluos/clientes-avaluos/generar-cargo-oxxo")
    Call<ClienteAvaluoInfo> generarCargoOxxo(@HeaderMap Map<String, String> headers, @Query("folio") String folio);

    @GET("/api/avaluos/clientes-avaluos/get-detail-id")
    Call<ClienteAvaluoInfo> getDetail(@HeaderMap Map<String, String> headers, @Query("itemID") int itemID);
}
