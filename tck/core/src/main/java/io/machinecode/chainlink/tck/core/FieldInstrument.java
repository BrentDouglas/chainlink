package io.machinecode.chainlink.tck.core;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FieldInstrument {

    private static void _usage() {
        System.out.println("Usage: java " + FieldInstrument.class.getName());
        System.out.println("    -i|--instrument path_to_class_file");
        System.out.println("    -f|--field field_name");
        System.out.println("    -c|--constant constant_value");
        System.out.println("    -v|--value new_value");
        System.out.println("   [-h|--help]");
    }

    public static void main(final String[] args) throws Exception {

        final Getopt opt = new Getopt("test-instrument", args, "i:f:c:v:h", new LongOpt[]{
                new LongOpt("instrument", LongOpt.REQUIRED_ARGUMENT, null, 'i'),
                new LongOpt("field", LongOpt.REQUIRED_ARGUMENT, null, 'f'),
                new LongOpt("constant", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
                new LongOpt("value", LongOpt.REQUIRED_ARGUMENT, null, 'v'),
                new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h')
        });

        final ClassPool pool = ClassPool.getDefault();
        String field = null;
        String value = null;
        String constant = null;
        final Set<String> instrument = new HashSet<String>();
        int c;
        while ((c = opt.getopt()) != -1) {
            switch (c) {
                case 'i':
                    instrument.add(opt.getOptarg());
                    break;
                case 'f':
                    field = opt.getOptarg();
                    break;
                case 'c':
                    constant = opt.getOptarg();
                    break;
                case 'v':
                    value = opt.getOptarg();
                    break;
                case 'h':
                default:
                    _usage();
                    return;
            }
        }

        if (value == null) {
            throw new IllegalStateException("-v|--value argument is required.");
        }
        if (field == null && constant == null) {
            throw new IllegalStateException("One of -f|--field or -c|--constant arguments are required.");
        }
        if (field != null && constant != null) {
            throw new IllegalStateException("Only one of -f|--field or -c|--constant may be specified.");
        }

        for (final String file : instrument) {
            if (field != null) {
                final InputStream in = new FileInputStream(file);
                final CtClass clazz = pool.makeClass(in);
                instrumentField(field, value, clazz);
                final DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
                try {
                    out.write(clazz.toBytecode());
                    System.out.println("Instrumented " + clazz.getName());
                } finally {
                    out.close();
                }
            } else {
                final InputStream in = new FileInputStream(file);
                final ClassWriter writer = new ClassWriter(0);
                final ClassReader reader = new ClassReader(in);
                reader.accept(instrumentConstant(constant, value, writer), 0);
                final FileOutputStream out = new FileOutputStream(file);
                try {
                    out.write(writer.toByteArray());
                    System.out.println("Instrumented " + reader.getClassName());
                } finally {
                    out.close();
                }
            }
        }
    }

    private static void instrumentField(final String field, final String value, final CtClass clazz) throws NotFoundException, CannotCompileException {
        final CtField old = clazz.getField(field);
        clazz.removeField(old);

        final CtClass type = old.getType();
        final CtField replace = new CtField(type, field, clazz);
        replace.setModifiers(old.getModifiers());

        if (String.class.getName().equals(type.getName())) {
            clazz.addField(replace, "\"" + value + "\"");
        } else {
            clazz.addField(replace, value);
        }
    }

    private static ClassVisitor instrumentConstant(final String constant, final String value, final ClassWriter writer) throws IOException {
        return new ClassVisitor(Opcodes.ASM5, writer) {
            @Override
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                    @Override
                    public void visitLdcInsn(final Object cst) {
                        if (constant.equals(cst)) {
                            super.visitLdcInsn(value);
                        } else {
                            super.visitLdcInsn(cst);
                        }
                    }
                };
            }
        };
    }
}
