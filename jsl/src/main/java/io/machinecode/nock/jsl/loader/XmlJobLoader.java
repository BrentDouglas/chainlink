package io.machinecode.nock.jsl.loader;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.jsl.xml.XmlJob;

import javax.batch.operations.NoSuchJobException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class XmlJobLoader extends AbstractJobLoader {

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

    private final ClassLoader loader;

    public XmlJobLoader(final ClassLoader loader) {
        this.loader = loader;
    }

    final TMap<String, Node> repos = new THashMap<String, Node>();

    public abstract String getPrefix();

    protected Node doLoad(final String id) {
        final Node cached = repos.get(id);
        if (cached != null) {
            return cached;
        }
        final InputStream stream = loader.getResourceAsStream(this.getPrefix() + id + ".xml");
        if (stream == null) {
            throw new NoSuchJobException();
        }

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
        final Node node = new Node(id, job);
        repos.put(id, node);
        return node;
    }
}
