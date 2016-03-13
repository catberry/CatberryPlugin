package org.buffagon.intellij.catberry;

import com.intellij.json.psi.JsonElementGenerator;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.lang.javascript.psi.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Prokofiev Alex
 */
public class CatberryProjectConfigurationManager implements ProjectComponent {
  public static final Logger LOG = Logger.getInstance(CatberryProjectConfigurationManager.class.getName());
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

  public void updateConfiguration() {
    final CatberryProjectSettingsProvider settingsProvider = CatberryProjectSettingsProvider.getInstance(project);
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        updateTemplateEngine(settingsProvider.getTemplateEngine());
      }
    });
  }

  private void updateTemplateEngine(final TemplateEngine templateEngine) {
    VirtualFile file = project.getBaseDir().findChild("package.json");
    String oldTemplateLibrary = null;
    final JsonElementGenerator jsonElementGenerator = new JsonElementGenerator(project);
    if (file == null) {
      LOG.debug("package.json not found");
      return;
    }
    JsonFile jsonFile = (JsonFile) PsiManager.getInstance(project).findFile(file);
    if (jsonFile != null) {
      Set<String> engineTagNames = new HashSet<String>();
      for (TemplateEngine engine : TemplateEngine.values()) {
        engineTagNames.add("catberry-" + engine);
      }
      List<JsonProperty> properties =
          PsiTreeUtil.getChildrenOfTypeAsList(jsonFile.getTopLevelValue(), JsonProperty.class);
      for (JsonProperty property : properties) {
        if (!property.getName().equals("dependencies"))
          continue;
        final JsonObject propertyObject = (JsonObject) property.getValue();
        if (propertyObject == null)
          continue;
        final List<JsonProperty> dependencies = PsiTreeUtil.getChildrenOfTypeAsList(propertyObject, JsonProperty.class);
        for (final JsonProperty dependency : dependencies) {
          if (!engineTagNames.contains(dependency.getName()))
            continue;
          if (!dependency.getName().equals("catberry-" + templateEngine)) {
            oldTemplateLibrary = dependency.getName();
            CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
              @Override
              public void run() {
                dependency.setName("catberry-" + templateEngine);
                if (dependency.getValue() != null)
                  dependency.getValue().delete();
                dependency.add(jsonElementGenerator.createStringLiteral(templateEngine.getVersion()));
              }
            });

          }
          break;
        }
      }
    }
    if (oldTemplateLibrary == null)
      return;

    file = project.getBaseDir().findChild("server.js");
    if(file == null) {
      LOG.debug("server.js not found");
      return;
    }

    JSFile jsFile = (JSFile) PsiManager.getInstance(project).findFile(file);
    if(jsFile != null) {
      final List<JSVarStatement> varStatements = PsiTreeUtil.getChildrenOfTypeAsList(jsFile, JSVarStatement.class);
      for(JSVarStatement varStatement : varStatements) {
        final List<JSVariable> variables = PsiTreeUtil.getChildrenOfTypeAsList(varStatement, JSVariable.class);
        for(JSVariable variable : variables) {
          JSExpression expression = variable.getInitializer();
          if(!(expression instanceof JSCallExpression))
            continue;
          JSCallExpression call = (JSCallExpression) expression;
          JSReferenceExpression refExpr = ObjectUtils.tryCast(call.getMethodExpression(), JSReferenceExpression.class);
          if(refExpr != null && "require".equals(refExpr.getReferenceName()) && refExpr.getQualifier() == null) {
            call.getChildren();
            JSExpression[] args = call.getArguments();
            if(args.length != 1)
              continue;
            if(!(args[0] instanceof JSLiteralExpression))
              continue;
            JSLiteralExpression value = (JSLiteralExpression) args[0];
            if(!oldTemplateLibrary.equals(value.getValue()))
              continue;
            // TODO: 13/03/16 not finished
//            JSGeneratorExpression gen = JSUtils.
          }
        }
      }
//      jsFile.accept(new JSElementVisitor() {
//        @Override
//        public void visitJSCallExpression(JSCallExpression node) {
//          super.visitJSCallExpression(node);
//          JSReferenceExpression refExpr = ObjectUtils.tryCast(node.getMethodExpression(), JSReferenceExpression.class);
//          if(refExpr != null && "require".equals(refExpr.getReferenceName()) && refExpr.getQualifier() == null) {
//            node.getArguments()[0].getChildren();
//          }
//        }
//      });
    }

  }

  public static CatberryProjectConfigurationManager getInstance(Project project) {
    return project.getComponent(CatberryProjectConfigurationManager.class);
  }
}
