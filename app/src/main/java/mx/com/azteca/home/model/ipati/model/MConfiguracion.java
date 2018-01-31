package mx.com.azteca.home.model.ipati.model;


import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOConfiguracion;
import mx.com.azteca.home.model.ipati.dao.DAODataset;
import mx.com.azteca.home.model.ipati.dao.DAODocumentInstance;
import mx.com.azteca.home.model.ipati.dao.DAODocumento;
import mx.com.azteca.home.model.ipati.dao.DAODocumentoAsignado;
import mx.com.azteca.home.model.ipati.dao.DAOEventLog;
import mx.com.azteca.home.model.ipati.dao.DAOField;
import mx.com.azteca.home.model.ipati.dao.DAOFormulario;
import mx.com.azteca.home.model.ipati.dao.DAOIpati;
import mx.com.azteca.home.model.ipati.dao.DAOPersona;
import mx.com.azteca.home.model.ipati.dao.DAOTipoDato;
import mx.com.azteca.home.model.ipati.dao.DAOUsuario;
import mx.com.azteca.home.model.ipati.dao.DAOVersionInfo;
import mx.com.azteca.home.model.ipati.dao.DAOWorkFlowOption;
import mx.com.azteca.home.model.ipati.dao.DAOWorkFlowStep;
import mx.com.azteca.home.model.ipati.pojo.BaseInfo;
import mx.com.azteca.home.model.ipati.pojo.Configuracion;
import mx.com.azteca.home.model.ipati.pojo.Documento;
import mx.com.azteca.home.model.ipati.pojo.DocumentoAsignado;
import mx.com.azteca.home.model.ipati.pojo.Usuario;
import mx.com.azteca.home.util.Functions;

public class MConfiguracion extends BaseModel {

    private DAOConfiguracion daoConfiguracion;
    private DAOUsuario daoUsuario;
    private DAOPersona daoPersona;
    private DAODocumento daoDocumento;
    private DAOFormulario daoFormulario;
    private DAODataset daoDataset;
    private DAODocumentoAsignado daoDocumentoAsignado;
    private DAOIpati daoIpati;
    private DAOField daoField;
    private DAODocumentInstance daoDocInstance;
    private DAOWorkFlowStep daoWorkFlowStep;
    private DAOWorkFlowOption daoWorkFlowOption;
    private DAOTipoDato daoTipoDato;
    private DAOVersionInfo daoVersionInfo;

    public MConfiguracion(Context context) {
        super(context);
    }

    public MConfiguracion(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        super.initialize();
        daoConfiguracion        = new DAOConfiguracion(getContext());
        daoPersona              = new DAOPersona(getContext());
        daoUsuario              = new DAOUsuario(getContext());
        daoDocumento            = new DAODocumento(getContext());
        daoFormulario           = new DAOFormulario(getContext());
        daoDataset              = new DAODataset(getContext());
        daoDocumentoAsignado    = new DAODocumentoAsignado(getContext());
        daoField                = new DAOField(getContext());
        daoDocInstance          = new DAODocumentInstance(getContext());
        daoWorkFlowStep         = new DAOWorkFlowStep(getContext());
        daoWorkFlowOption       = new DAOWorkFlowOption(getContext());
        daoTipoDato             = new DAOTipoDato(getContext());
        daoVersionInfo          = new DAOVersionInfo(getContext());
        daoIpati                = new DAOIpati();
    }

    public boolean isDispositivoBloqueado() {
        return daoConfiguracion.isDispositivoBloqueado();
    }

    public void eliminarInformacion() {
        daoPersona.truncate();
        daoUsuario.truncate();
        daoDocumento.truncate();
        daoFormulario.truncate();
        daoDataset.truncate();
        daoDocumentoAsignado.truncate();
        daoField.truncate();
        daoDocInstance.truncate();
        daoWorkFlowStep.truncate();
        daoWorkFlowOption.truncate();
        daoTipoDato.truncate();
        daoVersionInfo.truncate();
    }

    public void saveUsuarioLogin(int idUsuario) {
        Configuracion confUsuario = new Configuracion("Device", "Usuario", String.valueOf(idUsuario));
        daoConfiguracion.updateConfiguracion(confUsuario);
    }

    public void iniciarSesion(Usuario infoUsuario) throws Exception {
        try {
            MDocumento mDocumento            = new MDocumento(getContext(), getUserName(), getPasswd(), getWorkStation());
            MVersionInfo mVersionInfo        = new MVersionInfo(getContext(), getUserName(), getPasswd(), getWorkStation());
            MWorkflow mWorkflow              = new MWorkflow(getContext(), getUserName(), getPasswd(), getWorkStation());
            MDocumentoAsignado mDocAsignado  = new MDocumentoAsignado(getContext(), getUserName(), getPasswd(), getWorkStation());
            MField mField                    = new MField(getContext(), getUserName(), getPasswd(), getWorkStation());
            MDocumentInstance mDocInstance   = new MDocumentInstance(getContext(), getUserName(), getPasswd(), getWorkStation());
            MFormulario mFormulario          = new MFormulario(getContext(), getUserName(), getPasswd(), getWorkStation());

            List<Integer> listFormulario;
            List<Documento> listDocumentos;
            List<DocumentoAsignado> listDocAsignado;

            listFormulario  = new ArrayList<>();

            Log.e(getClass().getName(), "Inicio downloadDocumentos".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));
            listDocumentos  = mDocumento.downloadDocumentos(infoUsuario.IdBranch, infoUsuario.Identity);
            Log.e(getClass().getName(), "Fin downloadDocumentos".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));

