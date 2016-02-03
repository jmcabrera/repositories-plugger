package io.finin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * FIXME, TODO: Usiliser celle de AH
 */
public class Session {

  private final Map<String, String> map = new HashMap<>();

  public Session(Map<String, String> map) {
    this.map.putAll(map);
  }

  public String get(String key) {
    return map.get(key);
  }

  public String put(String key, String value) {
    return map.put(key, value);
  }

  public Map<String, String> all() {
    return Collections.unmodifiableMap(map);
  }

  @Override
  public String toString() {
    return map.toString();
  }

}
