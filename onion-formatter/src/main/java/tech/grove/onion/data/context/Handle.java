package tech.grove.onion.data.context;

public interface Handle {

    String className();

    String methodName();

    int lineNumber();


    static Handle of(StackWalker.StackFrame element) {
        return new StackFrameHandle(element);
    }

    public static final Handle DUMMY = new Handle() {
        @Override
        public String className() {
            return "class";
        }

        @Override
        public String methodName() {
            return "method";
        }

        @Override
        public int lineNumber() {
            return 0;
        }
    };
}
