package io.machinecode.chainlink.core.jsl.impl.task;

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClassFilter;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExceptionClassFilterImpl implements ExceptionClassFilter, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ExceptionClassFilterImpl.class);

    private final List<ExceptionClassImpl> includes;
    private final List<ExceptionClassImpl> excludes;

    public ExceptionClassFilterImpl(final List<ExceptionClassImpl> includes, final List<ExceptionClassImpl> excludes) {
        this.includes = includes == null ? Collections.<ExceptionClassImpl>emptyList() : includes;
        this.excludes = excludes == null ? Collections.<ExceptionClassImpl>emptyList() : excludes;
    }

    @Override
    public List<ExceptionClassImpl> getIncludes() {
        return this.includes;
    }

    @Override
    public List<ExceptionClassImpl> getExcludes() {
        return this.excludes;
    }

    public boolean matches(final Exception exception) throws ClassNotFoundException {
        final Class<?> clazz = exception.getClass();
        final ClassLoader loader = clazz.getClassLoader();
        for (final ExceptionClassImpl that : excludes) {
            try {
                if (that.matches(clazz, loader)) {
                    return false;
                }
            } catch (final ClassNotFoundException e) {
                // If it's not available in the exception's classloader, clearly it is not a match
            }
        }
        for (final ExceptionClassImpl that : includes) {
            try {
                if (that.matches(clazz, loader)) {
                    log.tracef(Messages.get("CHAINLINK-026000.exception.filter.matched"), that.getClassName(), clazz);
                    return true;
                }
            } catch (final ClassNotFoundException e) {
                // As above
            }
        }
        return false;
    }
}
