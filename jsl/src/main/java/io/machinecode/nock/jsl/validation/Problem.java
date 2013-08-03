package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.util.Strings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class Problem {

    private Problem(){}

    private static final ResourceBundle PROBLEMS;

    static {
        try {
            PROBLEMS = new PropertyResourceBundle(new InputStreamReader(Problem.class.getClassLoader().getResourceAsStream("problems.properties")));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String message(final String key) {
        return PROBLEMS.getString(key);
    }

    public static String notNullElement(final String element) {
        return new Formatter().format(PROBLEMS.getString("not.null.element"), element).toString();
    }

    public static String attributeRequired(final String attribute) {
        return new Formatter().format(PROBLEMS.getString("required.attribute"), attribute).toString();
    }

    public static String attributePositive(final String attribute, final int value) {
        return new Formatter().format(PROBLEMS.getString("positive.attribute"), attribute, value).toString();
    }

    public static String attributeMatches(final String attribute, final String value, final String... matches) {
        return new Formatter().format(PROBLEMS.getString("matches.attribute"), attribute, Strings.join(matches), value).toString();
    }

    public static String cycleDetected() {
        return new Formatter().format(PROBLEMS.getString("cycle.detected")).toString();
    }

    public static String nonUniqueId(final String id) {
        return new Formatter().format(PROBLEMS.getString("non.unique.id"), id).toString();
    }
}
