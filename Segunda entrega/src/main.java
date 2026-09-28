
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal del programa.
 *
 * Lee los archivos de vendedores, productos y ventas.
 * Luego genera los reportes solicitados.
 *
 * También verifica que la información de los archivos
 * tenga un formato válido y sea coherente.
 */

/**
 * Autores:
 * Hoover Gonzales Soto
 * Jehan Restrepo Villa
 * Jose Morales Perdomo
 * Alex Cortes Calle
 */

public class main {

    /**
     * Inicia el procesamiento de los archivos.
     */
    public static void main(String[] args) {

        try {

            Map<Long, String> vendedores =
                    leerVendedores("vendedores.txt");

            Map<String, Producto> productos =
                    leerProductos("productos.txt");

            Map<Long, Double> recaudacionVendedores =
                    new HashMap<>();

            Map<String, Integer> cantidadProductos =
                    new HashMap<>();

            procesarArchivosVentas(
                    vendedores,
                    productos,
                    recaudacionVendedores,
                    cantidadProductos
            );

            generarReporteVendedores(
                    vendedores,
                    recaudacionVendedores
            );

            generarReporteProductos(
                    productos,
                    cantidadProductos
            );

            System.out.println(
                    "Proceso finalizado exitosamente."
            );

        } catch (Exception e) {

            System.out.println(
                    "Error durante el procesamiento: "
                    + e.getMessage()
            );
        }
    }

    /**
     * Lee vendedores.txt y guarda los vendedores
     * utilizando el número de documento como identificador.
     */
    public static Map<Long, String> leerVendedores(
            String fileName) throws IOException {

        Map<Long, String> vendedores =
                new HashMap<>();

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(fileName))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(";");

                if (data.length != 4) {
                    throw new IOException(
                            "Formato incorrecto en vendedores.txt, "
                            + "línea " + lineNumber
                    );
                }

                if (data[0].trim().isEmpty()
                        || data[1].trim().isEmpty()
                        || data[2].trim().isEmpty()
                        || data[3].trim().isEmpty()) {

                    throw new IOException(
                            "Información incompleta en vendedores.txt, "
                            + "línea " + lineNumber
                    );
                }

                long documentNumber;

                try {

                    documentNumber =
                            Long.parseLong(
                                    data[1].trim()
                            );

                } catch (NumberFormatException e) {

                    throw new IOException(
                            "Número de documento inválido en "
                            + "vendedores.txt, línea "
                            + lineNumber
                    );
                }

                if (documentNumber <= 0) {
                    throw new IOException(
                            "Número de documento inválido en "
                            + "vendedores.txt, línea "
                            + lineNumber
                    );
                }

                String fullName =
                        data[2].trim()
                        + " "
                        + data[3].trim();

                if (vendedores.containsKey(documentNumber)) {
                    throw new IOException(
                            "Documento de vendedor duplicado: "
                            + documentNumber
                    );
                }

