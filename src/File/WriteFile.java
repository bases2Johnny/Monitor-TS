package File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Date;
import java.util.ArrayList;

public class WriteFile {

    public static ArrayList<Transaccion> read() {
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<Transaccion> trans = new ArrayList();
        try {
            archivo = new File("C:\\Users\\maiko\\Documents\\GitHub\\Monitor-TS\\transacciones\\transacciones\\dist\\Transacciones.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            String linea;
            String[] l;
            String ts = null;
            int cantRows = 0;
            Date fecha = null;
            int cantRowsDia = 0;
            Transaccion t;

            while ((linea = br.readLine()) != null) {
                l = linea.split(";");
                ts = l[0];
                fecha = Date.valueOf(l[1]);
                cantRows = Integer.valueOf(l[2]);
                cantRowsDia = Integer.valueOf(l[3]);
                t = new Transaccion(fecha, cantRows, cantRowsDia, ts);
                trans.add(t);
                System.out.print(t);
            }
                
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trans;
    }
}
