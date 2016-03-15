package org.buffagon.intellij.catberry.components;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.HashMap;
import com.intellij.util.indexing.FileBasedIndex;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Prokofiev Alex
 */
public class CatberryComponentUtils {
  public static Map<String, PsiFile> findComponents(Project project) {
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
          if(!found)
            continue;
          String logic = "./index.js";
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
}
