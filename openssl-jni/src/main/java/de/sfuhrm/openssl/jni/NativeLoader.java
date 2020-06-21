package de.sfuhrm.openssl.jni;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Loads the object files.
 * @author Stephan Fuhrmann
 */
class NativeLoader {
    /** Which objects have already been loaded? */
    private Set<Path> loaded;

    /** The object files loaded.  */
    private List<Path> objectFiles;

    /** The transfer for object files. */
    private ObjectTransfer objectTransfer;

    private static boolean isLoaded = false;

    static final String[] OBJECTS = {
            "libsslnative"
    };

    NativeLoader() throws IOException {
        loaded = new HashSet<>();
    }

    /**
     * Loads all object files.
     * @throws IOException if transferring the object files failed.
     */
    static void loadAll() throws IOException {
        if (isLoaded) {
            return;
        }
        NativeLoader nativeLoader = new NativeLoader();
        ObjectTransfer objectTransfer = new ObjectTransfer();
        objectTransfer.transfer(OBJECTS);
        nativeLoader.objectFiles = objectTransfer.getObjectFiles();
        for (Path path : nativeLoader.objectFiles) {
            nativeLoader.load(path);
        }
        isLoaded = true;
    }

    /** Loads an object file and remembers it was loaded. */
    void load(Path name) {
        if (!loaded.contains(name)) {
            if (Files.isRegularFile(name)) {
                System.load(name.toString());
                loaded.add(name);
            }
        }
    }
}
