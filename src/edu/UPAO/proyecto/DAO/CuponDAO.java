package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Cupon;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CuponDAO {

    private static final String RUTA_CSV = "cupones.csv";

    public static List<Cupon> cargarCupones() {
        List<Cupon> cupones = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(RUTA_CSV))) {
            String linea;
            boolean primera = true;

            while ((linea = br.readLine()) != null) {
                if (primera) { // 🔥 saltamos encabezado
                    primera = false;
                    continue;
                }

                String[] datos = linea.split(",");

                if (datos.length < 5) {
                    continue;
                }

                String codigo = datos[0].trim();
                double descuento = Double.parseDouble(datos[1].trim());
                boolean activo = Boolean.parseBoolean(datos[2].trim());

                String inicioStr = datos[3].trim().replace("-", "/");
                String finStr = datos[4].trim().replace("-", "/");

                // soporta tanto 4/10/2025 como 04/10/2025
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

                LocalDate inicio = LocalDate.parse(inicioStr, formatter);
                LocalDate fin = LocalDate.parse(finStr, formatter);

                cupones.add(new Cupon(codigo, descuento, activo, inicio, fin));

                cupones.add(new Cupon(codigo, descuento, activo, inicio, fin));
                System.out.println("📥 Cargado cupón: " + codigo
                        + " | desc=" + descuento
                        + " | activo=" + activo
                        + " | inicio=" + inicio
                        + " | fin=" + fin);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
System.out.println("✅ Total cupones cargados: " + cupones.size());

        return cupones;
    }
}
