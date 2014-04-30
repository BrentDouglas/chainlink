package io.machinecode.chainlink.server.chainlinkd;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.configuration.XmlChainlink;
import io.machinecode.chainlink.core.configuration.XmlConfiguration;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
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

            final Environment environment;
            final List<JobOperatorImpl> operators = new ArrayList<JobOperatorImpl>();
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
            if (config != null) {
                final JAXBContext context = JAXBContext.newInstance(XmlChainlink.class);
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                final XmlChainlink xml = (XmlChainlink) unmarshaller.unmarshal(new File(config));
                environment = (Environment) Class.forName(xml.getEnvironment().getClazz()).newInstance();

                for (final XmlConfiguration configuration : xml.getConfigurations()) {
                    final JobOperatorImpl operator = new JobOperatorImpl(configuration.produce());
                    operator.startup();
                    operators.add(operator);
                }
            } else {
                environment = Chainlink.environment();
            }
            for (final String id : Chainlink.configurations()) {
                environment.getJobOperator(id);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    for (final JobOperatorImpl operator : operators) {
                        operator.shutdown();
                    }
                }
            }));
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
