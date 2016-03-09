package org.buffagon.intellij.catberry;

/**
 * === Описание класса ===
 *
 * @author Прокофьев Алексей
 */
public interface CatberryReadOnlySettings {
  boolean isCatberryEnabled();

  String getTemplateEngineName();

  String getComponentsRoot();

  String getStoresRoot();
}
