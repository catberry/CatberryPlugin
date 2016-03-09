package org.buffagon.intellij.catberry;

import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import icons.CatberryIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;

/**
 * @author Prokofiev Alex
 */
public class CatberryProjectGenerator extends WebProjectTemplate<CatberryReadOnlySettings>
    implements Comparable<CatberryProjectGenerator>{
  public static final Logger LOG = Logger.getInstance(CatberryProjectGenerator.class.getName());

  @Nls
  @NotNull
  @Override
  public String getName() {
    return CatberryBundle.message("catberry.project.title");
  }

  @Override
  public Icon getIcon() {
    return CatberryIcons.LOGO_16;
  }

  @Override
  public String getDescription() {
    return CatberryBundle.message("catberry.project.description");
  }

  @Override
  public void generateProject(@NotNull final Project project, @NotNull final VirtualFile baseDir,
                              @NotNull final CatberryReadOnlySettings data, @NotNull final Module module) {
    ApplicationManager.getApplication().runWriteAction(
      new Runnable() {
        public void run() {
          final ModifiableRootModel modifiableModel =
              ModifiableModelsProvider.SERVICE.getInstance().getModuleModifiableModel(module);
          CatberryProjectSettingsProvider settingsProvider = CatberryProjectSettingsProvider.getInstance(project);
          CatberryProjectSettings.copy(data, settingsProvider);
          final String template = data.getTemplateEngineName();
          try {
            ProcessBuilder processBuilder = new ProcessBuilder();

            final String os = System.getProperty("os.name").toLowerCase();
            if(os.contains("win")) {
              processBuilder.command("catberry", "init", "--dest=" + baseDir.getPath(), "empty-" + template);
            } else {
              String env_path = System.getenv("PATH");
              if(!env_path.contains("/bin"))
                env_path = "/bin:" + env_path;

              if(!env_path.contains("/usr/bin"))
                env_path = "/usr/bin:" + env_path;

              if(!env_path.contains("/usr/local/bin"))
                env_path = "/usr/local/bin:" + env_path;

              final String command = "catberry init --dest=" + baseDir.getPath() + " empty-" + template;
              processBuilder.command("sh", "-c", "export PATH=" + env_path + "&& " + command);
            }

            Process process = processBuilder.start();
            process.waitFor();
          } catch (IOException e) {
            LOG.error(e.toString());
          } catch (InterruptedException e) {
            LOG.error(e.toString());
          }
          ModifiableModelsProvider.SERVICE.getInstance().commitModuleModifiableModel(modifiableModel);
        }
      }
    );
  }

  @NotNull
  @Override
  public GeneratorPeer<CatberryReadOnlySettings> createPeer() {
    return new CatberryGeneratorPeer();
  }

  @Override
  public int compareTo(@NotNull final CatberryProjectGenerator generator) {
    return getName().compareTo(generator.getName());
  }
}
