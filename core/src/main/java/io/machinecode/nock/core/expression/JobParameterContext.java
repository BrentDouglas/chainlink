package io.machinecode.nock.core.expression;

import io.machinecode.nock.jsl.util.MutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobParameterContext implements PropertyContext {

    private final List<MutablePair<String,String>> parameters;

    public JobParameterContext(final java.util.Properties parameters) {
        final List<MutablePair<String, String>> that = new ArrayList<MutablePair<String, String>>();
        for (final Entry<Object, Object> entry : parameters.entrySet()) {
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            that.add(MutablePair.of(
                    key instanceof String ? (String)key : key.toString(),
                    value instanceof String ? (String)value : value.toString()
            ));
        }
        this.parameters = Collections.unmodifiableList(that);
    }

    @Override
    public List<MutablePair<String, String>> getProperties() {
        return this.parameters;
    }
}