            Log.e(getClass().getName(), "Inicio downloadDocumentoAsignado".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));
            listDocAsignado = mDocAsignado.downloadDocumentoAsignado(0, infoUsuario.Identity);
            Log.e(getClass().getName(), "Fin downloadDocumentoAsignado".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));

            Log.e(getClass().getName(), "Inicio downloadVersion, downloadWorkflow".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));
            Set<Integer> setVersion = new HashSet<>();
            Set<String> setWorkflow = new HashSet<>();
            for (Documento documento : listDocumentos) {

                if (!setVersion.contains(documento.Identity)) {
                    setVersion.add(documento.Identity);
                    mVersionInfo.downloadVersion(documento.Identity);
                }

                String key = infoUsuario.IdBranch + "-" + documento.Identity + "-" + documento.Version;
                if (!setWorkflow.contains(key)) {
                    setWorkflow.add(key);
                    mWorkflow.downloadWorkflow(infoUsuario.IdBranch, documento.Identity, documento.Version, listFormulario);
                }
            }
            Log.e(getClass().getName(), "Fin downloadVersion, downloadWorkflow".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));

            Log.e(getClass().getName(), "Inicio downloadComplexType".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));
            Set<String> setComplextType = new HashSet<>();
            for (DocumentoAsignado docAsignado : listDocAsignado) {
                String key = docAsignado.IdDocumento + "-" + docAsignado.Version;
                if (!setComplextType.contains(key)) {
                    setComplextType.add(key);
                    mField.downloadComplexType(docAsignado.IdDocumento, docAsignado.Version);
                }
            }
            Log.e(getClass().getName(), "Fin downloadComplexType".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));


            Log.e(getClass().getName(), "Inicio downloadInstance".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));
            for (DocumentoAsignado docAsignado : listDocAsignado) {
                mDocInstance.downloadInstance(docAsignado.Folio);
            }
            Log.e(getClass().getName(), "Fin downloadInstance".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));


            Log.e(getClass().getName(), "Inicio getFormulario".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));
            for (int idForma : listFormulario) {
                mFormulario.downloadFormulario(idForma, infoUsuario.IdUsuario);
            }
            Log.e(getClass().getName(), "Fin getFormulario".concat(Functions.getInstance(Functions.Format.WINDOWS_DATE_TIME).applyTime(new java.util.Date().getTime())));

        }
        catch (Exception ex) {
            DAOEventLog.HandledException(getContext(), ex, "Error al descargar la informaci\u00f3n");
        }
    }

    public void cerrarSesion() {
        Configuracion confDevice = new Configuracion("Device", "Bloqueado", "true");
        daoConfiguracion.updateConfiguracion(confDevice);
    }

    public void iniciarSesion() {
        Configuracion confDevice = new Configuracion("Device", "Bloqueado", "false");
        daoConfiguracion.updateConfiguracion(confDevice);
    }


    public int getUsuarioLogIn() {
        return daoConfiguracion.usuarioLogIn();
    }

    public int getEmpresa() {
        return daoConfiguracion.getEmpresa();
    }

    public int getSucursal() {
        return daoConfiguracion.getSucursal();
    }

    public List<BaseInfo> getEmpresas(int idUsuario) throws Exception {
        try {
            String empresas = daoIpati.loadEmpresas(getSession().sessionID, idUsuario);
            if (empresas == null) {
                return null;
            }
            List<BaseInfo> listEmpresa = new ArrayList<>();
            JsonParser parser = new JsonParser();
            JsonArray jEmpresas = parser.parse(empresas).getAsJsonArray();
            for (JsonElement jElementEmpresa : jEmpresas) {
                JsonObject jEmpresa = jElementEmpresa.getAsJsonObject();

                BaseInfo infoEmpresa = new BaseInfo();
                infoEmpresa.Identity = jEmpresa.get("idEmpresa").getAsInt();
                infoEmpresa.Code = jEmpresa.get("codigo").getAsString();
                infoEmpresa.Nombre = jEmpresa.get("nombre").getAsString();

                String sucursales = daoIpati.loadSucursales(getSession().sessionID, infoEmpresa.Identity, idUsuario);

                if (sucursales != null) {
                    JsonArray jSucursales = parser.parse(sucursales).getAsJsonArray();
                    for (JsonElement jElementSucursal : jSucursales) {
                        JsonObject jSucursal = jElementSucursal.getAsJsonObject();
                        BaseInfo infoSucursal = new BaseInfo();
                        infoSucursal.Identity = jSucursal.get("id").getAsInt();
                        infoSucursal.Code = jSucursal.get("codigo").getAsString();
                        infoSucursal.Nombre = jSucursal.get("nombre").getAsString();
                        infoEmpresa.getChilds().add(infoSucursal);
                    }
                }

                listEmpresa.add(infoEmpresa);
            }
            return listEmpresa;
        }
        catch (Exception ex) {
            DAOEventLog.HandledException(getContext(), ex, "Error al descargar la informaci\u00f3n");
        }
        return null;
    }

    public void cleanInfo() {
        daoDocumento.truncate();
        daoFormulario.truncate();
        daoDataset.truncate();
        daoDocumentoAsignado.truncate();
        daoField.truncate();
        daoDocInstance.truncate();
        daoWorkFlowStep.truncate();
        daoWorkFlowOption.truncate();
        daoTipoDato.truncate();
    }

    public void updateEmpresa(int idEmpresa) {
        Configuracion confEmpresa = new Configuracion("Device", "Empresa", String.valueOf(idEmpresa));
        daoConfiguracion.updateConfiguracion(confEmpresa);
    }

    public void updateSucursal(int idSucursal) {
        Configuracion confSucursal = new Configuracion("Device", "Sucursal", String.valueOf(idSucursal));
        daoConfiguracion.updateConfiguracion(confSucursal);
    }

    public void updateConfiguracion(Configuracion configuracion) {
        daoConfiguracion.updateConfiguracion(configuracion);
    }

}
