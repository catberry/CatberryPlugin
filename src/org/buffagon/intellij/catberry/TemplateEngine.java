package org.buffagon.intellij.catberry;

import org.jetbrains.annotations.NotNull;

/**
 * @author Prokofiev Alex
 */
public enum TemplateEngine {
  HANDLEBARS("handlebars", "hbs"),
  DUST("dust", "dust"),
  JADE("jade", "jade");

  TemplateEngine(String name, String extension) {
    this.name = name;
    this.extension = extension;
  }

  private final String name;
  private final String extension;

  @NotNull
  public String getName() {
    return name;
  }

  @NotNull
  public String getExtension() {
    return extension;
  }

  @Override
  public String toString() {
    return name;
  }
}
