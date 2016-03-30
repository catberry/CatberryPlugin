package org.buffagon.intellij.catberry.components;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.ProcessingContext;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Prokofiev Alex
 */
public class TagNameCompletionContributor extends CompletionContributor {
  public TagNameCompletionContributor() {
    extend(CompletionType.BASIC,

        PlatformPatterns.psiElement(XmlToken.class).withParent(XmlTag.class),
        new CompletionProvider<CompletionParameters>() {
          public void addCompletions(@NotNull CompletionParameters parameters,
                                     ProcessingContext context,
                                     @NotNull CompletionResultSet resultSet) {
            Map<String, PsiFile> map = CatberryComponentUtils.findComponents(parameters.getOriginalFile().getProject());
            for(String key : map.keySet()) {
              if(CatberryConstants.SPECIAL_COMPONENT_NAMES.contains(key))
                continue;
              resultSet.addElement(LookupElementBuilder.create(CatberryConstants.CATBERRY_COMPONENT_TAG_PREFIX + key));
            }
          }
        }
    );
  }
}
