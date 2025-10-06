package edu.UPAO.proyecto;

import edu.UPAO.proyecto.Modelo.Venta;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VentasController {

    private static final String FILE_NAME = "ventas.csv";

    private static final List<Venta> VENTAS = new ArrayList<>();

    // Registrar una venta completa
    public static void registrarVenta(Venta v) {
        synchronized (VENTAS) {
            VENTAS.add(v);
        }
        // persistir en archivo (append)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bw.write(v.toFileLine()); // usamos el método de Venta
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Obtener todas las ventas registradas
    public static List<Venta> getVentas() {
        synchronized (VENTAS) {
            return Collections.unmodifiableList(new ArrayList<>(VENTAS));
        }
    }
}
