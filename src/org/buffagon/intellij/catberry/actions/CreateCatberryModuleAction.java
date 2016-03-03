package org.buffagon.intellij.catberry.actions;

import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateDirectoryOrPackageHandler;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.DirectoryChooserUtil;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiFileSystemItem;
import icons.CatberryIcons;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.CatberryProjectSettingsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Действие для создания нового Odoo модуля.
 *
 * @author Прокофьев Алексей
 */
public class CreateCatberryModuleAction extends DumbAwareAction {
  private static final Logger LOG = Logger.getInstance("#org.buffagon.intellij.catberry.actions.CreateCatberryModuleAction");

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final IdeView view = e.getData(LangDataKeys.IDE_VIEW);
    if (view == null) {
      return;
    }
    final Project project = e.getData(CommonDataKeys.PROJECT);
    final PsiDirectory directory = DirectoryChooserUtil.getOrChooseDirectory(view);
    if (directory == null) return;
    CreateDirectoryOrPackageHandler validator = new CreateDirectoryOrPackageHandler(project, directory, false, "") {
      @Override
      protected void createDirectories(String subDirName) {
        super.createDirectories(subDirName);
        PsiFileSystemItem element = getCreatedElement();
        if (element instanceof PsiDirectory)
          createCatberryModuleStructure((PsiDirectory) element);
      }
    };
    Messages.showInputDialog(project, "Please inter new Catberry module name",
                             "New Catberry Module", CatberryIcons.LOGO, "new_module", validator);
    final PsiFileSystemItem result = validator.getCreatedElement();
    if (result != null && result instanceof PsiDirectory) {
      PsiFile moduleFile = ((PsiDirectory) result).findFile(CatberryConstants.DEFAULT_MODULE_JS);
      if (moduleFile != null)
        view.selectElement(moduleFile);
      else
        view.selectElement(result);
    }
  }

  private void createCatberryModuleStructure(PsiDirectory directory) {
    createModuleFile(directory);
  }

  private static void createModuleFile(PsiDirectory directory) {
    PsiFile file = PsiFileFactory.getInstance(directory.getProject()).createFileFromText(
      CatberryConstants.CAT_COMPONENT_JSON, JsonFileType.INSTANCE,
      "{\n" +
        "\t\"name\": \""+directory.getName()+"\",\n" +
        "\t\"template\": \"./template.hbs\",\n" +
      "}\n"
    );
    directory.add(file);
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
    if (project == null || ideView == null) {
      return false;
    }
    CatberryProjectSettingsProvider settingsProvider = CatberryProjectSettingsProvider.getInstance(project);
    if(!settingsProvider.isCatberryEnabled())
      return false;
    final PsiDirectory[] directories = ideView.getDirectories();
    return directories.length == 1 && directories[0].getVirtualFile().equals(project.getBaseDir());
  }
}
