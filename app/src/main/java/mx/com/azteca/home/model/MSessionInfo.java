package mx.com.azteca.home.model;


import android.content.Context;

public class MSessionInfo extends BaseModel {


    public MSessionInfo(Context context) {
        super(context);
    }

    public MSessionInfo(Context context, String userName, String passwd, String workStation) {
        super(context, userName, passwd, workStation);
    }
}
