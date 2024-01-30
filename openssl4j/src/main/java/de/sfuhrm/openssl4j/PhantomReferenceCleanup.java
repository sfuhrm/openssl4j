package de.sfuhrm.openssl4j;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Frees native AbstractNative objects (ByteBuffers). The objects are allocated in the constructors
 * for OpenSSLMessageDigestNative, OpenSSLCipherNative, OpenSSLMacNative, and
 * OpenSSLSecureRandomNative and are not used once construction is complete.
 *
 * @author Stephan Fuhrmann
 */
class PhantomReferenceCleanup {

  /** The reference queue of unused AbstractNative objects. */
  private static final ReferenceQueue<Object> BYTE_BUFFER_REFERENCE_QUEUE = new ReferenceQueue<>();

  /** Is the thread running? */
  private static boolean running = false;

  private static final Set<NativePhantomReference> nativePhantomReferenceList =
      Collections.synchronizedSet(new HashSet<>());

  private static class NativePhantomReference extends PhantomReference<Object> {
    private final Consumer<ByteBuffer> freeFunction;
    private final ByteBuffer byteBuffer;

    NativePhantomReference(
        Object abstractNative, Consumer<ByteBuffer> freeFunction, ByteBuffer context) {
      super(abstractNative, BYTE_BUFFER_REFERENCE_QUEUE);
      this.freeFunction = freeFunction;
      this.byteBuffer = context;
    }

    public void free() {
      freeFunction.accept(byteBuffer);
    }
  }

  /** Enqueues a AbstractNative for later cleanup. */
  static void enqueueForCleanup(Object ref, Consumer<ByteBuffer> freeFunction, ByteBuffer context) {
    NativePhantomReference phantomReference =
        new NativePhantomReference(
            Objects.requireNonNull(ref),
            Objects.requireNonNull(freeFunction),
            Objects.requireNonNull(context));
    nativePhantomReferenceList.add(phantomReference);
    startIfNeeded();
  }

  /** Checks whether the queue thread is already running and starts it if not. */
  static synchronized void startIfNeeded() {
    if (!running) {
      running = true;
      Runnable r =
          () -> {
            try {
              while (true) {
                NativePhantomReference reference =
                    (NativePhantomReference) BYTE_BUFFER_REFERENCE_QUEUE.remove();
                reference.free();
                nativePhantomReferenceList.remove(reference);
              }
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          };
      Thread t = new Thread(r, "OpenSSL-Cleanup");
      t.setDaemon(true);
      t.start();
    }
  }
}
