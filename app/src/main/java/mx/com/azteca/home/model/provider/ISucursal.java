package mx.com.azteca.home.model.provider;


import java.util.List;
import java.util.Map;

import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioItem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ISucursal {

    @POST("/api/kernel/security/sucursales/add-members")
    Call<Void> addMembers(@HeaderMap Map<String, String> headers, @Query("idBranch") int idBranch, @Body List<UsuarioItem> users);

}
