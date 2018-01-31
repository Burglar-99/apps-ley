package mx.com.azteca.home.model;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import mx.com.azteca.home.entity.generator.DaoObject;
import mx.com.azteca.home.model.provider.IServer;
import mx.com.azteca.home.model.provider.IUsuario;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.util.BusinessException;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseModel {

    private Context context;
    private Retrofit retrofit;
    private String userName;
    private String passwd;
    private String workStation;

    public BaseModel(Context context) {
        this.context = context;
        initialize();
    }

    public BaseModel(Context context, String userName, String passwd, String workStation) {
        this.context = context;
        this.userName = userName;
        this.passwd = passwd;
        this.workStation = workStation;
        initialize();
    }

    public void initialize() {
    }

    private void build() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(360, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        this.retrofit = new Retrofit.Builder()
                //.baseUrl("http://ipati2.ddns.net:8088")
                //.baseUrl("http://192.168.100.3:50027")
                //.baseUrl("http://192.168.100.5:50027")
                //.baseUrl("http://192.168.111.121:50027")
                //.baseUrl("http://ipati2.ddns.net:8081")
                .baseUrl("http://192.168.15.100:8081")
                //.baseUrl("http://74.208.210.150:8081")
                //.baseUrl("http://192.168.111.121:50027")
                //.baseUrl("http://192.168.111.121:8080")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public Context getContext() {
        return context;
    }

    public Retrofit getRetrofit() {
        if (retrofit == null)
            build();
        return retrofit;
    }

    public SessionInfo getSession() throws Exception {
        try {
            DaoObject<SessionInfo> daoSession = new DaoObject<>();
            List<SessionInfo> listSession = daoSession.select(SessionInfo.class);
            for (SessionInfo session : listSession) {
                IServer apiServer = getRetrofit().create(IServer.class);
                Call<Boolean> callCliente = apiServer.isSessionExpired(session.sessionID);
                Response<Boolean> response = callCliente.execute();
                if (response.isSuccessful()) {
                    boolean alive = response.body();
                    if (!alive) {
                        daoSession.clearConditions();
                        daoSession.where("SessionID", session.sessionID);
                        daoSession.delete(SessionInfo.class);
                    }
                    else {
                        return session;
                    }
                }
                else {
                    throw new BusinessException("Ocurrió un problema al consultar el servidor");
                }
            }
            return getSession(userName, passwd, workStation);
        }
        catch (Exception ex) {
            throw handledError("Ocurrió un problema al consultar el servidor", ex);
        }
    }


    public SessionInfo getSession(String userName, String password, String workStation) throws Exception {
        return getSession(userName, password, workStation, false);
    }

    public SessionInfo getSession(String userName, String password, String workStation, boolean background) throws Exception {
        try {
            int timeZone = TimeZone.getDefault().getRawOffset();
            IUsuario apiUsuario = getRetrofit().create(IUsuario.class);
            Call<SessionInfo> call = apiUsuario.login(userName, password, workStation, timeZone);
            Response<SessionInfo> response = call.execute();
            if (response.isSuccessful()) {
                SessionInfo session = response.body();
                if (!background) {
                    DaoObject<SessionInfo> daoSession = new DaoObject<>();
                    daoSession.delete(SessionInfo.class);
                    daoSession.insert(session);
                }
                return session;
            }
            else {
                throw new Exception(response.errorBody().toString());
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError(ex.getMessage());
        }
    }


    public Exception handledError(String error) throws Exception {
        try {
            if (error != null && error.startsWith("{")) {
                JsonParser parser = new JsonParser();
                JsonObject jsonError = parser.parse(error).getAsJsonObject();
                if (jsonError.has("message")) {
                    return new BusinessException(jsonError.get("message").getAsString());
                }
            }
        }
        catch (Exception ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return new Exception("Ocurri\u00f3 un problema al consultar la informaci\u00f3n");
    }

    public Exception handledError(String message, Exception ex) throws Exception {
        return new Exception(message, ex);
    }

    public String getUserName() {
        return userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getWorkStation() {
        return workStation;
    }
}
