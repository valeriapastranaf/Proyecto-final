import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FoodSaver {

    static Scanner sc = new Scanner(System.in);

    static class Producto {
        String codigo, nombre, categoria;
        double precio;
        int cantidad;
        LocalDate vencimiento;

        Producto(String codigo, String nombre, double precio,
                int cantidad, String categoria, LocalDate vencimiento) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.precio = precio;
            this.cantidad = cantidad;
            this.categoria = categoria;
            this.vencimiento = vencimiento;
        }
    }

    static Producto[] productos = new Producto[100];

    static String[][] movimientos = new String[500][5];

    static int totalProductos = 0;
    static int totalMovimientos = 0;

    static double totalVendido = 0;
    static final int DIAS_ALERTA = 7;

    static DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {

        int opcion;

        do {
            System.out.println("\n================================");
            System.out.println("       FOODSAVER");
            System.out.println(" Control de alimentos y stock");
            System.out.println("================================");
            System.out.println("1. Registrar producto");
            System.out.println("2. Mostrar inventario");
            System.out.println("3. Registrar venta");
            System.out.println("4. Actualizar inventario");
            System.out.println("5. Productos proximos a vencer");
            System.out.println("6. Productos vencidos");
            System.out.println("7. Calcular perdidas");
            System.out.println("8. Reporte de ventas del dia");
            System.out.println("9. Salir");
            System.out.print("Seleccione una opcion: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    registrarProducto();
                    break;
                case 2:
                    mostrarInventario();
                    break;
                case 3:
                    registrarVenta();
                    break;
                case 4:
                    actualizarInventario();
                    break;
                case 5:
                    proximosVencer();
                    break;
                case 6:
                    productosVencidos();
                    break;
                case 7:
                    calcularPerdidas();
                    break;
                case 8:
                    reporteVentas();
                    break;
                case 9:
                    System.out.println("Gracias por utilizar FoodSaver.");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }

        } while (opcion != 9);

        sc.close();
    }

    static int leerEntero() {
        while (!sc.hasNextInt()) {
            System.out.print("Ingrese un numero valido: ");
            sc.nextLine();
        }
        int numero = sc.nextInt();
        sc.nextLine();
        return numero;
    }

    static double leerDouble() {
        while (!sc.hasNextDouble()) {
            System.out.print("Ingrese un valor numerico: ");
            sc.nextLine();
        }
        double numero = sc.nextDouble();
        sc.nextLine();
        return numero;
    }

    static void registrarProducto() {

        if (totalProductos >= productos.length) {
            System.out.println("Inventario lleno.");
            return;
        }

        System.out.println("\n--- REGISTRAR PRODUCTO ---");

        System.out.print("Codigo: ");
        String codigo = sc.nextLine().trim();

        if (codigo.isEmpty() || buscarProducto(codigo) != -1) {
            System.out.println("Codigo vacio o ya existente.");
            return;
        }

        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("El nombre es obligatorio.");
            return;
        }

        System.out.print("Precio: ");
        double precio = leerDouble();

        if (precio <= 0) {
            System.out.println("El precio debe ser mayor que cero.");
            return;
        }

        System.out.print("Cantidad: ");
        int cantidad = leerEntero();

        if (cantidad < 0) {
            System.out.println("Cantidad no valida.");
            return;
        }

        System.out.println("Categoria:");
        System.out.println("1. Bebidas");
        System.out.println("2. Frutas y verduras");
        System.out.println("3. Lacteos");
        System.out.println("4. Panaderia");
        System.out.println("5. Otros");

        int opcion = leerEntero();

        String categoria;

        switch (opcion) {
            case 1:
                categoria = "Bebidas";
                break;
            case 2:
                categoria = "Frutas y verduras";
                break;
            case 3:
                categoria = "Lacteos";
                break;
            case 4:
                categoria = "Panaderia";
                break;
            case 5:
                categoria = "Otros";
                break;
            default:
                System.out.println("Categoria no valida.");
                return;
        }

        System.out.print("Fecha de vencimiento (dd/MM/yyyy): ");
        LocalDate fecha;

        try {
            fecha = LocalDate.parse(sc.nextLine(), formato);
        } catch (Exception e) {
            System.out.println("Fecha incorrecta.");
            return;
        }

        productos[totalProductos] = new Producto(
                codigo, nombre, precio, cantidad, categoria, fecha);

        totalProductos++;

        guardarMovimiento("REGISTRO", codigo, cantidad, "Producto nuevo");

        System.out.println("Producto registrado correctamente.");
    }

    static int buscarProducto(String codigo) {

        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].codigo.equalsIgnoreCase(codigo)) {
                return i;
            }
        }

        return -1;
    }

    static void mostrarInventario() {

        System.out.println("\n--- INVENTARIO GENERAL ---");

        if (totalProductos == 0) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (int i = 0; i < totalProductos; i++) {

            Producto p = productos[i];

            System.out.println("\nCodigo: " + p.codigo);
            System.out.println("Nombre: " + p.nombre);
            System.out.println("Categoria: " + p.categoria);
            System.out.println("Precio: $" + p.precio);
            System.out.println("Cantidad: " + p.cantidad);
            System.out.println("Vencimiento: " + p.vencimiento.format(formato));
            System.out.println("Estado: " + estadoProducto(p));
        }
    }

    static String estadoProducto(Producto p) {

        LocalDate hoy = LocalDate.now();

        if (p.vencimiento.isBefore(hoy)) {
            return "VENCIDO";
        } else if (!p.vencimiento.isAfter(hoy.plusDays(DIAS_ALERTA))) {
            return "PROXIMO A VENCER";
        } else {
            return "EN BUEN ESTADO";
        }
    }

    static void registrarVenta() {

        System.out.println("\n--- REGISTRAR VENTA ---");

        System.out.print("Codigo del producto: ");
        String codigo = sc.nextLine();

        int posicion = buscarProducto(codigo);

        if (posicion == -1) {
            System.out.println("Producto no encontrado.");
            return;
        }

        Producto p = productos[posicion];

        if (p.vencimiento.isBefore(LocalDate.now())) {
            System.out.println("Venta rechazada: producto vencido.");
            return;
        }

        System.out.println("Producto: " + p.nombre);
        System.out.println("Stock disponible: " + p.cantidad);

        System.out.print("Cantidad a vender: ");
        int cantidad = leerEntero();

        if (cantidad <= 0 || cantidad > p.cantidad) {
            System.out.println("Cantidad no valida o stock insuficiente.");
            return;
        }

        double total = p.precio * cantidad;

        p.cantidad -= cantidad;
        totalVendido += total;

        guardarMovimiento("VENTA", codigo, cantidad,
                "Total: $" + total);

        System.out.println("Venta realizada correctamente.");
        System.out.println("Total: $" + total);
        System.out.println("Stock restante: " + p.cantidad);
    }

    static void actualizarInventario() {

        System.out.println("\n--- ACTUALIZAR INVENTARIO ---");

        System.out.print("Codigo del producto: ");
        String codigo = sc.nextLine();

        int posicion = buscarProducto(codigo);

        if (posicion == -1) {
            System.out.println("Producto no encontrado.");
            return;
        }

        Producto p = productos[posicion];

        System.out.println("Producto: " + p.nombre);
        System.out.println("Cantidad actual: " + p.cantidad);

        System.out.print("Unidades que desea agregar: ");
        int cantidad = leerEntero();

        if (cantidad <= 0 || cantidad > 1000000 - p.cantidad) {
            System.out.println("Cantidad no valida.");
            return;
        }

        p.cantidad += cantidad;

        guardarMovimiento("ACTUALIZACION", codigo, cantidad,
                "Entrada de inventario");

        System.out.println("Inventario actualizado.");
        System.out.println("Nuevo stock: " + p.cantidad);
    }

    static void proximosVencer() {

        System.out.println("\n--- PRODUCTOS PROXIMOS A VENCER ---");

        int contador = 0;

        for (int i = 0; i < totalProductos; i++) {

            Producto p = productos[i];

            if (p.cantidad > 0 &&
                    !p.vencimiento.isBefore(LocalDate.now()) &&
                    !p.vencimiento.isAfter(LocalDate.now().plusDays(DIAS_ALERTA))) {

                System.out.println(p.nombre + " | Cantidad: " +
                        p.cantidad + " | Vence: " +
                        p.vencimiento.format(formato));

                contador++;
            }
        }

        System.out.println("Total de productos en alerta: " + contador);
    }

    static void productosVencidos() {

        System.out.println("\n--- PRODUCTOS VENCIDOS ---");

        int contador = 0;

        for (int i = 0; i < totalProductos; i++) {

            Producto p = productos[i];

            if (p.cantidad > 0 &&
                    p.vencimiento.isBefore(LocalDate.now())) {

                System.out.println(p.nombre + " | Cantidad: " +
                        p.cantidad + " | Vencio: " +
                        p.vencimiento.format(formato));

                contador++;
            }
        }

        System.out.println("Total de productos vencidos: " + contador);
    }

    static void calcularPerdidas() {

        System.out.println("\n--- PERDIDAS ECONOMICAS ---");

        double perdidas = 0;
        int unidades = 0;
        int contador = 0;

        for (int i = 0; i < totalProductos; i++) {

            Producto p = productos[i];

            if (p.cantidad > 0 &&
                    p.vencimiento.isBefore(LocalDate.now())) {

                double perdidaProducto = p.precio * p.cantidad;

                perdidas += perdidaProducto;
                unidades += p.cantidad;
                contador++;

                System.out.println(p.nombre + ": $" +
                        perdidaProducto);
            }
        }

        System.out.println("----------------------------");
        System.out.println("Productos vencidos: " + contador);
        System.out.println("Unidades desperdiciadas: " + unidades);
        System.out.println("PERDIDA TOTAL: $" + perdidas);
    }

    static void reporteVentas() {

        System.out.println("\n--- REPORTE DE VENTAS DEL DIA ---");

        double acumulado = 0;
        int contador = 0;

        LocalDate hoy = LocalDate.now();

        for (int i = 0; i < totalMovimientos; i++) {

            if (movimientos[i][0].equals("VENTA") &&
                    movimientos[i][4].equals(hoy.toString())) {

                System.out.println("Codigo: " + movimientos[i][1]);
                System.out.println("Cantidad: " + movimientos[i][2]);
                System.out.println(movimientos[i][3]);

                String valor = movimientos[i][3].replace("Total: $", "");
                acumulado += Double.parseDouble(valor);
                contador++;
            }
        }

        System.out.println("----------------------------");
        System.out.println("Numero de ventas: " + contador);
        System.out.println("Total vendido hoy: $" + acumulado);
    }

    static void guardarMovimiento(String tipo, String codigo,
            int cantidad, String detalle) {

        if (totalMovimientos < movimientos.length) {

            movimientos[totalMovimientos][0] = tipo;
            movimientos[totalMovimientos][1] = codigo;
            movimientos[totalMovimientos][2] = String.valueOf(cantidad);
            movimientos[totalMovimientos][3] = detalle;
            movimientos[totalMovimientos][4] = LocalDate.now().toString();

            totalMovimientos++;
        }
    }
}