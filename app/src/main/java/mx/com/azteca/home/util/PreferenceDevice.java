package mx.com.azteca.home.util;


public enum PreferenceDevice {

    /**
     * IdUsuario logeado a la aplicación
     */
    LOGGED_USUER("LoginUser"),

    /**
     * Tutorial completado por el usuario
     */
    COMPLETED_GUIDE("GuideCompleted"),

    /**
     * Email del usuario
     */
    EMAIL_CUENTA("EmailConfirmacion"),

    /**
     *
     */
    ID_CLIENTE("IdCliente"),

    /**
     * Indica si los datos default de poblaciones, municipios y colonias fueron insertados.
     */
    DEFAULT_DATA_LOADED("DataLoaded"),


    USER_NAME("UserName"),

    USER_PASSWD("UserPasswd");

    public String Key;

    PreferenceDevice(String key) {
        this.Key = key;
    }

}
