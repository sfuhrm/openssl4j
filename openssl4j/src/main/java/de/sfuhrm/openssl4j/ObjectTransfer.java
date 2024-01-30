package de.sfuhrm.openssl4j;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.LineUnavailableException;

/**
 * Transfers the object files from the JAR file to a temporary directory. The temporary directory
 * will be deleted when the JVM shuts down.
 *
 * @author Stephan Fuhrmann
 */
final class ObjectTransfer {

  /** The destination temporary directory. */
  private Path targetDirectory;

  /** The libraries copies. */
  private final List<Path> libraries;

  private static Pattern linuxLibRegex = Pattern.compile("(\\w+)(\\.\\d+)?");

  public Path getTargetDir() {
    return targetDirectory; 
  }

  ObjectTransfer() throws IOException {
    targetDirectory = Files.createTempDirectory("native");

    libraries = new ArrayList<>();

    Runnable removeTarget =
    () -> {
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

  /** Gets a system property, enforcing that the value string is alphanumeric.
   * @param property the name of the property to get.
   * @return the value of the property consisting of its alphanumeric parts
   * and the non-alphanumeric parts being replaced with an
   * underscore character ('_').
   * @throws NullPointerException if property is null or not set.
   * */
  static String getSystemPropertyAlnum(String property) {
    Objects.requireNonNull(property);
    final String value = System.getProperty(property);
    Objects.requireNonNull(value, "System property " + property + " is null");
    StringBuilder result = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      final char c = value.charAt(i);
      if (Character.isLetterOrDigit(c)) {
        result.append(c);
      } else {
        result.append('_');
      }
    }
    return result.toString();
  }

  public static String getOsName() {
      return getSystemPropertyAlnum("os.name");
  }

  public static String getArchName() {
      return getSystemPropertyAlnum("os.arch");
  }

  public static String toLibraryName(String name) {
    switch(name) {
      case "libopenssl4j":
        return name + "-" + getOsName().replace(" ", "_") + "-" + getArchName().replace(" ", "_") + ".so";
      default:
        {
          if(getOsName().toLowerCase().contains("mac")) {
            // Running on Mac, need dynlib files.
            return name + ".dylib";
          }
          
          Matcher libScanner = linuxLibRegex.matcher(name);

          if(libScanner.find()) {
            if(libScanner.groupCount() > 1 && libScanner.group(2) != null) {
              return libScanner.group(1) + ".so" + libScanner.group(2);
            }
          }

          return name + ".so";
        }
    }
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
          continue; 
        }
      }

      try (InputStream inputStream =
          Files.newInputStream(Paths.get("src/main/resources/objects").resolve(libName))) {
        transferTo(inputStream, targetLibraryPath);
        continue; 
      }
    }

    try {
    }catch (Exception ex) {
      System.out.println("Error logging unpacking of jar. Error " + ex.toString());
    }

  }

  private void transferTo(InputStream inputStream, Path targetFile) throws IOException {
    Files.copy(inputStream, targetFile);
    Files.setPosixFilePermissions(
        targetFile,
        new HashSet<>(
            Arrays.asList(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ)));
    libraries.add(targetFile);
  }
}
