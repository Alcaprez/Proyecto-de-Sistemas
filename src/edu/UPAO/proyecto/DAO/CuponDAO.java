package edu.UPAO.proyecto.DAO;

import edu.UPAO.proyecto.Modelo.Cupon;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CuponDAO {
    // Se crea en la RAÍZ del proyecto (al lado de build.gradle/pom, no en src)
    private static final String RUTA = "data/cupones.csv";

    public static List<Cupon> listar() {
        File f = new File(RUTA);
        if (!f.exists()) return new ArrayList<>();
        List<Cupon> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), StandardCharsets.UTF_8))) {
            String header = br.readLine(); // skip
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                out.add(parse(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public static void guardarTodos(List<Cupon> cupones) {
        File f = new File(RUTA);
        f.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
            bw.write(Cupon.csvHeader());
            bw.newLine();
            for (Cupon c : cupones) {
                bw.write(c.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void upsert(Cupon cupon) {
        List<Cupon> all = listar();
        int idx = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getCodigo().equalsIgnoreCase(cupon.getCodigo())) {
                idx = i; break;
            }
        }
        if (idx >= 0) all.set(idx, cupon); else all.add(cupon);
        guardarTodos(all);
    }

    public static Optional<Cupon> buscarPorCodigo(String codigo) {
        return listar().stream().filter(c -> c.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }

    public static void eliminar(String codigo) {
        List<Cupon> all = listar().stream()
            .filter(c -> !c.getCodigo().equalsIgnoreCase(codigo))
            .collect(Collectors.toList());
        guardarTodos(all);
    }

    private static Cupon parse(String line) {
        // codigo,tipo,valor,sku_aplicado,minimo_compra,inicio,fin,activo,max_usos,usos
        List<String> t = splitCsv(line);
        String codigo = t.get(0);
        Cupon.TipoDescuento tipo = Cupon.TipoDescuento.valueOf(t.get(1));
        double valor = Double.parseDouble(t.get(2));
        String sku = t.get(3).isBlank() ? null : t.get(3);
        double minimo = Double.parseDouble(t.get(4));
        LocalDate inicio = t.get(5).isBlank() ? null : LocalDate.parse(t.get(5));
        LocalDate fin = t.get(6).isBlank() ? null : LocalDate.parse(t.get(6));
        boolean activo = Boolean.parseBoolean(t.get(7));
        int maxUsos = Integer.parseInt(t.get(8));
        int usos = Integer.parseInt(t.get(9));
        return new Cupon(codigo, tipo, valor, sku, minimo, inicio, fin, activo, maxUsos, usos);
    }

    private static List<String> splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"'); i++;
                    } else {
                        inQuotes = false;
                    }
                } else sb.append(ch);
            } else {
                if (ch == ',') {
                    out.add(sb.toString()); sb.setLength(0);
                } else if (ch == '"') {
                    inQuotes = true;
                } else sb.append(ch);
            }
        }
        out.add(sb.toString());
        return out;
    }
}