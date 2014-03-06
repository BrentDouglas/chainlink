package io.machinecode.chainlink.repository.jdbc;

import javax.sql.DataSource;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface DataSourceLookup {

    DataSource getDataSource();
}
