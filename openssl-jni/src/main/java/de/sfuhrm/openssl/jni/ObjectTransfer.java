package de.sfuhrm.openssl.jni;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Transfers the object files from the JAR file
 * to a temporary directory. The temporary directory
 * will be deleted when the JVM shuts down.
 * @author Stephan Fuhrmann
 */
class ObjectTransfer {

    /** The destination temporary directory. */
    private final Path targetDirectory;

    /** The libraries copies. */
    private final List<Path> libraries;

    ObjectTransfer() throws IOException {
        targetDirectory = Files.createTempDirectory("native");
        libraries = new ArrayList<>();
        Runnable removeTarget = () -> {
            for (Path p : libraries) {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Files.delete(targetDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(removeTarget));
    }

    static String toLibraryName(String name) {
        return name + "-" + PlatformHelper.getOsName() + "-" + PlatformHelper.getArchName() + ".so";
    }

    public List<Path> getObjectFiles() {
        return Collections.unmodifiableList(libraries);
    }

    public void transfer(String... names) throws IOException {
        for (String name : names) {
            String libName = toLibraryName(name);
            Path targetLibraryPath = targetDirectory.resolve(libName);

            try (InputStream inputStream = getClass().getResourceAsStream(libName)) {
                if (inputStream != null) {
                    transferTo(inputStream, targetLibraryPath);
                    break;
                }
            }

            try (InputStream inputStream = Files.newInputStream(Paths.get("target").resolve(libName))) {
                transferTo(inputStream, targetLibraryPath);
                break;
            }
        }
    }

    private void transferTo(InputStream inputStream, Path targetFile) throws IOException {
        Files.copy(inputStream, targetFile);
        Files.setPosixFilePermissions(targetFile, Set.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ));
        libraries.add(targetFile);
    }
}
