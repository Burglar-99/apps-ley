package mx.com.azteca.home.model;


import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mx.com.azteca.home.entity.generator.DaoObject;
import mx.com.azteca.home.model.ipati.dao.DAODocumentoAsignado;
import mx.com.azteca.home.model.ipati.dao.DAOUsuario;
import mx.com.azteca.home.model.ipati.model.MDocumento;
import mx.com.azteca.home.model.ipati.model.MField;
import mx.com.azteca.home.model.ipati.model.MFormulario;
import mx.com.azteca.home.model.ipati.model.MVersionInfo;
import mx.com.azteca.home.model.ipati.model.MWorkflow;
import mx.com.azteca.home.model.ipati.pojo.Documento;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.Usuario;
import mx.com.azteca.home.model.provider.IClientesAvaluos;
import mx.com.azteca.home.model.provider.IDocumentInstance;
import mx.com.azteca.home.model.provider.IReporteAvaluoExpress;
import mx.com.azteca.home.model.provider.pojo.ClienteAvaluoInfo;
import mx.com.azteca.home.model.provider.pojo.DocumentInstance;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import mx.com.azteca.home.model.provider.pojo.WorkflowStep;
import mx.com.azteca.home.util.BusinessException;
import mx.com.azteca.home.util.Functions;
import retrofit2.Call;
import retrofit2.Response;

public class MDocumentInstance extends BaseModel {

    public MDocumentInstance(Context context) {
        super(context);
    }

    public MDocumentInstance(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    /**
     * Crea una instancia del documento en el servidor.
     *
     * @param idDocumento
     * @param version
     *
     * @return DocumentInstance
     */
    public mx.com.azteca.home.model.provider.pojo.DocumentInstance create(int idDocumento, int version) throws Exception {
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", getSession().sessionID);
            IDocumentInstance apiDocumentInstance = super.getRetrofit().create(IDocumentInstance.class);
            Call<mx.com.azteca.home.model.provider.pojo.DocumentInstance> call = apiDocumentInstance.createInstance(mapHeader, idDocumento, version);
            Response<mx.com.azteca.home.model.provider.pojo.DocumentInstance> response = call.execute();
            if (response.isSuccessful()) {
                mx.com.azteca.home.model.provider.pojo.DocumentInstance documentInstance = response.body();
                documentInstance.idCurrentStep = documentInstance.currentStep.idWorkflowStep;
                DaoObject<DocumentInstance> daoObjectInsance = new DaoObject<>();
                daoObjectInsance.insert(documentInstance);
                DaoObject<WorkflowStep> daoWorkflowStep = new DaoObject<>();
                daoWorkflowStep.where("ID", documentInstance.currentStep.id);
                WorkflowStep info = daoWorkflowStep.single(WorkflowStep.class);

                if (info == null)
                    daoWorkflowStep.insert(documentInstance.currentStep);
                else
                    daoWorkflowStep.update(documentInstance.currentStep);

                return documentInstance;
            }
            else {
                throw new Exception(response.errorBody().string());
            }
        }
        catch (Exception ex) {
            throw handledError(ex.getMessage());
        }
    }

