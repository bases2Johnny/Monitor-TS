package File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

public class WriteFile {

    public static ArrayList<Transaccion> read() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<Transaccion> trans = new ArrayList();
        try {
            archivo = new File("./transacciones/transacciones/Transacciones.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String linea;
            String[] l;
            String ts = null;
            int cantRows = 0;
            int cantRowsDia = 0;
            Long tam = br.lines().count();
            //archivo = new File("./Transacciones.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            Transaccion t;
            String next;
            Integer j=1;
            linea = br.readLine();
            while (linea != null) {
                if(j == Integer.parseInt(tam.toString())){
                    String[] tableSpaces = linea.split(",");
                    for(int i=0; i< tableSpaces.length; i++){
                        String[] informacion = tableSpaces[i].split(";");
                        String nombre = informacion[0];
                        Date fecha = Date.valueOf(informacion[1]);
                        Integer rows = Integer.valueOf(informacion[2]);
                        Integer rowsD = Integer.valueOf(informacion[3]);
                        t= new Transaccion(fecha, rows, rowsD, nombre);
                        trans.add(t);
                    }
                }
                linea = br.readLine();
                j++;
//                l = linea.split(";");
//                ts = l[0];
////                fecha = Date.valueOf(l[1]);
//                cantRows = Integer.valueOf(l[2]);
//                cantRowsDia = Integer.valueOf(l[3]);
                
                
                //System.out.print(t);
            }
                
        } catch (IOException | NumberFormatException e) {
            System.err.println(e.getMessage());
        }
        return trans;
    }
}
