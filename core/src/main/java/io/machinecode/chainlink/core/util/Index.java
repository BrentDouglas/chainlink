package io.machinecode.chainlink.core.util;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Index {

    /**
     * Straight out of java.lang.String
     *
     * @see java.lang.String#indexOf(char[], int, int, char[], int, int, int)
     *
     * @return  the index of the first occurrence of the specified substring,
     *          starting at the specified index,
     *          or {@code -1} if there is no such occurrence.
     */
    public static int of(final CharSequence source, int sourceOffset, int sourceCount,
                  final CharSequence target, int targetOffset, int targetCount,
                  int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target.charAt(targetOffset);
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source.charAt(i) != first) {
                while (++i <= max && source.charAt(i) != first){}
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source.charAt(j) == target.charAt(k); j++, k++){}

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }
}
