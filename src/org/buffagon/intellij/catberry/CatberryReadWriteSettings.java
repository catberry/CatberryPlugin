package org.buffagon.intellij.catberry;

/**
 * === Описание класса ===
 *
 * @author Прокофьев Алексей
 */
public interface CatberryReadWriteSettings extends CatberryReadOnlySettings {
  void setCatberryEnabled(boolean catberryEnabled);

  void setTemplateEngine(TemplateEngine templateEngine);

  void setComponentsRoot(String componentsRoot);

  void setStoresRoot(String storesRoot);
}
