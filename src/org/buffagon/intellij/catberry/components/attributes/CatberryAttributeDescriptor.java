package org.buffagon.intellij.catberry.components.attributes;

import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.lang.javascript.psi.ecmal4.JSClass;
import com.intellij.lang.javascript.psi.stubs.JSClassIndex;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.ArrayUtil;
import com.intellij.xml.impl.BasicXmlAttributeDescriptor;
import com.intellij.xml.impl.XmlAttributeDescriptorEx;
import org.buffagon.intellij.catberry.settings.CatberryProjectConfigurationManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dennis.Ushakov
 */
public class CatberryAttributeDescriptor extends BasicXmlAttributeDescriptor implements XmlAttributeDescriptorEx {
  protected final Project project;
  private final String attributeName;

  public CatberryAttributeDescriptor(final Project project, String attributeName) {
    this.project = project;
    this.attributeName = attributeName;
  }

  @Override
  public PsiElement getDeclaration() {
    return null;
  }

  @Override
  public PsiElement getValueDeclaration(XmlElement xmlElement, String value) {
    CatberryProjectConfigurationManager configurationManager = CatberryProjectConfigurationManager.getInstance(project);
    PsiDirectory directory = configurationManager.getStoresDirectory();
    if(directory == null)
      return super.getValueDeclaration(xmlElement, value);
    final String requiredPath = directory.getVirtualFile().getPath() + "/" + value+".js";

    int index = value.lastIndexOf('/');
    String className = index == -1 ? value : value.substring(index+1);
    Collection<JSElement> elements = StubIndex.getElements(JSClassIndex.KEY, className, project,
        GlobalSearchScope.allScope(project), JSElement.class);

    for(JSElement element : elements) {
      if (element instanceof JSClass &&  element.getContainingFile().getVirtualFile().getPath().equals(requiredPath))
        return element;
    }
    return super.getValueDeclaration(xmlElement, value);
  }

  @Override
  public String getName() {
    return attributeName;
  }

  @Override
  public void init(PsiElement element) {}

  @Override
  public Object[] getDependences() {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  @Override
  public boolean isRequired() {
    return false;
  }

  @Override
  public boolean hasIdType() {
    return false;
  }

  @Override
  public boolean hasIdRefType() {
    return false;
  }

  @Override
  public boolean isEnumerated() {
    return true;
  }

  @Override
  public boolean isFixed() {
    return false;
  }

  @Override
  public String getDefaultValue() {
    return null;
  }

  @Override
  public String[] getEnumeratedValues() {
    CatberryProjectConfigurationManager configurationManager = CatberryProjectConfigurationManager.getInstance(project);
    if(!configurationManager.isCatberryEnabled())
      return ArrayUtil.EMPTY_STRING_ARRAY;

    PsiDirectory storesDirectory = configurationManager.getStoresDirectory();
    if(storesDirectory == null)
      return ArrayUtil.EMPTY_STRING_ARRAY;

    final String storesPath = storesDirectory.getVirtualFile().getPath();

    GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
    Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, "js", scope);
    List<String> keys = new LinkedList<String>();
    PsiManager psiManager = PsiManager.getInstance(project);
    for(VirtualFile virtualFile : virtualFiles) {
      if(!virtualFile.getPath().startsWith(storesPath))
        continue;
      String prefix = virtualFile.getPath().substring(storesPath.length(), virtualFile.getPath().lastIndexOf("/"));
      if(prefix.startsWith("/"))
        prefix = prefix.substring(1);
      if(prefix.length() != 0)
        prefix += "/";
      PsiFile psiFile = psiManager.findFile(virtualFile);
      if(psiFile == null)
        continue;
      for(JSClass jsClass: PsiTreeUtil.findChildrenOfType(psiFile, JSClass.class)) {
        keys.add(prefix+jsClass.getName());
      }
    }
    return keys.toArray(new String[keys.size()]);
  }

  @Override
  protected PsiElement getEnumeratedValueDeclaration(XmlElement xmlElement, String value) {
    return xmlElement;
  }

  @Nullable
  @Override
  public String handleTargetRename(@NotNull @NonNls String newTargetName) {
    return newTargetName;
  }
}
