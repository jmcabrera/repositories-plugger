package io.finin.test.rdas;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.finin.RemoteDataAccessor;
import io.finin.Selector;
import io.finin.Session;
import io.finin.Updater;

public abstract class TalkativeRDA implements RemoteDataAccessor {

  private String type;

  public TalkativeRDA(String type) {
    this.type = type;
  }

  @Override
  public String[] understands() {
    return new String[] { type };
  }

  @Override
  public Updater getUpdater(URL url) {
    return session -> {
      System.out.println(type + " --> '" + url + "': pushing '" + session);
    };
  }

  @Override
  public Selector getSelector(URL url) {
    return id -> {
      System.out.println(type + " <-- '" + url + "' selecting id#" + id);
      Map<String, String> map = new HashMap<>();
      map.put(type + "-url", url.toString());
      map.put("common-value", "from " + type);
      return new Session(map);
    };
  }

}
