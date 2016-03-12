package org.buffagon.intellij.catberry;

import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.impl.JsonFileImpl;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Prokofiev Alex
 */
public class CatberryProjectConfigurationManager implements ProjectComponent {
  private Project project;

  public CatberryProjectConfigurationManager(Project project) {
    this.project = project;
  }

  @Override
  public void projectOpened() {

  }

  @Override
  public void projectClosed() {

  }

  @Override
  public void initComponent() {

  }

  @Override
  public void disposeComponent() {

  }

  @NotNull
  @Override
  public String getComponentName() {
    return "CatberryProjectConfigurationManager";
  }

  public void updateConfiguration() {
    final CatberryProjectSettingsProvider settingsProvider = CatberryProjectSettingsProvider.getInstance(project);

    updateTemplateEngine(settingsProvider.getTemplateEngine());

  }

  private void updateTemplateEngine(final TemplateEngine templateEngine) {
    VirtualFile file = project.getBaseDir().findChild("package.json");
    boolean changed = false;
    if (file != null) {
      JsonFileImpl jsonFile = (JsonFileImpl) PsiManager.getInstance(project).findFile(file);
      if (jsonFile != null) {
        Set<String> engineTagNames = new HashSet<String>();
        for (TemplateEngine engine : TemplateEngine.values()) {
          engineTagNames.add("catberry-" + engine);
        }
        JsonProperty[] properties = PsiTreeUtil.getChildrenOfType(jsonFile.getTopLevelValue(), JsonProperty.class);
        if (properties != null) {
          for (JsonProperty property : properties) {
            if (!property.getName().equals("dependencies"))
              continue;
            JsonProperty[] dependencies = PsiTreeUtil.getChildrenOfType(property.getValue(), JsonProperty.class);
            if (dependencies == null)
              break;
            for (final JsonProperty dependency : dependencies) {
              if (!engineTagNames.contains(dependency.getName()))
                continue;
              if (!dependency.getName().equals("catberry-" + templateEngine)) {
                changed = true;
                dependency.setName(templateEngine.getName()); //FIXME: exception
              }
              break;
            }
          }
        }
      }
    }


  }

  public static CatberryProjectConfigurationManager getInstance(Project project) {
    return project.getComponent(CatberryProjectConfigurationManager.class);
  }
}
