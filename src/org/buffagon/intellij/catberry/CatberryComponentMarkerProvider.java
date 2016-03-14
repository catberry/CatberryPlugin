package org.buffagon.intellij.catberry;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlTag;
import icons.CatberryIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * @author Prokofiev Alex
 */
public class CatberryComponentMarkerProvider extends RelatedItemLineMarkerProvider {
  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
    if (element instanceof XmlTag) {
      XmlTag tag = (XmlTag) element;
      String name = tag.getName();
      if(!CatberryConstants.COMPONENTS_TAGS.contains(name)) {
        if(!name.startsWith("cat-"))
          return;
        name = name.substring(4);
      }

      Project project = element.getProject();
      PsiFile file =  CatberryComponentUtils.findComponent(project, name);
      if (file != null) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(AllIcons.General.OverridenMethod).setTarget(file).setTooltipText(
                CatberryBundle.message("navigate.to.component"));
        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }
}

