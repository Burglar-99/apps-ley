package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import java.util.HashMap;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOUsuario;
import mx.com.azteca.home.model.ipati.pojo.Usuario;

public class MUsuario extends BaseModel {

    private DAOUsuario daoUsuario;

    public MUsuario(Context context) {
        super(context);
    }

    public MUsuario(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.daoUsuario = new DAOUsuario(getContext());
    }

    public Usuario getDetail(int idUsuario) {
        return daoUsuario.getDetail(idUsuario);
    }

    public HashMap<String, Object> getDataLoginUsuario(int idUsuario) {
        return daoUsuario.getDataLoginUsuario(idUsuario);
    }

    public void guardar(Usuario usuario) {
        daoUsuario.guardar(usuario);
    }

}
