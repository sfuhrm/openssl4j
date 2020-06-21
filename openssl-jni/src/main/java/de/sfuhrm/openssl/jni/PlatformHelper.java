package de.sfuhrm.openssl.jni;

/** Helps determining the platform we're running on. */
class PlatformHelper {

    private PlatformHelper() {

    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }


    public static String getArchName() {
        return System.getProperty("os.arch");
    }
}
