/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba3parcial2;

/**
 *
 * @author USUARIO
 */

import java.util.Scanner;

public class Empresa {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        EmpleadoManager manager = new EmpleadoManager();
        int opcion = 0;

        do {
            System.out.println("**** MENU PRINCIPAL ****");
            System.out.println("1- Agregar empleado");
            System.out.println("2- Listar empleados NO despedidos");
            System.out.println("3- Agregar venta de empleado");
            System.out.println("4- Pagar empleado");
            System.out.println("5- Despedir empleado");
            System.out.println("6- Reporte de empleado");
            System.out.println("7- Salir");
            System.out.println("Escoja una opcion: ");
            opcion = scanner.nextInt();
            
            try {
                switch(opcion){
                    case 1:
                        System.out.println("Nombre: ");
                        String nombre = scanner.next();
                        
                        System.out.println("Salario: ");
                        double salario = scanner.nextDouble();
                        
                        manager.addEmployee(nombre, salario);
                        break;
                        
                    case 2:
                        manager.employeeList();
                        break;
                        
                    case 3:
                        System.out.println("Codigo del empleado: ");
                        int codeVenta = scanner.nextInt();
                        
                        System.out.println("Monto de venta: ");
                        double venta = scanner.nextDouble();
                        
                        manager.addSaleToEmployee(codeVenta, venta);
                        break;
                        
                    case 4:
                        System.out.println("Codigo del empleado: ");
                        int codePago = scanner.nextInt();
                        
                        manager.payEmployee(codePago);
                        break;
                        
                    case 5:
                        System.out.println("Codigo del empleado: ");
                        int codeDespido = scanner.nextInt();
                        
                        manager.fireEmployee(codeDespido);
                        break;
                    
                    case 6:
                        System.out.println("Codigo del empleado: ");
                        int codeReporte = scanner.nextInt();
                        
                        manager.printEmployee(codeReporte);
                        break;
                        
                    case 7:
                        System.out.println("Saliendo...");
                        break;
                        
                    default:
                        System.out.println("Opcion invalida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (opcion != 7);

    }
}
