package org.buffagon.intellij.catberry;

/**
 * === Описание класса ===
 *
 * @author Прокофьев Алексей
 */
public class CatberryProjectSettings implements CatberryReadWriteSettings {
  private boolean catberryEnabled = true;
  private String templateEngineName = "handlebars";
  private String componentsRoot = "catberry_components";
  private String storesRoot = "catberry_stores";

  public CatberryProjectSettings() {
  }

  public CatberryProjectSettings(boolean catberryEnabled, String templateEngineName, String componentsRoot, String storesRoot) {
    this.catberryEnabled = catberryEnabled;
    this.templateEngineName = templateEngineName;
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
  public String getTemplateEngineName() {
    return templateEngineName;
  }

  @Override
  public void setTemplateEngineName(String templateEngineName) {
    this.templateEngineName = templateEngineName;
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
    target.setTemplateEngineName(source.getTemplateEngineName());
  }
}
