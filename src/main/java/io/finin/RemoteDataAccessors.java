package io.finin;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * TODO: log
 * TODO: Gestion exceptions
 * TODO: Javadoc
 */
public class RemoteDataAccessors {

  static Map<String, RemoteDataAccessor> rdas = new HashMap<>();

  static {
    ServiceLoader<RemoteDataAccessor> snkl = ServiceLoader.load(RemoteDataAccessor.class);
    Iterator<RemoteDataAccessor> iterator = snkl.iterator();
    Map<String, Set<String>> conflicts = new HashMap<>();
    while (iterator.hasNext()) {
      RemoteDataAccessor next = iterator.next();
      for (String s : next.understands()) {
        RemoteDataAccessor prev = rdas.put(s, next);
        if (null != prev) {
          Set<String> conflict = conflicts.getOrDefault(s, new HashSet<>());
          conflict.add(prev.getClass().getName());
          conflict.add(next.getClass().getName());
        }
      }
    }
  }

  /**
   * TODO: Javadoc
   */
  public static final class MalformedConfigurationException extends RuntimeException {

    private static final long serialVersionUID = 5346884014264610539L;

    public MalformedConfigurationException(String msg, Exception ex) {
      super(msg, ex);
    }

    public MalformedConfigurationException(String msg) {
      super(msg);
    }
  }

  /**
   * TODO: log
   * TODO: Gestion exceptions
   * TODO: Javadoc
   */
  public static final class Conf {

    /**
     * TODO: Javadoc
     */
    public static enum Direction {
      SRC, SNK
    }

    public final Direction           direction;
    public final URL                 url;
    public final String              type;
    public final int                 order;
    public final boolean             mandatory;
    public final Map<String, String> other;

    public Conf(String direction, Map<String, String> all) throws MalformedConfigurationException {
      Map<String, String> _all = new HashMap<>(all);
      this.other = Collections.unmodifiableMap(_all);
      try {
        this.direction = Direction.valueOf(direction.toUpperCase());
      }
      catch (Exception e) {
        throw new MalformedConfigurationException("direction '" + direction + "' is not known");
      }
      try {
        this.url = new URL(_all.remove("url"));
      }
      catch (MalformedURLException mue) {
        throw new MalformedConfigurationException("url should be a valid url", mue);
      }
      this.type = _all.remove("type");
      try {
        this.order = Integer.parseInt(_all.remove("order"));
      }
      catch (NumberFormatException nfe) {
        throw new MalformedConfigurationException("order should be a number", nfe);
      }
      this.mandatory = Boolean.parseBoolean(_all.remove("mandatory"));

      if (this.order < 0) {
        throw new MalformedConfigurationException("order cannot be negative");
      }
    }

    public static Conf get(String conf) {
      Map<String, String> map = new HashMap<>();
      String[] split = conf.split("[=?&]");
      String direction = split[0];
      for (int i = 1; i < split.length;) {
        map.put(split[i++], split[i++]);
      }
      return new Conf(direction, map);
    }

  }

  public static RemoteDataAccessor getAccessor(String type) {
    return rdas.get(type);
  }

  public static Plan plan(String... confs) {
    Plan.BuilderCut builder = new Plan.Builder();
    for (String conf : confs) {
      Conf c = Conf.get(conf);
      RemoteDataAccessor rda = RemoteDataAccessors.getAccessor(c.type);
      switch (c.direction) {
        case SRC:
          builder = builder.with(rda.getSelector(c.url)).order(c.order).mandatory(c.mandatory).other(c.other);
          break;
        case SNK:
          builder = builder.with(rda.getUpdater(c.url)).order(c.order).mandatory(c.mandatory).other(c.other);
      }

    }
    return builder.end().get();
  }

}
