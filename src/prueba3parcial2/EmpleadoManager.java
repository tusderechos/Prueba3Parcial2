/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prueba3parcial2;

/**
 *
 * @author USUARIO
 */

import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class EmpleadoManager {
    
    private RandomAccessFile rcods, remps;

    public EmpleadoManager() {
        try {
            File mf = new File("company");
            mf.mkdir();

            rcods = new RandomAccessFile("company/codigos.emp", "rw");
            remps = new RandomAccessFile("company/empleados.emp", "rw");

            initCode();
            
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
    
    private void initCode() throws IOException {
        if(rcods.length() == 0){
            rcods.writeInt(1);
        }
    }
    
    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code+1);
        return code;
    } 
    
    public void addEmployee(String nombre, double salario)throws IOException{
        remps.seek(remps.length());
        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(nombre);
        remps.writeDouble(salario);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        
        //crear folder propio del empleado
        createEmployeeFolder(code);
        
    }
    
    private String employeeFolder(int code){
        return "company/empleado.emp"+code;
    }
    
    //crear el archivo de ventas
    private RandomAccessFile salesFileFor(int code) throws IOException{
        String dirPadre = employeeFolder(code); //con esto damos a entender cual es la direccion, y asi se guardarian las ventas de cada empleado
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String dir = dirPadre+"/ventas"+year+".emp"; //formato de la carpeta de ventas
        return new RandomAccessFile(dir, "rw");
    }
    
    /*FORMATO ARCHIVO VENTAS:
    VentasYear.emp
    double saldo
    boolean estadoPago - por mes*/
    private void createYearSaleFileFor(int code)throws IOException{
        RandomAccessFile rventas = salesFileFor(code);
        if (rventas.length()==0) { //validamos que el archivo este vacio
            for (int mes = 0; mes < 12; mes++) {
                rventas.writeDouble(0); //inicializamos que cada mes contenga 0
                rventas.writeBoolean(false);
            }
        }
    }
    
    private void createEmployeeFolder(int code)throws IOException{
        //crear el folder y el archivo de las ventas del aÃ±o
        File dir = new File(employeeFolder(code));
        dir.mkdir();
        createYearSaleFileFor(code);
    }
    
    public void employeeList()throws IOException{
        remps.seek(0); //para buscar lo que estamos, en este caso serian los empleados
        while(remps.getFilePointer()<remps.length()){
            int code = remps.readInt();
            String nombre = remps.readUTF();
            double salario = remps.readDouble();
            Date fecha = new Date(remps.readLong());
            if(remps.readLong()==0){
                System.out.println(nombre+" | "+code+" | "+salario+": $ | "+fecha);
            }
        }
    } 
    
    //validamos si esta activo el empleado
    private boolean isEmployeeActive(int code)throws IOException{
        remps.seek(0);
        while(remps.getFilePointer()<remps.length()){
            int codigo = remps.readInt();
            long pos = remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(16);
            if(remps.readLong()==0 && codigo==code){
                remps.seek(pos);
                return true;
            }
        }
        return false;
    }
    
    //despedir al empleado
    public boolean fireEmployee(int code)throws IOException{
        if(isEmployeeActive(code)){
            String nombre = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a: "+nombre);
            return true;
        }
        return false;
    }
    
    //agregarle ventas al empleado
    public void addSaleTo(int code, double venta)throws IOException{
        if(isEmployeeActive(code)){
            RandomAccessFile sales = salesFileFor(code);
            int pos = Calendar.getInstance().get(Calendar.MONTH)*9;
            sales.seek(pos);
            double monto = sales.readDouble();
            sales.seek(pos);
            sales.writeDouble(monto+venta);
            System.out.println("Venta de: $"+venta+" agregada.");
        }
    }
    
    
}
