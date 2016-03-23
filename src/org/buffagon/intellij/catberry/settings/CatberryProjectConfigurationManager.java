package org.buffagon.intellij.catberry.settings;

import com.intellij.ProjectTopics;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonValue;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.buffagon.intellij.catberry.TemplateEngine;
import org.buffagon.intellij.catberry.settings.providers.ComponentsRootProvider;
import org.buffagon.intellij.catberry.settings.providers.StoresRootProvider;
import org.buffagon.intellij.catberry.settings.providers.TemplateEngineProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Prokofiev Alex
 */

public class CatberryProjectConfigurationManager implements ProjectComponent {
  private static final Key<NotNullLazyValue<ModificationTracker>> TRACKER = Key.create("catberry.js.tracker");
  private static final Logger LOG = Logger.getInstance(CatberryProjectConfigurationManager.class.getName());
  private Project project;

  public CatberryProjectConfigurationManager(Project project) {
    this.project = project;
  }

  @Override
  public void projectOpened() {

  }

  @Override
  public void projectClosed() {

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
    return "CatberryProjectConfigurationManager";
  }

  public boolean isCatberryEnabled() {
    return getCatberryVersion() != null;
  }

  @Nullable
  private String getCatberryVersion() {
    if (DumbService.isDumb(project))
      return null;

    final NotNullLazyValue<ModificationTracker> tracker = getCatberryTracker();
    return CachedValuesManager.getManager(project).getCachedValue(project, new CachedValueProvider<String>() {
      @Nullable
      @Override
      public Result<String> compute() {
        VirtualFile vf = project.getBaseDir().findChild("package.json");
        if (vf == null)
          return Result.create(null, tracker.getValue());
        final JsonFile file = (JsonFile) PsiManager.getInstance(project).findFile(vf);
        if (file == null || file.getTopLevelValue()==null)
          return Result.create(null, tracker.getValue());
        JsonValue value = JsonPsiUtil.findPropertyValue(file.getTopLevelValue(), "dependencies/catberry");
        if (value != null)
          return Result.create(value.getText(), file);
        return Result.create(null, tracker.getValue());
      }
    });
  }

  @NotNull
  public String getComponentsRoot() {
    String value = null;
    if (!DumbService.isDumb(project)) {
      final NotNullLazyValue<ModificationTracker> tracker = getCatberryTracker();
      ComponentsRootProvider provider = new ComponentsRootProvider(project, tracker);
      value = CachedValuesManager.getManager(project).getCachedValue(project, provider);
    }

    if(value == null)
      value = CatberryConstants.CATBERRY_COMPONENTS;
    if(!value.endsWith("/"))
      value += "/";
    return value;
  }

  @Nullable
  public TemplateEngine getTemplateEngine() {
    if (DumbService.isDumb(project))
      return null;
    final NotNullLazyValue<ModificationTracker> tracker = getCatberryTracker();
    TemplateEngineProvider provider = new TemplateEngineProvider(project, tracker);
    return CachedValuesManager.getManager(project).getCachedValue(project, provider);
  }

  @NotNull
  public String getStoresRoot() {
    String value = null;
    if (!DumbService.isDumb(project)) {
      final NotNullLazyValue<ModificationTracker> tracker = getCatberryTracker();
      StoresRootProvider provider = new StoresRootProvider(project, tracker);
      value = CachedValuesManager.getManager(project).getCachedValue(project, provider);
    }

    if(value == null)
      value = CatberryConstants.CATBERRY_STORES;
    if(value.startsWith("./")) {
      value = new StringBuilder(value).delete(0,2).toString();
    }
    if(!value.endsWith("/"))
      value += "/";
    return value;
  }


  public static CatberryProjectConfigurationManager getInstance(Project project) {
    return project.getComponent(CatberryProjectConfigurationManager.class);
  }


  private NotNullLazyValue<ModificationTracker> getCatberryTracker() {
    NotNullLazyValue<ModificationTracker> tracker = project.getUserData(TRACKER);
    if (tracker == null) {
      tracker = new AtomicNotNullLazyValue<ModificationTracker>() {
        @NotNull
        @Override
        protected ModificationTracker compute() {
          return new CatberryModificationTracker(project);
        }
      };
      tracker = ((UserDataHolderEx) project).putUserDataIfAbsent(TRACKER, tracker);
    }
    return tracker;
  }

  private static class CatberryModificationTracker extends SimpleModificationTracker {
    CatberryModificationTracker(final Project project) {
      VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
        @Override
        public void fileCreated(@NotNull VirtualFileEvent event) {
          incModificationCount();
        }
      }, project);
      project.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
        @Override
        public void rootsChanged(ModuleRootEvent event) {
          incModificationCount();
        }
      });
    }
  }

}
