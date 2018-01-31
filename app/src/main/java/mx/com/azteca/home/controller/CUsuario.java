package mx.com.azteca.home.controller;

import mx.com.azteca.home.model.MUsuario;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import mx.com.azteca.home.util.PreferenceDevice;
import mx.com.azteca.home.view.LoginActivity;
import mx.com.azteca.home.view.MapActivity;
import mx.com.azteca.home.view.ipati.DocManagerActivity;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CUsuario extends BaseController {

    public ListenerUsuario listener;

    public CUsuario() {
        this.listener = new ListenerUsuario();
    }

    public void iniciarAplicacion() {
        Object loginUser = listener.getBaseActivity().getData(PreferenceDevice.LOGGED_USUER.Key);
        if (loginUser != null && (int)loginUser > 0) {
            Object guideCompleta = listener.getBaseActivity().getData(PreferenceDevice.COMPLETED_GUIDE.Key);
            if (guideCompleta == null || !(boolean) guideCompleta) {
                listener.iniciarNewThread(MapActivity.class);
              //  listener.iniciarNewThread(DocManagerActivity.class);
            }
            else {
              //  listener.iniciarNewThread(DocManagerActivity.class);
                listener.iniciarNewThread(MapActivity.class);
            }
        }
        else {
            listener.iniciarNewThread(LoginActivity.class);
        }
    }

    public void login(String userName, String password) {
        listener.mostrarProgress("Iniciando aplicaci\u00f3n");
        Observable.create((Observable.OnSubscribe<SessionInfo>) subscriber -> {
            try {
                String imei = listener.getBaseActivity().getData("IMEI").toString();
                MUsuario mUsuario = new MUsuario(listener.getBaseActivity());
                SessionInfo sessionInfo = mUsuario.login(userName, password, imei);
                subscriber.onNext(sessionInfo);
                subscriber.onCompleted();
            }
            catch (Exception e) {
                subscriber.onError(new Exception("Usuario y/o contrase\u00f1a incorrecta", e));
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<SessionInfo>() {
            @Override
            public void onCompleted() {
               // listener.iniciarNewThread(MapActivity.class);
                listener.iniciarNewThread(DocManagerActivity.class);
                listener.ocultarProgress();
            }
            @Override
            public void onError(Throwable e) {
                listener.notificarUsuario("Error", e.getMessage());
                listener.ocultarProgress();
            }
            @Override
            public void onNext(SessionInfo sessionInfo) {
                listener.getBaseActivity().setData(PreferenceDevice.LOGGED_USUER.Key, sessionInfo.user.identity);
                listener.getBaseActivity().setData(PreferenceDevice.ID_CLIENTE.Key, sessionInfo.user.getIdCliente());
                listener.getBaseActivity().setData(PreferenceDevice.USER_NAME.Key, userName);
                listener.getBaseActivity().setData(PreferenceDevice.USER_PASSWD.Key, password);
            }
        });
    }

    public void registrarCuenta(String nombre, String apPaterno, String apMaterno, String usuario, String contrasena, String confirmacion) {
        listener.mostrarProgress("Registrando usuario");
        Observable.create((Observable.OnSubscribe<UsuarioInfo>) subscriber -> {
            try {
                MUsuario mUsuario = new MUsuario(listener.getBaseActivity());
                SessionInfo sessionInfo = mUsuario.getSession("system", "a987654321/*", "", true);
                boolean exist = mUsuario.usuarioExist(sessionInfo.sessionID, usuario);
                if (exist) {
                    throw new Exception("El nombre de la cuenta ya existe");
                }
                UsuarioInfo usuarioInfo = mUsuario.create(sessionInfo.sessionID, nombre, apPaterno, apMaterno, usuario, contrasena, confirmacion);
                subscriber.onNext(usuarioInfo);
                subscriber.onCompleted();
            }
            catch (Exception e) {
                subscriber.onError(e);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<UsuarioInfo>() {
            @Override
            public void onCompleted() {
          //      listener.iniciarNewThread(MapActivity.class);
                listener.iniciarNewThread(DocManagerActivity.class);
                listener.ocultarProgress();
            }
            @Override
            public void onError(Throwable e) {
                listener.notificarUsuario("Error", e.getMessage());
                listener.ocultarProgress();
            }
            @Override
            public void onNext(UsuarioInfo usuarioInfo) {
                listener.getBaseActivity().setData(PreferenceDevice.LOGGED_USUER.Key, usuarioInfo.identity);
                listener.getBaseActivity().setData(PreferenceDevice.ID_CLIENTE.Key, usuarioInfo.getIdCliente());
                listener.getBaseActivity().setData(PreferenceDevice.USER_NAME.Key, usuario);
                listener.getBaseActivity().setData(PreferenceDevice.USER_PASSWD.Key, contrasena);
            }
        });
    }

    public void recuperarCuenta(String email) {
        listener.mostrarProgress("Enviando email");
        Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                MUsuario mUsuario = new MUsuario(getContext());
                mUsuario.recuperarCuenta(email);
                subscriber.onCompleted();
            }
            catch (Exception e) {
                subscriber.onError(e);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                listener.onRecuperarCuenta();
                listener.ocultarProgress();
            }
            @Override
            public void onError(Throwable e) {
                listener.notificarUsuario("Error", e.getMessage());
                listener.ocultarProgress();
            }
            @Override
            public void onNext(Boolean result) {
            }
        });
    }

    @Override
    public void subscribe(Listener listener) {
        if (listener instanceof  ListenerUsuario) {
            this.listener = (ListenerUsuario) listener;
        }
    }

    public class ListenerUsuario extends Listener {
        public void onRecuperarCuenta() {
        }
    }
}
