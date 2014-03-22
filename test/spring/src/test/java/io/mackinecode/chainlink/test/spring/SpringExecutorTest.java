package io.mackinecode.chainlink.test.spring;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.inject.spring.SpringArtifactLoader;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.ExecutorTest;
import org.junit.BeforeClass;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SpringExecutorTest extends ExecutorTest {

    private static AbstractApplicationContext context;

    @Override
    protected Builder _configuration() throws Exception {
        return super._configuration()
                .setArtifactLoaders(context.getBean(SpringArtifactLoader.class));
    }
    @Override
    protected ExecutionRepository _repository() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        return new MemoryExecutionRepository(tccl);
    }

    @BeforeClass
    public static void beforeClass() {
        context = new ClassPathXmlApplicationContext("beans.xml");
    }
}
