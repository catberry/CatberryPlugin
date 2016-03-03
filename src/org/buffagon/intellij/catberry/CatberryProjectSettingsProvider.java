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
 * Профайдер настроек плагина.
 *
 * @author Прокофьев Алексей
 */
@State(name = "CatberryProjectSettingsProvider", storages = {@Storage(file = StoragePathMacros.PROJECT_FILE)})
public class CatberryProjectSettingsProvider
    implements PersistentStateComponent<CatberryProjectSettingsProvider.State>, ProjectComponent {
  @Override
  public void projectOpened() {

  }

  @Override
  public void projectClosed() {

  }

  public static class State {
    public boolean myCatberryEnabled = false;
    public String myTemplateEngineName = "Handlebars";
  }

  private State myState = new State();

  public static CatberryProjectSettingsProvider getInstance(Project project) {
    return project.getComponent(CatberryProjectSettingsProvider.class);
  }

  @Override
  public State getState() {
    return myState;
  }

  @Override
  public void loadState(State state) {
    myState.myCatberryEnabled = state.myCatberryEnabled;
    myState.myTemplateEngineName = state.myTemplateEngineName;
  }

  public boolean isCatberryEnabled() {
    return myState.myCatberryEnabled;
  }

  public void setCatberryEnabled(boolean value) {
    myState.myCatberryEnabled = value;
  }

  public String getTemplateEngineName() {
    return myState.myTemplateEngineName;
  }

  public void setTemplateEngineName(String name) {
    myState.myTemplateEngineName = name;
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
