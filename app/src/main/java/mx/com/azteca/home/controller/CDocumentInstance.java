package mx.com.azteca.home.controller;


import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Date;

import mx.com.azteca.home.R;
import mx.com.azteca.home.model.MDocumentInstance;
import mx.com.azteca.home.model.MUsuario;
import mx.com.azteca.home.model.ipati.dao.DAODocumentInstance;
import mx.com.azteca.home.model.ipati.dao.DAODocumentoAsignado;
import mx.com.azteca.home.model.ipati.model.MComparable;
import mx.com.azteca.home.model.ipati.model.MDocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.DocumentInstance;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.WorkFlowStep;
import mx.com.azteca.home.model.provider.pojo.ClienteAvaluoInfo;
import mx.com.azteca.home.model.provider.pojo.Pago;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import mx.com.azteca.home.util.Functions;
import mx.com.azteca.home.util.PreferenceDevice;
import mx.com.azteca.home.view.LoginActivity;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CDocumentInstance extends BaseController {

    private ListenerDocumentInstance listener;

    public void loadUsuarioInfo() {
        listener.mostrarProgress("Cargando informaci\u00f3n");
        Observable.create((Observable.OnSubscribe<UsuarioInfo>) subscriber -> {
            try {
                int idUsuario = (int)listener.getBaseActivity().getData(PreferenceDevice.LOGGED_USUER.Key);
                MUsuario mUsuario = new MUsuario(getBaseActivity());
                UsuarioInfo info = mUsuario.getUsuarioInfo(idUsuario);
                subscriber.onNext(info);
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
                        getBaseActivity().ocultarProgress();
                    }
                    @Override
                    public void onError(Throwable e) {
                        listener.notificarUsuario("Error", e.getMessage());
                        listener.ocultarProgress();
                    }
                    @Override
                    public void onNext(UsuarioInfo info) {
                        listener.onLoadUsuario(info);
                    }
                });
    }

    public void createInstance() {
        listener.mostrarProgress("Creando aval\u00fao");
        Observable.create((Observable.OnSubscribe<DocumentInstance>) subscriber -> {
            try {
                MDocumentInstance mDocumentInstance = newInstanceModel(MDocumentInstance.class);
                mDocumentInstance.loadDocumentServer(false);
                mx.com.azteca.home.model.provider.pojo.DocumentInstance documentInstance = mDocumentInstance.create(9,1);
                documentInstance = mDocumentInstance.nextStep(documentInstance, documentInstance.currentStep.id);
                //
                DocumentInstance docInstance = new DocumentInstance();
                docInstance.ID                  = documentInstance.id;
                docInstance.IdDocumento         = documentInstance.idDocumento;
                docInstance.Version             = documentInstance.version;
                docInstance.Folio               = documentInstance.folio;
                docInstance.TipoDocumento       = documentInstance.tipoDocumento;
                docInstance.Fecha               = documentInstance.fecha.getTime();
                docInstance.FechaStr            = documentInstance.fechaStr;
                docInstance.Contenido           = documentInstance.contenido;
                docInstance.Referencia          = documentInstance.referencia;
                docInstance.Latitud             = documentInstance.latitud;
                docInstance.Longitud            = documentInstance.longitud;
                docInstance.Altitud             = documentInstance.altitud;
                docInstance.Posicion            = documentInstance.posicion;
                docInstance.IdUsuarioAsignado   = documentInstance.idUsuarioAsignado;
                docInstance.IdWorkflowStep      = documentInstance.idWorkflowStep;
                docInstance.IdStatus            = documentInstance.idStatus;
                docInstance.Status              = documentInstance.status;
                docInstance.StartStep           = documentInstance.startStep.getTime();
                docInstance.EndStep             = documentInstance.endStep.getTime();
                docInstance.HasStartStep        = documentInstance.hasStartStep;
                docInstance.HasEndStep          = documentInstance.hasEndStep;
                docInstance.IdSucursal          = documentInstance.idSucursal;
                docInstance.IdEmpresa           = documentInstance.idEmpresa;
                docInstance.LastUpdate          = documentInstance.lastUpdate.getTime();
                docInstance.UserUpdate          = documentInstance.userUpdate;
                docInstance.PCUpdate            = documentInstance.pcUpdate;
                docInstance.Nodes = "";
                //
                DocumentoAsignado documentoAsignado = new DocumentoAsignado();
                documentoAsignado.IdAlmacen         = documentInstance.id;
                documentoAsignado.IdDocumento       = documentInstance.idDocumento;
                documentoAsignado.Version           = documentInstance.version;
                documentoAsignado.Fecha             = documentInstance.fecha.getTime();
                documentoAsignado.Folio             = documentInstance.folio;
                documentoAsignado.Referencia        = documentInstance.referencia;
                documentoAsignado.Latitud           = documentInstance.latitud;
                documentoAsignado.Longitud          = documentInstance.longitud;
                documentoAsignado.Altitud           = documentInstance.altitud;
                documentoAsignado.Posicion          = documentInstance.posicion;
                documentoAsignado.IdWorkflowStep    = documentInstance.idWorkflowStep;
                documentoAsignado.IdStatus          = documentInstance.idStatus;
                documentoAsignado.IdEmpresa         = documentInstance.idEmpresa;
                documentoAsignado.IdSucursal        = documentInstance.idSucursal;
                documentoAsignado.LastUpdate        = documentInstance.lastUpdate.getTime();
                documentoAsignado.UserUpdate        = documentInstance.userUpdate;
                documentoAsignado.PCUpdate          = documentInstance.pcUpdate;
                documentoAsignado.Hora              = documentInstance.fecha.getTime();
                documentoAsignado.TipoDocumento     = documentInstance.tipoDocumento;
                documentoAsignado.Contenido         = documentInstance.contenido;
                documentoAsignado.WorkflowStep      = documentInstance.currentStep.state;
                documentoAsignado.Tag               = documentInstance.currentStep.tag;
                documentoAsignado.Estatus           = documentInstance.currentStep.state;
                //
                docInstance.ContenidoJson = documentInstance.contenidoJson.toString();
                DAODocumentInstance daoDocumentInstance = new DAODocumentInstance(getContext());
                daoDocumentInstance.guardar(docInstance);
                //
                DAODocumentoAsignado daoDocumentoAsignado = new DAODocumentoAsignado(getContext());
                daoDocumentoAsignado.guardar(documentoAsignado);
                //
                subscriber.onNext(docInstance);
                subscriber.onCompleted();
            }
            catch (Exception e) {
                subscriber.onError(e);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DocumentInstance>() {
                    @Override
                    public void onCompleted() {
                        listener.ocultarProgress();
                    }
                    @Override
                    public void onError(Throwable e) {
                        listener.notificarUsuario("Error", e.getMessage());
                        listener.ocultarProgress();
                    }
                    @Override
                    public void onNext(DocumentInstance documentInstance) {
                        listener.iniciarEdicion(documentInstance);
                    }
                });
    }

//mantener los comparables dl mapa principal

    private Location lastLocation;
    public void loadComparables(double lat, double lng) {
        try {
            Location location = new Location("DEVICE");
            location.setLatitude(lat);
            location.setLongitude(lng);
            if (lastLocation == null ||  lastLocation.distanceTo(location) > 5) {
                lastLocation = location;
                Observable.create((Observable.OnSubscribe<MarkerInfo>) subscriber -> {
                    try {
                        MComparable mComparable = new MComparable(CDocumentInstance.this.getBaseActivity());
                        SessionInfo sessionInfo =  mComparable.getSession("system", "a987654321/*", "", true);
                        String info = mComparable.loadComparablesExpress(sessionInfo.sessionID, "CASA", 1500, "VENTA", String.valueOf(lastLocation.getLatitude()), String.valueOf(lastLocation.getLongitude()));
                        JsonParser parser = new JsonParser();
                        JsonArray jComparables = parser.parse(info).getAsJsonArray();
                        for (JsonElement jElement : jComparables) {
                            JsonObject jInfo = jElement.getAsJsonObject();
                            MarkerInfo markerInfo = new MarkerInfo();
                            markerInfo.idregistro   = jInfo.get("idregistro").getAsInt();
                            markerInfo.lat          = jInfo.get("lat").getAsDouble();
                            markerInfo.lng          = jInfo.get("lng").getAsDouble();
                            markerInfo.precio       = jInfo.get("precio").getAsInt();
                            markerInfo.descripcion  = jInfo.get("descripcion").getAsString();
                            markerInfo.imagen       = jInfo.get("imagen").getAsString();
                            markerInfo.markerOptions = new MarkerOptions().position(new LatLng(markerInfo.lat, markerInfo.lng)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin)).title(markerInfo.descripcion);
                            subscriber.onNext(markerInfo);
                        }
                        subscriber.onCompleted();
                    }
                    catch (Exception e) {
                        subscriber.onError(e);
                    }
                })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MarkerInfo>() {
                            @Override
                            public void onCompleted() {
                            }
                            @Override
                            public void onError(Throwable e) {
                            }
                            @Override
                            public void onNext(MarkerInfo info) {
                                getBaseActivity().loadComparables(info);
                            }
                        });
            }
        }
        catch (Exception e) {
            this.getBaseActivity().notificarUsuario("Error", "Ocurri\u00f3 un problema al iniciar sesión.\n" + e.getMessage());
        }
    }

    public void loadReporte(int idDocumento, Date fecha, String folio, String user, String passwd, String imei) {
        Observable.create((Observable.OnSubscribe<String>) subscriber -> {
            try {
                MDocumentInstance mDocumentInstance = newInstanceModel(MDocumentInstance.class);
                String reporte = mDocumentInstance.loadReporte( idDocumento, Functions.getInstance(Functions.Format.DATE).applyTime(fecha.getTime()), folio);
                subscriber.onNext(reporte);
                subscriber.onCompleted();
            }
            catch (Exception ex) {
                subscriber.onError(ex);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        listener.ocultarProgress();
                        listener.onError(e.getMessage());
                    }
                    @Override
                    public void onNext(String result) {
                        if (listener != null)
                            listener.onLoadReporte(result);
                    }
                });
    }

    public void previosStep(String folio) {
        Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                MDocumentInstance mDocumentInstance = newInstanceModel(MDocumentInstance.class);
                mDocumentInstance.previousStep(folio);
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
            catch (Exception ex) {
                subscriber.onError(ex);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        listener.ocultarProgress();
                        listener.onError(e.getMessage());
                    }
                    @Override
                    public void onNext(Boolean result) {
                        listener.onPreviosStep();
                    }
                });
    }

    public void loadClienteAvaluo() {
        loadClienteAvaluo(true);
    }

    public void loadClienteAvaluo(boolean generar) {
        listener.mostrarProgress("Cargando informaci\u00f3n");
        Observable.create((Observable.OnSubscribe<ClienteAvaluoInfo>) subscriber -> {
            try {
                String folio        = getBaseActivity().getData("DOCUMENT_FOLIO", "").toString();
                MDocumentInstance mDocumentInstance = newInstanceModel(MDocumentInstance.class);
                ClienteAvaluoInfo avaluo =  mDocumentInstance.getClienteAvaluo(folio);
                if (avaluo == null && generar) {
                    avaluo = mDocumentInstance.createClienteAvaluo(folio);
                }
                else if (avaluo != null && avaluo.idStatus == 2) {
                    avaluo = mDocumentInstance.getClienteAvaluoDetail(avaluo.identity);
                }
                if (avaluo == null)
                    throw new Exception("Ocurrió un problema al cargar la informaci\u00f3n");
                subscriber.onNext(avaluo);
                subscriber.onCompleted();
            }
            catch (Exception ex) {
                subscriber.onError(ex);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ClienteAvaluoInfo>() {
                    @Override
                    public void onCompleted() {
                        listener.ocultarProgress();
                    }
                    @Override
                    public void onError(Throwable e) {
                        listener.ocultarProgress();
                        if (listener != null)
                            listener.onError(e.getMessage());
                        else
                            listener.notificarUsuario("Error", e.getMessage());
                    }
                    @Override
                    public void onNext(ClienteAvaluoInfo result) {
                        listener.onLoadClienteAvaluo(result);
                    }
                });
    }

    public void generarCargoTarjeta(String numeroTarjeta) {
        getBaseActivity().mostrarProgress("Generando cargo");
        Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                String folio = getBaseActivity().getData("DOCUMENT_FOLIO", "").toString();
                MDocumentInstance mDocumentInstance = newInstanceModel(MDocumentInstance.class);
                boolean result = mDocumentInstance.generarCargoTarjeta(folio, numeroTarjeta);
                if (!result) {
                    throw new Exception("El cargo no fue aceptado, favor de intentar de nuevo o elegir otro m\u00e9todo de pago");
                }
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
            catch (Exception ex) {
                subscriber.onError(ex);
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
                        getBaseActivity().notificarUsuario("Error", e.getMessage());
                        getBaseActivity().ocultarProgress();
                    }

                    @Override
                    public void onNext(Boolean result) {
                        listener.onCargoGenerado();
                    }
                });
    }

    public void generarCargoOxxo() {
        getBaseActivity().mostrarProgress("Generando cargo");
        Observable.create((Observable.OnSubscribe<Boolean>) subscriber -> {
            try {
                String folio = getBaseActivity().getData("DOCUMENT_FOLIO", "").toString();
                MDocumentInstance mDocumentInstance = newInstanceModel(MDocumentInstance.class);
                boolean result = mDocumentInstance.generarCargoOxxo(folio);
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
            catch (Exception ex) {
                subscriber.onError(ex);
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
                        getBaseActivity().notificarUsuario("Error", e.getMessage());
                        getBaseActivity().ocultarProgress();
                    }
                    @Override
                    public void onNext(Boolean result) {
                        listener.onReferenciaGenerada();
                    }
                });
    }

    public void verificarDocumento(int idDocumento, long fecha, String folio) {
        listener.mostrarProgress("Cargando informaci\u00f3nn");
        Observable.create((Observable.OnSubscribe<DocumentoAsignado>) subscriber -> {
            try {
                MDocumentoAsignado mDocAsignado = new MDocumentoAsignado(getContext());
                DocumentoAsignado docAsignado = mDocAsignado.getDetail(idDocumento, fecha, folio);
                MDocumentInstance mDocumentInstance = new MDocumentInstance(getContext());
                mx.com.azteca.home.model.ipati.model.MDocumentInstance mInstance = new mx.com.azteca.home.model.ipati.model.MDocumentInstance(getContext());
                docAsignado.setDocumentInstance(mInstance.getDocumentInstance(docAsignado.IdDocumento, docAsignado.Version, docAsignado.Folio) );
                WorkFlowStep step = mDocAsignado.loadWorkFlowOptions(docAsignado.IdWorkflowStep, docAsignado.IdDocumento, docAsignado.Version);
                docAsignado.setWorkflow(step);
                ClienteAvaluoInfo clienteAvaluo =  mDocumentInstance.getClienteAvaluo(folio);
                docAsignado.setClienteAvaluo(clienteAvaluo);
                subscriber.onNext(docAsignado);
                subscriber.onCompleted();
            }
            catch (Exception ex) {
                subscriber.onError(ex);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DocumentoAsignado>() {
                    @Override
                    public void onCompleted() {
                        listener.ocultarProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.notificarUsuario("Error", e.getMessage());
                        listener.ocultarProgress();
                    }

                    @Override
                    public void onNext(DocumentoAsignado result) {
                        listener.iniciarEdicion(result);
                    }
                });
    }

    public void logout() {
        try {
            getBaseActivity().setData(PreferenceDevice.LOGGED_USUER.Key, 0);
            listener.iniciarNewThread(LoginActivity.class);
        }
        catch (Exception ex) {
            listener.notificarUsuario("Error", "Ocurrió un problema al cerar la sesi\u00f3n");
        }
    }



    @Override
    public void subscribe(Listener listener) {
        if (listener instanceof  ListenerDocumentInstance) {
            this.listener = (ListenerDocumentInstance) listener;
        }
    }

    public class ListenerDocumentInstance extends Listener {
        public void onLoadUsuario(UsuarioInfo usuarioInfo) {
        }
        public void onLoadReporte(String file) {
        }
        public void onLoadClienteAvaluo(ClienteAvaluoInfo avaluo) {
        }
        public void onCargoGenerado() {
        }
        public void onReferenciaGenerada() {
        }
        public void onPreviosStep() {
        }
        public void solcitarPago(DocumentoAsignado docAsignado) {
        }
        public void iniciarEdicion(DocumentoAsignado docAsignado) {
        }
        public void mostrarReferencia(DocumentoAsignado docAsignado) {
        }
        public void onError(String error) {
        }

        public void iniciarEdicion(DocumentInstance docAsignado) {
        }
    }


    public class MarkerInfo {

        public int idregistro;
        public double lat;
        public double lng;
        public double precio;
        public String descripcion;
        public String imagen;

        public MarkerOptions markerOptions;
        public Marker marker;
    }
}
