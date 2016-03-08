package org.buffagon.intellij.catberry;

import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

/**
 @author Prokofiev Alex
 */
public class CatberryComponentStubIndex extends StringStubIndexExtension<PsiFile> {
    public static final StubIndexKey<String, PsiFile> KEY =
            StubIndexKey.createIndexKey("org.buffagon.intellij.catberry.CatberryComponentStubIndex");

    @NotNull
    @Override
    public StubIndexKey<String, PsiFile> getKey() {
        return KEY;
    }
}
