package org.buffagon.intellij.catberry;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Регистрирует пункт меню "Odoo" в настройках проекта.
 *
 * @author Прокофьев Алексей
 */
public class CatberryProjectSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll {
  private CatberryProjectSettingsPanel myPanel;
  private final CatberryProjectSettingsProvider mySettingsProvider;

  public CatberryProjectSettingsConfigurable(Project project) {
    mySettingsProvider = CatberryProjectSettingsProvider.getInstance(project);
  }

  @NotNull
  @Override
  public String getId() {
    return "catberry-project";
  }

  @Nullable
  @Override
  public Runnable enableSearch(String s) {
    return null;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Catberry JS";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    if (myPanel == null)
      myPanel = new CatberryProjectSettingsPanel();
    return myPanel.getPanel(mySettingsProvider);
  }

  @Override
  public boolean isModified() {
    return myPanel.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    myPanel.apply();
  }

  @Override
  public void reset() {
    myPanel.reset();
  }

  @Override
  public void disposeUIResources() {
    myPanel = null;
  }
}