    public mx.com.azteca.home.model.provider.pojo.DocumentInstance nextStep(mx.com.azteca.home.model.provider.pojo.DocumentInstance documentInstance, int idNextStep) throws Exception {
        try {
            SessionInfo session = getSession();
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", session.sessionID);
            IDocumentInstance apiDocumentInstance = super.getRetrofit().create(IDocumentInstance.class);
            Call<mx.com.azteca.home.model.provider.pojo.DocumentInstance> call = apiDocumentInstance.nextStep(mapHeader, documentInstance, idNextStep);
            Response<mx.com.azteca.home.model.provider.pojo.DocumentInstance> response = call.execute();
            if (response.isSuccessful()) {
                documentInstance = response.body();
                DaoObject<DocumentInstance> daoObject = new DaoObject<>();
                daoObject.where("id", documentInstance.id);
                daoObject.update(documentInstance);

                DaoObject<WorkflowStep> daoWorkflowStep = new DaoObject<>();
                daoWorkflowStep.where("ID", documentInstance.currentStep.id);
                WorkflowStep info = daoWorkflowStep.single(WorkflowStep.class);

                if (info == null)
                    daoWorkflowStep.insert(documentInstance.currentStep);
                else
                    daoWorkflowStep.update(documentInstance.currentStep);

                MFormulario mFormulario = new MFormulario(getContext(), getUserName(), getPasswd(),getWorkStation());
                mFormulario.downloadFormulario(documentInstance.currentStep.idFormulario, true, session.user.identity);
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

    public mx.com.azteca.home.model.provider.pojo.DocumentInstance previousStep(String folio) throws Exception {
        try {
            SessionInfo session = getSession();
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", session.sessionID);
            IDocumentInstance apiDocumentInstance = super.getRetrofit().create(IDocumentInstance.class);
            Call<mx.com.azteca.home.model.provider.pojo.DocumentInstance> call = apiDocumentInstance.previousStep(mapHeader, folio);
            Response<mx.com.azteca.home.model.provider.pojo.DocumentInstance> response = call.execute();
            if (response.isSuccessful()) {
                DocumentInstance documentInstance = response.body();
                DaoObject<DocumentInstance> daoObject = new DaoObject<>();
                daoObject.where("id", documentInstance.id);
                daoObject.update(documentInstance);

                DaoObject<WorkflowStep> daoWorkflowStep = new DaoObject<>();
                daoWorkflowStep.where("ID", documentInstance.currentStep.id);
                WorkflowStep info = daoWorkflowStep.single(WorkflowStep.class);
                if (info == null) {
                    daoWorkflowStep.insert(documentInstance.currentStep);
                }
                else {
                    daoWorkflowStep.update(documentInstance.currentStep);
                }
                // Al avanzar se consulta los datos del formulario
                MFormulario mFormulario = new MFormulario(getContext(), getUserName(), getPasswd(), getWorkStation());
                mFormulario.downloadFormulario(documentInstance.currentStep.idFormulario, true, session.user.identity);
                // El documento asignado es modificado el step
                DAODocumentoAsignado daoDocAsignado = new DAODocumentoAsignado(getContext());
                DocumentoAsignado docAsignado = daoDocAsignado.getDetail(documentInstance.idDocumento, documentInstance.fecha.getTime(), documentInstance.folio);
                docAsignado.IdWorkflowStep = documentInstance.idWorkflowStep;
                daoDocAsignado.update(docAsignado);

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


    public mx.com.azteca.home.model.provider.pojo.DocumentInstance saveInstanceJson(mx.com.azteca.home.model.provider.pojo.DocumentInstance documentInstance) throws Exception {
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", getSession().sessionID);
            IDocumentInstance apiDocumentInstance = super.getRetrofit().create(IDocumentInstance.class);
            Call<mx.com.azteca.home.model.provider.pojo.DocumentInstance> call = apiDocumentInstance.saveInstanceJson(mapHeader, documentInstance);
            Response<mx.com.azteca.home.model.provider.pojo.DocumentInstance> response = call.execute();
            if (response.isSuccessful()) {
                documentInstance = response.body();
                DaoObject<DocumentInstance> daoObject = new DaoObject<>();
                daoObject.where("id", documentInstance.id);
                daoObject.update(documentInstance);
                return documentInstance;
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

    public void loadDocumentServer(boolean downloadFormulario) throws Exception {
        try {
            MDocumento mDocumento = new MDocumento(getContext(), getUserName(), getPasswd(), getWorkStation());
            MVersionInfo mVersionInfo = new MVersionInfo(getContext(), getUserName(), getPasswd(), getWorkStation());
            MWorkflow mWorkflow = new MWorkflow(getContext(), getUserName(), getPasswd(), getWorkStation());
            MField mField = new MField(getContext(), getUserName(), getPasswd(), getWorkStation());
            MFormulario mFormulario = new MFormulario(getContext(), getUserName(), getPasswd(), getWorkStation());
            DaoObject<UsuarioInfo> daoUsuario = new DaoObject<>();
            UsuarioInfo usuario = daoUsuario.single(UsuarioInfo.class);
            List<Integer> listFormulario = new ArrayList<>();
            Documento documento = mDocumento.getServerDetail(9);
            mVersionInfo.downloadVersion(documento.Identity);
            mWorkflow.downloadWorkflow(usuario.idBranch, documento.Identity, documento.Version, listFormulario, true, true);
            Set<String> setComplextType = new HashSet<>();
            String key = documento.Identity + "-" + documento.Version;
            if (!setComplextType.contains(key)) {
                setComplextType.add(key);
                mField.downloadComplexType(documento.Identity, documento.Version);
            }

            if (downloadFormulario) {
                for (int idForma : listFormulario) {
                    mFormulario.downloadFormulario(idForma, usuario.identity);
                }
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError(ex.getMessage());
        }
    }

    public String loadReporte(int idDocumento, String fecha, String folio) throws Exception {
        try {
            Date fechaDoc = new Date(Functions.getInstance(Functions.Format.DATE).getTime(fecha));
            DaoObject<DocumentInstance> daoObject = new DaoObject<>();
            daoObject.where("IdDocumento", idDocumento);
            daoObject.where("Fecha", fechaDoc);
            daoObject.where("Folio", folio);
            DocumentInstance documentInstance = daoObject.single(DocumentInstance.class);
            if (documentInstance != null ) {
                String fileName = Environment.getExternalStorageDirectory().toString().concat("/valuapps/").concat(documentInstance.id).concat(".PDF");
                File localFile = new File(fileName);
                if (localFile.exists()) {
                    return fileName;
                }
                else {
                    Map<String, String> mapHeader = new HashMap<>();
                    mapHeader.put("Azteca-sessionID", getSession().sessionID);
                    IReporteAvaluoExpress apiReporte = super.getRetrofit().create(IReporteAvaluoExpress.class);
                    Call<String> call = apiReporte.loadAvaluoExpressReporte(mapHeader, idDocumento, fecha, folio);
                    Response<String> response = call.execute();
                    if (response.isSuccessful()) {
                        String pdf = response.body();
                        try
                        {
                            File file = new File(Environment.getExternalStorageDirectory().toString().concat("/valuapps/"));
                            if (!file.exists()) {
                                if (!file.mkdir())
                                    throw new Exception("Directory not exists");
                            }
                        }
                        catch (Exception ex) {
                            throw new Exception("No fue posible crear el directorio en el dispositivo, verifique el espeacio disponible.");
                        }
                        try {
                            final File dwldsPath = new File(Environment.getExternalStorageDirectory().toString().concat("/valuapps/").concat(documentInstance.id).concat(".PDF"));
                            byte[] pdfAsBytes = Base64.decode(pdf, 0);
                            FileOutputStream os = new FileOutputStream(dwldsPath, false);
                            os.write(pdfAsBytes);
                            os.flush();
                            os.close();
                        }
                        catch (Exception ex) {
                            throw new BusinessException("No fue posible crear el documento en el dispositivo, verifique el espeacio disponible.");
                        }

                        return Environment.getExternalStorageDirectory().toString().concat("/valuapps/").concat(documentInstance.id).concat(".PDF");
                    }
                    else {
                        throw new Exception(response.errorBody().string());
                    }
                }
            }
            else {
                throw new BusinessException("Documento incorrecto.");
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError(ex.getMessage());
        }
    }

    public ClienteAvaluoInfo getClienteAvaluo(String folio) throws Exception {
        try {
            DaoObject<ClienteAvaluoInfo> daoObject = new DaoObject<>();
            daoObject.where("Folio", folio);
            return daoObject.single(ClienteAvaluoInfo.class);
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError("Ocurrió un problema al generar el aval\u00fao", ex);
        }
    }

    public ClienteAvaluoInfo createClienteAvaluo(String folio) throws Exception {
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", getSession().sessionID);
            IClientesAvaluos apiCliente = super.getRetrofit().create(IClientesAvaluos.class);
            Call<ClienteAvaluoInfo> callClienteAvaluo = apiCliente.createAvaluoCliente(mapHeader, folio);
            Response<ClienteAvaluoInfo> response = callClienteAvaluo.execute();
            if (response.isSuccessful()) {
                ClienteAvaluoInfo info = response.body();
                DaoObject<ClienteAvaluoInfo> daoObject = new DaoObject<>();
                daoObject.insert(info);
                return  info;
            }
            else {
                throw new Exception(response.errorBody().toString());
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError("Ocurrió un problema al cargar la informaci\u00f3n", ex);
        }
    }

    public ClienteAvaluoInfo getClienteAvaluoDetail(int idClienteAvaluo) throws Exception {
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", getSession().sessionID);
            IClientesAvaluos apiCliente = super.getRetrofit().create(IClientesAvaluos.class);
            Call<ClienteAvaluoInfo> callClienteAvaluo = apiCliente.getDetail(mapHeader, idClienteAvaluo);
            Response<ClienteAvaluoInfo> response = callClienteAvaluo.execute();
            if (response.isSuccessful()) {
                ClienteAvaluoInfo info = response.body();
                DaoObject<ClienteAvaluoInfo> daoObject = new DaoObject<>();
                daoObject.where("Id", idClienteAvaluo);
                daoObject.update(info);
                return info;
            }
            else {
                throw new Exception(response.errorBody().toString());
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError("Ocurrió un problema al cargar la informaci\u00f3n", ex);
        }
    }

    public boolean generarCargoTarjeta( String folio, String numeroTarjeta) throws Exception {
        boolean cargoGenerado = false;
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", getSession().sessionID);
            IClientesAvaluos apiCliente = super.getRetrofit().create(IClientesAvaluos.class);
            Call<ClienteAvaluoInfo> callClienteAvaluo =  apiCliente.generarCargo(mapHeader, folio, numeroTarjeta);
            Response<ClienteAvaluoInfo> response = callClienteAvaluo.execute();
            if (response.isSuccessful()) {
                cargoGenerado = true;
                ClienteAvaluoInfo info = response.body();
                if (info != null) {
                    DaoObject<ClienteAvaluoInfo> daoObject = new DaoObject<>();
                    daoObject.where("Folio", folio);
                    daoObject.update(info);
                    cargoGenerado = info.idStatus == 3;
                }
            }
            else {
                throw new Exception(response.errorBody().toString());
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            if (!cargoGenerado)
                throw handledError("No fue posible generar el cargo a la tarjeta", ex);
        }
        return cargoGenerado;
    }

    public boolean generarCargoOxxo(String folio) throws Exception {
        boolean cargoGenerado = false;
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", getSession().sessionID);
            IClientesAvaluos apiCliente = super.getRetrofit().create(IClientesAvaluos.class);
            Call<ClienteAvaluoInfo> callClienteAvaluo =  apiCliente.generarCargoOxxo(mapHeader, folio);
            Response<ClienteAvaluoInfo> response = callClienteAvaluo.execute();
            if (response.isSuccessful()) {
                cargoGenerado = true;
                ClienteAvaluoInfo info = response.body();
                if (info != null) {
                    DaoObject<ClienteAvaluoInfo> daoObject = new DaoObject<>();
                    daoObject.where("Folio", folio);
                    daoObject.update(info);
                    cargoGenerado = info.idStatus == 2;
                }
            }
            else {
                throw new Exception(response.errorBody().string());
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            if (!cargoGenerado)
                throw handledError("No fue posible generar la refenrecia de pago", ex);
        }
        return cargoGenerado;
    }

}
