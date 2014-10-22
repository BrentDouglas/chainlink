package io.machinecode.chainlink.server.chainlinkd;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.se.management.SeEnvironment;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static java.lang.System.out;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Chainlinkd {

    private static final Logger log = Logger.getLogger(Chainlinkd.class);

    public static void main(final String... args) throws Throwable {
        try {
            final Getopt opt = new Getopt("chainlinkd", args, "c:p:h", new LongOpt[]{
                    new LongOpt("configuration", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
                    new LongOpt("properties", LongOpt.REQUIRED_ARGUMENT, null, 'p'),
                    new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h')
            });

            String config = null;
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
            final SeEnvironment environment = new SeEnvironment(config);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    Exception exception = null;
                    for (final JobOperatorImpl operator : environment.getJobOperators().values()) {
                        try {
                            operator.close();
                        } catch (final Exception e) {
                            if (exception == null) {
                                exception = e;
                            } else {
                                exception.addSuppressed(e);
                            }
                        }
                    }
                    if (exception != null) {
                        throw new RuntimeException(exception);
                    }
                }
            }));
            Chainlink.setEnvironment(environment);
            log.info(Messages.get("CHAINLINK-032000.chainlinkd.started"));
            final Object lock = new Object();
            while (true) {
                synchronized (lock) {
                    lock.wait();
                }
            }
        } catch (final Throwable e) {
            log.fatalf(e, Messages.get("CHAINLINK-032001.chainlinkd.exception"));
        }
    }

    private static void _usage() {
        out.println("Usage: chainlinkd -c|--configuration config_file_name -p|--properties properties_file_name -h|--help");
    }
}
