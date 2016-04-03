package org.buffagon.intellij.catberry.index;

import com.intellij.lang.javascript.index.FrameworkIndexingHandler;
import com.intellij.lang.javascript.index.JSImplicitElementsIndex;
import com.intellij.lang.javascript.index.JSIndexContentBuilder;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.ecmal4.JSClass;
import com.intellij.lang.javascript.psi.stubs.JSImplicitElement;
import com.intellij.lang.javascript.psi.stubs.impl.JSImplicitElementImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Prokofiev Alex
 */
public class CatberryIndexingHandler extends FrameworkIndexingHandler {
  @Override
  public void processFile(@NotNull JSFile file, @NotNull JSIndexContentBuilder indexBuilder) {
    if(!"Main.js".equals(file.getName()))
      return;

    final JSImplicitElementImpl.Builder elementBuilder =
        new JSImplicitElementImpl.Builder(file.getName(), file).setType(JSImplicitElement.Type.Class);

    elementBuilder.setTypeString("CatberryClass");
    indexBuilder.addImplicitElement(file.getName(), new JSImplicitElementsIndex.JSElementProxy(elementBuilder, 0));
  }

  @Override
  public boolean processCustomElement(@NotNull PsiElement customElement, @NotNull JSIndexContentBuilder builder) {
    final JSClass jsClass = ObjectUtils.tryCast(customElement, JSClass.class);
    if(jsClass == null)
      return false;

    if(!"Main".equals(jsClass.getName()))
      return false;

    final JSImplicitElementImpl.Builder elementBuilder =
        new JSImplicitElementImpl.Builder(jsClass.getName(), jsClass).setType(JSImplicitElement.Type.Class);

    elementBuilder.setTypeString("CatberryClass");
    builder.addImplicitElement(jsClass.getName(), new JSImplicitElementsIndex.JSElementProxy(elementBuilder, 0));
    return true;
  }

  @Override
  public boolean indexImplicitElement(@NotNull JSImplicitElement element, @Nullable IndexSink sink) {
    final String userID = element.getUserString();
    return false;
  }

}
