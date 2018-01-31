package mx.com.azteca.home.model.provider;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IServer {

    @GET("/api/kernel/server/check-session-expiration")
    Call<Boolean> isSessionExpired(@Query("sessionID") String sessionID);

}
