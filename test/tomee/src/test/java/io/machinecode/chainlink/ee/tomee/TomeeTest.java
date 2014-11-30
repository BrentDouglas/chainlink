package io.machinecode.chainlink.ee.tomee;

import io.machinecode.chainlink.core.management.JobOperatorView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
@RunWith(Arquillian.class)
public class TomeeTest extends Assert {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(WebArchive.class, TomeeTest.class.getSimpleName() + ".war")
                .addClass(TomeeTest.class);
    }

    @Test
    public void testTomee() throws Exception {
        final JobOperator operator = BatchRuntime.getJobOperator();
        assertNotNull(operator);
        assertEquals(JobOperatorView.class, operator.getClass());
    }
}
