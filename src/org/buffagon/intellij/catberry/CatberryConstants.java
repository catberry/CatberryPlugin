package org.buffagon.intellij.catberry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Prokofiev Alex
 */
public final class CatberryConstants {
  public static final String DEFAULT_MODULE_JS = "index.js";
  public static final String DEFAULT_TEMPLATE_ENGINE = "handlebars";
  public static final String CAT_COMPONENT_JSON = "cat-component.json";
  public static final String CATBERRY_COMPONENTS = "catberry_components";
  public static final String CATBERRY_STORES = "catberry_stores";
  public static final Set<String> COMPONENTS_TAGS = new HashSet<String>();

  static {
    COMPONENTS_TAGS.addAll(Arrays.asList("head"));
  }

}
