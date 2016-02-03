package io.finin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.finin.RemoteDataAccessors.MalformedConfigurationException;

/**
 * TODO: log
 * TODO: Gestion exceptions
 * TODO: Javadoc
 */
public class Plan {

  /**
   * TODO: log
   * TODO: Gestion exceptions
   * TODO: Javadoc
   */
  public static final class SelectorStep {

    public final Selector            selector;
    public final int                 order;
    public final boolean             mandatory;
    public final Map<String, String> other;

    private SelectorStep(Selector selector, boolean mandatory, int order, Map<String, String> other) {
      this.selector = selector;
      this.mandatory = mandatory;
      this.order = order;
      this.other = Collections.unmodifiableMap(new HashMap<>(other));
    }
  }

  /**
   * TODO: log
   * TODO: Gestion exceptions
   * TODO: Javadoc
   */
  public static final class UpdaterStep {

    public final Updater             updater;
    public final int                 order;
    public final boolean             mandatory;
    public final Map<String, String> other;

    private UpdaterStep(Updater updater, boolean mandatory, int order, Map<String, String> other) {
      this.updater = updater;
      this.mandatory = mandatory;
      this.order = order;
      this.other = Collections.unmodifiableMap(new HashMap<>(other));
    }
  }

  /**
   * TODO: log
   * TODO: Gestion exceptions
   * TODO: Javadoc
   */
  public static abstract class BuilderCut {

    public abstract Builder end();

    public AddingUpdatedBuilder with(Updater updater) {
      return new AddingUpdatedBuilder(end(), updater);
    }

    public AddingSelectorBuilder with(Selector selector) {
      return new AddingSelectorBuilder(end(), selector);
    }

  }

  /**
   * TODO: log
   * TODO: Gestion exceptions
   * TODO: Javadoc
   */
  public static final class Builder extends BuilderCut {
    private final List<UpdaterStep>  updaters  = new ArrayList<>();
    private final List<SelectorStep> selectors = new ArrayList<>();

    public Builder end() {
      return this;
    }

    public Plan get() {
      return new Plan(updaters, selectors);
    }

    private void add(UpdaterStep toAdd) {
      UpdaterStep curr;
      int i;
      for (i = 0; i < updaters.size(); i++) {
        curr = updaters.get(i);
        if (curr.order == toAdd.order) {
          throw new MalformedConfigurationException("order '" + curr.order + "' already taken");
        }
        if (toAdd.order < curr.order) {
          break;
        }
      }
      updaters.add(toAdd);
    }

    private void add(SelectorStep toAdd) {
      SelectorStep curr;
      int i;
      for (i = 0; i < selectors.size(); i++) {
        curr = selectors.get(i);
        if (curr.order == toAdd.order) {
          throw new MalformedConfigurationException("order '" + curr.order + "' already taken");
        }
        if (toAdd.order < curr.order) {
          break;
        }
      }
      selectors.add(i, toAdd);
    }
  }

  /**
   * TODO: log
   * TODO: Gestion exceptions
   * TODO: Javadoc
   */
  public static abstract class AddingStep<T> extends BuilderCut {

    protected final Builder       builder;
    protected final T             t;
    protected int                 order     = -1;
    protected Boolean             mandatory = false;
    protected Map<String, String> other     = null;

    public AddingStep(Builder builder, T t) {
      this.builder = builder;
      this.t = t;
    }

    public AddingStep<T> mandatory(boolean mandatory) {
      this.mandatory = mandatory;
      return this;
    }

    public AddingStep<T> order(int order) {
      this.order = order;
      return this;
    }

    public BuilderCut other(Map<String, String> other) {
      this.other = Collections.unmodifiableMap(new HashMap<>(other));
      return this;
    }

  }

  /**
   * TODO: log
   * TODO: Gestion exceptions
   * TODO: Javadoc
   */
  public static final class AddingSelectorBuilder extends AddingStep<Selector> {

    public AddingSelectorBuilder(Builder builder, Selector selector) {
      super(builder, selector);
    }

    public Builder end() {
      builder.add(new SelectorStep(t, mandatory, order, other));
      return builder;
    }
  }

  /**
   * TODO: log
   * TODO: Gestion exceptions
   * TODO: Javadoc
   */
  public static final class AddingUpdatedBuilder extends AddingStep<Updater> {

    public AddingUpdatedBuilder(Builder builder, Updater updater) {
      super(builder, updater);
    }

    public Builder end() {
      builder.add(new UpdaterStep(t, mandatory, order, other));
      return builder;
    }
  }

  private final List<UpdaterStep>  updaters;
  private final List<SelectorStep> selectors;

  private Plan(List<UpdaterStep> updaters, List<SelectorStep> selectors) {
    this.updaters = Collections.unmodifiableList(updaters);
    this.selectors = Collections.unmodifiableList(selectors);
  }

  public List<SelectorStep> selectors() {
    return selectors;
  }

  public List<UpdaterStep> updaters() {
    return updaters;
  }

}
