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
    public final double HWT = 0.99;
    public int NumTs;
    public int temp;
    public ArrayList<Object> freeMem = new ArrayList<>();

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
            info.setProperty("user", "maikol");
            info.setProperty("password", "maikol");
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
        int size = getNumTS();
        String[] names = new String[size];
        Conectar();
        CallableStatement cs = null;
        try {
            ts = conexion.createStatement();
            rs = ts.executeQuery("select tablespace_name from dba_tablespaces order by(tablespace_name)");

            int i = 0;
            while (rs.next()) {
                names[i] = rs.getString("tablespace_name");
                if ("TEMP".equals(names[i])) {
                    temp = i;
                }
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

    public int getNumTS() {
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

    public void getDays() {
        for (int i = 0; i < freeMem.size(); i++) {
            ArrayList<Object> a1 = (ArrayList<Object>) freeMem.get(i);
            System.out.println(a1.get(0));
            System.out.println(a1.get(1));
        }
        Statement ts;
        ResultSet rs;
        Conectar();
        CallableStatement cs = null;
        try {
            for (int i = 0; i < freeMem.size(); i++) {
                ArrayList<Object> a1 = (ArrayList<Object>) freeMem.get(i);
                if (!a1.get(0).equals("UNDOTBS1")) {
                    ts = conexion.createStatement();
                    double ax = (Double)a1.get(1)*1024*1024;
                    rs = ts.executeQuery("select (Monitor('" + a1.get(0) + "',10," + ax + ")) ax from dual");
                    rs.next();
                    double tmp = rs.getDouble("ax");
                    System.out.println(a1.get(0)+ ": " + tmp);
                }
            }
            conexion.close();
        } catch (SQLException e) {
            System.err.print(e.getMessage());
        }
    }

    public double[][] getSize() {
        ResultSet rs;
        ResultSet rs1;
        Conectar();
        CallableStatement cs = null;
        CallableStatement cs1 = null;
        try {
            String sql = "{call getMemory(?)}";
            String sql1 = "{call Getmemorytemp(?)}";
            cs = conexion.prepareCall(sql);
            cs1 = conexion.prepareCall(sql1);
            cs.registerOutParameter(1, OracleTypes.CURSOR);
            cs1.registerOutParameter(1, OracleTypes.CURSOR);
            cs.executeQuery();
            cs1.executeQuery();
            rs = (ResultSet) cs.getObject(1);
            rs1 = (ResultSet) cs1.getObject(1);

            double[][] data = new double[][]{
                new double[NumTs],
                new double[NumTs],
                new double[NumTs],};
            for (int i = 0; i < 5; i++) {
                if (i == temp) {
                    rs1.next();
                    double usado = rs1.getDouble(2) - rs1.getDouble(3);
                    data[0][i] = usado;

                    double topeA1 = rs1.getDouble(2) * HWT;
                    if (usado > topeA1) {
                        data[2][i] = rs1.getDouble(2) - usado;
                    } else {
                        double a = rs1.getDouble(2) - topeA1;
                        data[1][i] = rs1.getDouble(3) - a;
                        data[2][i] = a;
                    }
                } else {
                    rs.next();
                    data[0][i] = rs.getDouble(2);

                    double topeA = rs.getDouble(4) * HWT;
                    if (rs.getDouble(2) > topeA) {
                        //data[1][i] = topeA - rs.getDouble(2) ;
                        data[2][i] = rs.getDouble(4) - rs.getDouble(2);
                        ArrayList<Object> a11 = new ArrayList<>();
                        a11.add(rs.getString(1));
                        a11.add(0);
                        freeMem.add(a11);

                    } else {
                        double a = rs.getDouble(4) - topeA;
                        data[1][i] = rs.getDouble(3) - a;

                        ArrayList<Object> a11 = new ArrayList<>();
                        a11.add(rs.getString(1));
                        a11.add(rs.getDouble(3) - a);
                        freeMem.add(a11);

                        data[2][i] = a;
                    }
                }
            }
            getDays();
            return data;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
