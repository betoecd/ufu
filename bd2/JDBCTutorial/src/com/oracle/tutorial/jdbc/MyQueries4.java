package com.oracle.tutorial.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.sql.DatabaseMetaData;
import java.sql.BatchUpdateException;
import java.sql.Date;

public class MyQueries4
{
    Connection con;
    JDBCUtilities settings;  

    public MyQueries4(Connection connArg, JDBCUtilities settingsArg)
    {
        this.con = connArg;
        this.settings = settingsArg;
    }

    public static void populateTable(Connection con) throws SQLException, IOException
    {
        Statement stmt = null;
        String create = "";
        try
        {

            BufferedReader inputStream = null;
            Scanner scanned_line = null;
            String line;
            String[] value;
            value = new String[7];
            int countv;

            inputStream = new BufferedReader(new FileReader("/home/bits/Documents/UFU/bd2/JDBCTutorial/debito-populate-table.txt"));
            stmt = con.createStatement();
            stmt.executeUpdate("truncate table debito;");
            while ((line = inputStream.readLine()) != null)
            {
                countv=0;
                System.out.println("<<");
                //split fields separated by tab delimiters
                scanned_line = new Scanner(line);
                scanned_line.useDelimiter("\t");
                while (scanned_line.hasNext())
                {
                    System.out.println(value[countv++]=scanned_line.next());
                } //while
                if (scanned_line != null) { scanned_line.close(); }
                create = "insert into debito (numero_debito, valor_debito, " +
                    "motivo_debito, data_debito, numero_conta, nome_agencia, nome_cliente) " + "values (" +
                    value[0] +", "+ value[1] +", "+ value[2] +", '"+ value[3] +"', "+ value[4] +", '"+ value[5] +
                    "', '"+ value[6] + "');";
                //System.out.println("Executando DDL/DML:");
                stmt.executeUpdate(create);
            } //while
        }
        catch (SQLException e)
        {
            JDBCUtilities.printSQLException(e);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            if (stmt != null) { stmt.close(); }
        }
    }


    /*
    * Função responsável por realizar consulta na tabela deposito
    * e recuperar nome de todos os clientes do banco, com suas respectivas
    * somas de depósitos e empréstimos, caso existam.
    */
    public static void getMyData3(Connection con) throws SQLException, IOException
    {
        Statement stmt = null;
        String query = "SELECT	C.NOME_CLIENTE, C.NOME_AGENCIA, C.NUMERO_CONTA, "+
                            "COALESCE(SUM(D.SALDO_DEPOSITO  ),0) AS TOTAL_DEP, "+
                            "COALESCE(SUM(E.VALOR_EMPRESTIMO),0) AS TOTAL_EMP "+
                        "FROM CONTA AS C NATURAL LEFT JOIN "+
                            "(EMPRESTIMO AS E NATURAL FULL JOIN DEPOSITO AS D) "+
                        "GROUP BY C.NOME_CLIENTE, C.NOME_AGENCIA, C.NUMERO_CONTA";
        try
        {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("Nomes de todos os clientes do banco, com suas respectivas "+
                                "somas de depósitos e empréstimos, caso existam.: ");
            while (rs.next())
            {
                String nome_cliente = rs.getString("NOME_CLIENTE");
                String nome_agencia = rs.getString("NOME_AGENCIA");
                Integer numero_conta = rs.getInt("NUMERO_CONTA");
                float sum_depositos = rs.getFloat("TOTAL_DEP");
                float sum_emprestimos = rs.getFloat("TOTAL_EMP");
                
                System.out.println(nome_cliente+", "+nome_agencia+", "+numero_conta.toString()+", "+
                                   sum_depositos+", "+sum_emprestimos);
            }
        }
        catch (SQLException e)
        {
            JDBCUtilities.printSQLException(e);
        }
        finally
        {
            if (stmt != null) { stmt.close(); }
        }
    }


    /*
    * Opções disponíveis para consulta/atualização de dados em um ResultSet
    */
    public static void cursorHoldabilitySupport(Connection conn)
        throws SQLException, IOException
        {
            DatabaseMetaData dbMetaData = conn.getMetaData();

            System.out.println("ResultSet.HOLD_CURSORS_OVER_COMMIT = " +
                ResultSet.HOLD_CURSORS_OVER_COMMIT);

            System.out.println("ResultSet.CLOSE_CURSORS_AT_COMMIT = " +
                ResultSet.CLOSE_CURSORS_AT_COMMIT);

            System.out.println("Default cursor holdability: " +
                dbMetaData.getResultSetHoldability());

            System.out.println("Supports HOLD_CURSORS_OVER_COMMIT? " +
                dbMetaData.supportsResultSetHoldability(
                    ResultSet.HOLD_CURSORS_OVER_COMMIT));
                
            System.out.println("Supports CLOSE_CURSORS_AT_COMMIT? " +
                dbMetaData.supportsResultSetHoldability(
                    ResultSet.CLOSE_CURSORS_AT_COMMIT));

            System.out.println("Supports TYPE_FORWARD_ONLY and CONCUR_READ_ONLY? " +
                dbMetaData.supportsResultSetConcurrency(
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));

            System.out.println("Supports TYPE_FORWARD_ONLY and CONCUR_UPDATABLE? " +
                dbMetaData.supportsResultSetConcurrency(
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));
            
            System.out.println("Supports TYPE_SCROLL_INSENSITIVE and CONCUR_READ_ONLY? " +
                dbMetaData.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY));

