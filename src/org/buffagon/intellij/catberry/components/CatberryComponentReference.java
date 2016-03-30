package org.buffagon.intellij.catberry.components;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author Prokofiev Alex
 */
public class CatberryComponentReference extends PsiReferenceBase<HtmlTag> implements PsiPolyVariantReference {
  private final String key;
  public CatberryComponentReference(HtmlTag element, String key) {
    super(element);
    this.key = key;
  }


  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    Project project = myElement.getProject();
    Map<String, PsiFile> map = CatberryComponentUtils.findComponents(project);
    List<ResolveResult> results = new ArrayList<ResolveResult>();
    for (Map.Entry<String, PsiFile> entry: map.entrySet()) {
      if(entry.getKey().startsWith(key))
        results.add(new PsiElementResolveResult(entry.getValue()));
    }
    return results.toArray(new ResolveResult[results.size()]);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    ResolveResult[] resolveResults = multiResolve(false);
    return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }
}
