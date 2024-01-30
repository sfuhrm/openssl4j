package de.sfuhrm.openssl4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads the object files.
 *
 * @author Stephan Fuhrmann
 */
class NativeLoader {
  /** Which objects have already been loaded? */
  private final Set<Path> loaded;

  private static boolean isLoaded = false;

  private static NativeLoader instance;

  private ObjectTransfer objTransfer;

  static final String[] OBJECTS = {"libopenssl4j"};

  NativeLoader() {
    loaded = new HashSet<>();
  }

  public static Path getLibraryDir() {
    if(instance == null) {
      return null;
    }

    return instance.objTransfer.getTargetDir();
  }

  /**
   * Loads all object files.
   *
   * @throws IOException if transferring the object files failed.
   */
  static void loadAll() throws IOException {
    if (isLoaded) {
      return;
    }
    NativeLoader.instance = new NativeLoader();
    NativeLoader.instance.objTransfer = new ObjectTransfer();
    NativeLoader.instance.objTransfer.transfer(OBJECTS);
    for (Path path : NativeLoader.instance.objTransfer.getObjectFiles()) {
      if(!ObjectTransfer.getOsName().toLowerCase().contains("mac") && !path.toString().contains("openssl4j")){
        continue;
      }
      NativeLoader.instance.load(path);
    }
    isLoaded = true;
  }

  String getLibDependencies(Path file) {
    try{
      Process proc = Runtime.getRuntime().exec("ldd " + file.toString());

      BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
  
      proc.waitFor();
  
      String output = "";
  
      for(String curLine = reader.readLine(); curLine != null; curLine = reader.readLine()) {
        output += curLine + " \n";
      }
  
      return output;
    }catch(Exception ex) {
      return ex.toString();
    }
  }

  /** Loads an object file and remembers it was loaded. */
  final void load(Path name) {
    if (!loaded.contains(name)) {
      if (Files.isRegularFile(name)) {
        System.load(name.toString());
        loaded.add(name);
      }
    }
  }
}
