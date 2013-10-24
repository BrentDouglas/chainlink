package io.machinecode.nock.jsl.xml.loader;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.jsl.xml.XmlJob;
import io.machinecode.nock.jsl.xml.execution.XmlExecution;
import io.machinecode.nock.jsl.xml.execution.XmlFlow;
import io.machinecode.nock.jsl.xml.execution.XmlSplit;
import io.machinecode.nock.jsl.xml.execution.XmlStep;
import io.machinecode.nock.spi.Inheritable;
import io.machinecode.nock.spi.InheritableElement;
import io.machinecode.nock.spi.JobRepository;
import io.machinecode.nock.spi.ParentNotFoundException;
import io.machinecode.nock.spi.loader.JobLoader;

import javax.batch.operations.NoSuchJobException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class XmlJobLoader implements JobLoader, JobRepository {

    private static final Unmarshaller unmarshaller;

    static {
        final JAXBContext context;
        try {
            context = JAXBContext.newInstance(XmlJob.class);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    //private final String name;
    //private final XmlJob job;
    private final ClassLoader loader;

    public XmlJobLoader(final ClassLoader loader) {
        this.loader = loader;
    }

    final TMap<String, XmlNode> repos = new THashMap<String, XmlNode>();

    public abstract String getPrefix();

    @Override
    public XmlJob load(final String id) throws NoSuchJobException {
        final XmlJob job = doLoad(id).getJob();
        return job.inherit(this, id);
    }

    private XmlNode doLoad(final String id) {
        final XmlNode cached = repos.get(id);
        if (cached != null) {
            return cached;
        }
        final InputStream stream = loader.getResourceAsStream(this.getPrefix() + id + ".xml");
        if (stream == null) {
            throw new NoSuchJobException();
        }
        final XmlNode node = new XmlNode(id, stream);
        repos.put(id, node);
        return node;
    }

    @Override
    public <T extends InheritableElement<T>> T findParent(final Class<T> clazz, final T that, final String defaultJobXml) throws ParentNotFoundException {
        final String jslName = that.getJslName() == null ? defaultJobXml : that .getJslName();
        return findParent(clazz, that.getParent(), jslName);
    }

    @Override
    public <T extends InheritableElement<T>> T findParent(final Class<T> clazz, final String id, final String jslName) throws ParentNotFoundException {
        final XmlNode node = doLoad(jslName);
        if (node == null) {
            throw new NoSuchJobException();
        }
        return node.findParent(clazz, id, jslName);
    }

    private class XmlNode {

        private final String name;
        private final XmlJob job;

        public XmlNode(final String name, final InputStream stream) {
            this.name = name;
            job = addJob(this, stream);
        }

        final TMap<String, Inheritable> values = new THashMap<String, Inheritable>();

        public <T extends InheritableElement<T>> T findParent(final Class<T> clazz, final String id, final String jslName) throws ParentNotFoundException {
            if (this.name.equals(jslName)) {
                final Inheritable target = values.get(id);
                if (target == null) {
                    throw new ParentNotFoundException("Can't find '" + id + "' in '" + jslName + "'.");
                }
                if (!clazz.isAssignableFrom(target.getClass())) {
                    throw new ClassCastException();
                }
                return (T)target.inherit(XmlJobLoader.this, jslName);
            }
            throw new ParentNotFoundException("Can't find file '" + jslName + "'.");
        }

        public XmlJob getJob() {
            return job;
        }

        public XmlJob addJob(final XmlNode repository, final InputStream stream) {
            final XmlJob job;
            try {
                job = (XmlJob) unmarshaller.unmarshal(stream);
            } catch (final JAXBException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    //
                }
            }
            repository.values.put(job.getId(), job);
            for (final XmlExecution type : job.getExecutions()) {
                addType(type);
            }
            return job;
        }

        private void addType(final XmlExecution that) {
            if (that instanceof XmlFlow) {
                addFlow((XmlFlow) that);
            }
            if (that instanceof XmlStep) {
                addStep((XmlStep) that);
            }
            if (that instanceof XmlSplit) {
                for (final XmlFlow flow : ((XmlSplit) that).getFlows()) {
                    addFlow(flow);
                }
            }
        }

        private void addFlow(final XmlFlow that) {
            values.put(that.getId(), that);
            for (final XmlExecution type : that.getExecutions()) {
                addType(type);
            }
        }

        private void addStep(final XmlStep that) {
            values.put(that.getId(), that);
        }
    }
}
