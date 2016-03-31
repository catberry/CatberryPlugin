package org.buffagon.intellij.catberry.components.tags;

import com.intellij.codeInsight.completion.XmlTagInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.XmlTagNameProvider;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.components.CatberryComponentUtils;
import org.buffagon.intellij.catberry.settings.CatberryProjectConfigurationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * @author Prokofiev Alex
 */
public class CatberryComponentTagDescriptorsProvider implements XmlElementDescriptorProvider, XmlTagNameProvider {
  @Override
  public void addTagNameVariants(final List<LookupElement> elements, @NotNull XmlTag xmlTag, String prefix) {
    CatberryProjectConfigurationManager manager = CatberryProjectConfigurationManager.getInstance(xmlTag.getProject());
    
    if (!(xmlTag instanceof HtmlTag && manager.isCatberryEnabled())) return;

    final Project project = xmlTag.getProject();
    Map<String, PsiFile> map = CatberryComponentUtils.findComponents(project);
    for(Map.Entry<String, PsiFile> entry : map.entrySet()) {
      String key = entry.getKey();
      if(!CatberryConstants.SPECIAL_COMPONENT_NAMES.contains(entry.getKey()))
        key = CatberryConstants.CATBERRY_COMPONENT_TAG_PREFIX + key;
      elements.add(LookupElementBuilder.create(entry.getValue(), key).withInsertHandler(XmlTagInsertHandler.INSTANCE));
    }
  }

  @Nullable
  @Override
  public XmlElementDescriptor getDescriptor(XmlTag xmlTag) {
    final Project project = xmlTag.getProject();
    CatberryProjectConfigurationManager manager = CatberryProjectConfigurationManager.getInstance(project);

    if (!(xmlTag instanceof HtmlTag && manager.isCatberryEnabled())) return null;

    final XmlNSDescriptor nsDescriptor = xmlTag.getNSDescriptor(xmlTag.getNamespace(), false);
    final XmlElementDescriptor descriptor = nsDescriptor != null ? nsDescriptor.getElementDescriptor(xmlTag) : null;
    final boolean special = CatberryConstants.SPECIAL_COMPONENT_NAMES.contains(xmlTag.getName());
    if (descriptor != null && !(descriptor instanceof AnyXmlElementDescriptor || special)) return null;

    final String name = CatberryComponentUtils.normalizeName(xmlTag.getName());
    final PsiFile file = CatberryComponentUtils.findComponent(project, name);
    return file != null ? new CatberryComponentTagDescriptor(xmlTag.getName(), file) : null;
  }
}
