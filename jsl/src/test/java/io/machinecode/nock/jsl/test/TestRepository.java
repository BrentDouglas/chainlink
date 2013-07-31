package io.machinecode.nock.jsl.test;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.jsl.xml.XmlJob;
import io.machinecode.nock.jsl.xml.type.XmlFlow;
import io.machinecode.nock.jsl.xml.type.XmlSplit;
import io.machinecode.nock.jsl.xml.type.XmlStep;
import io.machinecode.nock.jsl.xml.util.Inheritable;
import io.machinecode.nock.jsl.xml.Repository;
import io.machinecode.nock.jsl.xml.ParentNotFoundException;
import io.machinecode.nock.jsl.xml.type.XmlType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TestRepository implements Repository {

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

    private final String name;
    private final XmlJob job;

    public TestRepository(final String name, final InputStream stream) {
        this.name = name;
        job = doAdd(this, stream);
    }

    final TMap<String, TestRepository> repos = new THashMap<String, TestRepository>();
    final TMap<String, Inheritable> values = new THashMap<String, Inheritable>();

    @Override
    public <T extends Inheritable<T>> T findParent(final Class<T> clazz, final T that) throws ParentNotFoundException {
        return findParent(clazz, that.getParent(), that.getJslName());
    }

    @Override
    public <T extends Inheritable<T>> T findParent(final Class<T> clazz, final String id, final String jslName) throws ParentNotFoundException {
        if (jslName == null || jslName.equals(this.name)) {
            final Inheritable target = values.get(id);
            if (target == null) {
                throw new ParentNotFoundException("Can't find '" + id + "' in '" + jslName + "'.");
            }
            if (!clazz.isAssignableFrom(target.getClass())) {
                throw new ClassCastException();
            }
            return (T)target.inherit(this);
        } else {
            final Repository repository = repos.get(jslName);
            if (repository == null) {
                throw new ParentNotFoundException("Can't find file '" + jslName + "'.");
            }
            return repository.findParent(clazz, id, jslName);
        }
    }

    @Override
    public XmlJob add(final String jslName, final InputStream stream) {
        if (jslName.equals(this.name)) {
            return doAdd(this, stream);
        }
        TestRepository repository = repos.get(jslName);
        if (repository == null) {
            repository = new TestRepository(jslName, stream);
            repos.put(jslName, repository);
        }
        return repository.getJob();
    }

    public XmlJob getJob() {
        return job;
    }

    public XmlJob doAdd(final TestRepository repository, final InputStream stream) {
        final XmlJob job;
        try {
            job = (XmlJob) unmarshaller.unmarshal(stream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        repository.values.put(job.getId(), job);
        for (final XmlType type : job.getTypes()) {
            addType(type);
        }
        return job;
    }

    private void addType(final XmlType that) {
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
        for (final XmlType type : that.getTypes()) {
            addType(type);
        }
    }

    private void addStep(final XmlStep that) {
        values.put(that.getId(), that);
    }
}
