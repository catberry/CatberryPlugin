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
import org.buffagon.intellij.catberry.CatberryBundle;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.CatberryProjectSettingsProvider;
import org.buffagon.intellij.catberry.TemplateEngine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;

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
    if (view == null)
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

    TemplateEngine templateEngine = CatberryProjectSettingsProvider.getInstance(project).getTemplateEngine();
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
      Enumeration<URL> resources =
          getClass().getClassLoader().getResources("templates/module_presets/component-" + templateEngine + "/*");
      File f = new File(path + File.separator + name);
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
      }
      final File baseDir = new File(path);

      while(resources.hasMoreElements()) {
        URI uri = resources.nextElement().toURI();
        String relative = uri.relativize(baseDir.toURI()).getPath();
        //todo:

        InputStream in = new FileInputStream(new File(uri));

        in.close();
      }
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

    CatberryProjectSettingsProvider settingsProvider = CatberryProjectSettingsProvider.getInstance(project);
    if (!settingsProvider.isCatberryEnabled())
      return false;

    final PsiDirectory[] directories = ideView.getDirectories();
    return (directories.length == 1 &&
        directories[0].getVirtualFile().getPath().contains(settingsProvider.getComponentsRoot()));
  }
}
