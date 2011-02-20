package com.asktom;

import java.sql.*;

class test {

public static void main (String args [])
   throws SQLException
{
    DriverManager.registerDriver
      (new oracle.jdbc.driver.OracleDriver());

    Connection conn = DriverManager.getConnection
         ("jdbc:oracle:thin:@localhost:1521:ORCL",
          "scott", "tiger");
    conn.setAutoCommit (false);

    Statement stmt = conn.createStatement();

    DbmsOutput dbmsOutput = new DbmsOutput( conn );

    dbmsOutput.enable( 1000000 );

    stmt.execute
    ( "begin emp_report; end;" );
    stmt.close();

    dbmsOutput.show();

    dbmsOutput.close();
    conn.close();
}

}
