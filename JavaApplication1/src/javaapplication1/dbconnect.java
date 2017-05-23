/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Iulian
 */
public class dbconnect {
    public static Connection con = null;
    static String url = "jdbc:mysql://localhost:3306/javabase";
    static    String username = "root";
    static     String password = "proiectpao";
    public static Connection connect() {
        
    System.out.println("Connecting database...");

    try (Connection connection = (Connection) DriverManager.getConnection(url, username, password)) {
        System.out.println("Database connected!");
        con = connection;
        } 
        
    catch (SQLException e) {
        throw new IllegalStateException("Cannot connect the database!", e);
        }
        return con;
    }
    
    public static int viewUser() throws SQLException {

    Statement stmt = null;
    ResultSet rs  = null;
    int nrutilz=0;
    String query ="SELECT PersonID, LastName, Parola FROM Users";
    try {
        con = (Connection) DriverManager.getConnection(url, username, password);
        stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        if (rs.absolute(1)== false)
        {
          
        }
        while (rs.next()) {
            int ID =rs.getInt(1);
            String nume = rs.getString(2);
            String parola = rs.getString(3);
            System.out.println(nume + "\t" + parola +"\t");
            nrutilz++; 
        }
        rs.close();
    } catch (SQLException e ) {
        //JDBCTutorialUtilities.printSQLException(e);
    } finally {
        if (stmt != null) { stmt.close(); }
    }
    return nrutilz;
    
}
    public static void createUser(String util, String parola) throws SQLException {
        //creaza un utilizator nou
        PreparedStatement ps = null;
        PreparedStatement ps1=null;
        ResultSet rs = null;
        try{
        con = (Connection) DriverManager.getConnection(url, username, password);
        String insert ="INSERT INTO Users (PersonID, LastName, Parola) VALUES (?, ? ,?)";
        String check="SELECT * FROM Users WHERE LastName = ?";
        //testeaza daca utilizatorul deja exista
        ps1=con.prepareStatement(check);
        ps1.setString(1,util);
        rs=ps1.executeQuery();
        if(rs.absolute(1)) {
            System.out.println("utilizatorul deja exista!");
            con.close();
            return;
        }
        else {
            md5crypt parolaCriptata = new md5crypt();
            String parolaFinal=parolaCriptata.crypt(parola);
            int nr=viewUser()+1;
            ps=con.prepareStatement(insert);
            ps.setInt(1,nr);
            ps.setString(2, util);
            ps.setString(3, parolaFinal);
            ps.executeUpdate();  
        }
        }
        catch (SQLException e) { 
            System.out.println(e);
            System.out.println("Actualizare esuata!");
        }
       
        
    }
    public static void deleteUser(String util) throws SQLException {
        //creaza un utilizator nou
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        try
        {
            con = (Connection) DriverManager.getConnection(url, username, password);
            String delete ="DELETE FROM Users WHERE LastName = ?";
            String check="SELECT * FROM Users WHERE LastName = ?";
            ps1=con.prepareStatement(check);
            ps1.setString(1,util);
            rs=ps1.executeQuery();
            if (!(rs.absolute(1))) 
            {
                System.out.println("user inexistent!");
                con.close();
                return;
            }
            else 
            {
                ps=con.prepareStatement(delete);
                ps.setString(1, util);
                ps.executeUpdate();
                con.close();
            }
        }
        catch (SQLException e) { 
            //con.rollback();
            System.out.println(e);
            System.out.println("Stergere esuata!");
        }
    }
    public static boolean checkUser(String util, String parola) throws SQLException {
        //verifica daca exista utilizatorul in bd. se foloseste la logare
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            con = (Connection) DriverManager.getConnection(url, username, password);
            String check="SELECT * FROM Users WHERE (LastName = ?) AND (Parola = ?)";
            ps=con.prepareStatement(check);
            
            md5crypt parolaCriptata = new md5crypt();
            String parolaFinal=parolaCriptata.crypt(parola);
            ps.setString(1,util); ps.setString(2,parolaFinal);
            rs=ps.executeQuery();
            while (rs.next()) {
                String nume = rs.getString(2);
                String pass = rs.getString(3);       
                if (parolaFinal.equals(pass) && nume.equals(util))
                {con.close(); return true; }   
            }
            return false;
        }
        catch (SQLException e) { 
            System.out.println(e);
            System.out.println("Nu s-a putut face interogarea.");
            con.close();
            return false;
        }
        
        
    }
     public static boolean adminLogin(String util, String parola){
         if (util.equals(username) && parola.equals(password))
             return true;
         return false;
     }
    public void populareTabel (javax.swing.JTable TabelDate) {
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            con = (Connection) DriverManager.getConnection(url, username, password);
            rs = stmt.executeQuery("SELECT * FROM Users");
            
            DefaultTableModel tm = (DefaultTableModel) TabelDate.getModel();
            
            //stergem toate liniile actuale din JTable
            while (tm.getRowCount() > 0)
                tm.removeRow(0);

            //determinam numarul de coloane
            int nr_col = rs.getMetaData().getColumnCount();
            while (rs.next())
            {
                Object[] objects = new Object[nr_col];
                for (int i = 0; i < nr_col; i++)
                    objects[i] = rs.getObject(i + 1);
                tm.addRow(objects);
            }
            rs.close();
        } 
        catch (SQLException ex)
        {
            System.out.println("Eroare la executarea interogarii!");
        }
    }
}


