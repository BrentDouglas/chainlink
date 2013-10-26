package io.machinecode.nock.jsl.visitor;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.jsl.util.ImmutablePair;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.PropertiesElement;
import io.machinecode.nock.spi.element.Property;
import io.machinecode.nock.spi.element.partition.Plan;
import io.machinecode.nock.spi.util.Pair;
import io.machinecode.nock.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class JobTraversal {

    private static final Logger log = Logger.getLogger(JobTraversal.class);

    private final TMap<String, Pair<ExecutionWork, String>> transitions;
    private final TMap<Element, List<Pair<String, String>>> properties;

    public JobTraversal(final VisitorNode root) {
        this.transitions = new THashMap<String, Pair<ExecutionWork, String>>(root.transitions.size());
        for (final Transition that : root.transitions) {
            final VisitorNode next = root.ids.get(that.id);
            this.transitions.put(that.id, ImmutablePair.of((ExecutionWork)next.value, that.next));
        }
        this.properties = new THashMap<Element, List<Pair<String, String>>>();
        _addProperties(root, null); //Root should always be a JobImpl which is a PropertiesElement
    }

    private void _addProperties(final VisitorNode node, final List<Pair<String, String>> parent) {
        final List<Pair<String, String>> properties;
        if (node.element instanceof PropertiesElement) {
            properties = new ArrayList<Pair<String, String>>(0);
            for (final Property property : ((PropertiesElement)node.element).getProperties().getProperties()) {
                properties.add(property);
            }
        } else if (node.element instanceof Plan) { //TODO This needs to respect the partition element
            properties = new ArrayList<Pair<String, String>>(0);
            for (final Properties x : ((Plan)node.element).getProperties()) {
                for (final Property property : x.getProperties()) {
                    properties.add(property);
                }
            }
        } else {
            properties = parent;
        }
        this.properties.put(node.element, Collections.unmodifiableList(properties));
        for (final VisitorNode child : node.children) {
            _addProperties(child, properties);
        }
    }

    public ExecutionWork next(final String next) {
        final Pair<ExecutionWork, String> pair =  this.transitions.get(next);
        return pair == null ? null : pair.getName();
    }

    public List<? extends Pair<String, String>> properties(final Element element) {
        final List<? extends Pair<String, String>> properties = this.properties.get(element);
        if (properties == null) {
            log.error("no properties for" + element.getClass().getCanonicalName());
            return Collections.emptyList();
        }
        return properties;
    }
}
