package io.machinecode.chainlink.jsl.loader;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.jsl.xml.XmlJob;
import io.machinecode.chainlink.spi.util.Messages;

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

    @Override
    protected Node doLoad(final String jslName) {
        final Node cached = repos.get(jslName);
        if (cached != null) {
            return cached;
        }
        final InputStream stream = loader.getResourceAsStream(this.getPrefix() + jslName + ".xml");
        if (stream == null) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-003000.job.loader.no.file", this.getPrefix() + jslName + ".xml"));
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
        final Node node = new Node(jslName, job);
        repos.put(jslName, node);
        return node;
    }
}
