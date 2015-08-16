/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.expression;

import io.machinecode.chainlink.core.util.Index;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Expression {

    static final String DEFAULT_START =  "?:";
    static final String DEFAULT_END =  ";";

    static final String JOB_PROPERTIES = "#{jobProperties['";
    static final String SYSTEM_PROPERTIES = "#{systemProperties['";
    static final String JOB_PARAMETERS = "#{jobParameters['";
    static final String PARTITION_PLAN = "#{partitionPlan['";

    static final String AFTER = "']}";

    static final int DEFAULT_START_LENGTH = DEFAULT_START.length();
    static final int DEFAULT_END_LENGTH = DEFAULT_END.length();

    static final int JOB_PROPERTIES_LENGTH = JOB_PROPERTIES.length();
    static final int SYSTEM_PROPERTIES_LENGTH = SYSTEM_PROPERTIES.length();
    static final int JOB_PARAMETERS_LENGTH = JOB_PARAMETERS.length();
    static final int PARTITION_PLAN_LENGTH = PARTITION_PLAN.length();

    static final int AFTER_LENGTH = AFTER.length();

    private static CharSequence attributeValue(final CharSequence unresolved, final int start, final Resolver... resolvers) {
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
                                                final int start, final Resolver[] resolvers) {
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
                                         final int start, final Resolver[] resolvers) {
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
                                        final int start, final Resolver[] resolvers) {
        int selectedStartProperty = Integer.MAX_VALUE;
        int endProperty = -1;
        Resolver selectedResolver = null;
        for (final Resolver resolver : resolvers) {
            final int startProperty = Index.of(unresolved, 0, unresolvedLength, resolver.prefix, 0, resolver.length, start);
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

        final CharSequence property = unresolved.subSequence(selectedStartProperty + selectedResolver.length, endProperty);
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
                                          final int start, final Resolver[] resolvers) {
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
                context.properties,
                context.parameters,
                context.system
        ).toString();
    }

    public static String resolvePartitionProperty(final String that, final PartitionPropertyContext context) {
        if (that == null) {
            return null;
        }
        return attributeValue(that, 0, context.properties).toString();
    }
}
