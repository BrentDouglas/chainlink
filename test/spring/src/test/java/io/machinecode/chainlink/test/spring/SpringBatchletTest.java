package io.machinecode.chainlink.test.spring;

import io.machinecode.chainlink.marshalling.jdk.JdkMarshallingProvider;
import io.machinecode.chainlink.se.configuration.SeConfiguration.Builder;
import io.machinecode.chainlink.inject.spring.SpringArtifactLoader;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.batchlet.BatchletTest;
import org.junit.BeforeClass;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SpringBatchletTest extends BatchletTest {

    private static AbstractApplicationContext context;

    @Override
    protected Builder _configuration() throws Exception {
        return super._configuration()
                .setArtifactLoaders(context.getBean(SpringArtifactLoader.class));
    }
    @Override
    protected ExecutionRepository _repository() {
        return new MemoryExecutionRepository(new JdkMarshallingProvider());
    }

    @BeforeClass
    public static void beforeClass() {
        context = new ClassPathXmlApplicationContext("beans.xml");
    }
}
