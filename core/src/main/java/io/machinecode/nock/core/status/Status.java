package io.machinecode.nock.core.status;

import javax.batch.runtime.BatchStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Status {
    private static final String STARTING = "STARTING";
    private static final String STARTED = "STARTED";
    private static final String STOPPING = "STOPPING";
    private static final String STOPPED = "STOPPED";
    private static final String FAILED = "FAILED";
    private static final String COMPLETED = "COMPLETED";
    private static final String ABANDONED = "ABANDONED";

    public static boolean matches(final CharSequence reference, final CharSequence target) {
        final int length = reference.length();
        for (int i = 0; i < length; ++i) {
            //TODO
        }
        return true;
    }

    public static boolean matches(final BatchStatus reference, final CharSequence target) {
        return matches(reference.name(), target);
    }
}
