/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conexion;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

/**
 *
 * @author jimen
 */
public class DB {
    private Connection conexion;
    public final double HWT = 0.80;
    public int NumTs;
    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    public DB Conectar() {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Properties info = new Properties();
            info.setProperty("user", "niva");
            info.setProperty("password", "niva");
            //info.setProperty("internal_logon","sysdba");
            conexion = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", info);
            if (conexion != null) {
                System.out.println("Conexion exitosa a esquema HR");
            } else {
                System.out.println("Conexion fallida");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    public void query(String query) {
        Statement st;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            st = conexion.createStatement();
            boolean execute = st.execute(query); // = conexion.prepareCall(query);
            //cs.execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    


    public String[] getTableSpace_name() {
        Statement ts;
        ResultSet rs;
        int size= getNumTS();
        String[] names = new String[size];
        Conectar();
        CallableStatement cs = null;
        try {
            ts = conexion.createStatement();
            rs = ts.executeQuery("select tablespace_name from dba_tablespaces order by(tablespace_name)");
            
            int i = 0;
            while(rs.next()){
                names[i] = rs.getString("tablespace_name");
                System.out.println(rs.getString("tablespace_name"));
                i++;
            }
            conexion.close();
            return names;
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
        return null;
    }

    public int getNumTS(){
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            ts = conexion.createStatement();
            rs = ts.executeQuery("SELECT COUNT(tablespace_name) ax FROM dba_tablespaces");
            rs.next();
            NumTs = rs.getInt("ax");
            conexion.close();
            return NumTs;
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
        return 0;
    }
    
    public void getDays(){
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            ts = conexion.createStatement();
            rs = ts.executeQuery("select Round(Monitor('SYSAUX',1)*24,2) ax from dual");
            rs.next();
            double tmp = rs.getDouble("ax");
            System.out.println("********"+tmp);
            System.out.println("Conexion.DB.getDays()");
            conexion.close();
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
    }
    
    public double[][] getSize(){
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try{
            String sql = "{call getMemory(?)}";
            cs = conexion.prepareCall(sql);
            //cs.registerOutParameter(1, OracleTypes.FLOAT);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs.executeQuery();
            rs= (ResultSet) cs.getObject(1);
            //System.out.println(rs.getFloat(1));
            //return Double.valueOf(String.valueOf(rs.getFloat(1)));
            
            double[][] data = new double[][]{
                new double[NumTs],
                new double[NumTs],
                new double[NumTs],
            };
            for (int i = 0; rs.next(); i++) {
                if(i == 2)
                    i++;
                data[0][i] = rs.getDouble(4);
                
                double topeA = rs.getDouble(4) * HWT;
                if(rs.getDouble(2) > topeA){
                    data[1][i] = topeA;
                    data[2][i] = rs.getDouble(2) - topeA;
                }
                else
                    data[1][i] = rs.getDouble(2);
            }
            return data;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    } 
}



