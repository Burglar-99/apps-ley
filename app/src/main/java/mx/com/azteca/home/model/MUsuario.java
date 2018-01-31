package mx.com.azteca.home.model;


import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import mx.com.azteca.home.entity.generator.DaoObject;
import mx.com.azteca.home.model.provider.ICliente;
import mx.com.azteca.home.model.provider.ISucursal;
import mx.com.azteca.home.model.provider.IUsuario;
import mx.com.azteca.home.model.provider.pojo.ClienteInfo;
import mx.com.azteca.home.model.provider.pojo.Pago;
import mx.com.azteca.home.model.provider.pojo.PersonaInfo;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioItem;
import mx.com.azteca.home.util.BusinessException;
import retrofit2.Call;
import retrofit2.Response;

public class MUsuario extends BaseModel {

    public MUsuario(Context context) {
        super(context);
    }

    public MUsuario(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    public SessionInfo login(String userName, String password, String workStation) throws Exception {
        try {
            int timeZone = TimeZone.getDefault().getRawOffset();
            IUsuario apiUsuario = super.getRetrofit().create(IUsuario.class);
            Call<SessionInfo> call = apiUsuario.login(userName, password, workStation, timeZone);
            Response<SessionInfo> response = call.execute();
            if (response.isSuccessful()) {
                SessionInfo sessionInfo = response.body();
                DaoObject<SessionInfo> daoSession = new DaoObject<>();
                daoSession.delete(SessionInfo.class);
                daoSession.insert(sessionInfo);
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Azteca-sessionID", sessionInfo.sessionID);
                ICliente apiCliente = super.getRetrofit().create(ICliente.class);
                Call<ClienteInfo> callCliente = apiCliente.clienteByUsuario(mapHeader, sessionInfo.user.identity);
                Response<ClienteInfo> responseCliente = callCliente.execute();
                if (responseCliente.isSuccessful()) {
                    ClienteInfo cliente = responseCliente.body();
                    sessionInfo.user.setIdCliente(cliente.identity);
                    DaoObject<ClienteInfo> daoObject = new DaoObject<>();
                    daoObject.where("Id", cliente.identity);
                    ClienteInfo clienteInfo = daoObject.single(ClienteInfo.class);
                    if (clienteInfo != null) {
                        daoObject.update(cliente);
                    }
                    else {
                        daoObject.insert(cliente);
                    }
                    // Limpiamos las metodos de pago del cliente
                    DaoObject<Pago> daoPago = new DaoObject<>();
                    daoPago.delete(Pago.class);
                    MPago mPago = new MPago(getContext(), userName, password, workStation);
                    mPago.loadMetodosPago(cliente.identity);
                }
                else {
                    throw new Exception("Error al consultar el cliente");
                }
                DaoObject<SessionInfo> daoObject = new DaoObject<>();
                daoObject.where("SessionID", sessionInfo.sessionID);
                SessionInfo info = daoObject.single(SessionInfo.class);
                if (info != null) {
                    daoObject.update(sessionInfo);
                }
                else {
                    daoObject.insert(sessionInfo);
                }
                DaoObject<UsuarioInfo> daoUsuario = new DaoObject<>();
                daoUsuario.delete(UsuarioInfo.class);
                daoUsuario.insert(sessionInfo.user);
                DaoObject<PersonaInfo> daoPersona = new DaoObject<>();
                daoPersona.delete(PersonaInfo.class);
                daoPersona.insert(sessionInfo.user.persona);
                return sessionInfo;
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

    public boolean usuarioExist(String sessionID, String user) throws Exception {
        try {
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", sessionID);
            ICliente apiCliente = super.getRetrofit().create(ICliente.class);
            Call<Boolean> call = apiCliente.usuarioExist(mapHeader, user);
            Response<Boolean> response = call.execute();
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

    public UsuarioInfo create(String sessionID, String nombre, String apPaterno, String apMaterno, String usuario, String contrasena, String confirmacion) throws Exception {
        try {
            if (contrasena.trim().equals(confirmacion.trim())) {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Azteca-sessionID", sessionID);
                IUsuario apiUsuario = super.getRetrofit().create(IUsuario.class);
                Call<UsuarioInfo> call = apiUsuario.create(mapHeader);
                Response<UsuarioInfo> response = call.execute();
                if (response.isSuccessful()) {
                    UsuarioInfo usuarioInfo = response.body();
                    usuarioInfo.code                        = usuario;
                    usuarioInfo.correo                      = usuario;
                    usuarioInfo.userName                    = usuario;
                    usuarioInfo.password                    = contrasena;
                    usuarioInfo.persona.nombre              = nombre;
                    usuarioInfo.persona.apellidoPaterno     = apPaterno;
                    usuarioInfo.persona.apellidoMaterno     = apMaterno;
                    usuarioInfo.idBranch                    = 1;
                    usuarioInfo.idRol                       = 5;
                    call = apiUsuario.update(mapHeader, usuarioInfo);
                    response = call.execute();
                    if (response.isSuccessful()) {
                        usuarioInfo = response.body();
                        // Agregar usuario a la sucursal
                        addUserSucursal(sessionID, usuarioInfo.idBranch, usuarioInfo.identity);
                        ICliente apiCliente = super.getRetrofit().create(ICliente.class);
                        Call<ClienteInfo> callCliente = apiCliente.create(mapHeader);
                        Response<ClienteInfo> responseCliente = callCliente.execute();
                        if (responseCliente.isSuccessful()) {
                            ClienteInfo cliente = responseCliente.body();
                            cliente.nombre = usuarioInfo.persona.nombre;
                            cliente.idListaPrecios = 1;
                            callCliente = apiCliente.update(mapHeader, cliente);
                            responseCliente = callCliente.execute();
                            if (responseCliente.isSuccessful()) {
                                cliente = responseCliente.body();
                                usuarioInfo.setIdCliente(cliente.identity);
                                Call<Void> callClienteUsuario = apiCliente.updateClienteUsuario(mapHeader, cliente.identity, usuarioInfo.identity);
                                Response<Void> responseClienteUsuario = callClienteUsuario.execute();
                                if (!responseClienteUsuario.isSuccessful()) {
                                    throw new Exception(responseClienteUsuario.errorBody().string());
                                }
                                else {
                                    DaoObject<UsuarioInfo> daoUsuario = new DaoObject<>();
                                    daoUsuario.delete(UsuarioInfo.class);
                                    daoUsuario.insert(usuarioInfo);
                                    DaoObject<PersonaInfo> daoPersona = new DaoObject<>();
                                    daoUsuario.delete(PersonaInfo.class);
                                    daoPersona.insert(usuarioInfo.persona);
                                    DaoObject<ClienteInfo> daoCliente = new DaoObject<>();
                                    daoUsuario.delete(ClienteInfo.class);
                                    daoCliente.insert(cliente);
                                }
                            }
                            else {
                                throw new Exception(responseCliente.errorBody().string());
                            }
                        }
                        else {
                            throw new Exception(responseCliente.errorBody().string());
                        }
                        return response.body();
                    }
                    else {
                        throw new Exception(response.errorBody().string());
                    }
                }
                else {
                    throw new Exception("Ocurri\u00f3 un problema al crear el usuario");
                }
            }
            else {
                throw new Exception("La contrase\u00f1a no coincide con la cofirmaci\u00f3n");
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError(ex.getMessage());
        }
    }

    private void addUserSucursal(String sessionID, int idSucursal, int idUsuario) throws Exception {
        try {
            UsuarioItem item = new UsuarioItem();
            item.id = idUsuario;
            List<UsuarioItem> listItems = new ArrayList<>();
            listItems.add(item);
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Azteca-sessionID", sessionID);
            ISucursal apiSucursal = super.getRetrofit().create(ISucursal.class);
            Call<Void> call = apiSucursal.addMembers(mapHeader, idSucursal, listItems);
            Response<Void> response = call.execute();
            if (!response.isSuccessful()) {
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

    public UsuarioInfo getUsuarioInfo(int idUsuario) throws Exception {
        try {
            DaoObject<UsuarioInfo> daoUsuario = new DaoObject<>();
            daoUsuario.where("ID", idUsuario);
            UsuarioInfo info = daoUsuario.single(UsuarioInfo.class);
            DaoObject<PersonaInfo> daoPersona = new DaoObject<>();
            daoPersona.where("Id", info.idPersona);
            info.persona =  daoPersona.single(PersonaInfo.class);
            return info;
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError("Ocurri\u00f3 un problema al consultar el usuario", ex);
        }
    }

    public void recuperarCuenta(String correo) throws Exception {
        try {
            IUsuario apiUsuario = super.getRetrofit().create(IUsuario.class);
            Call<Void> call = apiUsuario.restorePasswd(correo, "restorePass", "0");
            Response<Void> response = call.execute();
            if (!response.isSuccessful()) {
                throw new Exception(response.errorBody().toString());
            }
        }
        catch (BusinessException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw handledError("Ocurri\u00f3 un problema al cargar la informaci\u00f3n", ex);
        }
    }
}
