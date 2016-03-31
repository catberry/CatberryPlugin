package org.buffagon.intellij.catberry.components;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.JsonValue;
import com.intellij.json.psi.impl.JsonStringLiteralImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.HashMap;
import com.intellij.util.indexing.FileBasedIndex;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.buffagon.intellij.catberry.TemplateEngine;
import org.buffagon.intellij.catberry.settings.CatberryProjectConfigurationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * @author Prokofiev Alex
 */
public class CatberryComponentUtils {
  @NotNull
  public static Map<String, PsiFile> findComponents(@NotNull final Project project) {
    Map<String, PsiFile> result = new HashMap<String, PsiFile>();
    Collection<VirtualFile> virtualFiles =
        FileBasedIndex.getInstance().getContainingFiles(FilenameIndex.NAME, CatberryConstants.CAT_COMPONENT_JSON,
            GlobalSearchScope.allScope(project));
    for (VirtualFile virtualFile : virtualFiles) {
      JsonFile psiFile = (JsonFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (psiFile != null) {
        JsonProperty[] properties = PsiTreeUtil.getChildrenOfType(psiFile.getTopLevelValue(), JsonProperty.class);
        if (properties != null) {
          for (JsonProperty property : properties) {
            if (!property.getName().equals("name"))
              continue;
            if (property.getValue() != null && property.getValue() instanceof JsonStringLiteral)
              result.put(((JsonStringLiteral) property.getValue()).getValue(), psiFile);
            break;
          }
        }
      }
    }
    return result;
  }

  @Nullable
  public static PsiFile findComponent(@NotNull final Project project, @NotNull final String name) {
    Collection<VirtualFile> virtualFiles =
        FileBasedIndex.getInstance().getContainingFiles(FilenameIndex.NAME, CatberryConstants.CAT_COMPONENT_JSON,
            GlobalSearchScope.allScope(project));
    for (VirtualFile virtualFile : virtualFiles) {
      JsonFile psiFile = (JsonFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (psiFile != null) {
        JsonProperty[] properties = PsiTreeUtil.getChildrenOfType(psiFile.getTopLevelValue(), JsonProperty.class);
        if (properties != null) {
          boolean found = false;
          for (JsonProperty property : properties) {
            if (!property.getName().equals("name"))
              continue;
            if (property.getValue() != null && property.getValue() instanceof JsonStringLiteral)
              found = name.equals(((JsonStringLiteral) property.getValue()).getValue());
            break;
          }
          if (!found)
            continue;
          String logic = CatberryConstants.DEFAULT_COMPONENT_JS;
          for (JsonProperty property : properties) {
            if (!property.getName().equals("logic"))
              continue;
            if (property.getValue() != null && property.getValue() instanceof JsonStringLiteral)
              logic = ((JsonStringLiteral) property.getValue()).getValue();
            break;
          }
          VirtualFile f = psiFile.getVirtualFile().getParent();
          f = f.findFileByRelativePath(logic);
          if (f != null)
            return PsiManager.getInstance(project).findFile(f);
          return null;
        }
      }
    }
    return null;
  }

  @Nullable
  public static PsiFile findComponentTemplate(@NotNull final Project project, @NotNull String name) {
    Collection<VirtualFile> virtualFiles =
        FileBasedIndex.getInstance().getContainingFiles(FilenameIndex.NAME, CatberryConstants.CAT_COMPONENT_JSON,
            GlobalSearchScope.allScope(project));
    CatberryProjectConfigurationManager manager = CatberryProjectConfigurationManager.getInstance(project);
    final TemplateEngine engine = manager.getTemplateEngine();
    if(engine == null)
      return null;
    for (VirtualFile virtualFile : virtualFiles) {
      JsonFile psiFile = (JsonFile) PsiManager.getInstance(project).findFile(virtualFile);
      if (psiFile != null) {
        JsonStringLiteralImpl value = ObjectUtils.tryCast(JsonPsiUtil.findPropertyValue(
            psiFile.getTopLevelValue(), "name"), JsonStringLiteralImpl.class);
        if (value == null || !name.equals(value.getValue()))
          continue;

        String template = "./" + CatberryConstants.DEFAULT_TEMPLATE_PREFIX + engine.getExtension();
        value = ObjectUtils.tryCast(JsonPsiUtil.findPropertyValue(
            psiFile.getTopLevelValue(), "template"), JsonStringLiteralImpl.class);
        if (value != null)
          template = value.getValue();

        VirtualFile f = psiFile.getVirtualFile().getParent();
        f = f.findFileByRelativePath(template);
        return f != null ? PsiManager.getInstance(project).findFile(f) : null;
      }
    }
    return null;
  }

  @NotNull
  public static String normalizeName(@NotNull String name) {
    if (name.startsWith(CatberryConstants.CATBERRY_COMPONENT_TAG_PREFIX))
      name = name.substring(CatberryConstants.CATBERRY_COMPONENT_TAG_PREFIX.length());
    return name;
  }
}
