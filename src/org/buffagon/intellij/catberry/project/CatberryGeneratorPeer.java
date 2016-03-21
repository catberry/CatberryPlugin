package org.buffagon.intellij.catberry.project;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.platform.WebProjectGenerator;
import org.buffagon.intellij.catberry.settings.CatberryProjectSettings;
import org.buffagon.intellij.catberry.settings.CatberryProjectSettingsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * === Описание класса ===
 *
 * @author Прокофьев Алексей
 */
public class CatberryGeneratorPeer implements WebProjectGenerator.GeneratorPeer<CatberryProjectSettings> {
  private CatberryProjectSettings settings;
  private CatberryProjectSettingsPanel panel;

  public CatberryGeneratorPeer() {
    settings = new CatberryProjectSettings();
    panel = new CatberryProjectSettingsPanel(settings);
    panel.reset();
  }

  @NotNull
  @Override
  public JComponent getComponent() {
    return panel.getRootPanel();
  }

  @Override
  public void buildUI(@NotNull SettingsStep settingsStep) {
    settingsStep.addSettingsComponent(panel.getRootPanel());
  }

  @NotNull
  @Override
  public CatberryProjectSettings getSettings() {
    panel.apply();
    return settings;
  }

  @Nullable
  @Override
  public ValidationInfo validate() {
    return null;
  }

  @Override
  public boolean isBackgroundJobRunning() {
    return false;
  }

  @Override
  public void addSettingsStateListener(@NotNull WebProjectGenerator.SettingsStateListener settingsStateListener) {

  }
}
