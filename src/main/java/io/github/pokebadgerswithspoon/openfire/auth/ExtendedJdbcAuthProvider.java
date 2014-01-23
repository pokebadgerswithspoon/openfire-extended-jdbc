/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.pokebadgerswithspoon.openfire.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.Log;

/**
 * Extended version of JdbcAuthProvider which is capable to check password on
 * database side.
 * It passes username and password to a precofigured query and expects one row
 * with 0 or 1 inside.
 *
 * <h2>Dovecot compatible example:<h2>
 * <pre>
 * SELECT count(*) FROM postfix.mailbox u
 *   WHERE u.username=CONCAT(?,"@example.com")
 *     AND u.password=ENCRYPT(?, SUBSTRING_INDEX(u.password, "$", 3))
 * </pre>
 * Assuming Openfire is running in example.com domain
 *
 * @author lauri
 */
public class ExtendedJdbcAuthProvider extends JdbcAuthProvider {

    /**
     * Openfire configuration property
     */
    public static final String SERVER_SIDE_PASSWORD_CHECK_PROPERTY = "extendedJdbcAuthProvider.passwordCheckSql";
    static final String NO = "no";
    private final String passwordCheckSQL;

    public ExtendedJdbcAuthProvider() {
        Log.info("ExtendedJdbcAuthProvider is getting ready to handle logins");
        JiveGlobals.migrateProperty(SERVER_SIDE_PASSWORD_CHECK_PROPERTY);
        passwordCheckSQL = JiveGlobals.getProperty(SERVER_SIDE_PASSWORD_CHECK_PROPERTY);
        boolean useDeligate = stringMeansNo(passwordCheckSQL);

        if (useDeligate) {
            throw new RuntimeException("ERROR. Configured to use ExtendedJdbcAuthProvider, but " + SERVER_SIDE_PASSWORD_CHECK_PROPERTY + " property is not set");
        } else {
            Log.debug("Password checker SQL: " + passwordCheckSQL);
        }
    }

    @Override
    public void authenticate(String username, String password) throws UnauthorizedException {
        try {
            Connection connection = getConnection();
            try {
                PreparedStatement stmt = connection.prepareStatement(passwordCheckSQL);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new UnauthorizedException("SQL error while checking password of " + username + ". Result set supported to return 1 row with 1 or 0 in first column. We got ZERO rows.");
                } else {
                    boolean ok = rs.getBoolean(1);
                    if (rs.next()) {
                        throw new UnauthorizedException("SQL error while checking password of " + username + ". Result set supported to return 1 row with 1 or 0 in first column. We got MORE than one ROW.");
                    } else if (!ok) {
                        throw new UnauthorizedException("Password mismatch");
                    }
                }
            } finally {
                DbConnectionManager.closeConnection(connection);
            }
        } catch (SQLException ex) {
            throw new UnauthorizedException("SQL error while checking password of " + username, ex);
        }
    }

    private static boolean stringMeansNo(String s) {
        return s == null || NO.equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
    }
}
