package io.machinecode.chainlink.core.marshalling;

import io.machinecode.chainlink.spi.marshalling.Marshalling;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MarshallingTest extends Assert {

    Marshalling marshalling;

    @Before
    public void before() throws Exception {
        marshalling = create();
    }

    protected Marshalling create() throws Exception {
        return new JdkMarshallingFactory().produce(null, null);
    }

    @Test
    public void testMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final String in = "asdf";
            final byte[] bytes = marshalling.marshall(in);
            final String out = marshalling.unmarshall(bytes, String.class, tccl);

            assertEquals(in, out);
            assertNotSame(in, out);
        }
        {
            final long in = 3465;
            final byte[] bytes = marshalling.marshallLong(in);
            final long out = marshalling.unmarshallLong(bytes, tccl);

            assertEquals(in, out);
        }

        try {
            marshalling.unmarshallLong(null, tccl);
            fail();
        } catch (final Exception e) {
            //
        }

        {
            final String[] in = new String[] {"asd","sdf","dfg"};
            final byte[] bytes = marshalling.marshall(in);
            final String[] out = marshalling.unmarshall(bytes, String[].class, tccl);
            assertArrayEquals(in, out);
            assertNotSame(in, out);
        }
        {
            final byte[] out = marshalling.marshall(null);
            assertNull(out);
        }
        {
            final String[] out = marshalling.unmarshall(null, String[].class, tccl);
            assertNull(out);
        }
    }

    @Test
    public void testByteArrayMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final byte[] in = "asdf".getBytes(StandardCharsets.UTF_8);
            final byte[] bytes = marshalling.marshall(in);
            final byte[] out = marshalling.unmarshall(bytes, byte[].class, tccl);
            assertArrayEquals(in, out);
            assertNotSame(in, out);
        }
    }

    @Test
    public void testShortArrayMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final short[] in = new short[]{1,2,3};
            final byte[] bytes = marshalling.marshall(in);
            final short[] out = marshalling.unmarshall(bytes, short[].class, tccl);
            assertArrayEquals(in, out);
            assertNotSame(in, out);
        }
    }

    @Test
    public void testCharArrayMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final char[] in = new char[]{1,2,3};
            final byte[] bytes = marshalling.marshall(in);
            final char[] out = marshalling.unmarshall(bytes, char[].class, tccl);
            assertArrayEquals(in, out);
            assertNotSame(in, out);
        }
    }

    @Test
    public void testIntArrayMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final int[] in = new int[]{1,2,3};
            final byte[] bytes = marshalling.marshall(in);
            final int[] out = marshalling.unmarshall(bytes, int[].class, tccl);
            assertArrayEquals(in, out);
            assertNotSame(in, out);
        }
    }

    @Test
    public void testLongArrayMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final long[] in = new long[]{1,2,3};
            final byte[] bytes = marshalling.marshall(in);
            final long[] out = marshalling.unmarshall(bytes, long[].class, tccl);
            assertArrayEquals(in, out);
            assertNotSame(in, out);
        }
    }

    @Test
    public void testFloatArrayMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final float[] in = new float[]{1,2,3};
            final byte[] bytes = marshalling.marshall(in);
            final float[] out = marshalling.unmarshall(bytes, float[].class, tccl);
            assertArrayEquals(in, out, 0.01f);
            assertNotSame(in, out);
        }
    }

    @Test
    public void testDoubleArrayMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final double[] in = new double[]{1,2,3};
            final byte[] bytes = marshalling.marshall(in);
            final double[] out = marshalling.unmarshall(bytes, double[].class, tccl);
            assertArrayEquals(in, out, 0.01f);
            assertNotSame(in, out);
        }
    }

    @Test
    public void testBooleanArrayMarshalling() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        {
            final boolean[] in = new boolean[]{true,false,true};
            final byte[] bytes = marshalling.marshall(in);
            final boolean[] out = marshalling.unmarshall(bytes, boolean[].class, tccl);
            assertEquals(in.length, out.length);
            for (int i = 0; i < in.length; ++i) {
                assertTrue(in[i] == out[i]);
            }
            assertNotSame(in, out);
        }
    }

    @Test
    public void testClonableClone() throws Exception {
        final CloneableValue in = new CloneableValue("asdf");
        final CloneableValue out = marshalling.clone(in);
        final CloneableValue nout = marshalling.clone(null);

        assertNull(nout);
        assertEquals(in, out);
        assertNotSame(in, out);
        assertSame(in.value, out.value);
    }

    @Test
    public void testSerializableClone() throws Exception {
        final SerializableValue in = new SerializableValue("asdf");
        final SerializableValue out = marshalling.clone(in);
        final SerializableValue nout = marshalling.clone(null);

        assertNull(nout);
        assertEquals(in, out);
        assertNotSame(in, out);
    }

    private static class SerializableValue implements Serializable {

        String value;

        private SerializableValue() {
        }

        private SerializableValue(final String value) {
            this.value = value;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final SerializableValue that = (SerializableValue) o;

            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    private static class CloneableValue implements Cloneable {

        String value;

        private CloneableValue() {
        }

        private CloneableValue(final String value) {
            this.value = value;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            final CloneableValue that = (CloneableValue)super.clone();
            that.value = value;
            return that;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final CloneableValue that = (CloneableValue) o;

            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }
}
