package javax.batch.runtime;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public enum BatchStatus {
    STARTING,
    STARTED,
    STOPPING,
    STOPPED,
    FAILED,
    COMPLETED,
    ABANDONED
}
