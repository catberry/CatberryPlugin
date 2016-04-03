package org.buffagon.intellij.catberry.settings.providers;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.util.ObjectUtils;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author Prokofiev Alex
 */
public class StoresDirectoryProvider extends BaseConfigurationProvider<PsiDirectory>
    implements CachedValueProvider<PsiDirectory> {

  private final NotNullLazyValue<ModificationTracker> tracker;
  public StoresDirectoryProvider(Project project, NotNullLazyValue<ModificationTracker> tracker) {
    super(project);
    this.tracker = tracker;
  }

  @Nullable
  @Override
  public Result<PsiDirectory> compute() {
    final JsonFile packageJsonFile = findPackageJsonFile();
    if (packageJsonFile == null)
      return Result.create(createValue(null), tracker.getValue());

    final JSFile mainJsFile = findMainJsFile(packageJsonFile);
    if(mainJsFile == null)
      return Result.create(createValue(null), tracker.getValue());

    final JsonFile configFile = findConfigFile(mainJsFile);
    if(configFile == null || configFile.getTopLevelValue() == null)
      return Result.create(createValue(null), tracker.getValue());

    JsonStringLiteral jsonValue = ObjectUtils.tryCast(JsonPsiUtil.findPropertyValue(
        configFile.getTopLevelValue(), "storesDirectory"), JsonStringLiteral.class);
    if(jsonValue == null )
      return Result.create(createValue(null), configFile, mainJsFile, packageJsonFile);

    return Result.create(createValue(jsonValue.getValue()), configFile, mainJsFile, packageJsonFile);
  }

  @Nullable
  private PsiDirectory createValue(@Nullable String relativePath) {
    if(relativePath == null)
      relativePath = CatberryConstants.CATBERRY_STORES;
    VirtualFile virtualFile = project.getBaseDir().findFileByRelativePath(relativePath);
    if(virtualFile == null)
      return null;
    PsiManager manager = PsiManager.getInstance(project);
    return manager.findDirectory(virtualFile);
  }
}
