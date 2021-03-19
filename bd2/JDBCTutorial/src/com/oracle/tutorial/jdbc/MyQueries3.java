package com.oracle.tutorial.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyQueries3 {
  
  Connection con;
  JDBCUtilities settings;  
  
  public MyQueries3(Connection connArg, JDBCUtilities settingsArg) {
    this.con = connArg;
    this.settings = settingsArg;
  }

  public static void getMyData(Connection con) throws SQLException {
    Statement stmt = null;
    String query =
      "SELECT D.NOME_CLIENTE, SUM(D.SALDO_DEPOSITO) AS D_SOMA, SUM(E.VALOR_EMPRESTIMO) AS E_SOMA " +
      "FROM DEPOSITO AS D INNER JOIN EMPRESTIMO AS E " +
        "ON D.NOME_CLIENTE = E.NOME_CLIENTE " +
      "GROUP BY D.NOME_CLIENTE";

    try {
      stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      System.out.println("Cliente - Soma depositos - Soma empréstimos\n");
      while (rs.next()) {
        String cliente = rs.getString("NOME_CLIENTE");
        float sum_depositos = rs.getFloat("D_SOMA");
        float sum_emprestimos = rs.getFloat("E_SOMA");
        System.out.println(cliente + " - " + sum_depositos + " - " + sum_emprestimos);
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

 	    MyQueries3.getMyData(myConnection);

    } catch (SQLException e) {
      JDBCUtilities.printSQLException(e);
    } finally {
      JDBCUtilities.closeConnection(myConnection);
    }

  }
}
