package org.buffagon.intellij.catberry.components;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


/**
 * @author Prokofiev Alex
 */
public class CatberryComponentReference extends PsiReferenceBase<XmlTag>{
  private final String key;
  public CatberryComponentReference(HtmlTag element, String key) {
    super(element);
    this.key = key;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    Project project = myElement.getProject();

    Map<String, PsiFile> map = CatberryComponentUtils.findComponents(project);
    return map.get(key);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }
}
