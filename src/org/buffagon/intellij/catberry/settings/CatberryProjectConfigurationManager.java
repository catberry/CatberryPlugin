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
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.buffagon.intellij.catberry.CatberryConstants;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.buffagon.intellij.catberry.TemplateEngine;
import org.buffagon.intellij.catberry.settings.providers.ComponentsDirectoriesProvider;
import org.buffagon.intellij.catberry.settings.providers.StoresDirectoryProvider;
import org.buffagon.intellij.catberry.settings.providers.TemplateEngineProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Prokofiev Alex
 */
public class CatberryProjectConfigurationManager implements ProjectComponent {
  private static final Key<NotNullLazyValue<ModificationTracker>> TRACKER = Key.create("catberry.js.tracker");
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
  public PsiDirectory[] getComponentsDirectories() {
    if (DumbService.isDumb(project))
      return new PsiDirectory[0];

    final NotNullLazyValue<ModificationTracker> tracker = getCatberryTracker();
    ComponentsDirectoriesProvider provider = new ComponentsDirectoriesProvider(project, tracker);
    String[] res = CachedValuesManager.getManager(project).getCachedValue(project, provider);

    List<PsiDirectory> result = new ArrayList<PsiDirectory>(res.length);
    for(String path : res) {
      VirtualFile virtualFile = project.getBaseDir().findFileByRelativePath(path);
      if(virtualFile == null)
        continue;
      PsiManager manager = PsiManager.getInstance(project);
      PsiDirectory dir = manager.findDirectory(virtualFile);
      if(dir != null)
        result.add(dir);
    }
    return result.toArray(new PsiDirectory[result.size()]);
  }

  @Nullable
  public TemplateEngine getTemplateEngine() {
    if (DumbService.isDumb(project))
      return null;
    final NotNullLazyValue<ModificationTracker> tracker = getCatberryTracker();
    TemplateEngineProvider provider = new TemplateEngineProvider(project, tracker);
    return CachedValuesManager.getManager(project).getCachedValue(project, provider);
  }

  @Nullable
  public PsiDirectory getStoresDirectory() {
    if (DumbService.isDumb(project))
      return null;
    final NotNullLazyValue<ModificationTracker> tracker = getCatberryTracker();
    StoresDirectoryProvider provider = new StoresDirectoryProvider(project, tracker);
    String res = CachedValuesManager.getManager(project).getCachedValue(project, provider);
    VirtualFile virtualFile = project.getBaseDir().findFileByRelativePath(res);
    if(virtualFile == null)
      return null;
    PsiManager manager = PsiManager.getInstance(project);
    return manager.findDirectory(virtualFile);
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
