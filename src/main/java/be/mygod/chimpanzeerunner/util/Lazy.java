package be.mygod.chimpanzeerunner.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public abstract class Lazy {
    private Lazy() { }

    public static <T> Supplier<T> wrap(Supplier<T> supplier) {
        return new Supplier<T>() {
            private boolean initialized;
            private T value;

            @Override
            public T get() {
                if (!initialized) {
                    value = supplier.get();
                    initialized = true;
                }
                return value;
            }
        };
    }

    public static <T> Supplier<T> wrapThreadSafe(Supplier<T> supplier) {
        return new Supplier<T>() {
            private final AtomicBoolean initializing = new AtomicBoolean();
            private final CountDownLatch initLatch = new CountDownLatch(1);
            private T value;

            @Override
            public T get() {
                if (initializing.compareAndSet(false, true)) {
                    value = supplier.get();
                    initLatch.countDown();
                } else try {
                    initLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
                return value;
            }
        };
    }
}
