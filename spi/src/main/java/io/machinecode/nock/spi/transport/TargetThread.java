package io.machinecode.nock.spi.transport;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class TargetThread {

    public static final TargetThread THIS = new TargetThread(Type.THIS, null);
    public static final TargetThread ANY = new TargetThread(Type.ANY, null);

    private final Type type;
    private final Executable executable;

    private TargetThread(final Type type, final Executable executable) {
        this.type = type;
        this.executable = executable;
    }

    public Type getType() {
        return type;
    }

    public Executable getExecutable() {
        return executable;
    }

    public static TargetThread that(final Executable executable) {
        if (executable == null) {
            throw new IllegalArgumentException();
        }
        return new TargetThread(Type.THAT, executable);
    }

    public enum Type {
        THIS,
        THAT,
        ANY
    }
}