            System.out.println("Supports TYPE_SCROLL_INSENSITIVE and CONCUR_UPDATABLE? " +
                dbMetaData.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE));
            
            System.out.println("Supports TYPE_SCROLL_SENSITIVE and CONCUR_READ_ONLY? " +
                dbMetaData.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY));

            System.out.println("Supports TYPE_SCROLL_SENSITIVE and CONCUR_UPDATABLE? " +
                dbMetaData.supportsResultSetConcurrency(
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        }


    /*
    * Função responsável por modificar a coluna saldo_deposito da tabela deposito
    * utilizando um multiplicador inserido na execução do método
    */
    public static void modifyPrices(Connection con) throws SQLException, IOException
    {
        Statement stmt = null;
        try
        {
            System.out.println("Digite o multiplicador como um numero real (Ex.: 5% = 1,05):");
            Scanner in = new Scanner(System.in);
            double percentage = in.nextDouble();

            stmt = con.createStatement();
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_UPDATABLE);
            ResultSet uprs = stmt.executeQuery("SELECT * FROM DEPOSITO");
            while (uprs.next())
            {
                float f = uprs.getFloat("SALDO_DEPOSITO");
                f = f * (float) percentage;
                uprs.updateFloat("SALDO_DEPOSITO", f);
                uprs.updateRow();
            }
        }
        catch (SQLException e )
        {
            JDBCTutorialUtilities.printSQLException(e);
        }
        finally
        {
            if (stmt != null) { stmt.close(); }
        }
    }


    /*
    * Função responsável por povoar a tabela debito utilizando o batch
    */
    public static void populateTableBatch(Connection con) throws SQLException, IOException
    {
        con.setAutoCommit(false);
        Statement stmt = null;
        String create = "";
        try
        {

            BufferedReader inputStream = null;
            Scanner scanned_line = null;
            String line;
            String[] value;
            value = new String[7];
            int countv;

            inputStream = new BufferedReader(new FileReader("/home/bits/Documents/UFU/bd2/JDBCTutorial/debito-populate-table.txt"));
            stmt = con.createStatement();
            stmt.addBatch("truncate table debito");
            while ((line = inputStream.readLine()) != null)
            {
                countv=0;
                System.out.println("<<");
                //split fields separated by tab delimiters
                scanned_line = new Scanner(line);
                scanned_line.useDelimiter("\t");
                while (scanned_line.hasNext())
                {
                    System.out.println(value[countv++]=scanned_line.next());
                } //while
                if (scanned_line != null) { scanned_line.close(); }
                create = "insert into debito (numero_debito, valor_debito, " +
                    "motivo_debito, data_debito, numero_conta, nome_agencia, nome_cliente) " + "values (" +
                    value[0] +", "+ value[1] +", "+ value[2] +", '"+ value[3] +"', "+ value[4] +", '"+ value[5] +
                    "', '"+ value[6] + "')";
                //System.out.println("Executando DDL/DML:");
                stmt.addBatch(create);
            } //while
            int[] updateCounts = stmt.executeBatch();
            con.commit();
        }
        catch (SQLException e)
        {
            JDBCUtilities.printSQLException(e);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            if (stmt != null) { stmt.close(); }
            con.setAutoCommit(true);
        }
    }


    /*
    * Função responsável por inserir dados na tabela debito através de
    * parâmtros da função
    */
    public static void insertRow(
        Connection  con,
        Integer     numero_debito,
        double      valor_debito,
        Integer     motivo_debito,
        String      data_debito,
        Integer     numero_conta,
        String      nome_agencia,
        String      nome_cliente
    )
    throws SQLException, IOException
    {
        Statement stmt = null;
        try
        {
            stmt = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE
            );
            ResultSet uprs = stmt.executeQuery("SELECT * FROM DEBITO");
            uprs.moveToInsertRow(); //posiciona no ponto de inserção da tabela
            uprs.updateInt("numero_debito", numero_debito);
            uprs.updateDouble("valor_debito", valor_debito);
            uprs.updateShort("motivo_debito", motivo_debito.shortValue());
            uprs.updateDate("data_debito", Date.valueOf(data_debito));
            uprs.updateInt("numero_conta", numero_conta);
            uprs.updateString("nome_agencia", nome_agencia);
            uprs.updateString("nome_cliente", nome_cliente);
            uprs.insertRow(); //insere a linha na tabela

            uprs.beforeFirst(); //posiciona-se novamente na posição anterior ao primeiro registro
        }
        catch (SQLException e )
        {
            JDBCTutorialUtilities.printSQLException(e);
        }
        finally
        {
            if (stmt != null) { stmt.close(); }
        }
    }


    public static void main(String[] args)
    {
        JDBCUtilities myJDBCUtilities;
        Connection myConnection = null;
        if (args[0] == null)
        {
            System.err.println("Properties file not specified at command line");
            return;
        }
        else 
        {
            try
            {
                myJDBCUtilities = new JDBCUtilities(args[0]);
            }
            catch (Exception e)
            {
                System.err.println("Problem reading properties file " + args[0]);
                e.printStackTrace();
                return;
            }
        }

        try
        {
            myConnection = myJDBCUtilities.getConnection();
            MyQueries4.insertRow(
                myConnection,
                2000,
                150.00,
                1,
                "2014-01-23",
                46248,
                "UFU",
                "Carla Soares Sousa"
            );
            MyQueries4.insertRow(
                myConnection,
                2001,
                200.00,
                2,
                "2014-01-23",
                26892,
                "Glória",
                "Carolina Soares Souza"
            );
            MyQueries4.insertRow(
                myConnection,
                2002,
                500.00,
                3,
                "2014-01-23",
                70044,
                "Cidade Jardim",
                "Eurides Alves da Silva"
            );
        }
        catch (SQLException e)
        {
            JDBCUtilities.printSQLException(e);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            JDBCUtilities.closeConnection(myConnection);
        }
    }
}
