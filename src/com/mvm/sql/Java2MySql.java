/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mvm.sql;

import com.mvm.games.records.Record;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Administrator
 */
public class Java2MySql {
    String url = "jdbc:mariabd://localhost:3306/"; 
    String dbName = "rubik"; 
    String driver = "org.mariadb.jdbc.Driver"; 
    
    String userName = "root"; 
    String password = "rubik"; 
        
    public void connection(){
        
        try { 
            Class.forName(driver).newInstance(); 
            Connection conn = DriverManager.getConnection(url+dbName,userName,password); 
            conn.close(); 
        } catch (Exception e) { 
            e.printStackTrace(); }

    }
    
    public void data(Record rec){
        
    }
            
}
