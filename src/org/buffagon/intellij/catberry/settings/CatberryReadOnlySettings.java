package org.buffagon.intellij.catberry.settings;

import org.buffagon.intellij.catberry.TemplateEngine;

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
