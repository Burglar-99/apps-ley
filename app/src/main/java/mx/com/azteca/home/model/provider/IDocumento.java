package mx.com.azteca.home.model.provider;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface IDocumento {

    @GET("api/kernel/bpm/documentos/user-list")
    Call<String> loadListByUser(@HeaderMap Map<String, String> headers, @Query("idDocumento") int idDocumento, @Query("fecha") String fecha, @Query("folio") String folio);

}
