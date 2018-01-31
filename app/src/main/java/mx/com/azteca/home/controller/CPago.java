package mx.com.azteca.home.controller;


import android.util.Log;

import org.json.JSONObject;

import java.util.List;

import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Token;
import mx.com.azteca.home.entity.generator.DaoObject;
import mx.com.azteca.home.model.MPago;
import mx.com.azteca.home.model.MUsuario;
import mx.com.azteca.home.model.provider.pojo.Pago;
import mx.com.azteca.home.model.provider.pojo.PersonaInfo;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import mx.com.azteca.home.util.IpatiServer;
import mx.com.azteca.home.util.PreferenceDevice;
import mx.com.azteca.home.util.PreferenceServer;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CPago extends BaseController {

    public ListenerPago listener;

    public CPago() {
        this.listener = new ListenerPago();
    }

    public void loadMetodosPago() {
        Observable.create((Observable.OnSubscribe<List<Pago>>) subscriber -> {
            try {
                MPago mPago = new MPago(getBaseActivity());
                subscriber.onNext(mPago.cargarMetodosPago());
                subscriber.onCompleted();
            }
            catch (Exception e) {
                subscriber.onError(e);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Pago>>() {
            @Override
            public void onCompleted() {
                getBaseActivity().ocultarProgress();
            }

            @Override
            public void onError(Throwable e) {
                getBaseActivity().notificarUsuario("Error", e.getMessage());
                getBaseActivity().ocultarProgress();
            }

            @Override
            public void onNext(List<Pago> pagos) {
                if (listener != null) {
                    listener.cargarMetodosPago(pagos);
                }
            }
        });
    }

    public void update(String numTarjeta, int mes, int anio, String cvv) {
        getBaseActivity().mostrarProgress("Actualizando informaci\u00f3n");

        Pago pago = new Pago();
        pago.numTarjeta = numTarjeta;
        pago.mesExpira  = mes;
        pago.anioExpira = anio;
        pago.cvv = cvv;

        int idCliente = Integer.parseInt(getBaseActivity().getData(PreferenceDevice.ID_CLIENTE.Key).toString());
        int idUsuario = Integer.parseInt(getBaseActivity().getData(PreferenceDevice.LOGGED_USUER.Key).toString());

        try {
            createConektaToken(idUsuario, pago, new Token.CreateToken() {
                @Override
                public void onCreateTokenReady(JSONObject data) {
                    try {
                        getBaseActivity().ocultarProgress();
                        if (data.has("details")) {
                            getBaseActivity().notificarUsuario("Error", data.getJSONArray("details").getJSONObject(0).getString("message"));
                        }
                        else if (data.has("id")) {
                            String tokenID = data.getString("id");
                            saveConektaCard(idCliente, idUsuario, tokenID, pago);
                        }
                    }
                    catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                    }
                }
            });
        }
        catch (Exception ex) {
            getBaseActivity().notificarUsuario("Error", ex.getMessage());
        }
    }

    public void delete(String numTarjeta) {
        getBaseActivity().mostrarProgress("Actualizando informaci\u00f3n");
        Observable.create((Observable.OnSubscribe<Pago>) subscriber -> {
            try {
                int idCliente = Integer.parseInt(getBaseActivity().getData(PreferenceDevice.ID_CLIENTE.Key).toString());
                String imei = listener.getBaseActivity().getData("IMEI").toString();
                MUsuario mUsuario = new MUsuario(listener.getBaseActivity());
                SessionInfo sessionInfo = mUsuario.getSession(listener.getBaseActivity().getData(PreferenceServer.USURIO.Key, "MobileUser").toString(),
                        listener.getBaseActivity().getData(PreferenceServer.PASSWD.Key, "L0g1n@dm1n").toString(), imei);

                MPago mPago = new MPago(getBaseActivity());
                mPago.deleteClienteTarjeta(sessionInfo.sessionID,idCliente, numTarjeta);

                mPago.delete(numTarjeta);
                subscriber.onCompleted();
            }
            catch (Exception e) {
                subscriber.onError(e);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Pago>() {
            @Override
            public void onCompleted() {
                getBaseActivity().ocultarProgress();
                loadMetodosPago();
            }

            @Override
            public void onError(Throwable e) {
                getBaseActivity().notificarUsuario("Error", e.getMessage());
                getBaseActivity().ocultarProgress();
            }

            @Override
            public void onNext(Pago pago) {
            }
        });
    }

    public void saveConektaCard(int idCliente, int idUsuario, String token, Pago pago) {
        getBaseActivity().mostrarProgress("Cargando informaci\u00f3n");
        Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                String imei = listener.getBaseActivity().getData("IMEI").toString();
                MUsuario mUsuario = new MUsuario(listener.getBaseActivity());
                SessionInfo sessionInfo = mUsuario.getSession(listener.getBaseActivity().getData(PreferenceServer.USURIO.Key, "MobileUser").toString(),
                        listener.getBaseActivity().getData(PreferenceServer.PASSWD.Key, "L0g1n@dm1n").toString(), imei);
                MPago mPago = new MPago(getBaseActivity());
                mPago.createConektaCard(sessionInfo.sessionID, idCliente, idUsuario, token, pago);
                subscriber.onNext(true);
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
                getBaseActivity().ocultarProgress();
            }

            @Override
            public void onError(Throwable e) {
                getBaseActivity().ocultarProgress();
                getBaseActivity().notificarUsuario("Error", e.getMessage());
            }

            @Override
            public void onNext(Boolean result) {
                listener.onTarjetaRegistrada();
            }
        });
    }


    @Override
    public void subscribe(BaseController.Listener listener) {
        if (listener instanceof ListenerPago)
            this.listener = (ListenerPago)listener;
    }

    public class ListenerPago extends Listener {

        public void cargarMetodosPago(List<Pago> pagos) {

        }
        public void onTarjetaRegistrada() {

        }
    }

}
