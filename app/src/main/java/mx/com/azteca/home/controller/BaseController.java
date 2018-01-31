package mx.com.azteca.home.controller;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;
import mx.com.azteca.home.entity.generator.DaoObject;
import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.provider.pojo.Pago;
import mx.com.azteca.home.model.provider.pojo.PersonaInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import mx.com.azteca.home.util.IpatiServer;
import mx.com.azteca.home.util.PreferenceDevice;
import mx.com.azteca.home.view.BaseActivity;

public abstract class BaseController {

    private BaseActivity baseActivity;
    private Context context;

    private String userName;
    private String passwd;
    private String deviceID;

    public BaseActivity getBaseActivity() {
        return baseActivity;
    }

    public void setBaseActivity(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return baseActivity == null ?  context : baseActivity;
    }


    public void createConektaToken(int idUsuario, Pago pago, Token.CreateToken tokenListener) throws Exception {
        if (getBaseActivity() != null)
            getBaseActivity().mostrarProgress("Cargamdo informaci\u00f3n");
        DaoObject<UsuarioInfo> daoObject = new DaoObject<>();
        daoObject.where("ID", idUsuario);
        UsuarioInfo userInfo = daoObject.single(UsuarioInfo.class);
        DaoObject<PersonaInfo> daoPersona = new DaoObject<>();
        daoPersona.where("Id", userInfo.idPersona);
        PersonaInfo personaInfo = daoPersona.single(PersonaInfo.class);
        Conekta.setLanguage("es");
        Conekta.setPublicKey(IpatiServer.CONEKTA_PUBLIC_KEY.metodo);
        Conekta.setApiVersion("2.0.0");
        Conekta.collectDevice(getBaseActivity());
        Card card = new Card(personaInfo.getFullName(), pago.numTarjeta.replace(" ", ""), pago.cvv, String.valueOf(pago.mesExpira), String.valueOf(pago.anioExpira));
        Token token = new Token(getBaseActivity());
        token.onCreateTokenListener(data-> {
            if (getBaseActivity() != null)
                getBaseActivity().ocultarProgress();
            tokenListener.onCreateTokenReady(data);
        });
        token.create(card);
    }

    public <T extends BaseModel> T newInstanceModel(Class<T> model) {
        try {
            String userName;
            String passwd;
            String deviceID;
            if (getBaseActivity() != null ) {
                userName = (String) getBaseActivity().getData(PreferenceDevice.USER_NAME.Key, "");
                passwd   = (String)getBaseActivity().getData(PreferenceDevice.USER_PASSWD.Key, "");
                deviceID = (String)getBaseActivity().getData("IMEI", "");
            }
            else {
                userName = this.userName;
                passwd   = this.passwd;
                deviceID = this.deviceID;
            }
            Constructor<T> cons  = model.getConstructor(Context.class, String.class, String.class, String.class);
            return cons.newInstance(getContext(), userName, passwd, deviceID);
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;

    }


    public abstract void subscribe(Listener listener);

    public abstract class Listener {


        public void notificarUsuario(String title, String message) {
            if (getBaseActivity() != null)
                getBaseActivity().notificarUsuario(title, message);
        }

        public void mostrarProgress(String title, String message) {
        }

        public void mostrarProgress(String message) {
            if (getBaseActivity() != null)
                getBaseActivity().mostrarProgress(message);
        }

        public void ocultarProgress() {
            if (getBaseActivity() != null)
                getBaseActivity().ocultarProgress();
        }


        public BaseActivity getBaseActivity() {
            return baseActivity;
        }

        public void iniciarNewThread(Class<? extends AppCompatActivity> activity) {
            if (baseActivity != null) {
                baseActivity.iniciarNewThread(activity);
            }
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

}
