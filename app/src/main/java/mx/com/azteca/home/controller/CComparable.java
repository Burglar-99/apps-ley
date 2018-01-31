package mx.com.azteca.home.controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import mx.com.azteca.home.model.ipati.model.MComparable;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CComparable extends BaseController {

    public void loadComparable() {
        try {
            String id = getBaseActivity().getData("id").toString();
            Observable.create((Observable.OnSubscribe<ComparableInfo>) subscriber -> {
                try {
                    MComparable mComparable = new MComparable(CComparable.this.getBaseActivity());
                    SessionInfo sessionInfo =  mComparable.getSession("system", "a987654321/*", "", true);
                    String info = mComparable.loadComparablesImagenesSeleccion(sessionInfo.sessionID, id);
                    if (info != null) {
                        JsonParser parser = new JsonParser();
                        JsonArray jComparables = parser.parse(info).getAsJsonArray();
                        JsonObject jComparable = jComparables.get(0).getAsJsonObject();
                        ComparableInfo comparableInfo = new ComparableInfo();
                        comparableInfo.descripcion  = jComparable.get("descripcion").getAsString();
                        comparableInfo.calle        = jComparable.get("calle").getAsString();
                        comparableInfo.numero       = jComparable.get("numero").getAsString();
                        comparableInfo.entreCalle   = jComparable.get("entreCalles").getAsString();
                        comparableInfo.colonia      = jComparable.get("colonia").getAsString();
                        comparableInfo.municipio    = jComparable.get("municipio").getAsString();
                        comparableInfo.inmueble     = jComparable.get("inmueble").getAsString();
                        comparableInfo.tipo         = jComparable.get("tipo").getAsString();
                        comparableInfo.edad         = jComparable.get("edad").getAsString();
                        comparableInfo.precio       = jComparable.get("precio").getAsString();
                        comparableInfo.recamaras    = jComparable.get("recamaras").getAsString();
                        comparableInfo.patio        = jComparable.get("patio").getAsString();
                        comparableInfo.banos        = jComparable.get("banos").getAsString();
                        comparableInfo.cochera      = jComparable.get("cochera").getAsString();
                        for (JsonElement jsonElement : jComparables) {
                            if (jsonElement.getAsJsonObject().get("idregistro").getAsString().equals(id)) {
                                comparableInfo.listImagen.add(jsonElement.getAsJsonObject().get("urlImagen").getAsString());
                            }
                        }
                        subscriber.onNext(comparableInfo);
                    }
                    subscriber.onCompleted();
                }
                catch (Exception e) {
                    subscriber.onError(e);
                }
            })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<ComparableInfo>() {
                @Override
                public void onCompleted() {
                }
                @Override
                public void onError(Throwable e) {
                }
                @Override
                public void onNext(ComparableInfo info) {
                    getBaseActivity().loadComparableInfo(info);
                }
            });
        }
        catch (Exception e) {
            this.getBaseActivity().notificarUsuario("Error", "Ocurri\u00f3 un problema al iniciar sesión.\n" + e.getMessage());
        }
    }

    @Override
    public void subscribe(Listener listener) {
    }

    public class ComparableInfo {

        public String distancia;
        public String descripcion;
        public String calle;
        public String numero;
        public String entreCalle;
        public String colonia;
        public String municipio;
        public String inmueble;
        public String tipo;
        public String edad;
        public String precio;
        public String recamaras;
        public String patio;
        public String banos;
        public String cochera;
        public String urlImagen;

        public List<String> listImagen;

        public ComparableInfo() {
            this.listImagen = new ArrayList<>();
        }
    }

}
