package org.buffagon.intellij.catberry.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.buffagon.intellij.catberry.CatberryBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Register menu item "Catberry JS" in project settings.
 *
 * @author Prokofiev Alex
 */
public class CatberryProjectSettingsConfigurable implements SearchableConfigurable, Configurable.NoScroll {
  private CatberryProjectSettingsPanel myPanel;
  private final CatberryProjectSettingsProvider mySettingsProvider;
  private final CatberryProjectConfigurationManager projectConfigurationManager;

  public CatberryProjectSettingsConfigurable(Project project) {
    mySettingsProvider = CatberryProjectSettingsProvider.getInstance(project);
    projectConfigurationManager = CatberryProjectConfigurationManager.getInstance(project);
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
    return CatberryBundle.message("catberry.project.title");
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
      myPanel = new CatberryProjectSettingsPanel(mySettingsProvider);
    return myPanel.getRootPanel();
  }

  @Override
  public boolean isModified() {
    return myPanel.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    myPanel.apply();

    projectConfigurationManager.updateConfiguration();


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
