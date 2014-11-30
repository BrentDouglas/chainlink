package io.machinecode.chainlink.ee.wildfly;

import io.machinecode.chainlink.core.management.JobOperatorView;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
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
public class WildFlyTest extends Assert {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(JavaArchive.class, WildFlyTest.class.getSimpleName() + ".jar")
                .addClass(WildFlyTest.class)
                .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml");
    }

    @Test
    public void testWildfly() throws Exception {
        final JobOperator operator = BatchRuntime.getJobOperator();
        assertNotNull(operator);
        assertEquals(JobOperatorView.class, operator.getClass());
    }
}
