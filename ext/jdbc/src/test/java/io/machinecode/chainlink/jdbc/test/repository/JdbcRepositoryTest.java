/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.jdbc.test.repository;

import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.base.DummyDataSource;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.jboss.logging.Logger;
import org.junit.After;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JdbcRepositoryTest extends RepositoryTest {

    private static final Logger log = Logger.getLogger(JdbcRepositoryTest.class);

    private static volatile DataSource dataSource;

    final String prefix = System.getProperty("database.prefix") + ".";
    final String url = System.getProperty(prefix + "database.url");
    final String driverName = System.getProperty("database.driver");
    final String user = System.getProperty(prefix + "database.user");
    final String password = System.getProperty(prefix + "database.password", "");

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        log.infof("Connection: {\n\turl: '%s'\n\tdriver: '%s'\n\tuser: '%s'\n\tpassword: '%s'\n}", this.url, this.driverName, this.user, this.password);
        if (dataSource == null) {
            dataSource = new DummyDataSource(this.url, this.driverName);
        }
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                return _repository = JdbcRepository.create(new DataSourceLookup() {
                    @Override
                    public DataSource getDataSource() {
                        return dataSource;
                    }
                }, user, password);
            }
        });
    }

    @After
    public void after() throws Exception {
        Connection connection = null;
        try {
            if (dataSource == null) {
                dataSource = new DummyDataSource(this.url, this.driverName);
            }
            connection = dataSource.getConnection(this.user, this.password);
            connection.setAutoCommit(false);
            connection.prepareStatement("delete from job_instance;").executeUpdate();
            connection.prepareStatement("delete from metric;").executeUpdate();
            connection.prepareStatement("delete from property;").executeUpdate();
            connection.commit();
        } catch (final Exception e) {
            if (connection != null) {
                connection.rollback();
            }
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
