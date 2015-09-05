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
package io.machinecode.chainlink.redis.test.repository;

import io.machinecode.chainlink.core.repository.MutableMetricImpl;
import io.machinecode.chainlink.redis.repository.RedisRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.junit.After;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RedisRepositoryTest extends RepositoryTest {

    protected JedisShardInfo info;

    private JedisShardInfo _info() {
        if (info == null) {
            info = new JedisShardInfo(
                    System.getProperty("redis.host"),
                    Integer.parseInt(System.getProperty("redis.port"))
            );
        }
        return info;
    }

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                return _repository = new RedisRepository(
                        _info(),
                        MutableMetricImpl.class.getClassLoader(),
                        dependencies.getMarshalling()
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        final Jedis jedis = _info().createResource();
        try {
            jedis.flushDB();
        } finally {
            jedis.disconnect();
        }
    }
}
