package org.buffagon.intellij.catberry;

/**
 * @author Prokofiev Alex
 */
public enum TemplateEngine {
  HANDLEBARS("handlebars", "^2.0.1"),
  DUST("dust", "^3.0.13"),
  JADE("jade", "^1.1.6");

  TemplateEngine(String name, String version) {
    this.name = name;
    this.version = version;
  }

  private final String name;
  private final String version;

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return name;
  }
}
