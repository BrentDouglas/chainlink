package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.element.transition.Transition;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TransitionWork extends Transition, Serializable {

    Result runTransition(String id) throws Exception;

    String element();

    class Result {
        public final BatchStatus batchStatus;
        public final String exitStatus;
        public final String next;
        public final String restartId;

        public Result(final BatchStatus batchStatus, final String exitStatus, final String next, final String restartId) {
            this.batchStatus = batchStatus;
            this.exitStatus = exitStatus;
            this.next = next;
            this.restartId = restartId;
        }

        public static Result next(final String next) {
            return new Result(null, null, next, null);
        }

        public static Result status(final BatchStatus batchStatus, final String exitStatus) {
            return new Result(batchStatus, exitStatus, null, null);
        }

        public static Result status(final BatchStatus batchStatus, final String exitStatus, final String restartId) {
            return new Result(batchStatus, exitStatus, null, restartId);
        }
    }
}
