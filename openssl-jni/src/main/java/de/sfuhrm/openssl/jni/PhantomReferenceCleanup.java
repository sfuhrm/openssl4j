package de.sfuhrm.openssl.jni;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.nio.ByteBuffer;

/**
 * Frees native ByteBuffer objects.
 * The ByteBuffer objects are allocated in {@linkplain AbstractNative#AbstractNative()}
 * and are not used any longer.
 * @author Stephan Fuhrmann
 */
class PhantomReferenceCleanup {

    /** The reference queue of unused ByteBuffer objects. */
    private static final ReferenceQueue<ByteBuffer> BYTE_BUFFER_REFERENCE_QUEUE = new ReferenceQueue<>();

    /** Is the thread running? */
    private static boolean running = false;

    /** Enqueues a ByteBuffer for later cleanup. */
    static void enqueueForCleanup(ByteBuffer bb) {
        PhantomReference<ByteBuffer> phantomReference = new PhantomReference<ByteBuffer>(bb, BYTE_BUFFER_REFERENCE_QUEUE);
        startIfNeeded();
    }

    /** Checks whether the queue thread is already
     * running and starts it if not.
     * */
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
