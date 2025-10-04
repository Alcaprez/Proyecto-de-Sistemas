package edu.UPAO.proyecto.Modelo;
import java.time.YearMonth;

public class PuntoMensual {
    private YearMonth mes;
    private double total;

    public PuntoMensual(YearMonth mes, double total) {
        this.mes = mes;
        this.total = total;
    }

    public YearMonth getMes() { return mes; }
    public double getTotal() { return total; }

    @Override
    public String toString() {
        return mes + " → " + total;
    }
}
