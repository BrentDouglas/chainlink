package io.machinecode.chainlink.tck.core;

import com.ibm.jbatch.tck.artifacts.chunkartifacts.ConnectionHelper;
import com.ibm.jbatch.tck.artifacts.chunktypes.CheckpointData;
import com.ibm.jbatch.tck.artifacts.common.StatusConstants;
import com.ibm.jbatch.tck.artifacts.reusable.MyCounter;
import com.ibm.jbatch.tck.artifacts.specialized.TransitionDecider;
import com.ibm.jbatch.tck.utils.AssertionUtils;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class ArquillianInstrument {

    private static void _usage() {
        System.out.println("Usage: java " + ArquillianInstrument.class.getName());
        System.out.println("    -c|--configuration-factory fqcn");
        System.out.println("    -i|--instrument path_to_class_file");
        System.out.println("   [-e|--export path_to_export_to]");
        System.out.println("   [-l|--library path_to_library]");
        System.out.println("   [-r|--resource path_to_resource]");
        System.out.println("   [-m|--meta-inf-resource path_to_resource]");
        System.out.println("   [-w|--web-inf-resource path_to_resource]");
        System.out.println("   [-h|--help]");
    }

    public static void main(final String[] args) throws Exception {

        final Getopt opt = new Getopt("arquillian-instrument", args, "c:i:e:l:r:m:w:h", new LongOpt[]{
                new LongOpt("configuration-factory", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
                new LongOpt("instrument", LongOpt.REQUIRED_ARGUMENT, null, 'i'),
                new LongOpt("export", LongOpt.REQUIRED_ARGUMENT, null, 'e'),
                new LongOpt("library", LongOpt.REQUIRED_ARGUMENT, null, 'l'),
                new LongOpt("resource", LongOpt.REQUIRED_ARGUMENT, null, 'r'),
                new LongOpt("meta-inf-resource", LongOpt.REQUIRED_ARGUMENT, null, 'm'),
                new LongOpt("web-inf-resource", LongOpt.REQUIRED_ARGUMENT, null, 'w'),
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h')
        });

        final ClassPool pool = ClassPool.getDefault();
        String configurationFactory = null;
        String export = null;
        final Set<String> instrument = new HashSet<String>();
        final StringBuilder lb = new StringBuilder("new String[]{");
        final StringBuilder rb = new StringBuilder("new String[]{");
        final StringBuilder mb = new StringBuilder("new String[]{");
        final StringBuilder wb = new StringBuilder("new String[]{");
        boolean lf = true;
        boolean rf = true;
        boolean mf = true;
        boolean wf = true;
        int c;
        while ((c = opt.getopt()) != -1) {
            switch (c) {
                case 'c':
                    configurationFactory = opt.getOptarg();
                    break;
                case 'i':
                    instrument.add(opt.getOptarg());
                    break;
                case 'e':
                    //Super robust...
                    export = "\"" + opt.getOptarg() + "\"";
                    break;
                case 'l':
                    if (lf) {
                        lb.append('"').append(opt.getOptarg()).append('"');
                        lf = false;
                    } else {
                        lb.append(',').append('"').append(opt.getOptarg()).append('"');
                    }
                    break;
                case 'r':
                    if (rf) {
                        rb.append('"').append(opt.getOptarg()).append('"');
                        rf = false;
                    } else {
                        rb.append(',').append('"').append(opt.getOptarg()).append('"');
                    }
                    break;
                case 'm':
                    if (mf) {
                        mb.append('"').append(opt.getOptarg()).append('"');
                        mf = false;
                    } else {
                        mb.append(',').append('"').append(opt.getOptarg()).append('"');
                    }
                    break;
                case 'w':
                    if (wf) {
                        wb.append('"').append(opt.getOptarg()).append('"');
                        wf = false;
                    } else {
                        wb.append(',').append('"').append(opt.getOptarg()).append('"');
                    }
                    break;
                case 'h':
                default:
                    _usage();
                    return;
            }
        }
        final String libraries = lf ? "null" : lb.append('}').toString();
        final String resources = rf ? "null" : rb.append('}').toString();
        final String metaResources = mf ? "null" : mb.append('}').toString();
        final String webResources = wf ? "null" : wb.append('}').toString();

        for (final String file : instrument) {
            final InputStream stream = new FileInputStream(file);
            final CtClass test = pool.makeClass(stream);

            instrument(test, configurationFactory, export, libraries, resources, metaResources, webResources);

            final DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
            try {
                out.write(test.toBytecode());
                System.out.println("Instrumented " + test.getName());
            } finally {
                out.close();
            }
        }
    }

    public static void instrument(final CtClass test, final String configurationFactory, final String export, final String libraries, final String resources, final String metaResources, final String webResources) throws IOException, CannotCompileException, NotFoundException, ClassNotFoundException {
        final ConstPool constPool = test.getClassFile().getConstPool();

        //This should also remove the @Ignore from TransactionTests
        final AnnotationsAttribute runWith = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        final Annotation rw = new Annotation("org.junit.runner.RunWith", constPool);
        rw.addMemberValue("value", new ClassMemberValue("org.jboss.arquillian.junit.Arquillian", constPool));
        runWith.addAnnotation(rw);
        test.getClassFile().addAttribute(runWith);

        for (final CtMethod method : test.getMethods()) {
            final AnnotationsAttribute annotations = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            if (method.getAnnotation(org.junit.Test.class) != null
                    || method.getAnnotation(org.testng.annotations.Test.class) != null) {
                annotations.addAnnotation(new Annotation("org.junit.Test", constPool));
            }
            if (method.getAnnotation(org.junit.Ignore.class) != null) {
                annotations.addAnnotation(new Annotation("org.junit.Ignore", constPool));
            }
            if (method.getAnnotation(org.junit.BeforeClass.class) != null
                    || method.getAnnotation(org.testng.annotations.BeforeClass.class) != null
                    || method.getAnnotation(org.testng.annotations.BeforeTest.class) != null
                    || method.getAnnotation(org.testng.annotations.BeforeMethod.class) != null) {
                annotations.addAnnotation(new Annotation("org.junit.Before", constPool));
            }
            if (method.getAnnotation(org.junit.AfterClass.class) != null
                    || method.getAnnotation(org.testng.annotations.AfterClass.class) != null
                    || method.getAnnotation(org.testng.annotations.AfterTest.class) != null
                    || method.getAnnotation(org.testng.annotations.AfterMethod.class) != null) {
                annotations.addAnnotation(new Annotation("org.junit.After", constPool));
            }
            if (Modifier.isStatic(method.getModifiers())) {
                method.setModifiers(Modifier.PUBLIC);
                method.addLocalVariable("this", test);
            }
            method.getMethodInfo().addAttribute(annotations);
        }

        final CtMethod deploy = new CtMethod(ClassPool.getDefault().get(JavaArchive.class.getName()), "__chainlink_deploy", new CtClass[0], test);
        deploy.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
        deploy.setBody(
                "return " + ArquillianInstrument.class.getName()+ ".deploy("
                        + "Class.forName(\"" + test.getName() + "\"),"
                        + "Class.forName(\"" + configurationFactory + "\"),"
                        + export + ","
                        + libraries + ","
                        + resources + ","
                        + metaResources + ","
                        + webResources +
                ");"
        );

        final AnnotationsAttribute deployment = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        deployment.addAnnotation(new Annotation("org.jboss.arquillian.container.test.api.Deployment", constPool));
        deploy.getMethodInfo().addAttribute(deployment);

        test.addMethod(deploy);
    }

    public static WebArchive deploy(final Class<?> test, final Class<?> configurationFactory, final String export, final String[] libraries, final String[] resources, final String[] metaResources, final String[] webResources) {
        try {
            final WebArchive archive = ShrinkWrap.create(WebArchive.class, test.getSimpleName() + ".war")
                    .addClass(test)
                    .addClass(configurationFactory)
                    .addPackage(AssertionUtils.class.getPackage())
                    .addPackage(ConnectionHelper.class.getPackage())
                    .addPackage(CheckpointData.class.getPackage())
                    .addPackage(StatusConstants.class.getPackage())
                    .addPackage(MyCounter.class.getPackage())
                    .addPackage(TransitionDecider.class.getPackage())
                    .addAsManifestResource(new File("target/classes/META-INF/MANIFEST.MF"));
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
                exportDir.mkdirs();
                archive.as(ZipExporter.class).exportTo(new File(exportDir, archive.getName()), true);
            }
            return archive;
        } catch (final RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
