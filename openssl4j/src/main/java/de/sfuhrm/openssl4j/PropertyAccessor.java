package de.sfuhrm.openssl4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class PropertyAccessor {
  private static Properties properties;

  /**
   * Gets a property.
   *
   * @param name the property name.
   * @param defaultValue the default value if the property was not set.
   */
  static String get(String name, String defaultValue) {
    if (properties == null) {
      properties = loadOpenssl4jProperties();
    }
    return (String) properties.getOrDefault(name, defaultValue);
  }

  private static Properties loadOpenssl4jProperties() {
    Properties result = new Properties();
    try (InputStream inputStream =
        ObjectTransfer.class.getResourceAsStream("/META-INF/openssl4j.properties")) {
      if (inputStream != null) {
        result.load(inputStream);
      }
    } catch (IOException e) {
    }
    return result;
  }
}
