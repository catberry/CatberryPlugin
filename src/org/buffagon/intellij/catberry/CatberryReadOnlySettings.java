package org.buffagon.intellij.catberry;

/**
 * === Описание класса ===
 *
 * @author Прокофьев Алексей
 */
public interface CatberryReadOnlySettings {
  boolean isCatberryEnabled();

  TemplateEngine getTemplateEngine();

  String getComponentsRoot();

  String getStoresRoot();
}
