package org.buffagon.intellij.catberry.settings.providers;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.impl.JsonStringLiteralImpl;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.util.ObjectUtils;
import org.buffagon.intellij.catberry.JsonPsiUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author Prokofiev Alex
 */
@SuppressWarnings("Duplicates")
public class StoresRootProvider implements CachedValueProvider<String> {
  private final Project project;
  private final NotNullLazyValue<ModificationTracker> tracker;
  public StoresRootProvider(Project project, NotNullLazyValue<ModificationTracker> tracker) {
    this.project = project;
    this.tracker = tracker;
  }

  @Nullable
  @Override
  public Result<String> compute() {
    VirtualFile packageVFile = project.getBaseDir().findChild("package.json");
    if (packageVFile == null)
      return Result.create(null, tracker.getValue());

    final JsonFile packageJsonFile = (JsonFile) PsiManager.getInstance(project).findFile(packageVFile);
    if (packageJsonFile == null || packageJsonFile.getTopLevelValue()==null)
      return Result.create(null, tracker.getValue());

    // find main js file
    final JsonStringLiteralImpl value = ObjectUtils.tryCast(JsonPsiUtil.findPropertyValue(
        packageJsonFile.getTopLevelValue(), "main"), JsonStringLiteralImpl.class);
    if (value == null)
      return Result.create(null, tracker.getValue());

    VirtualFile mainJsVFile = packageVFile.getParent().findFileByRelativePath(value.getValue());
    if (mainJsVFile == null)
      return Result.create(null, tracker.getValue());

    final JSFile mainJsFile = ObjectUtils.tryCast(PsiManager.getInstance(project).findFile(mainJsVFile), JSFile.class);
    if (mainJsFile == null)
      return Result.create(null, tracker.getValue());

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
      return Result.create(null, tracker.getValue());

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
      return Result.create(null, tracker.getValue());

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
      return Result.create(null, tracker.getValue());

    //config file
    final VirtualFile configVFile = mainJsVFile.getParent().findFileByRelativePath(configVariablePath);
    if(configVFile == null)
      return Result.create(null, tracker.getValue());

    final JsonFile configFile = ObjectUtils.tryCast(PsiManager.getInstance(project).findFile(configVFile), JsonFile.class);
    if(configFile == null || configFile.getTopLevelValue()==null)
      return Result.create(null, tracker.getValue());

    //stores directory
    JsonStringLiteral jsonValue = ObjectUtils.tryCast(JsonPsiUtil.findPropertyValue(
        configFile.getTopLevelValue(), "storesDirectory"), JsonStringLiteral.class);
    if(jsonValue == null )
      return Result.create(null, configFile, mainJsFile, packageJsonFile);
    return Result.create(jsonValue.getValue(), configFile, mainJsFile, packageJsonFile);
  }
}
