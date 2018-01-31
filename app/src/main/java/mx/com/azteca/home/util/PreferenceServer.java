package mx.com.azteca.home.util;


public enum PreferenceServer {

    USURIO("pref_server_user"),
    PASSWD("pref_server_passwd");

    public String Key;

    PreferenceServer(String key) {
        this.Key = key;
    }

}
