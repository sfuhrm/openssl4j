package de.sfuhrm.openssl4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Transfers the object files from the JAR file
 * to a temporary directory. The temporary directory
 * will be deleted when the JVM shuts down.
 * @author Stephan Fuhrmann
 */
final class ObjectTransfer {

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

    private static String getOsName() {
        return System.getProperty("os.name");
    }


    private static String getArchName() {
        return System.getProperty("os.arch");
    }

    static String toLibraryName(String name) {
        return name + "-" + getOsName() + "-" + getArchName() + ".so";
    }

    final List<Path> getObjectFiles() {
        return Collections.unmodifiableList(libraries);
    }

    final void transfer(String... names) throws IOException {
        for (String name : names) {
            String libName = toLibraryName(name);
            Path targetLibraryPath = targetDirectory.resolve(libName);

            try (InputStream inputStream = getClass().getResourceAsStream("/objects/" + libName)) {
                if (inputStream != null) {
                    transferTo(inputStream, targetLibraryPath);
                    break;
                }
            }

            try (InputStream inputStream = Files.newInputStream(Paths.get("src/main/resources/objects").resolve(libName))) {
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
