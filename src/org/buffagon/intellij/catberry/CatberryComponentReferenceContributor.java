package org.buffagon.intellij.catberry;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author Prokofiev Alex
 */
public class CatberryComponentReferenceContributor extends PsiReferenceContributor {
  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    registrar.registerReferenceProvider(PlatformPatterns.psiElement(HtmlTag.class),
        new PsiReferenceProvider() {
          @NotNull
          @Override
          public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
            HtmlTag htmlTag = (HtmlTag) element;
            String name = htmlTag.getName();
            if(name.startsWith("cat-"))
              name = name.substring(4);
            return new PsiReference[]{new CatberryComponentReference(htmlTag, name)};
          }
        });
  }
}
