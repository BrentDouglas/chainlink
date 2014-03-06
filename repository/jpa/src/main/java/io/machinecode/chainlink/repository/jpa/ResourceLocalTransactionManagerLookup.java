package io.machinecode.chainlink.repository.jpa;

import javax.persistence.EntityManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResourceLocalTransactionManagerLookup implements TransactionManagerLookup {
    @Override
    public ExtendedTransactionManager getTransactionManager(final EntityManager entityManager) {
        return new ResourceLocalTransactionManager(entityManager);
    }
}
