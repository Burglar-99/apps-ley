package mx.com.azteca.home.util;


public enum IpatiServer {


    LOGIN("/api/kernel/security/usuarios/login"),                                               // get
    FORM_LOAD_JOSON_UI("/api/kernel/bpm/formularios/json"),                                     // get
    GET_DATASET("/api/kernel/bpm/datasets/result"),                                             // post
    GET_OFFLINE_DATASET("/api/kernel/bpm/datasets/offline-result"),                             // get
    GET_DATASET_RESULT("/api/kernel/bpm/datasets/result"),                                      // get
    GET_DETAIL_DATASET_BY_ID("/api/kernel/bpm/datasets/get-detail-id"),                         // get
    DOCUMENTS_USER("/api/kernel/bpm/documentos/user-list"),                                     // get
    LOAD_ASIGNADOS("/api/kernel/bpm/documentos/load-asignados"),                                // get
    LOAD_ASIGNADOS_OFFLINE("/api/kernel/bpm/documentos/load-asignados-offline"),                // get
    GET_DETAIL_FORMULARIO("/api/kernel/bpm/formularios/get-detail-id"),                         // get
    LOAD_WORKFLOW("/api/kernel/bpm/workflow/load-workflow"),                                    // get
    LOAD_VERSION_FIELDS("/api/kernel/bpm/campos/version-fields"),                               // get
    LOAD_EMPRESAS("/api/kernel/security/empresas/load-by-usuario"),                             // get
    LOAD_SUCURSALES("/api/kernel/security/empresas/load-sucursales"),                           // get
    SAVE_INSTANCE("/api/kernel/bpm/documentos/save-instance"),                                  // post
    CREATE_INSTANCE("/api/kernel/bpm/documentos/create-instance-json"),                         // get
    LOAD_INSTANCE("/api/kernel/bpm/documentos/load-instance-text"),                             // get
    LOAD_ACTIVE_VERSION("/api/kernel/bpm/documentos/active-version"),                           // get
    LOAD_FIELDS_COMPLEX_TYPE ("/api/kernel/bpm/documentos/load-complex-type"),                  // get
    FILE_SERVER("/file-server/"),                                                                // get
    GET_DETAIL_DOCUMENTO("/api/kernel/bpm/documentos/get-detail-id"),                           // get
    LOAD_COMPARABLE_EXPRESS("/api/comparables/load-comparable-express"),                        // get
    LOAD_COMPARABLE_SELECCION("/api/comparables/load-comparable-seleccion"),                    // get
    LOAD_COMPARABLE_IMAGENES_SELECCION("/api/comparables/load-comparable-imagenes-seleccion"),  // get
    CONEKTA_PUBLIC_KEY("key_CsmaCW7hcvDLuLXTuLuE2sw");


    public String metodo;

    IpatiServer(String metodo) {
        this.metodo = metodo;
    }

    public String getUrl() {
        //return "http://ipati2.ddns.net:8088" + metodo;
        return "http://192.168.15.100:8081" + metodo;
        //return "http://192.168.100.5:50027" + metodo;
        //return "http://192.168.111.121:50027" + metodo;
        //return "http://192.168.111.121:8080" + metodo;
        //return "http:// " + metodo;
    }

    public String getImageUrl(String fileUrl, int width) {
        return getUrl() + fileUrl + "?width=" + width;
    }

}