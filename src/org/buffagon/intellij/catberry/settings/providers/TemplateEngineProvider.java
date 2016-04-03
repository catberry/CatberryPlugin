package org.buffagon.intellij.catberry.settings.providers;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonValue;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.HashMap;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.buffagon.intellij.catberry.TemplateEngine;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author Prokofiev Alex
 */
public class TemplateEngineProvider extends BaseConfigurationProvider<TemplateEngine>
    implements CachedValueProvider<TemplateEngine> {
  private final NotNullLazyValue<ModificationTracker> tracker;
  private final Map<String, TemplateEngine> engines = new HashMap<String, TemplateEngine>();
  public TemplateEngineProvider(Project project, NotNullLazyValue<ModificationTracker> tracker) {
    super(project);

    this.tracker = tracker;
    for (TemplateEngine engine : TemplateEngine.values()) {
      engines.put("catberry-" + engine, engine);
    }
  }

  @Nullable
  @Override
  public Result<TemplateEngine> compute() {
    final JsonFile packageJsonFile = findPackageJsonFile();
    if (packageJsonFile == null)
      return Result.create(null, tracker.getValue());

    if(packageJsonFile.getTopLevelValue()==null)
      return Result.create(null, packageJsonFile);

    JsonValue dependencies = JsonPsiUtil.findPropertyValue(packageJsonFile.getTopLevelValue(), "dependencies");
    if(dependencies == null)
      return Result.create(null, packageJsonFile);
    List<JsonProperty> properties = PsiTreeUtil.getChildrenOfTypeAsList(dependencies, JsonProperty.class);
    for (JsonProperty property : properties) {
      TemplateEngine engine = engines.get(property.getName());
      if (engine != null)
        return Result.create(engine, packageJsonFile);
    }
    return Result.create(null, packageJsonFile);
  }
}
