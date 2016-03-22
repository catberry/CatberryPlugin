package org.buffagon.intellij.catberry;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonValue;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author Prokofiev Alex
 */
public final class JsonPsiUtil {
  @Nullable
  public static JsonValue findPropertyValue(@NotNull final JsonFile file, @NotNull final String path) {
    JsonValue currentValue = file.getTopLevelValue();
    for(final String name : path.split("/")) {
      //TODO: сделать выборку json свойств
      List<JsonProperty> properties = PsiTreeUtil.getChildrenOfTypeAsList(currentValue, JsonProperty.class);
      for (JsonProperty property : properties) {
        if (!property.getName().equals(name))
          continue;
        currentValue = property.getValue();
        if (currentValue == null)
          return null;
        final List<JsonProperty> dependencies = PsiTreeUtil.getChildrenOfTypeAsList(propertyObject, JsonProperty.class);
        for (final JsonProperty dependency : dependencies) {
          if (!"catberry".equals(dependency.getName()))
            continue;
          value = dependency.getValue();
          break;
        }
      }
    if(value != null)
      return Result.create(value.getText(), value.getContainingFile());
  }
}
