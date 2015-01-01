package io.machinecode.chainlink.core.marshalling;

import io.machinecode.chainlink.spi.marshalling.Marshalling;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

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
        final String in = "asdf";
        final byte[] bytes = marshalling.marshall(in);

        final String out = marshalling.unmarshall(bytes, String.class, tccl);

        assertEquals(in, out);
        assertNotSame(in, out);
    }

    @Test
    public void testClonableClone() throws Exception {
        final CloneableValue in = new CloneableValue("asdf");
        final CloneableValue out = marshalling.clone(in);

        assertEquals(in, out);
        assertNotSame(in, out);
        assertSame(in.value, out.value);
    }

    @Test
    public void testSerializableClone() throws Exception {
        final SerializableValue in = new SerializableValue("asdf");
        final SerializableValue out = marshalling.clone(in);

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
