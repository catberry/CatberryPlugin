package org.buffagon.intellij.catberry;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.html.HtmlTag;
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
    if (element instanceof HtmlTag) {
      HtmlTag htmlTag = (HtmlTag) element;
      String name = htmlTag.getName();
      if (name.startsWith("cat-"))
        name = name.substring(4);
      Project project = element.getProject();
      Map<String, PsiFile> map = CatberryComponentUtils.findComponents(project);
      PsiFile file = map.get(name);
      if (file != null) {
        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(CatberryIcons.LOGO_16).setTarget(file).setTooltipText("Navigate to a component");
        result.add(builder.createLineMarkerInfo(element));
      }
    }
  }
}

