package io.machinecode.chainlink.repository.jdbc;

import javax.sql.DataSource;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface DataSourceLookup {

    DataSource getDataSource();
}
