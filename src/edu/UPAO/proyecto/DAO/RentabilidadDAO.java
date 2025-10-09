package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Rentabilidad;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RentabilidadDAO {

    // 🟢 Lee el archivo CSV separado por ";"
    public static List<Rentabilidad> leerDesdeCSV() {
        List<Rentabilidad> lista = new ArrayList<>();
        String rutaArchivo = "Rentabilidad.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            br.readLine(); // omite encabezado

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length < 3) continue;

                String sede = datos[0].trim();
                double ventas = parseNumero(datos[1].trim());
                double costos = parseNumero(datos[2].trim());

                lista.add(new Rentabilidad(sede, ventas, costos));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 🟢 Permite leer números con comas o puntos según Excel
    private static double parseNumero(String valor) {
        valor = valor.replace("S/", "").replace(",", "").trim();
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            try {
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                return format.parse(valor).doubleValue();
            } catch (ParseException ex) {
                return 0;
            }
        }
    }
}









