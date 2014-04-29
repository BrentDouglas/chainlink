package io.machinecode.chainlink.server.chainlinkd;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.core.configuration.XmlChainlink;
import io.machinecode.chainlink.core.configuration.XmlConfiguration;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.management.Environment;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Chainlinkd {

    public static void main(final String... args) throws Throwable {
        final Getopt opt = new Getopt("chainlinkd", args, "c:h", new LongOpt[]{
                new LongOpt("configuration", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h')
        });

        String config = null;

        int c;
        while ((c = opt.getopt()) != -1) {
            switch (c) {
                case 'c':
                    config = opt.getOptarg();
                    break;
                case 'h':
                default:
                    _usage();
                    return;
            }
        }

        final Environment environment;
        final List<JobOperatorImpl> operators = new ArrayList<JobOperatorImpl>();
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
        final Object lock = new Object();
        while (true) {
            synchronized (lock) {
                lock.wait();
            }
        }
    }

    private static void _usage() {
        out.println("Usage: chainlinkd -c|--configuration config_file_name -h|--help");
    }
}
