package io.machinecode.chainlink.core.util;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Strings {

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
