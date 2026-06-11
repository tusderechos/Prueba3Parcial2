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
import java.text.SimpleDateFormat;
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
        if (rcods.length() == 0)
            rcods.writeInt(1);
    }
    
    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code + 1);
        
        return code;
    } 
    
    public void addEmployee(String nombre, double salario) throws IOException {
        remps.seek(remps.length());
        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(nombre);
        remps.writeDouble(salario);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        
        createEmployeeFolder(code);
    }
    
    private String employeeFolder(int code) {
        return "company/empleado.emp" + code;
    }
    
    private RandomAccessFile salesFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        File folder = new File(dirPadre);
        
        folder.mkdirs();
        
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String dir = dirPadre + "/ventas" + year + ".emp";
        RandomAccessFile sales = new RandomAccessFile(dir, "rw");
        
        if (sales.length() == 0) {
            for (int mes = 0; mes < 12; mes++) {
                sales.writeDouble(0);
                sales.writeBoolean(false);
            }
        }
        
        sales.seek(0);
        return sales;
    }
    
    private void createYearSaleFileFor(int code) throws IOException {
        RandomAccessFile rventas = salesFileFor(code);
        rventas.close();
    }
    
    private void createEmployeeFolder(int code) throws IOException {
        File dir = new File(employeeFolder(code));
        dir.mkdir();
        createYearSaleFileFor(code);
    }
    
    public void employeeList() throws IOException {
        remps.seek(0);
        
        while (remps.getFilePointer() < remps.length()) {
            int code = remps.readInt();
            String nombre = remps.readUTF();
            double salario = remps.readDouble();
            Date fecha = new Date(remps.readLong());
            
            if (remps.readLong() == 0)
                System.out.println(nombre + " | " + code + " | " + salario + ": $ | " + fecha);
        }
    }
    
    private boolean isEmployeeActive(int code) throws IOException {
        remps.seek(0);
        
        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            long pos = remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(16);
            
            if(remps.readLong() == 0 && codigo == code) {
                remps.seek(pos);
                return true;
            }
        }
        return false;
    }
    
    private boolean findEmployee(int code) throws IOException {
        remps.seek(0);
        
        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            long pos = remps.getFilePointer();
            
            remps.readUTF();
            remps.skipBytes(24);
            
            if (codigo == code) {
                remps.seek(pos);
                return true;
            }
        }
        return false;
    }
    
    public boolean fireEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) {
            String nombre = remps.readUTF();
            
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            
            System.out.println("Despidiendo a: " + nombre);
            return true;
        }
        
        return false;
    }
    
    public void addSaleTo(int code, double venta) throws IOException {
        addSaleToEmployee(code, venta);
    }
    
    public void addSaleToEmployee(int code, double monto) throws IOException {
        if (isEmployeeActive(code)) {
            RandomAccessFile sales = salesFileFor(code);
            int pos = Calendar.getInstance().get(Calendar.MONTH) * 9;
            
            sales.seek(pos);
            
            double ventasActuales = sales.readDouble();
            sales.seek(pos);
            
            sales.writeDouble(ventasActuales+monto);
            System.out.println("Venta de: $" + monto + " agregada");
        } else
            System.out.println("Empleado no existe o esta despedido");
    }
    
    public RandomAccessFile billsFilefor(int code) throws IOException {
        File folder = new File(employeeFolder(code));
        folder.mkdirs();
        String dir = employeeFolder(code) + "/recibos.emp";
        
        return new RandomAccessFile(dir, "rw");
    }
    
    public boolean isEmployeePayed(int code) throws IOException {
        if (!isEmployeeActive(code))
            return false;
        
        RandomAccessFile sales = salesFileFor(code);
        int pos = Calendar.getInstance().get(Calendar.MONTH) * 9;
        
        sales.seek(pos);
        sales.skipBytes(8);
        
        boolean pagado = sales.readBoolean();
        sales.close();
        
        return pagado;
    }
    
    public void payEmployee(int code) throws IOException {
        if (!isEmployeeActive(code) || isEmployeePayed(code)) {
            System.out.println("No se pudo pagar");
            return;
        }
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        
        RandomAccessFile sales = salesFileFor(code);
        int pos = month * 9;
        
        sales.seek(pos);
        double ventas = sales.readDouble();
        
        String nombre = "";
        double salarioBase = 0;
        
        if (findEmployee(code)) {
            nombre = remps.readUTF();
            salarioBase = remps.readDouble();
        }
        
        double sueldo = salarioBase + (ventas * 0.10);
        double deduccion = sueldo * 0.035;
        double total = sueldo - deduccion;
        
        RandomAccessFile bills = billsFilefor(code);
        bills.seek(bills.length());
        bills.writeLong(cal.getTimeInMillis());
        bills.writeDouble(sueldo);
        bills.writeDouble(deduccion);
        bills.writeInt(year);
        bills.writeInt(month + 1);
        
        sales.seek(pos + 8);
        sales.writeBoolean(true);
        
        System.out.println("Empleado " + nombre + " se le pago Lps. " + total);
    }
    
    public void printEmployee(int code) throws IOException {
        if( !findEmployee(code)) {
            System.out.println("Empleado no encontrado");
            return;
        }
        
        String nombre = remps.readUTF();
        double salario = remps.readDouble();
        Date fecha = new Date(remps.readLong());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        System.out.println("Codigo: " + code);
        System.out.println("Nombre: " + nombre);
        System.out.println("Salario: " + salario);
        System.out.println("Fecha de contratacion: " + sdf.format(fecha));
        
        RandomAccessFile sales = salesFileFor(code);
        double totalVentas = 0;
        
        for (int mes = 1; mes <= 12; mes++) {
            double ventas = sales.readDouble();
            sales.readBoolean();
            totalVentas += ventas;
            System.out.println("Mes " + mes + " : " + ventas);
        }
        
        System.out.println("Total de ventas del año: " + totalVentas);
        
        RandomAccessFile bills = billsFilefor(code);
        long recibos = bills.length() / 32;
        System.out.println("Total de pagos realizados: " + recibos);
    }
    
}
