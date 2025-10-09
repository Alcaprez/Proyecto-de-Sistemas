package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.RankingFinanciero;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class RankingFinancieroDAO {

    public static List<RankingFinanciero> leerDesdeCSV() {
        List<RankingFinanciero> lista = new ArrayList<>();
        String rutaArchivo = "RankingFinanciero.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            br.readLine(); // omite encabezado

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length < 3) continue;

                String sede = datos[0].trim();
                double ventas = parseNumero(datos[1].trim());
                double transacciones = parseNumero(datos[2].trim());

                lista.add(new RankingFinanciero(sede, ventas, transacciones));
            }

            // 🔹 Ordenar por promedio descendente (mayor promedio = mejor ranking)
            lista = lista.stream()
                    .sorted(Comparator.comparingDouble(RankingFinanciero::getPromedio).reversed())
                    .collect(Collectors.toList());

            // 🔹 Asignar puestos
            int puesto = 1;
            for (RankingFinanciero rf : lista) {
                rf.setRanking(puesto++);
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

