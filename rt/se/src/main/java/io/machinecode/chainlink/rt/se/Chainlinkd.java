package io.machinecode.chainlink.rt.se;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.Messages;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import static java.lang.System.out;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Chainlinkd implements AutoCloseable, Callable<Void> {

    private static final Logger log = Logger.getLogger(Chainlinkd.class);

    public static void main(final String... args) throws Throwable {
        try {
            final Getopt opt = new Getopt("chainlinkd", args, "c:p:h", new LongOpt[]{
                    new LongOpt("configuration", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
                    new LongOpt("properties", LongOpt.REQUIRED_ARGUMENT, null, 'p'),
                    new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h')
            });

            String config = Constants.Defaults.CHAINLINK_XML;
            String props = null;

            int c;
            while ((c = opt.getopt()) != -1) {
                switch (c) {
                    case 'c':
                        config = opt.getOptarg();
                        break;
                    case 'p':
                        props = opt.getOptarg();
                        break;
                    case 'h':
                    default:
                        _usage();
                        return;
                }
            }
            if (props != null) {
                final File propertiesFile = new File(props);
                if (propertiesFile.exists()) {
                    final Properties sp = System.getProperties();
                    final Properties properties = new Properties();
                    properties.load(new FileInputStream(propertiesFile));
                    //TODO Should passed in properties override ones from the file?
                    for (final String prop : sp.stringPropertyNames()) {
                        properties.put(prop, sp.getProperty(prop));
                    }
                    System.setProperties(properties);
                }
            }
            new Chainlinkd(config).call();
        } catch (final Throwable e) {
            log.fatalf(e, Messages.get("CHAINLINK-032001.chainlinkd.exception"));
            System.exit(1);
        }
    }

    private static void _usage() {
        out.println("Usage: chainlinkd -c|--configuration config_file_name -p|--properties properties_file_name -h|--help");
    }

    public Chainlinkd(final String config) {
        this.config = config;
    }

    private final String config;
    private final Object lock = new Object();

    private volatile boolean running = true;

    @Override
    public Void call() throws Exception {
        try (final SeEnvironment environment = new SeEnvironment(config)) {
            //TODO Should be able to configure eager or lazy start
            environment.loadConfiguration();
            Chainlink.setEnvironment(environment);
            log.info(Messages.get("CHAINLINK-032000.chainlinkd.started"));
            while (running) {
                synchronized (lock) {
                    lock.wait();
                }
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        running = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
