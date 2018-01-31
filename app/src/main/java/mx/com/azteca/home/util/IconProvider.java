package mx.com.azteca.home.util;


import mx.com.azteca.home.R;

public class IconProvider {


    private static IconProvider INSTANCE;

    private IconProvider() {
    }

    public static synchronized int getIcon(int id) {
        if (IconProvider.INSTANCE == null) {
            IconProvider.INSTANCE = new IconProvider();
        }
        return IconProvider.INSTANCE.getDrawable(id);
    }


    private int getDrawable(int id) {
        switch (id) {
            case -2:
                return R.mipmap.ic_comparable;
            case -1:
                return R.mipmap.ic_inmueble;
            case 0:
                return R.mipmap.ic_iglesia;
            case 1:
                return R.mipmap.ic_mercado;
            case 2:
                return R.mipmap.ic_super_mercado;
            case 3:
                return R.mipmap.ic_locales;
            case 4:
                return R.mipmap.ic_plaza;
            case 5:
                return R.mipmap.ic_parque;
            case 6:
                return R.mipmap.ic_jardin;
            case 7:
                return R.mipmap.ic_cancha;
            case 8:
                return R.mipmap.ic_centro_deportivo;
            case 9:
                return R.mipmap.ic_escuela;
            case 10:
                return R.mipmap.ic_hospital;
            case 11:
                return R.mipmap.ic_banco;
            case 12:
                return R.mipmap.ic_centro_comunitario;
            default:
                return R.mipmap.ic_default;
        }
    }
}
