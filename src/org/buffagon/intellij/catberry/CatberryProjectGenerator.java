package org.buffagon.intellij.catberry;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import icons.CatberryIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * @author buffagon.
 */
public class CatberryProjectGenerator extends WebProjectTemplate<Object> {
  @Nls
  @NotNull
  @Override
  public String getName() {
    return "Catberry Project";
  }

  @Override
  public Icon getIcon() {
    return CatberryIcons.LOGO_16;
  }

  @Override
  public String getDescription() {
    return "Create project from catberry cli util";
  }

  @Override
  public void generateProject(@NotNull final Project project, @NotNull final VirtualFile baseDir, @NotNull Object data, @NotNull final Module module) {
    ApplicationManager.getApplication().runWriteAction(
        new Runnable() {
          public void run() {
            final ModifiableRootModel modifiableModel = ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module);
            String template = CatberryProjectSettingsProvider.getInstance(project).getTemplateEngineName();
            try {
              Process process = new ProcessBuilder().directory(new File(baseDir.getPath())).command(
                  "catberry", "init", "empty-" + template).start();
              process.waitFor();
            } catch (IOException e) {
              e.printStackTrace();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel);
          }
        }
    );
  }

  @NotNull
  @Override
  public GeneratorPeer<Object> createPeer() {
    return new GeneratorPeer<Object>() {
      @NotNull
      @Override
      public JComponent getComponent() {
        return new JPanel();
      }

      @Override
      public void buildUI(@NotNull SettingsStep settingsStep) {

      }

      @NotNull
      @Override
      public Object getSettings() {
        return new Object();
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
      public void addSettingsStateListener(@NotNull SettingsStateListener settingsStateListener) {

      }
    };
  }
}
