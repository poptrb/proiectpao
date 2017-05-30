/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import com.mysql.jdbc.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
/**
 *
 * @author Iulian
 */
public class dbchestionar {
    public static Connection con = null;
    static String url = "jdbc:mysql://localhost:3306/javabase";
    static String username = "root";
    static String password = "proiectpao";
    
    public void createChestionar (String numeChest, Integer nrIntrebari, ArrayList<String>
            Intrebari, ArrayList<String> Rasp, ArrayList<Integer> raspcorect) throws SQLException 
    {
        
            con = (Connection) DriverManager.getConnection(url, username, password);
            DatabaseMetaData dbm = con.getMetaData();
                // verifica daca chestionarul deja exista
            ResultSet tables = dbm.getTables(null, null, numeChest, null);
            if (tables.next()) {
                 // Chestionarul exista
                 System.out.println("Chestionarul deja exista!");
                 String message = "Chestionarul deja exista!";
                    JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
                    JOptionPane.ERROR_MESSAGE);
                 con.close(); return;
                }
            else {
       
            try{
            
            String create = "CREATE TABLE " + numeChest +" (CorpIntrebare varchar (512), RaspCorect int, Rasp1 varchar(255), Rasp2 varchar(255), Rasp3 varchar(255))";
            String intrebare = "INSERT INTO " + numeChest +" (CorpIntrebare, RaspCorect, Rasp1, Rasp2, Rasp3) VALUES (?,?,?,?,?)";
            //String raspuns ="ALTER TABLE ? ADD "
            //String check = "SELECT * FROM Users WHERE LastName = ?"
            PreparedStatement ps1=null;
            PreparedStatement ps2=null;
            ResultSet rs = null;
            ps1=con.prepareStatement(create);
            ps2=con.prepareStatement(intrebare);
            ps1.executeUpdate();
            int j=0; //un contor pentru raspunsuri
            for (int i=0; i<nrIntrebari; i++)
            {
               ps2.setString(1,Intrebari.get(i)); 
               ps2.setInt(2,raspcorect.get(i)); 
               ps2.setString(3,Rasp.get(j));
               ps2.setString(4,Rasp.get(j+1));
               ps2.setString(5,Rasp.get(j+2));
               j=j+3;
               ps2.executeUpdate();
            }
            con.close();
            }
            catch (SQLException e) { 
        System.out.println(e);
        System.out.println("Actualizare esuata!");
            }
        }    
    }
    
    static ArrayList<String> chestlist; 
    static ArrayList<String> reslist;
    static String res=null;
    public static ArrayList<String> viewChestionar(String numeChest) throws SQLException 
    {
        try{
            res=null;
            con = (Connection) DriverManager.getConnection(url, username, password);
            String view = "SELECT * FROM "+numeChest;
            PreparedStatement ps1=null;
            ResultSet rs = null;
            reslist = new ArrayList<String>();
            ps1=con.prepareStatement(view);
            rs=ps1.executeQuery();
            while (rs.next()) 
            {
                String CorpIntrebare = rs.getString(1);
                Integer Rasp = rs.getInt(2);
                String raspunsuri[] = new String [4];
                raspunsuri[1]=rs.getString(3);
                raspunsuri[2]=rs.getString(4);
                raspunsuri[3]=rs.getString(5);
                reslist.add(numeChest);
                reslist.add(rs.getString(1));
                reslist.add(Rasp.toString());
                reslist.add(rs.getString(3));
                reslist.add(rs.getString(4));
                reslist.add(rs.getString(5));
                
            }
            
            return reslist;
        }
         catch (SQLException e) { 
        System.out.println(e);
        System.out.println("Afisare esuata!");
        ResultSet rs = null;
        return reslist;
    }
        
    }
    public void deleteChestionar(String numeChest) throws SQLException 
    {
        try{
            con = (Connection) DriverManager.getConnection(url, username, password);
            String delete = "DROP TABLE "+numeChest;
            PreparedStatement ps1=null;
            ps1=con.prepareStatement(delete);
            ps1.executeUpdate();
            con.close();
        }
        catch (SQLException e) {
            con.close(); System.out.println("Chestionarul de sters nu exista!");
        }
    }
    public static ArrayList<String> viewTables() throws SQLException 
    {
        //afiseaza toate chestionarele
        try{
            con = (Connection) DriverManager.getConnection(url, username, password);
            String view = "SHOW tables";
            ResultSet rs = null;
            Statement stmt = con.createStatement();;
            rs=stmt.executeQuery(view);
            reslist = new ArrayList<String>();
            int i=0;
            DatabaseMetaData meta = con.getMetaData();
            ResultSet res = meta.getTables(null, null, null, 
            new String[] {"TABLE"});
            while(res.next())
            {
        
                reslist.add(res.getString("TABLE_NAME"));
                //System.out.print(reslist.get(i)+"\t");
                i++;
            }
            con.close();
            return reslist;
        }
        catch (SQLException e) {
            con.close(); 
            
        }
        return reslist;
    }
    
    
}
