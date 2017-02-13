package org.filteredpush.kuration.util;

import java.sql.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cobalt
 * Date: 02.10.2013
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
public class GeoRefDBCache implements Cache {

    String sTable = "GeoRef";
    Connection conn = null;
    PreparedStatement ps = null;
    PreparedStatement ps2 = null;

    public GeoRefDBCache() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost/FPCache?" +
                       "user=fpcache&password=fpc4ch3");
            ps = conn.prepareStatement("SELECT answer FROM GeoRef WHERE Country = ? AND " +
                                                    "State = ? AND " +
                                                    "County = ? AND " +
                                                    "Locality = ?");

            ps2 = conn.prepareStatement("INSERT into GeoRef (Country, State, County, Locality, answer) " +
                                            "VALUES (?,?,?,?,?)");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public String lookup(List<String> key) {
        ResultSet rs = null;
        try {
            for (int i = 0; i < key.size(); i++) {
                ps.setString(i+1,key.get(i));
            }
            boolean hadResults = ps.execute();
            if (hadResults) {
                rs = ps.getResultSet();
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
        }
        return null;
    }

    @Override
    public void insert(List<String> entry) {
        try {
            for (int i = 0; i < entry.size(); i++) {
                ps2.setString(i+1,entry.get(i));
            }
            ps.execute();
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
