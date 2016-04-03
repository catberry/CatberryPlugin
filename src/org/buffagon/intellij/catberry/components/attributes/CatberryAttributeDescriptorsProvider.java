package org.buffagon.intellij.catberry.components.attributes;

import com.intellij.lang.javascript.psi.stubs.JSClassIndex;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlAttributeDescriptorsProvider;
import org.buffagon.intellij.catberry.components.tags.CatberryComponentTagDescriptor;
import org.buffagon.intellij.catberry.settings.CatberryProjectConfigurationManager;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dennis.Ushakov
 */
public class CatberryAttributeDescriptorsProvider implements XmlAttributeDescriptorsProvider {
  @Override
  public XmlAttributeDescriptor[] getAttributeDescriptors(XmlTag xmlTag) {
    final Project project = xmlTag.getProject();
    CatberryProjectConfigurationManager manager = CatberryProjectConfigurationManager.getInstance(project);

    if (manager.isCatberryEnabled() && (xmlTag.getDescriptor() instanceof CatberryComponentTagDescriptor)) {
      return new CatberryAttributeDescriptor[] {new CatberryAttributeDescriptor(project, "cat-store")};
    }
    return XmlAttributeDescriptor.EMPTY;
  }


  @Nullable
  @Override
  public XmlAttributeDescriptor getAttributeDescriptor(final String attrName, XmlTag xmlTag) {
    if (xmlTag != null && "cat-store".equals(attrName)) {
      return new CatberryAttributeDescriptor(xmlTag.getProject(), "cat-store");
    }
    return null;
  }
}
