package io.machinecode.nock.spi.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class Messages {

    private Messages(){}

    private static final ResourceBundle MESSAGES;

    static {
        try {
            MESSAGES = new PropertyResourceBundle(new InputStreamReader(Messages.class.getClassLoader().getResourceAsStream("messages.properties")));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String get(final String key) {
        return MESSAGES.getString(key);
    }

    public static String format(final String key, final Object... args) {
        return new Formatter().format(get(key), args).toString();
    }
}
