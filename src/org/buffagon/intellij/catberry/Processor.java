package org.buffagon.intellij.catberry;

/**
 * @author Prokofiev Alex
 */
public interface Processor<T, E> {
  E process(T value);
}
