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
package io.machinecode.chainlink.mongo.test.repository;

import com.mongodb.MongoClient;
import io.machinecode.chainlink.repository.mongo.MongoRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.jboss.logging.Logger;
import org.jongo.Jongo;
import org.junit.After;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MongoRepositoryTest extends RepositoryTest {

    private static final Logger log = Logger.getLogger(MongoRepositoryTest.class);

    private Jongo jongo;

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        final String host = System.getProperty("mongo.host", "127.0.0.1");
        final int port = Integer.parseInt(System.getProperty("mongo.port", "27017"));
        final String database = System.getProperty("mongo.database");
        log.infof("Connection: {\n\turl: '%s'\n}", host);
        jongo = new Jongo(new MongoClient(host, port).getDB(database));
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                return _repository = new MongoRepository(jongo, dependencies.getMarshalling(), true);
            }
        });
    }

    @After
    public void after() throws Exception {
        jongo.getCollection("job_instances").remove();
        jongo.getCollection("job_executions").remove();
        jongo.getCollection("step_executions").remove();
        jongo.getCollection("partition_executions").remove();
    }
}
