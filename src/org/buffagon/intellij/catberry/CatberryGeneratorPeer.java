package org.buffagon.intellij.catberry;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.platform.WebProjectGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * === Описание класса ===
 *
 * @author Прокофьев Алексей
 */
public class CatberryGeneratorPeer implements WebProjectGenerator.GeneratorPeer<CatberryReadOnlySettings> {
  private CatberryProjectSettings settings;
  private CatberryProjectSettingsPanel panel;

  public CatberryGeneratorPeer() {
    settings = new CatberryProjectSettings();
    settings.setCatberryEnabled(true);
    panel = new CatberryProjectSettingsPanel(settings);
    panel.reset();
  }

  @NotNull
  @Override
  public JComponent getComponent() {
    return panel.getSettingsPanel();
  }

  @Override
  public void buildUI(@NotNull SettingsStep settingsStep) {
    settingsStep.addSettingsComponent(panel.getSettingsPanel());
  }

  @NotNull
  @Override
  public CatberryReadOnlySettings getSettings() {
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
