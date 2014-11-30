package io.machinecode.chainlink.tck.core.loader;

import io.machinecode.chainlink.inject.spring.SpringArtifactLoader;
import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class SpringArtifactLoaderFactory implements ArtifactLoaderFactory {

    private static AbstractApplicationContext context;

    static {
        context = new ClassPathXmlApplicationContext("beans.xml");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                context.close();
            }
        }));
    }
    @Override
    public ArtifactLoader produce(final LoaderConfiguration configuration) {
        return context.getBean(SpringArtifactLoader.class);
    }
}
