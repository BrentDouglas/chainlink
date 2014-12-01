package io.machinecode.chainlink.tck.core;

import com.ibm.jbatch.tck.artifacts.chunkartifacts.ConnectionHelper;
import com.ibm.jbatch.tck.artifacts.chunktypes.CheckpointData;
import com.ibm.jbatch.tck.artifacts.common.StatusConstants;
import com.ibm.jbatch.tck.artifacts.reusable.MyCounter;
import com.ibm.jbatch.tck.artifacts.specialized.TransitionDecider;
import com.ibm.jbatch.tck.utils.AssertionUtils;
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
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class ArquillianInstrument {

    public static void main(final String[] args) throws Exception {
        final ClassPool pool = ClassPool.getDefault();
        final String configurationFactory = args[0];
        for (int i = 1; i < args.length; ++i) {
            final String file = args[i];
            final InputStream stream = new FileInputStream(file);
            final CtClass test = pool.makeClass(stream);

            instrument(test, configurationFactory);

            final DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
            try {
                out.write(test.toBytecode());
                System.out.println("Instrumented " + test.getName());
            } finally {
                out.close();
            }
        }
    }

    public static void instrument(final CtClass test, final String configurationFactory) throws IOException, CannotCompileException, NotFoundException, ClassNotFoundException {
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
        deploy.setBody("return io.machinecode.chainlink.tck.core.ArquillianInstrument.deploy(Class.forName(\"" + test.getName() + "\"),Class.forName(\"" + configurationFactory + "\"));");

        final AnnotationsAttribute deployment = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        deployment.addAnnotation(new Annotation("org.jboss.arquillian.container.test.api.Deployment", constPool));
        deploy.getMethodInfo().addAttribute(deployment);

        test.addMethod(deploy);
    }

    public static WebArchive deploy(final Class<?> test, final Class<?> configurationFactory) {
        return ShrinkWrap.create(WebArchive.class)
                .addClass(test)
                .addClass(configurationFactory)
                .addPackage(AssertionUtils.class.getPackage())
                .addPackage(ConnectionHelper.class.getPackage())
                .addPackage(CheckpointData.class.getPackage())
                .addPackage(StatusConstants.class.getPackage())
                .addPackage(MyCounter.class.getPackage())
                .addPackage(TransitionDecider.class.getPackage())
                .addAsResource(new File("target/test-classes/coherence-cache-config.xml"))
                .addAsResource(new File("target/test-classes/ehcache.xml"))
                .addAsResource(new File("target/test-classes/hazelcast.xml"))
                .addAsResource(new File("target/test-classes/tck-udp.xml"))
                .addAsResource(new File("target/test-classes/ChainlinkMessages_en.properties"))
                .addAsResource(new File("target/test-classes/META-INF"))
                .addAsManifestResource(new File("target/classes/META-INF/MANIFEST.MF"))
                .addAsLibraries(new File("target/deps/chainlink-tck-core.jar"))
                .addAsLibraries(new File("target/deps/com.ibm.jbatch.tck.spi.jar"))
                .addAsLibraries(new File("target/deps/testng.jar"));
    }
}
