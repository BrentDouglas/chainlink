package io.machinecode.chainlink.inject.guice;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface BindingProvider {

    List<Binding> getBindings();

    class Binding {
        final Class<?> satisfies;
        final String name;
        final Class<?> type;

        public Binding(final Class<?> satisfies, final String name, final Class<?> type) {
            this.satisfies = satisfies;
            this.name = name;
            this.type = type;
        }

        public Class<?> getSatisfies() {
            return satisfies;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public static Binding of(final Class<?> satisfies, final String name, final Class<?> type) {
            return new Binding(satisfies, name, type);
        }
    }
}
