package mint.security;

public class NoTrace extends RuntimeException {

    public NoTrace(final String msg) {
        super(msg);
        this.setStackTrace(new StackTraceElement[0]);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}