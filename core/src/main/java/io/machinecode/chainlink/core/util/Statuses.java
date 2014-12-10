package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import org.jboss.logging.Logger;

import javax.batch.runtime.BatchStatus;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Statuses {

    private static final Logger log = Logger.getLogger(Statuses.class);

    /**
     * @see java.util.regex.Pattern#quote(String)
     *
     * @param source Sequence to quote
     * @param start  Start index of the sequence to quote
     * @param end    End index of the sequence to quote
     * @param query  builder to accept the quoted pattern
     */
    private static void quote(final CharSequence source, final int start, final int end, final StringBuilder query) {
        int slashEIndex = Index.of(source, 0, end,  "\\E", 0, 2, start);
        if (slashEIndex == -1) {
            query.append("\\Q")
                    .append(source.subSequence(start, end))
                    .append("\\E");
            return;
        }

        query.append("\\Q");
        int current = 0;
        while ((slashEIndex = Index.of(source, 0, end,  "\\E", 0, 2, current)) != -1) {
            query.append(source.subSequence(current, slashEIndex));
            current = slashEIndex + 2;
            query.append("\\E\\\\E\\Q");
        }
        query.append(source.subSequence(current, source.length()));
        query.append("\\E");
    }

    private static int find(final CharSequence reference, int start, final int length) {
        for (int i = start; i < length; ++i) {
            switch (reference.charAt(i)) {
                case '*':
                case '?':
                    return i;
            }
        }
        return length;
    }

    public static boolean matches(final CharSequence reference, final CharSequence target) {
        final int rl = reference.length();

        final StringBuilder query = new StringBuilder();
        for (int ri = 0; ri < rl; ++ri) {
            final char r = reference.charAt(ri);
            switch (r) {
                case '*':
                    query.append(".*");
                    break;
                case '?':
                    query.append(".{1}");
                    break;
                default:
                    final int end = find(reference, ri + 1, rl);
                    quote(reference, ri, end, query);
                    ri = end - 1;
            }
        }
        return Pattern.compile(query.toString())
                .matcher(target)
                .matches();
    }

    public static boolean matches(final CharSequence reference, final BatchStatus target) {
        return matches(reference, target.name());
    }

    public static boolean isStopping(final BatchStatus status) {
        return BatchStatus.STOPPING.equals(status);
    }

    public static boolean isStopping(final ExecutionContext context) {
        return BatchStatus.STOPPING.equals(context.getJobContext().getBatchStatus());
    }

    public static boolean isComplete(final ExecutionContext context) {
        return isComplete(context.getJobContext().getBatchStatus());
    }

    public static boolean isComplete(final BatchStatus status) {
        return BatchStatus.COMPLETED.equals(status)
                || BatchStatus.FAILED.equals(status)
                || BatchStatus.STOPPED.equals(status)
                || BatchStatus.ABANDONED.equals(status);
    }

    public static boolean isFailed(final BatchStatus status) {
        return BatchStatus.FAILED.equals(status);
    }
}
