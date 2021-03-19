package com.oracle.tutorial.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyQueries2 {
  
  Connection con;
  JDBCUtilities settings;  
  
  public MyQueries2(Connection connArg, JDBCUtilities settingsArg) {
    this.con = connArg;
    this.settings = settingsArg;
  }

  public static void getMyData(Connection con) throws SQLException {
    Statement stmt = null;
    String query =
      "SELECT NOME_CLIENTE, SUM(SALDO_DEPOSITO) AS SOMA " +
      "FROM DEPOSITO " +
      "GROUP BY NOME_CLIENTE";

    try {
      stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      System.out.println("Lista de clientes: \t Saldo depositos");
      while (rs.next()) {
        String cliente = rs.getString("NOME_CLIENTE");
        float sum_depositos = rs.getFloat("SOMA");
        System.out.println(cliente + " \t" + sum_depositos);
      }
    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      if (stmt != null) { stmt.close(); }
    }
  }


  public static void main(String[] args) {
    JDBCUtilities myJDBCUtilities;
    Connection myConnection = null;
    if (args[0] == null) {
      System.err.println("Properties file not specified at command line");
      return;
    } else {
      try {
        myJDBCUtilities = new JDBCUtilities(args[0]);
      } catch (Exception e) {
        System.err.println("Problem reading properties file " + args[0]);
        e.printStackTrace();
        return;
      }
    }

    try {
      myConnection = myJDBCUtilities.getConnection();

 	    MyQueries2.getMyData(myConnection);

    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      JDBCUtilities.closeConnection(myConnection);
    }

  }
}
