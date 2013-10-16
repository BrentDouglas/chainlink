package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.TargetThread;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanImpl implements Plan {

    private PlanImpl[] then = null;
    private PlanImpl[] always = null;
    private PlanImpl[] cancel = null;
    private PlanImpl[] fail = null;

    private final int maxThreads;
    private final Executable[] executable;
    private final TargetThread target;
    private final String context;

    public PlanImpl(final int maxThreads, final Executable[] executable, final TargetThread target, final String context) {
        this.maxThreads = maxThreads;
        this.executable = executable;
        this.target = target;
        this.context = context;
    }

    public PlanImpl(final Executable executable, final TargetThread target, final String context) {
        this(-1, new Executable[]{executable}, target, context);
    }

    public PlanImpl(final Executable[] executable, final TargetThread target, final String context) {
        this(-1, executable, target, context);
    }

    @Override
    public int getMaxThreads() {
        return maxThreads;
    }

    @Override
    public Executable[] getExecutables() {
        return executable;
    }

    @Override
    public TargetThread getTargetThread() {
        return target;
    }

    @Override
    public String getElement() {
        return context;
    }

    @Override
    public PlanImpl[] then() {
        return this.then;
    }

    @Override
    public PlanImpl[] always() {
        return this.always;
    }

    @Override
    public PlanImpl[] fail() {
        return this.fail;
    }

    @Override
    public PlanImpl[] cancel() {
        return this.cancel;
    }

    public PlanImpl then(final PlanImpl then) {
        this.then = new PlanImpl[]{then};
        return this;
    }

    public PlanImpl then(final PlanImpl[] then) {
        this.then = then;
        return this;
    }

    public PlanImpl always(final PlanImpl always) {
        this.always = new PlanImpl[]{always};
        return this;
    }

    public PlanImpl fail(final PlanImpl fail) {
        this.fail = new PlanImpl[]{fail};
        return this;
    }

    public PlanImpl cancel(final PlanImpl cancel) {
        this.cancel = new PlanImpl[]{cancel};
        return this;
    }
}
