package io.machinecode.chainlink.jsl.core.loader;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.jsl.core.inherit.InheritableJob;
import io.machinecode.chainlink.jsl.core.inherit.execution.InheritableExecution;
import io.machinecode.chainlink.jsl.core.inherit.execution.InheritableFlow;
import io.machinecode.chainlink.jsl.core.inherit.execution.InheritableSplit;
import io.machinecode.chainlink.jsl.core.inherit.execution.InheritableStep;
import io.machinecode.chainlink.spi.Inheritable;
import io.machinecode.chainlink.spi.InheritableElement;
import io.machinecode.chainlink.spi.loader.JobRepository;
import io.machinecode.chainlink.spi.ParentNotFoundException;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.util.Messages;

import javax.batch.operations.NoSuchJobException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class AbstractJobLoader implements JobLoader, JobRepository {

    @Override
    public InheritableJob<?,?,?,?> load(final String jslName) throws NoSuchJobException {
        final InheritableJob<?,?,?,?> job = doLoad(jslName).getJob();
        return job.inherit(this, jslName);
    }

    protected abstract Node doLoad(final String jslName) throws NoSuchJobException;

    @Override
    public <T extends InheritableElement<T>> T findParent(final Class<T> clazz, final T that, final String defaultJobXml) throws ParentNotFoundException {
        final String jslName = that.getJslName() == null ? defaultJobXml : that .getJslName();
        return findParent(clazz, that.getParent(), jslName);
    }

    @Override
    public <T extends InheritableElement<T>> T findParent(final Class<T> clazz, final String id, final String jslName) throws ParentNotFoundException {
        final Node node = doLoad(jslName);
        if (node == null) {
            throw new NoSuchJobException();
        }
        return node.findParent(clazz, id, jslName);
    }

    protected class Node {

        private final String name;
        private final InheritableJob<?, ?, ?, ?> job;

        public Node(final String name, final InheritableJob<?, ?, ?, ?> job) {
            this.name = name;
            this.job = addJob(this, job);
        }

        final TMap<String, Inheritable> values = new THashMap<String, Inheritable>();

        public <T extends InheritableElement<T>> T findParent(final Class<T> clazz, final String id, final String jslName) throws ParentNotFoundException {
            if (this.name.equals(jslName)) {
                final Inheritable target = values.get(id);
                if (target == null) {
                    throw new ParentNotFoundException(Messages.format("CHAINLINK-003001.job.loader.id.not.in.file", id, jslName));
                }
                if (!clazz.isAssignableFrom(target.getClass())) {
                    throw new ClassCastException();
                }
                return clazz.cast(target.inherit(AbstractJobLoader.this, jslName));
            }
            throw new ParentNotFoundException(Messages.format("CHAINLINK-003000.job.loader.no.file", jslName));
        }

        public InheritableJob getJob() {
            return job;
        }

        public InheritableJob<?,?,?,?> addJob(final Node repository, final InheritableJob<?,?,?,?> job) {
            repository.values.put(job.getId(), job);
            for (final Inheritable<?> type : job.getExecutions()) {
                addType((InheritableExecution<?>)type);
            }
            return job;
        }

        private void addType(final InheritableExecution that) {
            if (that instanceof InheritableFlow) {
                addFlow((InheritableFlow) that);
            }
            if (that instanceof InheritableStep) {
                addStep((InheritableStep) that);
            }
            if (that instanceof InheritableSplit) {
                for (final Inheritable<?> flow : ((InheritableSplit<?,?>) that).getFlows()) {
                    addFlow((InheritableFlow<?,?,?>)flow);
                }
            }
        }

        private void addFlow(final InheritableFlow<?,?,?> that) {
            values.put(that.getId(), that);
            for (final Inheritable<?> type : that.getExecutions()) {
                addType((InheritableExecution<?>)type);
            }
        }

        private void addStep(final InheritableStep that) {
            values.put(that.getId(), that);
        }
    }
}
