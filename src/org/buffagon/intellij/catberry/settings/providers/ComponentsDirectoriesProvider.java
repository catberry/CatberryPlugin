package org.buffagon.intellij.catberry.settings.providers;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.JsonValue;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.PsiTreeUtil;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Prokofiev Alex
 */
public class ComponentsDirectoriesProvider extends BaseConfigurationProvider<String[]>
    implements CachedValueProvider<String[]> {

  private final NotNullLazyValue<ModificationTracker> tracker;
  public ComponentsDirectoriesProvider(Project project, NotNullLazyValue<ModificationTracker> tracker) {
    super(project);
    this.tracker = tracker;
  }

  @Nullable
  @Override
  public Result<String[]> compute() {
    List<Object> dependencies = new LinkedList<Object>();
    for(JsonFile configFile : findConfigFiles()) {
      dependencies.add(configFile);
      JsonValue jsonValue = JsonPsiUtil.findPropertyValue(configFile.getTopLevelValue(), "componentsGlob");
      if(jsonValue == null)
        continue;
      List<String> paths = new LinkedList<String>();
      if(jsonValue instanceof JsonStringLiteral) {
        paths.add(((JsonStringLiteral)jsonValue).getValue());
      } else if(jsonValue instanceof JsonArray) {
        for(JsonStringLiteral item: PsiTreeUtil.findChildrenOfType(jsonValue, JsonStringLiteral.class))
          paths.add(item.getValue());
      }
      return Result.create(createValue(paths.toArray(new String[paths.size()])), jsonValue);
    }
    dependencies.add(tracker.getValue());
    return Result.create(createValue(), dependencies);
  }

  @NotNull
  private String[] createValue(@NotNull String ... paths) {
    if(paths.length == 0)
      paths = new String[] {CatberryConstants.CATBERRY_COMPONENTS};
    List<String> result = new ArrayList<String>(paths.length);
    for(String path : paths) {
      result.add(path.split("\\*")[0]);
    }
    return result.toArray(new String[result.size()]);
  }
}
