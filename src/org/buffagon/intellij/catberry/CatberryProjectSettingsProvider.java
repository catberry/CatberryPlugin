/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.buffagon.intellij.catberry;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Catberry settings provider.
 *
 * @author Prokofiev Alex
 */
@State(name = "CatberryProjectSettingsProvider", storages = {@Storage(file = StoragePathMacros.PROJECT_FILE)})
public class CatberryProjectSettingsProvider
    implements PersistentStateComponent<CatberryProjectSettings>, ProjectComponent, CatberryReadWriteSettings {
  @Override
  public void projectOpened() {
  }

  @Override
  public void projectClosed() {
  }

  private CatberryProjectSettings settings = new CatberryProjectSettings();

  public static CatberryProjectSettingsProvider getInstance(Project project) {
    return project.getComponent(CatberryProjectSettingsProvider.class);
  }

  @Override
  public CatberryProjectSettings getState() {
    return settings;
  }

  @Override
  public void loadState(CatberryProjectSettings state) {
    settings.setCatberryEnabled(state.isCatberryEnabled());
    settings.setTemplateEngineName(state.getTemplateEngineName());
    settings.setComponentsRoot(state.getComponentsRoot());
    settings.setStoresRoot(state.getStoresRoot());
  }

  public boolean isCatberryEnabled() {
    return settings.isCatberryEnabled();
  }

  public void setCatberryEnabled(boolean value) {
    settings.setCatberryEnabled(value);
  }

  public String getTemplateEngineName() {
    return settings.getTemplateEngineName();
  }

  public void setTemplateEngineName(String name) {
    settings.setTemplateEngineName(name);
  }

  public String getComponentsRoot() {
    return settings.getComponentsRoot();
  }

  public void setComponentsRoot(String root) {
    settings.setComponentsRoot(root);
  }

  public String getStoresRoot() {
    return settings.getStoresRoot();
  }

  public void setStoresRoot(String root) {
    settings.setStoresRoot(root);
  }


  @Override
  public void initComponent() {
  }

  @Override
  public void disposeComponent() {
  }

  @NotNull
  @Override
  public String getComponentName() {
    return "CatberryProjectSettingsProvider";
  }
}

