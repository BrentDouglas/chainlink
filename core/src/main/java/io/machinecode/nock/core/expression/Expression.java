package io.machinecode.nock.core.expression;

import io.machinecode.nock.core.util.Index;
import io.machinecode.nock.jsl.util.MutablePair;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Expression {

    private static final String EMPTY = "";

    private static final String DEFAULT_START =  "?:";
    private static final String DEFAULT_END =  ";";

    private static final String JOB_PROPERTIES = "#{jobProperties['";
    private static final String SYSTEM_PROPERTIES = "#{systemProperties['";
    private static final String JOB_PARAMETERS = "#{jobParameters['";
    private static final String PARTITION_PLAN = "#{partitionPlan['";

    private static final String AFTER = "']}";

    private static final int DEFAULT_START_LENGTH = DEFAULT_START.length();
    private static final int DEFAULT_END_LENGTH = DEFAULT_END.length();

    private static final int JOB_PROPERTIES_LENGTH = JOB_PROPERTIES.length();
    private static final int SYSTEM_PROPERTIES_LENGTH = SYSTEM_PROPERTIES.length();
    private static final int JOB_PARAMETERS_LENGTH = JOB_PARAMETERS.length();
    private static final int PARTITION_PLAN_LENGTH = PARTITION_PLAN.length();

    private static final int AFTER_LENGTH = AFTER.length();


    private static final PropertyResolver SYSTEM_PROPERTY_RESOLVER = new PropertyResolver() {

        private final List<MutablePair<String,String>> properties;

        {
            this.properties = new ArrayList<MutablePair<String, String>>();
            for (final Entry<Object, Object> entry : System.getProperties().entrySet()) {
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                this.properties.add(MutablePair.of(
                        key instanceof String ? (String)key : key.toString(),
                        value instanceof String ? (String)value : value.toString()
                ));
            }
        }

        @Override
        public CharSequence resolve(final CharSequence value) {
            return get(value, properties);
        }
    };

    private static CharSequence attributeValue(final CharSequence unresolved, final String prefix, final int prefixLength,
                                               final int start, final PropertyResolver resolver) {
        final StringBuilder builder = new StringBuilder();
        final int unresolvedLength = unresolved.length();
        int before = start;
        int after;
        while ((after = principleValueExpression(builder, unresolved, unresolvedLength, prefix, prefixLength, before, resolver)) != before) {
            before = after;
        }
        return builder;
    }

    private static int principleValueExpression(final StringBuilder builder, final CharSequence unresolved, final int unresolvedLength,
                                                final String prefix, final int prefixLength, final int start, final PropertyResolver resolver) {
        final int len = builder.length();
        final int startDefault = _indexOfDefaultExpressionStart(unresolved, unresolvedLength, start);
        final int value = _valueExpressions(builder, unresolved, startDefault, prefix, prefixLength, start, resolver);
        if (value < startDefault) {
            builder.append(unresolved.subSequence(value, startDefault));
        }
        final int endDefault = _indexOfDefaultExpressionEnd(unresolved, unresolvedLength, startDefault);
        final boolean expectsDefault = startDefault < unresolvedLength;
        //If it is not terminated were treating it as a literal so were eating the ?: and starting PrincipalValueExpression again
        if (endDefault == -1 && expectsDefault) {
            final int end = startDefault + DEFAULT_START_LENGTH;
            builder.append(unresolved.subSequence(startDefault, end));
            return end;
        }
        if (expectsDefault && builder.length() == len) {
            return _defaultExpression(builder, unresolved, endDefault, prefix, prefixLength, startDefault, resolver);
        }
        return endDefault == -1 ? unresolvedLength : endDefault;
    }

    /**
     * @return The index of the position for the next scanning function to resume from.
     */
    private static int _valueExpressions(final StringBuilder builder, final CharSequence unresolved, final int unresolvedLength,
                                         final String prefix, final int prefixLength, final int start, final PropertyResolver resolver) {
        int before = start;
        int after;
        while ((after = valueExpression(builder, unresolved, unresolvedLength, prefix, prefixLength, before, resolver)) != before) {
            before = after;
        }
        return after;
    }

    /**
     * @return The index of the position for the next scanning function to resume from.
     */
    private static int valueExpression(final StringBuilder builder, final CharSequence unresolved, final int unresolvedLength,
                                        final String prefix, final int prefixLength, final int start, final PropertyResolver resolver) {
        final int startProperty = Index.of(unresolved, 0, unresolvedLength, prefix, 0, prefixLength, start);
        if (startProperty == -1) {
            return start;
        }
        final int endProperty = Index.of(unresolved, 0, unresolvedLength, AFTER, 0, AFTER_LENGTH, startProperty);
        if (endProperty == -1) {
            return start;
        }

        builder.append(unresolved.subSequence(start, startProperty));

        final CharSequence property = unresolved.subSequence(startProperty + prefixLength, endProperty);
        final CharSequence resolved = resolver.resolve(property);

        if (resolved.length() > 0) {
            builder.append(resolved);
        }
        return endProperty + AFTER_LENGTH;
    }

    /**
     * @return The start index of the default value expression or the end of the sequence.
     */
    private static int _indexOfDefaultExpressionStart(final CharSequence unresolved, final int unresolvedLength, final int start) {
        final int startDefault = Index.of(unresolved, 0, unresolvedLength, DEFAULT_START, 0, DEFAULT_START_LENGTH, start);
        if (startDefault == -1) {
            return unresolvedLength;
        }
        //final int endDefault = Index.of(unresolved, 0, unresolvedLength, DEFAULT_END, 0, DEFAULT_END_LENGTH, startDefault);
        //if (endDefault == -1) {
        //    return unresolvedLength;
        //}
        return startDefault;
    }

    /**
     * @return The start index of the default value expression or the end of the sequence.
     */
    private static int _indexOfDefaultExpressionEnd(final CharSequence unresolved, final int unresolvedLength, final int start) {
        final int endDefault = Index.of(unresolved, 0, unresolvedLength, DEFAULT_END, 0, DEFAULT_END_LENGTH, start);
        if (endDefault == -1) {
            return -1;
        }
        return endDefault + DEFAULT_END_LENGTH;
    }

    /**
     * @return The index of the position for the next scanning function to resume from.
     */
    private static int _defaultExpression(final StringBuilder builder, final CharSequence unresolved, final int unresolvedLength,
                                          final String prefix, final int prefixLength, final int start, final PropertyResolver resolver) {
        final CharSequence defaultValue = unresolved.subSequence(start + DEFAULT_START_LENGTH, unresolvedLength - DEFAULT_END_LENGTH);
        final int defaultValueLength = defaultValue.length();
        int value = _valueExpressions(builder, defaultValue, defaultValueLength, prefix, prefixLength, 0, resolver);
        if (value < defaultValueLength) {
            builder.append(defaultValue.subSequence(value, defaultValueLength));
        }
        return unresolvedLength;
    }

    private static <T extends Pair<String, String>> String get(final CharSequence value, final List<T> properties) {
        final ListIterator<T> it = properties.listIterator(properties.size());
        while (it.hasPrevious()) {
            final T property = it.previous();
            if (value.equals(property.getName())) {
                return property.getValue();
            }
        }
        return EMPTY;
    }

    public static <T extends Pair<String, String>> String resolveExecutionProperty(final String that, final JobPropertyContext context) {
        if (that == null) {
            return null;
        }
        CharSequence then = attributeValue(that, JOB_PROPERTIES, JOB_PROPERTIES_LENGTH, 0, new PropertyResolver() {
            @Override
            public String resolve(final CharSequence value) {
                return get(value, context.getProperties());
            }
        });
        then = attributeValue(then, JOB_PARAMETERS, JOB_PARAMETERS_LENGTH, 0, new PropertyResolver() {
            @Override
            public String resolve(final CharSequence value) {
                return get(value, context.getParameters());
            }
        });
        return attributeValue(then, SYSTEM_PROPERTIES, SYSTEM_PROPERTIES_LENGTH, 0, SYSTEM_PROPERTY_RESOLVER).toString();
    }


    public static <T extends Pair<String, String>> String resolvePartitionProperty(final String that, final PropertyContext context) {
        if (that == null) {
            return null;
        }
        return attributeValue(that, PARTITION_PLAN, PARTITION_PLAN_LENGTH, 0, new PropertyResolver() {
            @Override
            public String resolve(final CharSequence value) {
                return get(value, context.getProperties());
            }
        }).toString();
    }
}
