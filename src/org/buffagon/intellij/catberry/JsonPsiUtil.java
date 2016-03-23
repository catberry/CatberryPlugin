package org.buffagon.intellij.catberry;

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
  public static JsonValue findPropertyValue(@NotNull final JsonValue rootValue, @NotNull final String path) {
    JsonValue currentValue = rootValue;
    for(final String name : path.split("/")) {
      List<JsonProperty> properties = PsiTreeUtil.getChildrenOfTypeAsList(currentValue, JsonProperty.class);
      boolean found = false;
      for (JsonProperty property : properties) {
        if (!property.getName().equals(name))
          continue;
        currentValue = property.getValue();
        found = true;
      }
      if (!found)
        return null;
    }
    return currentValue;

  }
}
