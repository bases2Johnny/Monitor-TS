/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transacciones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;

/**
 *
 * @author Admin
 */
public class WriteFile {

    public void write(Transaccion t) {

        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter out = null;
        try {
            fw = new FileWriter("Transacciones.txt", true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.println(t.getTablespace() + ";" + t.getFecha() + ";" + t.getCantRows() + ";" + t.getCantRowsDia());
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        } finally {
            if (out != null) {
                out.close();
            } //exception handling left as an exercise for the reader
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        }
    }

    public static void read() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            archivo = new File("Transacciones.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String linea;
            String[] l;
            String ts=null;
            int cantRows=0;
            Date fecha= null;
            int cantRowsDia=0;
            Transaccion t;
            while ((linea = br.readLine()) != null) {
                l = linea.split(";");
                ts = l[0];
                fecha = Date.valueOf(l[1]);
                cantRows = Integer.valueOf(l[2]);
                cantRowsDia = Integer.valueOf(l[3]);
                t = new Transaccion(fecha,cantRows,cantRowsDia,ts);
                System.out.print(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
