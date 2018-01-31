package mx.com.azteca.home.model.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.com.azteca.home.model.provider.pojo.ClienteReferencia;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IDataset {

    @POST("api/kernel/bpm/datasets/result")
    Call<JsonArray> getDataset(@HeaderMap Map<String, String> headers, @Query("idDataset") int idDataset, @Body HashMap<String, String> parametros);

}
