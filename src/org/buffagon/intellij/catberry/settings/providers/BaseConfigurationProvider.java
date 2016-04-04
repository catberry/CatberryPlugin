package org.buffagon.intellij.catberry.settings.providers;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.impl.JsonStringLiteralImpl;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.util.ObjectUtils;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Prokofiev Alex
 */
@SuppressWarnings("WeakerAccess")
public abstract class  BaseConfigurationProvider<T> implements CachedValueProvider<T> {
  protected final Project project;

  public BaseConfigurationProvider(Project project) {
    this.project = project;
  }

  @Nullable
  protected JsonFile findPackageJsonFile() {
    VirtualFile packageVFile = project.getBaseDir().findChild("package.json");
    if (packageVFile == null)
      return null;
    return ObjectUtils.tryCast(PsiManager.getInstance(project).findFile(packageVFile), JsonFile.class);
  }

  @Nullable
  protected JSFile findMainJsFile(@NotNull JsonFile packageJsonFile) {
    final JsonStringLiteralImpl value = ObjectUtils.tryCast(JsonPsiUtil.findPropertyValue(
        packageJsonFile.getTopLevelValue(), "main"), JsonStringLiteralImpl.class);
    if (value == null)
      return null;

    VirtualFile mainJsVFile = packageJsonFile.getVirtualFile().getParent().findFileByRelativePath(value.getValue());
    if (mainJsVFile == null)
      return null;

    return ObjectUtils.tryCast(PsiManager.getInstance(project).findFile(mainJsVFile), JSFile.class);
  }

  @NotNull
  protected JsonFile[] findConfigFiles() {
    VirtualFile configDir = project.getBaseDir().findChild("config");
    if(configDir == null)
      return new JsonFile[0];
    List<JsonFile> files = new LinkedList<JsonFile>();
    PsiManager psiManager = PsiManager.getInstance(project);
    for(VirtualFile file : configDir.getChildren()) {
      JsonFile jsonFile = ObjectUtils.tryCast(psiManager.findFile(file), JsonFile.class);
      if(jsonFile != null)
        files.add(jsonFile);
    }
    return files.toArray(new JsonFile[files.size()]);
  }

  @Nullable
  protected JsonFile findConfigFile(@NotNull JSFile mainJsFile) {
    // find importing catberry in main js
    final Ref<JSVariable> catberryVariableRef = new Ref<JSVariable>(null);
    mainJsFile.acceptChildren(new JSRecursiveElementVisitor() {
      @Override
      public void visitJSCallExpression(JSCallExpression node) {
        super.visitJSCallExpression(node);
        // check parent is variable
        JSVariable variable = ObjectUtils.tryCast(node.getParent(), JSVariable.class);
        if (variable == null)
          return;
        JSReferenceExpression refExpr = ObjectUtils.tryCast(node.getMethodExpression(), JSReferenceExpression.class);
        if (refExpr != null && "require".equals(refExpr.getReferenceName()) && refExpr.getQualifier() == null) {
          JSExpression[] args = node.getArguments();
          if (args.length != 1)
            return;
          if (!(args[0] instanceof JSLiteralExpression))
            return;
          final JSLiteralExpression value = (JSLiteralExpression) args[0];
          if (!"catberry".equals(value.getValue()))
            return;
          catberryVariableRef.set(variable);
        }
      }
    });

    final JSVariable catberryVariable = catberryVariableRef.get();
    if (catberryVariable == null)
      return null;

    // find initialization catberry in main js

    final Ref<String> configVariableNameRef = new Ref<String>(null);
    mainJsFile.acceptChildren(new JSRecursiveElementVisitor() {
      @Override
      public void visitJSCallExpression(JSCallExpression node) {
        super.visitJSCallExpression(node);
        // check parent is variable
        JSVariable variable = ObjectUtils.tryCast(node.getParent(), JSVariable.class);
        if (variable == null)
          return;
        JSReferenceExpression refExpr = ObjectUtils.tryCast(node.getMethodExpression(), JSReferenceExpression.class);
        String exprName = catberryVariable.getName() + ".create";
        if (refExpr == null || !exprName.equals(refExpr.getCanonicalText()))
          return;
        JSExpression[] args = node.getArguments();
        if (args.length != 1)
          return;
        JSReferenceExpression argExpression = ObjectUtils.tryCast(args[0], JSReferenceExpression.class);
        if (argExpression == null)
          return;
        configVariableNameRef.set(argExpression.getCanonicalText());
      }
    });

    final String configVariableName = configVariableNameRef.get();
    if (configVariableName == null)
      return null;

    // find config loading
    final Ref<String> configVariablePathRef = new Ref<String>(null);
    mainJsFile.acceptChildren(new JSRecursiveElementVisitor() {
      @Override
      public void visitJSCallExpression(JSCallExpression node) {
        super.visitJSCallExpression(node);
        // check parent is variable
        JSVariable variable = ObjectUtils.tryCast(node.getParent(), JSVariable.class);
        if (variable == null || !configVariableName.equals(variable.getName()))
          return;
        JSReferenceExpression refExpr = ObjectUtils.tryCast(node.getMethodExpression(), JSReferenceExpression.class);
        if (refExpr == null || !"require".equals(refExpr.getReferenceName()) || refExpr.getQualifier() != null)
          return;

        JSExpression[] args = node.getArguments();
        if (args.length != 1)
          return;
        if (!(args[0] instanceof JSLiteralExpression))
          return;
        final JSLiteralExpression value = ObjectUtils.tryCast(args[0], JSLiteralExpression.class);
        if (value != null && value.getValue() != null)
          configVariablePathRef.set(value.getValue().toString());
      }
    });

    final String configVariablePath = configVariablePathRef.get();
    if (configVariablePath == null)
      return null;

    final VirtualFile configVFile = mainJsFile.getVirtualFile().getParent().findFileByRelativePath(configVariablePath);
    if(configVFile == null)
      return null;

    return ObjectUtils.tryCast(PsiManager.getInstance(project).findFile(configVFile), JsonFile.class);
  }

}