                vendedores.put(
                        documentNumber,
                        fullName
                );
            }
        }

        if (vendedores.isEmpty()) {
            throw new IOException(
                    "El archivo vendedores.txt está vacío."
            );
        }

        return vendedores;
    }

    /**
     * Lee productos.txt y guarda la información
     * de cada producto utilizando su ID.
     */
    public static Map<String, Producto> leerProductos(
            String fileName) throws IOException {

        Map<String, Producto> productos =
                new HashMap<>();

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(fileName))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(";");

                if (data.length != 3) {
                    throw new IOException(
                            "Formato incorrecto en productos.txt, "
                            + "línea " + lineNumber
                    );
                }

                String productId =
                        data[0].trim();

                String productName =
                        data[1].trim();

                if (productId.isEmpty()
                        || productName.isEmpty()
                        || data[2].trim().isEmpty()) {

                    throw new IOException(
                            "Información incompleta en productos.txt, "
                            + "línea " + lineNumber
                    );
                }

                double price;

                try {

                    price =
                            Double.parseDouble(
                                    data[2].trim()
                            );

                } catch (NumberFormatException e) {

                    throw new IOException(
                            "Precio inválido en productos.txt, "
                            + "línea " + lineNumber
                    );
                }

                if (price < 0) {
                    throw new IOException(
                            "El precio no puede ser negativo en "
                            + "productos.txt, línea "
                            + lineNumber
                    );
                }

                if (productos.containsKey(productId)) {
                    throw new IOException(
                            "ID de producto duplicado: "
                            + productId
                    );
                }

                Producto producto =
                        new Producto(
                                productId,
                                productName,
                                price
                        );

                productos.put(
                        productId,
                        producto
                );
            }
        }

        if (productos.isEmpty()) {
            throw new IOException(
                    "El archivo productos.txt está vacío."
            );
        }

        return productos;
    }

    /**
     * Busca todos los archivos de ventas de la carpeta
     * y procesa cada uno.
     */
    public static void procesarArchivosVentas(
            Map<Long, String> vendedores,
            Map<String, Producto> productos,
            Map<Long, Double> recaudacionVendedores,
            Map<String, Integer> cantidadProductos)
            throws IOException {

        File folder = new File(".");
        File[] files = folder.listFiles();

        if (files == null) {
            throw new IOException(
                    "No se pudo acceder a la carpeta del proyecto."
            );
        }

        boolean foundSalesFile = false;

        for (File file : files) {

            if (!file.isFile()) {
                continue;
            }

            String fileName = file.getName();

            if (fileName.startsWith("ventas_")
                    && fileName.endsWith(".txt")) {

                foundSalesFile = true;

                procesarArchivoVentas(
                        file,
                        vendedores,
                        productos,
                        recaudacionVendedores,
                        cantidadProductos
                );
            }
        }

        if (!foundSalesFile) {
            throw new IOException(
                    "No se encontraron archivos de ventas."
            );
        }
    }

    /**
     * Lee un archivo de ventas y acumula la información
     * de dinero recaudado y cantidad de productos vendidos.
     */
    public static void procesarArchivoVentas(
            File file,
            Map<Long, String> vendedores,
            Map<String, Producto> productos,
            Map<Long, Double> recaudacionVendedores,
            Map<String, Integer> cantidadProductos)
            throws IOException {

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(file))) {

            String line = reader.readLine();

            if (line == null) {
                throw new IOException(
                        "El archivo está vacío: "
                        + file.getName()
                );
            }

            String[] sellerData =
                    line.split(";");

            if (sellerData.length != 2) {
                throw new IOException(
                        "Formato incorrecto en el archivo: "
                        + file.getName()
                );
            }

            long sellerId;

            try {

                sellerId =
                        Long.parseLong(
                                sellerData[1].trim()
                        );

            } catch (NumberFormatException e) {

                throw new IOException(
                        "Documento de vendedor inválido en: "
                        + file.getName()
                );
            }

            if (!vendedores.containsKey(sellerId)) {
                throw new IOException(
                        "El vendedor "
                        + sellerId
                        + " no existe en vendedores.txt."
                );
            }

            double totalSales = 0;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] saleData =
                        line.split(";");

                if (saleData.length != 2) {
                    throw new IOException(
                            "Formato incorrecto en "
                            + file.getName()
                            + ", línea "
                            + lineNumber
                    );
                }

                String productId =
                        saleData[0].trim();

                int quantity;

                try {

                    quantity =
                            Integer.parseInt(
                                    saleData[1].trim()
                            );

                } catch (NumberFormatException e) {

                    throw new IOException(
                            "Cantidad inválida en "
                            + file.getName()
                            + ", línea "
                            + lineNumber
                    );
                }

                if (quantity <= 0) {
                    throw new IOException(
                            "La cantidad debe ser mayor que cero en "
                            + file.getName()
                            + ", línea "
                            + lineNumber
                    );
                }

                Producto product =
                        productos.get(productId);

                if (product == null) {
                    throw new IOException(
                            "El producto "
                            + productId
                            + " no existe en productos.txt."
                    );
                }

                double saleValue =
                        product.getPrice() * quantity;

                totalSales += saleValue;

                int previousQuantity =
                        cantidadProductos.containsKey(productId)
                        ? cantidadProductos.get(productId)
                        : 0;

                cantidadProductos.put(
                        productId,
                        previousQuantity + quantity
                );
            }

            double previousTotal =
                    recaudacionVendedores.containsKey(sellerId)
                    ? recaudacionVendedores.get(sellerId)
                    : 0.0;

            recaudacionVendedores.put(
                    sellerId,
                    previousTotal + totalSales
            );
        }
    }

    /**
     * Crea el reporte de vendedores y los ordena
     * de mayor a menor según el dinero recaudado.
     */
    public static void generarReporteVendedores(
            Map<Long, String> vendedores,
            Map<Long, Double> recaudacionVendedores)
            throws IOException {

        List<VendedorReporte> reportes =
                new ArrayList<>();

        for (Map.Entry<Long, String> entry
                : vendedores.entrySet()) {

            long sellerId =
                    entry.getKey();

            double total =
                    recaudacionVendedores.containsKey(sellerId)
                    ? recaudacionVendedores.get(sellerId)
                    : 0.0;

            reportes.add(
                    new VendedorReporte(
                            entry.getValue(),
                            total
                    )
            );
        }

        Collections.sort(
                reportes,
                new Comparator<VendedorReporte>() {

                    @Override
                    public int compare(
                            VendedorReporte vendedor1,
                            VendedorReporte vendedor2) {

                        return Double.compare(
                                vendedor2.getTotal(),
                                vendedor1.getTotal()
                        );
                    }
                }
        );

        try (PrintWriter writer =
                     new PrintWriter(
                             new FileWriter(
                                     "reporte_vendedores.csv"
                             )
                     )) {

            for (VendedorReporte reporte : reportes) {

                writer.println(
                        reporte.getName()
                        + ";"
                        + String.format(
                                "%.2f",
                                reporte.getTotal()
                        )
                );
            }
        }
    }

    /**
     * Crea el reporte de productos y los ordena
     * de mayor a menor según la cantidad vendida.
     */
    public static void generarReporteProductos(
            Map<String, Producto> productos,
            Map<String, Integer> cantidadProductos)
            throws IOException {

        List<ProductoReporte> reportes =
                new ArrayList<>();

        for (Producto producto :
                productos.values()) {

            int quantity =
                    cantidadProductos.containsKey(
                            producto.getId()
                    )
                    ? cantidadProductos.get(
                            producto.getId()
                    )
                    : 0;

            reportes.add(
                    new ProductoReporte(
                            producto.getName(),
                            producto.getPrice(),
                            quantity
                    )
            );
        }

        Collections.sort(
                reportes,
                new Comparator<ProductoReporte>() {

                    @Override
                    public int compare(
                            ProductoReporte producto1,
                            ProductoReporte producto2) {

                        return Integer.compare(
                                producto2.getQuantity(),
                                producto1.getQuantity()
                        );
                    }
                }
        );

        try (PrintWriter writer =
                     new PrintWriter(
                             new FileWriter(
                                     "reporte_productos.csv"
                             )
                     )) {

            for (ProductoReporte reporte : reportes) {

                writer.println(
                        reporte.getName()
                        + ";"
                        + String.format(
                                "%.2f",
                                reporte.getPrice()
                        )
                );
            }
        }
    }

    /**
     * Representa un producto del archivo productos.txt.
     */
    public static class Producto {

        private String id;
        private String name;
        private double price;

        public Producto(
                String id,
                String name,
                double price) {

            this.id = id;
            this.name = name;
            this.price = price;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }

    /**
     * Representa la información necesaria para
     * generar el reporte de vendedores.
     */
    public static class VendedorReporte {

        private String name;
        private double total;

        public VendedorReporte(
                String name,
                double total) {

            this.name = name;
            this.total = total;
        }

        public String getName() {
            return name;
        }

        public double getTotal() {
            return total;
        }
    }

    /**
     * Representa la información necesaria para
     * generar el reporte de productos.
     */
    public static class ProductoReporte {

        private String name;
        private double price;
        private int quantity;

        public ProductoReporte(
                String name,
                double price,
                int quantity) {

            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}