package mx.com.azteca.home.model.provider;

import java.util.List;
import java.util.Map;

import mx.com.azteca.home.model.provider.pojo.ClienteInfo;
import mx.com.azteca.home.model.provider.pojo.MetodoPago;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ICliente {

    @GET("/api/ventas/clientes/create")
    Call<ClienteInfo> create(@HeaderMap Map<String, String> headers);

    @POST("/api/ventas/clientes/update")
    Call<ClienteInfo> update(@HeaderMap Map<String, String> headers, @Body ClienteInfo catalogInfo);

    @POST("/api/ventas/clientes-usuarios/update-cliente-usuario")
    Call<Void> updateClienteUsuario(@HeaderMap Map<String, String> headers, @Query("idCliente") int idCliente, @Query("idUsuario") int idUsuario);

    @GET("/api/ventas/clientes-usuarios/usuario-exist")
    Call<Boolean> usuarioExist(@HeaderMap Map<String, String> headers, @Query("user") String user);

    @POST("/api/ventas/clientes-usuarios/create-tarjeta")
    Call<Boolean> createClienteTarjeta(@HeaderMap Map<String, String> headers, @Body MetodoPago info);

    @POST("/api/ventas/clientes-usuarios/delete-tarjeta")
    Call<Boolean> deleteClienteTarjeta(@HeaderMap Map<String, String> headers, @Query("idCliente") int idCliente, @Query("numTarjeta") String numTarjeta);

    @POST("/api/ventas/clientes-usuarios/create-conekta-card")
    Call<Boolean> createConektaCard(@HeaderMap Map<String, String> headers, @Query("idCliente") int idCliente, @Query("idUsuario") int idUsuario, @Query("tokenId") String tokenId, @Body MetodoPago info);

    @GET("/api/ventas/clientes-usuarios/cliente-by-usuario")
    Call<ClienteInfo> clienteByUsuario(@HeaderMap Map<String, String> headers, @Query("idUsaurio") int idUsaurio);

    @GET("/api/ventas/clientes-usuarios/cliente-metodo-pago")
    Call<List<MetodoPago>> clienteMetodoPago(@HeaderMap Map<String, String> headers, @Query("idCliente") int idCliente);

    @GET("/api/ventas/clientes-usuarios/get-public-token")
    Call<String> getPublicToken(@HeaderMap Map<String, String> headers);
}
