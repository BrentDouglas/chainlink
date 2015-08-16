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

import io.machinecode.chainlink.repository.jpa.EntityManagerLookup;
import io.machinecode.chainlink.repository.jpa.JpaRepository;
import io.machinecode.chainlink.repository.jpa.ResourceLocalTransactionManagerLookup;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JpaRepositoryFactory implements RepositoryFactory {

    private static EntityManagerFactory factory;

    static {
        factory = Persistence.createEntityManagerFactory("TestPU");
        final EntityManager em = factory.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("delete from JpaJobInstance").executeUpdate();
            em.createQuery("delete from JpaMetric").executeUpdate();
            em.createQuery("delete from JpaProperty").executeUpdate();
            em.flush();
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                factory.close();
            }
        }));
    }

    @Override
    public Repository produce(final Dependencies dependencies, final PropertyLookup properties) {
        return new JpaRepository(new EntityManagerLookup() {
            @Override
            public EntityManagerFactory getEntityManagerFactory() {
                return factory;
            }
        }, new ResourceLocalTransactionManagerLookup());
    }
}
