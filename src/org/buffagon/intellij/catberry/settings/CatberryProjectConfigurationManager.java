package org.buffagon.intellij.catberry.settings;

import com.intellij.json.psi.JsonElementGenerator;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.impl.JSChangeUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.buffagon.intellij.catberry.TemplateEngine;
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

  public boolean isCatberryEnabled() {
    //// TODO: 21/03/16 calc value from config files
    return true;
  }

  public String getComponentsRoot() {
    //// TODO: 21/03/16 calc value from config files
    return "catberry_components";
  }

  public TemplateEngine getTemplateEngine() {
    //// TODO: 21/03/16 calc value from config files
    return TemplateEngine.HANDLEBARS;
  }

  public String getStoresRoot() {
    //// TODO: 21/03/16 calc value from config files
    return "catberry_stores";
  }


  private void updateTemplateEngine(final TemplateEngine templateEngine) {
    VirtualFile file = project.getBaseDir().findChild("package.json");
    final Set<String> engineTagNames = new HashSet<String>();
    for (TemplateEngine engine : TemplateEngine.values()) {
      engineTagNames.add("catberry-" + engine);
    }


    if (file == null) {
      LOG.debug("package.json not found");
      return;
    }
    JsonFile jsonFile = (JsonFile) PsiManager.getInstance(project).findFile(file);
    if (jsonFile != null) {
      changeTemplateEngineInJsonConfig(templateEngine, engineTagNames, jsonFile);
    }

    file = project.getBaseDir().findChild("server.js");
    if(file == null) {
      LOG.debug("server.js not found");
      return;
    }

    JSFile jsFile = (JSFile) PsiManager.getInstance(project).findFile(file);
    if(jsFile != null)
      changeTemplateEngineInJsConfig(templateEngine, engineTagNames, jsFile);


    file = project.getBaseDir().findChild("browser.js");
    if(file == null) {
      LOG.debug("browser.js not found");
      return;
    }

    jsFile = (JSFile) PsiManager.getInstance(project).findFile(file);
    if(jsFile != null)
      changeTemplateEngineInJsConfig(templateEngine, engineTagNames, jsFile);
  }

  private void changeTemplateEngineInJsonConfig(final TemplateEngine templateEngine, Set<String> engineTagNames, JsonFile jsonFile) {
    final JsonElementGenerator jsonElementGenerator = new JsonElementGenerator(project);
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
          CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
            @Override
            public void run() {
              dependency.setName("catberry-" + templateEngine);
              PsiElement newValue = jsonElementGenerator.createStringLiteral(templateEngine.getVersion());
              if (dependency.getValue() != null)
                dependency.getValue().replace(newValue);
              else
                dependency.add(newValue);
            }
          });

        }
        break;
      }
    }
  }

  private void changeTemplateEngineInJsConfig(final TemplateEngine templateEngine, final Set<String> engineTagNames, JSFile jsFile) {
    // NOTE: iteration on JsVariables may be better
    jsFile.acceptChildren(new JSRecursiveElementVisitor() {
      @Override
      public void visitJSCallExpression(JSCallExpression node) {
        super.visitJSCallExpression(node);
        JSReferenceExpression refExpr = ObjectUtils.tryCast(node.getMethodExpression(), JSReferenceExpression.class);
        if(refExpr != null && "require".equals(refExpr.getReferenceName()) && refExpr.getQualifier() == null) {
          JSExpression[] args = node.getArguments();

          if(args.length != 1)
            return;
          if(!(args[0] instanceof JSLiteralExpression))
            return;
          final JSLiteralExpression value = (JSLiteralExpression) args[0];
          //noinspection SuspiciousMethodCalls
          if(!engineTagNames.contains(value.getValue()))
            return;
          CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
            @Override
            public void run() {
              value.replace(JSChangeUtil.createExpressionFromText(project, "'catberry-" + templateEngine + "'").getPsi());
            }
          });
        }
      }
    });
  }

  public static CatberryProjectConfigurationManager getInstance(Project project) {
    return project.getComponent(CatberryProjectConfigurationManager.class);
  }


}
