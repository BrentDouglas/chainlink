package io.machinecode.nock.spi.util;

import javax.batch.runtime.BatchStatus;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class Message {

    private Message(){}

    private static final ResourceBundle MESSAGES;

    static {
        try {
            MESSAGES = new PropertyResourceBundle(new InputStreamReader(Message.class.getClassLoader().getResourceAsStream("messages.properties")));
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

    public static String executionsRequired() {
        return new Formatter().format(get("validation.executions.required")).toString();
    }

    public static String notNullElement(final String element) {
        return new Formatter().format(get("validation.not.null.element"), element).toString();
    }

    public static String attributeRequired(final String attribute) {
        return new Formatter().format(get("validation.required.attribute"), attribute).toString();
    }

    public static String attributePositive(final String attribute, final int value) {
        return new Formatter().format(get("validation.positive.attribute"), attribute, value).toString();
    }

    public static String attributeMatches(final String attribute, final String value, final String... matches) {
        return new Formatter().format(get("validation.matches.attribute"), attribute, Strings.join(matches), value).toString();
    }

    public static String nonUniqueId(final String id) {
        return new Formatter().format(get("validation.non.unique.id"), id).toString();
    }

    public static String cycleDetected() {
        return new Formatter().format(get("validation.cycle.detected")).toString();
    }

    public static String invalidTransition(final String element) {
        return new Formatter().format(get("validation.invalid.transition"), element).toString();
    }

    public static String cantRestartBatchStatus(final long id, final BatchStatus status) {
        return new Formatter().format(get("validation.cant.restart.batch.status"), id, BatchStatus.STOPPED, BatchStatus.FAILED, status).toString();
    }

    public static String cantRestartJob(final long id) {
        return new Formatter().format(get("validation.cant.restart.job"), id).toString();
    }

    public static String artifactWithWrongClass(final String id, final String clazz, final String as) {
        return new Formatter().format(get("validation.artifact.id.wrong.class"), id, clazz, as).toString();
    }

    public static String cantLoadMatchingArtifact(final String id, final String clazz) {
        return new Formatter().format(get("validation.cant.load.matching.artifact"), id, clazz).toString();
    }
}
