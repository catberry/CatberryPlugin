package org.buffagon.intellij.catberry.actions;

import com.intellij.ide.IdeView;
import com.intellij.ide.util.DirectoryChooserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import icons.CatberryIcons;
import org.buffagon.intellij.catberry.*;
import org.buffagon.intellij.catberry.settings.CatberryProjectConfigurationManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * Action for create new Catberry component.
 *
 * @author Prokofiev Alex
 */
public class CreateCatberryComponentAction extends DumbAwareAction {
  public static final Logger LOG = Logger.getInstance(CreateCatberryStoreAction.class.getName());

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final IdeView view = e.getData(LangDataKeys.IDE_VIEW);
    if (view == null || e.getProject() == null)
      return;

    final Project project = e.getData(CommonDataKeys.PROJECT);
    final PsiDirectory directory = DirectoryChooserUtil.getOrChooseDirectory(view);
    if (directory == null)
      return;

    String name = Messages.showInputDialog(project, CatberryBundle.message("new.cat.component.prompt"),
        CatberryBundle.message("new.cat.component"), CatberryIcons.LOGO_16, "component", null);
    if (name == null)
      return;

    final String path = directory.getVirtualFile().getPath();
    CatberryProjectConfigurationManager configurationManager = CatberryProjectConfigurationManager.getInstance(project);

    TemplateEngine templateEngine = configurationManager.getTemplateEngine();
    if(templateEngine == null) {
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        @Override
        public void run() {
          Notification notification = CatberryConstants.CATBERRY_NOTIFICATION_GROUP.createNotification(
              CatberryBundle.message("template.engine.not.set"), NotificationType.ERROR);
          Notifications.Bus.notify(notification, project);
        }
      });
      return;
    }
    if (!createCatberryModuleStructure(path, name, templateEngine, e.getProject()))
      return;

    LocalFileSystem.getInstance().refreshWithoutFileWatcher(false);
    PsiDirectory componentDir = directory.findSubdirectory(name);
    if (componentDir == null)
      return;

    PsiFile componentFile = componentDir.findFile(CatberryConstants.CAT_COMPONENT_JSON);
    if (componentFile == null)
      return;

    view.selectElement(componentFile);
  }

  private boolean createCatberryModuleStructure(@NotNull final String path, @NotNull final String name,
                                                @NotNull final TemplateEngine templateEngine,
                                                @NotNull final Project project) {
    try {
      final String targetPath = path + File.separator + name;
      File f = new File(targetPath);
      if (f.exists()) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
          @Override
          public void run() {
            Notification notification = CatberryConstants.CATBERRY_NOTIFICATION_GROUP.createNotification(
                CatberryBundle.message("component.already.exists"), NotificationType.ERROR);
            Notifications.Bus.notify(notification, project);
          }
        });
        return false;
      } else {
        f.mkdirs();
      }
      final String camelCaseName = StringUtil.toCamelCase(name, "-");
      final String resourcesDir = "templates/module_presets/component-" + templateEngine + "/";
      URL url = CreateCatberryComponentAction.class.getClassLoader().getResource(resourcesDir);
      FileUtils.copyResourcesRecursively(url, f, false);
      final Processor<String, String> processor = new Processor<String, String>() {
        @Override
        public String process(String value) {
          value = value.replace(CatberryConstants.TEMPLATE_NAME, name);
          return value.replace(CatberryConstants.TEMPLATE_PASCAL_NAME, camelCaseName);
        }
      };
      FileSystemWorker.processTextFilesRecursively(f, processor);
    } catch (Exception e) {
      LOG.error(e);
      return false;
    }
    return true;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    boolean enabled = isEnabled(e);
    e.getPresentation().setVisible(enabled);
    e.getPresentation().setEnabled(enabled);
  }

  private static boolean isEnabled(AnActionEvent e) {
    Project project = e.getData(CommonDataKeys.PROJECT);

    final IdeView ideView = e.getData(LangDataKeys.IDE_VIEW);
    if (project == null || ideView == null)
      return false;

    CatberryProjectConfigurationManager configurationManager = CatberryProjectConfigurationManager.getInstance(project);
    if (!configurationManager.isCatberryEnabled())
      return false;

    final PsiDirectory[] directories = ideView.getDirectories();
    if(directories.length != 1)
      return false;
    URI base = new File(project.getBaseDir().getPath()).toURI();
    URI current = new File(directories[0].getVirtualFile().getPath()).toURI();
    String relativePath = base.relativize(current).getPath();
    return relativePath.startsWith(configurationManager.getComponentsRoot());
  }
}
