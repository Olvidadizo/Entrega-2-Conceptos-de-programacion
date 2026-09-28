/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

/**
* La clase crea:
* Un archivo que contiene todos los productos ofrecidos por la empresa
* Un archivo que contiene la información de los vendedores de la empresa
* Un archivo de ventas por cada vendedor de la empresa
* <p>
* AL ejecutarse (teniendo el metodo main) la clase muestra un mensaje si se generaron los archivos
* o no se generaron, ademas de no solicitar entrada de información para ejecutarse.
* </p>
*
* @author
* Hoover Gonzales Soto
* Alex Cortes Calle
* Jose Morales Perdomo
* Jehan Restrepo Villa
*/

public class GenerateInfoFiles{
    
    /**
    * Almacena las ID de los productos generados en productos.txt
    * Estas ID son posteriormente usadas al generar los archivos de ventas.
    */    
    private static ArrayList<String> productsIds = new ArrayList<>();
    
    /**
    * Se utiliza una única instancia de Random para generar todos los valores
    * pseudoaleatorios del programa,
    * evitando crear múltiples instancias innecesariamente.
    */
    private static final Random RANDOM = new Random();
    
    /**
    * Intenta generar los archivos. 
    * Si tiene éxito, notifica al usuario.
    * Si falla, muestra que archivos generaron errores.
    */
    public static void main(String[] args) throws IOException{
        try{
            int numberOfProducts = RANDOM.nextInt(100) + 3;
            int numberOfVendors = RANDOM.nextInt(50) + 3;
            createProductsFile(numberOfProducts);
            createSalesMenInfoFile(numberOfVendors);
            createFilesForAllSalesmen();
            System.out.println(" Éxito al generar archivos ");
        } catch (IOException e){
            System.out.println("Error al generar archivos: "+ e.getMessage());
        }
    }
    
    /**
    * Crea el archivo productos.txt con el número de productos solicitado
    * 
    * Limpia la lista de IDs y almacena en ella los identificadores
    * de los prodcutos generados. Esto permite reutilizar las IDs
    * al generar los archivos de ventas sin tener que volver a leer
    * el archivo productos.txt
    */
    
    public static void createProductsFile(int productsCount) throws IOException{        
        productsIds.clear();
        PrintWriter writer = 
                new PrintWriter(
                        new FileWriter("productos.txt")
                );
        
        String[] productNames = {
            "computadora", "celular", "nevera", "teclado",
            "mouse", "monitor", "parlantes", "lavadora"
        };
                        
        for(int i = 0; i < productsCount; i++){
            String productId = String.format("P%03d", i);
            productsIds.add(productId);
            String productName = productNames[
                        RANDOM.nextInt(productNames.length)
                    ];            
            int price = RANDOM.nextInt(1990001) + 10000;
            
            writer.println(
                    productId + ";" 
                    + productName + ";"
                    + price
            );                    
        }
        
        writer.close();
    }
 /**
    * genera el archivo vendedores.txt
    * cada fila del archivo contiene la información de un vendedor.
    * 
    * Se utiliza un set para garantizar que dos vendedores
    * no tengan un mismo número de cédula.
    
    * el rango utilizado permite generar hasta 3501 números
    * de cédula distintos. Si salesmanCount supera dicho límite,
    * el ciclo no termina.
    */
    public static void createSalesMenInfoFile(int salesmanCount) throws IOException{
        PrintWriter writer = 
                new PrintWriter(
                    new FileWriter("vendedores.txt")
        );
        
        String[] nombres = {
            "Juan", "Carlos", "Andrés","David", "Daniel", "Santiago", "Sebastián",
            "Alejandro", "Mateo", "Nicolás", "Miguel", "Julián", "Felipe", "Gabriel",
            "Diego", "Samuel", "Tomás", "Luis", "Camilo", "Jorge", "María", "Laura",
            "Andrea", "Valentina", "Sofía", "Isabella", "Camila", "Natalia", "Daniela", "Paula"
        };
        
        String[] apellidos = {
            "Gonzalez", "Rodriguez", "Martinez", "Garcia", "Lopez", "Hernandez", "Perez",
            "Sanchez", "Ramirez", "Torres", "Gomez", "Diaz", "Vargas", "Castro", "Morales",
            "Rojas", "Jimenez", "Ruiz", "Mendoza", "Moreno", "Muñoz", "Restrepo", "Quintero",
            "Cardona", "Cortes", "Valencia", "Ortiz", "Marin", "Suarez", "Rivera"
        };
        
        Set<Integer> documentNumbers = new HashSet<>();
        while(documentNumbers.size() < salesmanCount) {
            int number = 1000000 + RANDOM.nextInt(35001);            
            documentNumbers.add(number);
        }
        
        for(int number: documentNumbers){
            String nombre = nombres[RANDOM.nextInt(nombres.length)];
            String apellido = apellidos[RANDOM.nextInt(apellidos.length)];
            writer.println(
                "CC;" + number
                + ";" + nombre
                + ";" + apellido
            );
        }
        
        writer.close();
    }
    
 /**
    * Dado un vendedor, genera su archivo de ventas con el formato:
    * ventas_ID_nombre.txt.
    *
    * Para efectos del problema, cada fila de venta representa
    * una transacción independiente. Por lo tanto, el número de
    * ventas corresponde al número de filas de productos en el archivo.
    *
    * Un mismo producto puede aparecer en varias filas; cada aparición
    * representa una venta diferente.
    */
    public static void createSalesManFile(
            int randomSalesCount,
            String name,
            long id) throws IOException{
        
        String fileName = "ventas_" + id + "_" + name + ".txt";
        
        PrintWriter writer = 
                new PrintWriter(
                    new FileWriter(fileName)
                );        
        writer.println("CC;" + id);
        
        for(int i = 0; i < randomSalesCount; i++){
            String productId = 
                    productsIds.get(
                        RANDOM.nextInt(productsIds.size())
                    );
            
            int quantity = RANDOM.nextInt(10) + 1;
            writer.println(productId + ";" + quantity);
        }
        
        writer.close();
    }
    
    /**
    * Este método lee el archivo vendedores.txt
    * y por cada vendedor que lee
    * llama al método createSalesManFile para crear su archivo de ventas
    */
    public static void createFilesForAllSalesmen() throws IOException{
        BufferedReader reader = 
                new BufferedReader(
                        new FileReader("vendedores.txt")
                );
        
        String line;
        while( (line = reader.readLine()) != null){
            String[] data = line.split(";");
            long id = Long.parseLong(data[1]);
            String name = data[2];
            
            int randomSalesCount = RANDOM.nextInt(100) + 1;
            
            createSalesManFile(randomSalesCount, name, id);
        }
        
        reader.close();
    }
    
}
