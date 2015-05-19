/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author Tom
 */
public class DBUtils {

    private static final Logger logger = Logger.getLogger("myLogger");

    /**
     * Rolls back transaction and logs possible error.
     *
     * @param conn connection
     */
    public static void doRollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                if (conn.getAutoCommit()) {
                    throw new IllegalStateException("Connection is in the autocommit mode!");
                }
                conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error when doing rollback", ex);
            }
        }
    }

    public static void switchAutocommitBackToTrue(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error when switching autocommit mode back to true", ex);
            }
        }
    }

    /**
     * Reads SQL statements from file. SQL commands in file must be separated by
     * a semicolon.
     *
     * @param is sql script to be read
     * @return array of command strings
     */
    private static String[] readSqlStatements(InputStream is) {
        try {
            char buffer[] = new char[256];
            StringBuilder result = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(is, "UTF-8");
            while (true) {
                int count = reader.read(buffer);
                if (count < 0) {
                    break;
                }
                result.append(buffer, 0, count);
            }
            return result.toString().split(";");
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read ", ex);
        }
    }

    /**
     * Try to execute script for creating tables. If tables already exist,
     * appropriate exception is catched and ignored.
     *
     * @param ds dataSource
     * @param is script for creating tables
     * @throws SQLException when operation fails
     */
    public static void tryCreateTables(DataSource ds, InputStream is) throws SQLException {
        try {
            executeSqlScript(ds, is);
            logger.warning("Tables created");
        } catch (SQLException ex) {
            if (!"X0Y32".equals(ex.getSQLState())) {
                throw ex;
            }
        }
    }

    /**
     * Executes SQL script.
     *
     * @param ds datasource
     * @param is sql script to be executed
     * @throws SQLException when operation fails
     */
    public static void executeSqlScript(DataSource ds, InputStream is) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            for (String sqlStatement : readSqlStatements(is)) {
                if (!sqlStatement.trim().isEmpty()) {
                    try (PreparedStatement preparedStatement = conn.prepareStatement(sqlStatement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            }
        }
    }
}
