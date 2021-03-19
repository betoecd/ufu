package com.oracle.tutorial.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyQueries {
  
  Connection con;
  JDBCUtilities settings;  
  
  public MyQueries(Connection connArg, JDBCUtilities settingsArg) {
    this.con = connArg;
    this.settings = settingsArg;
  }

  public static void getMyData(Connection con) throws SQLException {
    Statement stmt = null;
    String query =
      "(SELECT S.SUP_NAME, COUNT(C.COF_NAME) " +
      "FROM COFFEES AS C NATURAL INNER JOIN SUPPLIERS AS S " +
      "WHERE C.SUP_ID = S.SUP_ID " +
      "GROUP BY S.SUP_NAME)" + 
      "UNION " + 
      "(SELECT S.SUP_NAME, SUM(0) " +
      "FROM SUPPLIERS AS S LEFT JOIN COFFEES AS C " +
      "ON S.SUP_ID = C.SUP_ID " +
      "WHERE C.COF_NAME IS NULL " +
      "GROUP BY S.SUP_NAME)";

    try {
      stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      System.out.println("Lista de fornecedores com as qtds de tipos de café: ");
      while (rs.next()) {
        String coffeeName = rs.getString(1);
        String qtd_coffees = rs.getString(2);
        System.out.println("     " + coffeeName + " - QTD: " + qtd_coffees);
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

 	    MyQueries.getMyData(myConnection);

    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      JDBCUtilities.closeConnection(myConnection);
    }

  }
}
