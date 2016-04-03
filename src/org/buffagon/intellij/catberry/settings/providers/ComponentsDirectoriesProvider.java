package org.buffagon.intellij.catberry.settings.providers;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.JsonValue;
import com.intellij.json.psi.impl.JsonStringLiteralImpl;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Prokofiev Alex
 */
public class ComponentsDirectoriesProvider extends BaseConfigurationProvider<PsiDirectory[]>
    implements CachedValueProvider<PsiDirectory[]> {

  private final NotNullLazyValue<ModificationTracker> tracker;
  public ComponentsDirectoriesProvider(Project project, NotNullLazyValue<ModificationTracker> tracker) {
    super(project);
    this.tracker = tracker;
  }

  @Nullable
  @Override
  public Result<PsiDirectory[]> compute() {
    final JsonFile packageJsonFile = findPackageJsonFile();
    if (packageJsonFile == null)
      return Result.create(createValue(), tracker.getValue());

    final JSFile mainJsFile = findMainJsFile(packageJsonFile);
    if(mainJsFile == null)
      return Result.create(createValue(), tracker.getValue());

    final JsonFile configFile = findConfigFile(mainJsFile);
    if(configFile == null || configFile.getTopLevelValue() == null)
      return Result.create(createValue(), tracker.getValue());

    JsonValue jsonValue = JsonPsiUtil.findPropertyValue(configFile.getTopLevelValue(), "componentsGlob");
    if(jsonValue == null)
      return Result.create(createValue(), configFile, mainJsFile, packageJsonFile);
    List<String> paths = new LinkedList<String>();
    if(jsonValue instanceof JsonStringLiteral) {
      paths.add(((JsonStringLiteral)jsonValue).getValue());
    } else if(jsonValue instanceof JsonArray) {
      for(JsonStringLiteral item: PsiTreeUtil.findChildrenOfType(jsonValue, JsonStringLiteral.class))
        paths.add(item.getValue());
    }
    return Result.create(createValue(paths.toArray(new String[paths.size()])), configFile, mainJsFile, packageJsonFile);
  }

  @NotNull
  private PsiDirectory[] createValue(@NotNull String ... paths) {
    if(paths.length == 0)
      paths = new String[] {CatberryConstants.CATBERRY_COMPONENTS};
    List<PsiDirectory> result = new ArrayList<PsiDirectory>(paths.length);
    for(String path : paths) {
      path = path.split("\\*")[0];
      VirtualFile virtualFile = project.getBaseDir().findFileByRelativePath(path);
      if(virtualFile == null)
        continue;
      PsiManager manager = PsiManager.getInstance(project);
      PsiDirectory dir = manager.findDirectory(virtualFile);
      if(dir != null)
        result.add(dir);
    }
    return result.toArray(new PsiDirectory[result.size()]);
  }
}
