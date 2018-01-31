package mx.com.azteca.home.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.com.azteca.home.entity.generator.DaoObject;
import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.provider.IDataset;
import mx.com.azteca.home.model.provider.IDocumentInstance;
import mx.com.azteca.home.model.provider.pojo.ClienteReferencia;
import mx.com.azteca.home.model.provider.pojo.DocumentInstance;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.util.BusinessException;
import retrofit2.Call;
import retrofit2.Response;

public class MDataset extends BaseModel {


    public MDataset(Context context) {
        super(context);
    }

    public MDataset(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    public JsonArray getDataset(int idDataset, HashMap<String, String> parametros) throws Exception {
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", getSession().sessionID);
            IDataset apiDataset = super.getRetrofit().create(IDataset.class);
            Call<JsonArray> call = apiDataset.getDataset(mapHeader, idDataset, parametros);
            Response<JsonArray> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
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

}
