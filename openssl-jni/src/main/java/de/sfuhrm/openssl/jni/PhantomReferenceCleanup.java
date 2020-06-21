package de.sfuhrm.openssl.jni;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.nio.ByteBuffer;

class PhantomReferenceCleanup {
    private static final ReferenceQueue<ByteBuffer> BYTE_BUFFER_REFERENCE_QUEUE = new ReferenceQueue<>();

    private static boolean running = false;

    static void enqueueForCleanup(ByteBuffer bb) {
        PhantomReference<ByteBuffer> phantomReference = new PhantomReference<ByteBuffer>(bb, BYTE_BUFFER_REFERENCE_QUEUE);
        startIfNeeded();
    }

    static synchronized void startIfNeeded() {
        if (!running) {
            running = true;
            Runnable r = () -> {
                try {
                    while (true) {
                        Reference<ByteBuffer> reference = (Reference<ByteBuffer>) BYTE_BUFFER_REFERENCE_QUEUE.remove(0);
                        AbstractNative.removeContext(reference.get());
                    }
                } catch (InterruptedException e) {
                }
            };
            Thread t = new Thread(r, "OpenSSL-Cleanup");
            t.setDaemon(true);
            t.start();
        }
    }
}
