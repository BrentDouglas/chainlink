package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.element.transition.Transition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TransitionWork extends Transition, Serializable {

    Result runTransition() throws Exception;

    class Result {
        public final BatchStatus batchStatus;
        public final String exitStatus;
        public final String next;

        public Result(final BatchStatus batchStatus, final String exitStatus, final String next) {
            this.batchStatus = batchStatus;
            this.exitStatus = exitStatus;
            this.next = next;
        }

        public static Result next(final String next) {
            return new Result(null, null, next);
        }

        public static Result status(final BatchStatus batchStatus, final String exitStatus) {
            return new Result(batchStatus, exitStatus, null);
        }
    }
}
