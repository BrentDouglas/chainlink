package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.core.expression.PropertyContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CopyTest extends Assert {

    @Test
    public void testImmutableCopy() {
        {
            final List<String> in = new ArrayList<String>() {{
                add("foo");
                add("bar");
                add("baz");
            }};
            final List<String> out = Copy.immutableCopy(in, null, new Copy.ExpressionTransformer<String,String,PropertyContext>() {
                @Override
                public String transform(final String that, final PropertyContext context) {
                    return that;
                }
            });
            assertEquals(3, out.size());
            assertTrue(out.contains("foo"));
            assertTrue(out.contains("bar"));
            assertTrue(out.contains("baz"));
            try {
                out.remove("foo");
                fail();
            } catch (final UnsupportedOperationException e) {
                //
            }

            in.remove("foo");
            assertTrue(out.contains("foo"));
        }
        {
            final List<String> in = new ArrayList<String>() {{
                add("foo");
                add("bar");
                add("baz");
            }};
            final List<String> out = Copy.immutableCopy(in, null, new Copy.ExpressionTransformer<String,String,PropertyContext>() {
                @Override
                public String transform(final String that, final PropertyContext context) {
                    return that.substring(1);
                }
            });
            assertEquals(3, out.size());
            assertTrue(out.contains("oo"));
            assertTrue(out.contains("ar"));
            assertTrue(out.contains("az"));
            try {
                out.remove("oo");
                fail();
            } catch (final UnsupportedOperationException e) {
                //
            }

            in.remove("foo");
            assertTrue(out.contains("oo"));
        }
        {
            final List<String> in = new ArrayList<String>() {{
                add("foo");
                add("bar");
                add("baz");
            }};
            final List<String> out = Copy.immutableCopy(in, null, new Copy.ExpressionTransformer<String,String,PropertyContext>() {
                @Override
                public String transform(final String that, final PropertyContext context) {
                    return null;
                }
            });
            assertEquals(0, out.size());
            try {
                out.remove("foo");
                fail();
            } catch (final UnsupportedOperationException e) {
                //
            }
        }
        assertNotNull(Copy.immutableCopy(null, null, new Copy.ExpressionTransformer<String,String,PropertyContext>() {
            @Override
            public String transform(final String that, final PropertyContext context) {
                return null;
            }
        }));
    }
}
