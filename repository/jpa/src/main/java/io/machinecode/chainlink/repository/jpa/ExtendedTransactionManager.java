package io.machinecode.chainlink.repository.jpa;

import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ExtendedTransactionManager extends TransactionManager {

    boolean isResourceLocal();
}
