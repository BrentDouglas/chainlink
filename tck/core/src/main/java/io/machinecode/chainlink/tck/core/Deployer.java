package io.machinecode.chainlink.tck.core;

import com.ibm.jbatch.tck.artifacts.chunkartifacts.ConnectionHelper;
import com.ibm.jbatch.tck.artifacts.chunktypes.CheckpointData;
import com.ibm.jbatch.tck.artifacts.common.StatusConstants;
import com.ibm.jbatch.tck.artifacts.reusable.MyCounter;
import com.ibm.jbatch.tck.artifacts.specialized.TransitionDecider;
import com.ibm.jbatch.tck.utils.AssertionUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Deployer {

    public static WebArchive deploy(final Class<?> test, final Class<?>[] classes, final String export, final String[] libraries, final String[] resources, final String[] metaResources, final String[] webResources) {
        try {
            final WebArchive archive = ShrinkWrap.create(WebArchive.class, test.getSimpleName() + ".war")
                    .addClass(test)
                    .addPackage(AssertionUtils.class.getPackage())
                    .addPackage(ConnectionHelper.class.getPackage())
                    .addPackage(CheckpointData.class.getPackage())
                    .addPackage(StatusConstants.class.getPackage())
                    .addPackage(MyCounter.class.getPackage())
                    .addPackage(TransitionDecider.class.getPackage())
                    .addAsManifestResource(new File("target/classes/META-INF/MANIFEST.MF"));
            if (classes != null) {
                for (final Class<?> clazz : classes) {
                    archive.addClass(clazz);
                }
            }
            if (libraries != null) {
                for (final String library : libraries) {
                    archive.addAsLibraries(new File(library));
                }
            }
            if (resources != null) {
                for (final String resource : resources) {
                    archive.addAsResource(new File(resource));
                }
            }
            if (metaResources != null) {
                for (final String resource : metaResources) {
                    final File file = new File(resource);
                    archive.addAsResource(file, "META-INF/" + file.getName());
                }
            }
            if (webResources != null) {
                for (final String resource : webResources) {
                    archive.addAsWebInfResource(new File(resource));
                }
            }
            if (export != null) {
                final File exportDir = new File(export);
                if (exportDir.isDirectory() || exportDir.mkdirs()) {
                    archive.as(ZipExporter.class).exportTo(new File(exportDir, archive.getName()), true);
                } else {
                    throw new RuntimeException(export + " is either not a directory or cannot be created as a directory."); // TODO Message
                }
            }
            return archive;
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
