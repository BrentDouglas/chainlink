package io.machinecode.nock.spi.util;

import java.util.Collection;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Strings {

    public static String join(final Collection<String> set) {
        final StringBuilder builder = new StringBuilder();
        for (final String that : set) {
            if (builder.length() != 0) {
                builder.append(',');
            }
            builder.append('\'')
                    .append(that)
                    .append('\'');
        }
        return builder.toString();
    }

    public static String join(final String... strings) {
        final StringBuilder builder = new StringBuilder();
        for (final String that : strings) {
            if (builder.length() != 0) {
                builder.append(',');
            }
            builder.append('\'')
                    .append(that)
                    .append('\'');
        }
        return builder.toString();
    }
}
