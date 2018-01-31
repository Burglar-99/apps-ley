package mx.com.azteca.home.model.ipati.model;


import android.content.Context;

import mx.com.azteca.home.model.BaseModel;
import mx.com.azteca.home.model.ipati.dao.DAOTipoDato;
import mx.com.azteca.home.model.ipati.pojo.TipoDato;

public class MTipoDato extends BaseModel {

    private DAOTipoDato daoTipoDato;

    public MTipoDato(Context context) {
        super(context);
    }

    public MTipoDato(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.daoTipoDato =  new DAOTipoDato(getContext());
    }

    public TipoDato getDetail(int idTipoDato) {
        return daoTipoDato.getDetail(idTipoDato);
    }

}
