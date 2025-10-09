package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.RotacionProducto;
import java.io.*;
import java.util.*;

public class RotacionProductoDAO {

    // 📂 Ruta del archivo CSV (ajústala si es necesario)
    private static final String ARCHIVO = "RotacionProductos.csv";

    public static List<RotacionProducto> leerDesdeCSV() {
        List<RotacionProducto> productos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                // Saltar encabezado
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }

                // Evita líneas vacías
                if (linea.trim().isEmpty()) continue;

                // Dividir por punto y coma o coma
                String[] partes = linea.split("[;,]");

                if (partes.length < 4) {
                    System.out.println("❌ Línea inválida: " + linea);
                    continue;
                }

                try {
                    String producto = partes[0].trim();
                    String sede = partes[1].trim();
                    double precio = Double.parseDouble(partes[2].trim());
                    int ventas = Integer.parseInt(partes[3].trim());

                    RotacionProducto p = new RotacionProducto(producto, sede, precio, ventas);
                    productos.add(p);
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Error en valores numéricos en línea: " + linea);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("⚠️ No se encontró el archivo CSV: " + ARCHIVO);
        } catch (IOException e) {
            System.out.println("⚠️ Error al leer el archivo CSV: " + e.getMessage());
        }

        return productos;
    }
}








