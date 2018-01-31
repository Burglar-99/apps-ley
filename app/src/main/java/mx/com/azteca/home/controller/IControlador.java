package mx.com.azteca.home.controller;


public interface IControlador {

    void notificarUsuario(String titulo, String mensaje);
    void mostrarProgress(String mensaje);
    void ocultarProgress();

}
