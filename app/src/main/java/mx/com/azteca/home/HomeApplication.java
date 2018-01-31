package mx.com.azteca.home;


import android.app.Application;
import android.content.Intent;

import mx.com.azteca.home.entity.EntityProvider;
import mx.com.azteca.home.model.provider.pojo.ClienteAvaluoInfo;
import mx.com.azteca.home.model.provider.pojo.ClienteInfo;
import mx.com.azteca.home.model.provider.pojo.DocumentInstance;
import mx.com.azteca.home.model.provider.pojo.Pago;
import mx.com.azteca.home.model.provider.pojo.PersonaInfo;
import mx.com.azteca.home.model.provider.pojo.SessionInfo;
import mx.com.azteca.home.model.provider.pojo.UsuarioInfo;
import mx.com.azteca.home.model.provider.pojo.WorkflowStep;
import mx.com.azteca.home.service.DataLoaderService;

public class HomeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EntityProvider.setDatabaseName("HomeDB");
        EntityProvider.getEntities().add(SessionInfo.class);
        EntityProvider.getEntities().add(DocumentInstance.class);
        EntityProvider.getEntities().add(WorkflowStep.class);
        EntityProvider.getEntities().add(Pago.class);
        EntityProvider.getEntities().add(UsuarioInfo.class);
        EntityProvider.getEntities().add(PersonaInfo.class);
        EntityProvider.getEntities().add(ClienteInfo.class);
        EntityProvider.getEntities().add(ClienteAvaluoInfo.class);
        EntityProvider.newSession(getBaseContext());

        // Servicio
        if (!DataLoaderService.DATA_LOADING) {
            Intent intent = new Intent(this, DataLoaderService.class);
            startService(intent);
        }
    }

}
