package mx.com.azteca.home.model.provider;


import java.util.Map;

import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IUsuario {

    @GET("/api/kernel/security/usuarios/login")
    Call<SessionInfo> login(@Query("userName") String userName, @Query("password") String password, @Query("workStation") String workStation, @Query("timeZone") int timeZone);

    @GET("/api/kernel/security/usuarios/create")
    Call<UsuarioInfo> create(@HeaderMap Map<String, String> headers);

    @POST("/api/kernel/security/usuarios/update")
    Call<UsuarioInfo> update(@HeaderMap Map<String, String> headers, @Body UsuarioInfo catalogInfo);

    @GET("/api/security/usuarios/send-mail-user")
    Call<Void> restorePasswd(@Query("emailDestino") String emailDestino, @Query("emailMensaje") String emailMensaje, @Query("idUsuario") String idUsuario);



}
