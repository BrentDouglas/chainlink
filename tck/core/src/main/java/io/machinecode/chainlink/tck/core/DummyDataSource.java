package io.machinecode.chainlink.tck.core;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DummyDataSource implements DataSource {

    private final String url;
    private final String driver;

    public DummyDataSource(final String url, final String driver) {
        this.url = url;
        this.driver = driver;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Driver driver;
            try {
                driver = DriverManager.getDriver(url);
            } catch (final SQLException e) {
                driver = Driver.class.cast(Class.forName(this.driver).newInstance());
                DriverManager.registerDriver(driver);
            }
            return driver.connect(url, new Properties());
        } catch (final ClassNotFoundException cnfe) {
            throw new SQLException(cnfe);
        } catch (final InstantiationException ie) {
            throw new SQLException(ie);
        } catch (final IllegalAccessException iae) {
            throw new SQLException(iae);
        }
    }

    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        try {
            Driver driver;
            try {
                driver = DriverManager.getDriver(url);
            } catch (final SQLException e) {
                driver = Driver.class.cast(Class.forName(this.driver).newInstance());
                DriverManager.registerDriver(driver);
            }
            final Properties properties = new Properties();
            properties.setProperty("user", username);
            properties.setProperty("password", password);
            return driver.connect(url, properties);
        } catch (final ClassNotFoundException cnfe) {
            throw new SQLException(cnfe);
        } catch (final InstantiationException ie) {
            throw new SQLException(ie);
        } catch (final IllegalAccessException iae) {
            throw new SQLException(iae);
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }
}
