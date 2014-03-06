package io.machinecode.chainlink.repository.jpa;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExtendedTransactionManager extends TransactionManager {

    boolean isResourceLocal();
}
