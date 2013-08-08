package io.machinecode.nock.core.expression;

import io.machinecode.nock.jsl.util.Pair;

import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Expression {

    private static final String EMPTY = "";

    private static final String EXPRESSION_START = "#{";
    private static final String PROPERTY_START = "['";
    private static final String PROPERTY_END =  "']";
    private static final String EXPRESSION_END =  "}";
    private static final String DEFAULT_VALUE_START =  "?:";
    private static final String DEFAULT_VALUE_END =  ";";

    private static final String JOB_PROPERTIES = "jobProperties";
    private static final String SYSTEM_PROPERTIES = "systemProperties";
    private static final String JOB_PARAMETERS = "jobParameters";
    private static final String PARTITION_PLAN = "partitionPlan";

    private static final PropertyResolver SYSTEM_PROPERTY_RESOLVER = new PropertyResolver() {
        @Override
        public String resolve(final String value) {
            return get(value, System.getProperties());
        }
    };


    private static String resolve(final String unresolved, final String type, final PropertyResolver from) {
        final String beforeProperty = EXPRESSION_START + type + PROPERTY_START;
        final int startProperty = unresolved.indexOf(beforeProperty);
        if (startProperty < 0) {
            return unresolved;
        }
        final String afterProperty = PROPERTY_END + EXPRESSION_END;
        final int endProperty = unresolved.indexOf(afterProperty);
        if (endProperty < 0) {
            return unresolved;
        }
        final StringBuilder builder = new StringBuilder()
                .append(unresolved.substring(0, startProperty + beforeProperty.length()));

        final String property = unresolved.substring(startProperty + beforeProperty.length(), endProperty);
        final String resolved = from.resolve(property);

        if (!EMPTY.equals(resolved)) {
            builder.append(resolved);
        }

        final int startDefault = unresolved.indexOf(DEFAULT_VALUE_START, endProperty + (PROPERTY_END + EXPRESSION_END).length());
        if (startDefault < 0) {
            return unresolved;
        }
        final int endDefault = unresolved.indexOf(DEFAULT_VALUE_END);
        if (endDefault < 0) {
            return unresolved;
        }
        builder.append(from.resolve(unresolved.substring(startDefault + DEFAULT_VALUE_START.length(), endDefault)));

        return builder.toString();
    }

    private static <T extends Pair<String, String>> String get(final String value, final List<T> properties) {
        final ListIterator<T> it = properties.listIterator(properties.size());
        while (it.hasPrevious()) {
            final T property = it.previous();
            if (value.equals(property.getKey())) {
                return property.getValue();
            }
        }
        return EMPTY;
    }

    private static String get(final String value, final Properties parameters) {
        for (final Entry<Object, Object> entry : parameters.entrySet()) {
            if (value.equals(entry.getKey().toString())) {
                return entry.getValue().toString();
            }
        }
        return EMPTY;
    }

    public static <T extends Pair<String, String>> String resolveBuildTime(final String that, final List<T> properties) {
        if (that == null) {
            return null;
        }
        final String then = resolve(that, JOB_PROPERTIES, new PropertyResolver() {
            @Override
            public String resolve(final String value) {
                return get(value, properties);
            }
        });
        return resolve(then, SYSTEM_PROPERTIES, SYSTEM_PROPERTY_RESOLVER);
    }

    public static String resolveStartTime(final String that, final Properties parameters) {
        if (that == null) {
            return null;
        }
        return resolve(that, JOB_PARAMETERS, new PropertyResolver() {
            @Override
            public String resolve(final String value) {
                return get(value, parameters);
            }
        });
    }

    public static <T extends Pair<String, String>> String resolvePartition(final String that, final List<T> properties) {
        if (that == null) {
            return null;
        }
        return resolve(that, PARTITION_PLAN, new PropertyResolver() {
            @Override
            public String resolve(final String value) {
                return get(value, properties);
            }
        });
    }
}
