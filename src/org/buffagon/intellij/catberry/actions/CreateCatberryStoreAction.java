package org.buffagon.intellij.catberry.actions;

import com.intellij.ide.IdeView;
import com.intellij.ide.util.DirectoryChooserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import icons.CatberryIcons;
import org.buffagon.intellij.catberry.CatberryBundle;
import org.buffagon.intellij.catberry.CatberryProjectSettingsProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

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

    final String path = directory.getVirtualFile().getPath();

    if (!createCatberryStore(path, name))
      return;

    LocalFileSystem.getInstance().refreshWithoutFileWatcher(false);
    PsiFile storeFile = directory.findFile(name);
    if (storeFile == null)
      return;

    view.selectElement(storeFile);
  }

  private boolean createCatberryStore(String path, String name) {
    try {
      Process process = new ProcessBuilder("catberry", "addstore", "--dest=" + path, name).start();
      return process.waitFor() == 0;
    } catch (IOException e) {
      LOG.error(e);
    } catch (InterruptedException e) {
      LOG.error(e);
    }
    return false;
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
