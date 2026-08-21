package support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Config {
  private Config() {}

  public static String baseUrl() {
    Properties properties = new Properties();
    try (InputStream stream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
      if (stream == null) throw new IllegalStateException("config.properties was not found");
      properties.load(stream);
    } catch (IOException exception) {
      throw new IllegalStateException("Unable to load config.properties", exception);
    }
    return properties.getProperty("base.url");
  }

  public static Map<String, String> credentials() {
    String json = System.getenv("MAESTRO_CREDS");
    if (json == null || json.isBlank()) return Map.of();
    Matcher matcher = Pattern.compile("\\\"(username|password)\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"").matcher(json);
    java.util.Map<String, String> values = new java.util.HashMap<>();
    while (matcher.find()) values.put(matcher.group(1), matcher.group(2));
    return Map.copyOf(values);
  }
}
