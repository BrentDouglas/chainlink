/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.tck.core.DummyDataSource;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JdbcRepositoryFactory implements RepositoryFactory {

    private static final Logger log = Logger.getLogger(JdbcRepositoryFactory.class);

    private static DataSource dataSource;
    private static String username;
    private static String password;

    static {
        try {
            final String prefix = System.getProperty("database.prefix") + ".";
            dataSource = new DummyDataSource(System.getProperty(prefix + "database.url"), System.getProperty(prefix + "database.driver"));
            username = System.getProperty(prefix + "database.user");
            password = System.getProperty(prefix + "database.password", "");
            try (final Connection connection = username == null
                        ? dataSource.getConnection()
                        : dataSource.getConnection(username, password)) {
                connection.setAutoCommit(false);
                try {
                    try (final PreparedStatement statement = connection.prepareStatement("delete from job_instance;")) {
                        statement.executeUpdate();
                    }
                    try (final PreparedStatement statement = connection.prepareStatement("delete from metric;")) {
                        statement.executeUpdate();
                    }
                    try (final PreparedStatement statement = connection.prepareStatement("delete from property;")) {
                        statement.executeUpdate();
                    }
                    connection.commit();
                } catch (final SQLException e) {
                    connection.rollback();
                    throw e;
                }
            }
        } catch (RuntimeException e) {
            log.error("", e);
            throw e;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws SQLException {
        return JdbcRepository.create(new DataSourceLookup() {
            @Override
            public DataSource getDataSource() {
                return dataSource;
            }
        }, username, password);
    }
}
