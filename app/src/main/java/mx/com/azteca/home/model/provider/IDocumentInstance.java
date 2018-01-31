package mx.com.azteca.home.model.provider;

import java.util.Map;

import mx.com.azteca.home.model.provider.pojo.DocumentInstance;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IDocumentInstance {

    @GET("api/kernel/bpm/documentos/create-instance-json")
    Call<DocumentInstance> createInstance(@HeaderMap Map<String, String> headers, @Query("idDocumento") int idDocumento, @Query("version") int version);

    @POST("api/kernel/bpm/documentos/next-step")
    Call<DocumentInstance> nextStep(@HeaderMap Map<String, String> headers, @Body DocumentInstance documentInstance, @Query("idNextStep") int idNextStep);

    @GET("api/kernel/bpm/documentos/previous-step")
    Call<DocumentInstance> previousStep(@HeaderMap Map<String, String> headers, @Query("folio") String folio);

    @POST("api/kernel/bpm/documentos/save-instance-json")
    Call<DocumentInstance> saveInstanceJson(@HeaderMap Map<String, String> headers, @Body DocumentInstance documentInstance);
}
