package io.machinecode.chainlink.tck.core;

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
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestInstrument {

    private static void _usage() {
        System.out.println("Usage: java " + TestInstrument.class.getName());
        System.out.println("    -i|--instrument path_to_class_file");
        System.out.println("   [-c|--class fqcn]");
        System.out.println("   [-e|--export path_to_export_to]");
        System.out.println("   [-l|--library path_to_library]");
        System.out.println("   [-r|--resource path_to_resource]");
        System.out.println("   [-m|--meta-inf-resource path_to_resource]");
        System.out.println("   [-w|--web-inf-resource path_to_resource]");
        System.out.println("   [-h|--help]");
    }

    public static void main(final String[] args) throws Exception {

        final Getopt opt = new Getopt("test-instrument", args, "c:i:e:l:r:m:w:h", new LongOpt[]{
                new LongOpt("instrument", LongOpt.REQUIRED_ARGUMENT, null, 'i'),
                new LongOpt("class", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
                new LongOpt("export", LongOpt.REQUIRED_ARGUMENT, null, 'e'),
                new LongOpt("library", LongOpt.REQUIRED_ARGUMENT, null, 'l'),
                new LongOpt("resource", LongOpt.REQUIRED_ARGUMENT, null, 'r'),
                new LongOpt("meta-inf-resource", LongOpt.REQUIRED_ARGUMENT, null, 'm'),
                new LongOpt("web-inf-resource", LongOpt.REQUIRED_ARGUMENT, null, 'w'),
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h')
        });

        final ClassPool pool = ClassPool.getDefault();
        String export = null;
        final Set<String> instrument = new HashSet<String>();
        final StringBuilder cb = new StringBuilder("new Class[]{");
        final StringBuilder lb = new StringBuilder("new String[]{");
        final StringBuilder rb = new StringBuilder("new String[]{");
        final StringBuilder mb = new StringBuilder("new String[]{");
        final StringBuilder wb = new StringBuilder("new String[]{");
        boolean cf = true;
        boolean lf = true;
        boolean rf = true;
        boolean mf = true;
        boolean wf = true;
        int c;
        while ((c = opt.getopt()) != -1) {
            switch (c) {
                case 'c':
                    if (cf) {
                        cb.append("Class.forName(\"").append(opt.getOptarg()).append("\")");
                        cf = false;
                    } else {
                        cb.append(',').append("Class.forName(\"").append(opt.getOptarg()).append("\")");
                    }
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
        final String classes = cf ? "null" : cb.append('}').toString();
        final String libraries = lf ? "null" : lb.append('}').toString();
        final String resources = rf ? "null" : rb.append('}').toString();
        final String metaResources = mf ? "null" : mb.append('}').toString();
        final String webResources = wf ? "null" : wb.append('}').toString();

        for (final String file : instrument) {
            final InputStream stream = new FileInputStream(file);
            final CtClass test = pool.makeClass(stream);

            instrument(test, classes, export, libraries, resources, metaResources, webResources);

            try (final DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
                out.write(test.toBytecode());
                System.out.println("Instrumented " + test.getName());
            }
        }
    }

    public static void instrument(final CtClass test, final String classes, final String export, final String libraries, final String resources, final String metaResources, final String webResources) throws IOException, CannotCompileException, NotFoundException, ClassNotFoundException {
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

        final CtMethod deploy = new CtMethod(ClassPool.getDefault().get(WebArchive.class.getName()), "__chainlink_deploy", new CtClass[0], test);
        deploy.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
        deploy.setBody(
                "return " + Deployer.class.getName()+ ".deploy("
                        + "Class.forName(\"" + test.getName() + "\"),"
                        + classes + ","
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
}
