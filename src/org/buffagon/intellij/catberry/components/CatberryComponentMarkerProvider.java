package org.buffagon.intellij.catberry.components;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import org.buffagon.intellij.catberry.CatberryBundle;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Prokofiev Alex
 */
public class CatberryComponentMarkerProvider extends RelatedItemLineMarkerProvider {
  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
    if (element instanceof XmlTag) {
      XmlTag tag = (XmlTag) element;

      String name = tag.getName();
      if(!CatberryConstants.SPECIAL_COMPONENT_NAMES.contains(name)) {
        if(!name.startsWith(CatberryConstants.CATBERRY_COMPONENT_TAG_PREFIX))
          return;
        name = CatberryComponentUtils.normalizeName(name);
      }

      final Project project = element.getProject();
      final PsiFile file =  CatberryComponentUtils.findComponentTemplate(project, name);
      if (file != null) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(AllIcons.General.OverridenMethod).setTarget(file).setTooltipText(
                CatberryBundle.message("navigate.to.component"));
        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }
}

