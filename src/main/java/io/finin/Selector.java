package io.finin;

/**
 * TODO: Javadoc
 */
@FunctionalInterface
public interface Selector {
  
  Session fetch(String id);

}
