public class OsArch {
  public static void main(String[] args) {
    System.out.printf("%s-%s", System.getProperty("os.name"), System.getProperty("os.arch"));
  }
}
