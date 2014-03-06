package io.machinecode.chainlink.core.expression;

import io.machinecode.chainlink.core.util.Index;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

import java.util.Properties;

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

    private static CharSequence attributeValue(final CharSequence unresolved, final int start, final PropertyResolver... resolvers) {
        final StringBuilder builder = new StringBuilder();
        final int unresolvedLength = unresolved.length();
        int before = start;
        int after;
        while ((after = principleValueExpression(builder, unresolved, unresolvedLength, before, resolvers)) != before) {
            before = after;
        }
        return builder;
    }

    private static int principleValueExpression(final StringBuilder builder, final CharSequence unresolved, final int unresolvedLength,
                                                final int start, final PropertyResolver[] resolvers) {
        final int len = builder.length();
        final int startDefault = _indexOfDefaultExpressionStart(unresolved, unresolvedLength, start);
        final int value = _valueExpressions(builder, unresolved, startDefault,  start, resolvers);
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
            return _defaultExpression(builder, unresolved, endDefault, startDefault, resolvers);
        }
        return endDefault == -1 ? unresolvedLength : endDefault;
    }

    /**
     * @return The index of the position for the next scanning function to resume from.
     */
    private static int _valueExpressions(final StringBuilder builder, final CharSequence unresolved, final int unresolvedLength,
                                         final int start, final PropertyResolver[] resolvers) {
        int before = start;
        int after;
        while ((after = _valueExpression(builder, unresolved, unresolvedLength, before, resolvers)) != before) {
            before = after;
        }
        return after;
    }

    /**
     * @return The index of the position for the next scanning function to resume from.
     */
    private static int _valueExpression(final StringBuilder builder, final CharSequence unresolved, final int unresolvedLength,
                                        final int start, final PropertyResolver[] resolvers) {
        int selectedStartProperty = Integer.MAX_VALUE;
        int endProperty = -1;
        PropertyResolver selectedResolver = null;
        for (final PropertyResolver resolver : resolvers) {
            final int startProperty = Index.of(unresolved, 0, unresolvedLength, resolver.prefix(), 0, resolver.length(), start);
            if (startProperty == -1) {
                continue;
            }
            endProperty = Index.of(unresolved, 0, unresolvedLength, AFTER, 0, AFTER_LENGTH, startProperty);
            if (endProperty == -1) {
                continue;
            }
            if (startProperty < selectedStartProperty) {
                selectedResolver = resolver;
                selectedStartProperty = startProperty;
            }
        }

        if (selectedResolver == null) {
            return start;
        }

        builder.append(unresolved.subSequence(start, selectedStartProperty));

        final CharSequence property = unresolved.subSequence(selectedStartProperty + selectedResolver.length(), endProperty);
        final CharSequence resolved = selectedResolver.resolve(property);

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
                                          final int start, final PropertyResolver[] resolvers) {
        final CharSequence defaultValue = unresolved.subSequence(start + DEFAULT_START_LENGTH, unresolvedLength - DEFAULT_END_LENGTH);
        final int defaultValueLength = defaultValue.length();
        int value = _valueExpressions(builder, defaultValue, defaultValueLength, 0, resolvers);
        if (value < defaultValueLength) {
            builder.append(defaultValue.subSequence(value, defaultValueLength));
        }
        return unresolvedLength;
    }

    public static String resolveExecutionProperty(final String that, final JobPropertyContext context) {
        if (that == null) {
            return null;
        }
        return attributeValue(that, 0,
                new PropertyResolverImpl(JOB_PROPERTIES, JOB_PROPERTIES_LENGTH, context.getProperties()),
                new PropertyResolverImpl(JOB_PARAMETERS, JOB_PARAMETERS_LENGTH, context.getParameters()),
                SYSTEM_PROPERTY_RESOLVER
        ).toString();
    }


    public static String resolvePartitionProperty(final String that, final PropertyContext context) {
        if (that == null) {
            return null;
        }
        return attributeValue(that, 0, new PropertyResolverImpl(PARTITION_PLAN, PARTITION_PLAN_LENGTH, context.getProperties())).toString();
    }

    private static class PropertyResolverImpl implements PropertyResolver {

        private final String prefix;
        private final int length;
        private final Properties properties;

        private PropertyResolverImpl(final String prefix, final int length, final Properties properties) {
            this.prefix = prefix;
            this.length = length;
            this.properties = properties;
        }

        @Override
        public String prefix() {
            return this.prefix;
        }

        @Override
        public int length() {
            return this.length;
        }

        @Override
        public CharSequence resolve(final CharSequence value) {
            if (this.properties == null) {
                return EMPTY;
            }
            final String that = this.properties.getProperty(value.toString());
            return that == null ? EMPTY : that;
        }
    }

    private static final PropertyResolver SYSTEM_PROPERTY_RESOLVER = new PropertyResolverImpl(SYSTEM_PROPERTIES, SYSTEM_PROPERTIES_LENGTH, null) {
        @Override
        public CharSequence resolve(final CharSequence value) {
            final String that = System.getProperty(value.toString());
            return that == null ? EMPTY : that;
        }
    };
}
