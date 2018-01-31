package mx.com.azteca.home.util;



public class Formula {

    private static Formula FORMULA;

    private Formula() {
    }

    public synchronized static double getZoom(double radio, double width) {
        if (Formula.FORMULA == null) {
            Formula.FORMULA = new Formula();
        }
        return Formula.FORMULA.calcularZoom(radio, width);
    }

    private synchronized double calcularZoom(double radio,  double width) {
        return Math.log( (width * 156543.03392 * Math.cos(20.1* Math.PI /180)) / (2*radio) ) / Math.log(2);
    }

}
