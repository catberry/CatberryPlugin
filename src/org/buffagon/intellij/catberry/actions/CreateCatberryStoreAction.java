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
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import icons.CatberryIcons;
import org.buffagon.intellij.catberry.CatberryBundle;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.CatberryProjectSettingsProvider;
import org.buffagon.intellij.catberry.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Action for create new Catberry store.
 *
 * @author Prokofiev Alex
 */
public class CreateCatberryStoreAction extends DumbAwareAction {
  public static final Logger LOG = Logger.getInstance(CreateCatberryStoreAction.class.getName());

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final IdeView view = e.getData(LangDataKeys.IDE_VIEW);
    if (view == null) {
      return;
    }
    final Project project = e.getData(CommonDataKeys.PROJECT);
    final PsiDirectory directory = DirectoryChooserUtil.getOrChooseDirectory(view);
    if (directory == null)
      return;

    String name = Messages.showInputDialog(project, CatberryBundle.message("new.cat.store.prompt"),
        CatberryBundle.message("new.cat.store"), CatberryIcons.LOGO_16, "Store", null);
    if (name == null)
      return;
    name = StringUtils.toCamelCase(name, "-");
    final String path = directory.getVirtualFile().getPath();

    if (!createCatberryStore(path, name, e.getProject()))
      return;

    LocalFileSystem.getInstance().refreshWithoutFileWatcher(false);
    PsiFile storeFile = directory.findFile(name + ".js");
    if (storeFile == null)
      return;

    view.selectElement(storeFile);
  }

  private boolean createCatberryStore(@NotNull final String path, @NotNull final String name,
                                      @NotNull final Project project) {
    InputStream in = getClass().getClassLoader().getResourceAsStream("templates/module_presets/Store.js");
    try {
      File f = new File(path + File.separator + name + ".js");
      if (f.exists()) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
          @Override
          public void run() {
            Notification notification = CatberryConstants.CATBERRY_NOTIFICATION_GROUP.createNotification(
                CatberryBundle.message("store.already.exists"), NotificationType.ERROR);
            Notifications.Bus.notify(notification, project);
          }
        });
        return false;
      }
      BufferedWriter writer = new BufferedWriter(new FileWriter(f));
      BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
      String buf;
      while ((buf = rdr.readLine()) != null) {
        writer.write(buf.replace(CatberryConstants.TEMPLATE_PASCAL_NAME, name));
        writer.newLine();
      }
      writer.close();
      in.close();
    } catch (IOException e) {
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
        directories[0].getVirtualFile().getPath().contains(settingsProvider.getStoresRoot()));
  }
}
