package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOPersona;
import mx.com.azteca.home.model.ipati.pojo.Persona;

public class MPersona extends BaseModel {

    private DAOPersona daoPersona;

    public MPersona(Context context) {
        super(context);
    }

    public MPersona(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.daoPersona = new DAOPersona(getContext());
    }

    public Persona getDetail(int idPersona) {
        return daoPersona.getDetail(idPersona);
    }

    public void guardar(Persona persona) {
        daoPersona.guardar(persona);
    }

}
