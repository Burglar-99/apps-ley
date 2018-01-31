package mx.com.azteca.home.model.provider;


import java.util.Date;
import java.util.Map;

import mx.com.azteca.home.model.provider.pojo.DocumentInstance;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;

public interface IReporteAvaluoExpress {

    @GET("api/appraisals/reporte-avaluos-express/load-avaluo-express-reporte")
    Call<String> loadAvaluoExpressReporte(@HeaderMap Map<String, String> headers, @Query("idDocumento") int idDocumento, @Query("fecha") String fecha, @Query("folio") String folio);

}
