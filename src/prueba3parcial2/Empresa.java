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
        Scanner lea = new Scanner(System.in);
        int opc = 0;

        do {
            System.out.println("********MENU PRINCIPAL********");
            System.out.println("1- Agregar empleado");
            System.out.println("2- Listar empleados NO despedidos");
            System.out.println("3- Agregar venta de empleado");
            System.out.println("4- Pagar empleado");
            System.out.println("5- Despedir empleado");
            System.out.println("6- Salir");
            System.out.println("Escoja una opcion:");

        } while (opc != 6);

    }
}