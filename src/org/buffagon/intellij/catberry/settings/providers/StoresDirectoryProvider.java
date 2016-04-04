package org.buffagon.intellij.catberry.settings.providers;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.util.ObjectUtils;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Prokofiev Alex
 */
public class StoresDirectoryProvider extends BaseConfigurationProvider<String>
    implements CachedValueProvider<String> {

  private final NotNullLazyValue<ModificationTracker> tracker;
  public StoresDirectoryProvider(Project project, NotNullLazyValue<ModificationTracker> tracker) {
    super(project);
    this.tracker = tracker;
  }

  @SuppressWarnings("Duplicates")
  @Nullable
  @Override
  public Result<String> compute() {
    List<Object> dependencies = new LinkedList<Object>();
    for(JsonFile configFile : findConfigFiles()) {
      dependencies.add(configFile);
      JsonStringLiteral jsonValue = ObjectUtils.tryCast(
          JsonPsiUtil.findPropertyValue(configFile.getTopLevelValue(),"storesDirectory"), JsonStringLiteral.class);
      if(jsonValue == null)
        continue;
      return Result.create(createValue(jsonValue.getValue()), configFile);
    }
    dependencies.add(tracker.getValue());
    return Result.create(createValue(null), dependencies);
  }

  @Nullable
  private String createValue(@Nullable String relativePath) {
    return  relativePath == null ? CatberryConstants.CATBERRY_STORES : relativePath;
  }
}
