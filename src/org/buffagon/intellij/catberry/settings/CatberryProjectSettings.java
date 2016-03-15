package org.buffagon.intellij.catberry.settings;

import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.TemplateEngine;

/**
 * === Описание класса ===
 *
 * @author Прокофьев Алексей
 */
public class CatberryProjectSettings implements CatberryReadWriteSettings {
  private boolean catberryEnabled = true;
  private TemplateEngine templateEngine = TemplateEngine.HANDLEBARS;
  private String componentsRoot = CatberryConstants.CATBERRY_COMPONENTS;
  private String storesRoot = CatberryConstants.CATBERRY_STORES;

  public CatberryProjectSettings() {
  }

  public CatberryProjectSettings(boolean catberryEnabled, TemplateEngine templateEngine, String componentsRoot, String storesRoot) {
    this.catberryEnabled = catberryEnabled;
    this.templateEngine = templateEngine;
    this.componentsRoot = componentsRoot;
    this.storesRoot = storesRoot;
  }

  @Override
  public boolean isCatberryEnabled() {
    return catberryEnabled;
  }

  @Override
  public void setCatberryEnabled(boolean catberryEnabled) {
    this.catberryEnabled = catberryEnabled;
  }

  @Override
  public TemplateEngine getTemplateEngine() {
    return templateEngine;
  }

  @Override
  public void setTemplateEngine(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  @Override
  public String getComponentsRoot() {
    return componentsRoot;
  }

  @Override
  public void setComponentsRoot(String componentsRoot) {
    this.componentsRoot = componentsRoot;
  }

  @Override
  public String getStoresRoot() {
    return storesRoot;
  }

  @Override
  public void setStoresRoot(String storesRoot) {
    this.storesRoot = storesRoot;
  }

  public static void copy(CatberryReadOnlySettings source, CatberryReadWriteSettings target) {
    target.setCatberryEnabled(source.isCatberryEnabled());
    target.setComponentsRoot(source.getComponentsRoot());
    target.setStoresRoot(source.getStoresRoot());
    target.setTemplateEngine(source.getTemplateEngine());
  }
}
