package com.heaven7.java.injectknife.plugin;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;

import java.util.List;

/**
 * @author heaven7
 */
public class InjectServiceGenerator {

    private static final String SUFFIX = "_$InjectService$";
    private static final String QN_KEEP_FIELDS    = "com.heaven7.java.injectknife.internal.KeepFields";
    private static final String QN_INJECT_SERVICE = "com.heaven7.java.injectknife.internal.InjectService";
    private static final String QN_PROVIDE_METHOD = "com.heaven7.java.injectknife.internal.ProvideMethod";

    private final PsiClass mPsiClass;

    public InjectServiceGenerator(PsiClass mPsiClass) {
        this.mPsiClass = mPsiClass;
    }

    public void generate(final List<PsiMethod> methods) {
        new WriteCommandAction.Simple(mPsiClass.getProject(), mPsiClass.getContainingFile()) {
            @Override
            protected void run() throws Throwable {
                generateImpl(methods);
            }
        }.execute();
    }

    private void generateImpl(List<PsiMethod> methods) {
        final Project project = mPsiClass.getProject();
        final JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mPsiClass.getProject());
        PsiDirectory dir = mPsiClass.getContainingFile().getContainingDirectory();
        PsiJavaFile file = (PsiJavaFile) mPsiClass.getContainingFile();
        // . -> $
        String targetClassName = Util.getTargetClassName(file.getPackageName(), mPsiClass.getQualifiedName());
        targetClassName += SUFFIX;
        //fullname of target
        final String fullName = file.getPackageName() + "." + targetClassName;

        //delete if exist
        PsiClass psiClass_pre = JavaPsiFacade.getInstance(project).findClass(fullName,
                GlobalSearchScope.projectScope(project));
        if(psiClass_pre != null){
            psiClass_pre.delete();
        }
       //generate class
        PsiClass targetPsiClass = createJavaFile(targetClassName, "@" + QN_KEEP_FIELDS + "\n public final class "
                + targetClassName + "{\n  "+ generateFields(methods) +"  \n}");
        styleManager.shortenClassReferences(targetPsiClass);
        dir.add(targetPsiClass);

        //add annotation to current class
        PsiModifierList list = mPsiClass.getModifierList();
        PsiAnnotation pa = list.findAnnotation(QN_INJECT_SERVICE);
        if(pa != null){
            pa.delete();
        }
        list.addAnnotation(QN_INJECT_SERVICE + "(" + fullName + ".class)");

        for(PsiMethod m : mPsiClass.getMethods()){
            if(m.isConstructor()){
                continue;
            }
            PsiModifierList modifierList = m.getModifierList();
            pa = modifierList.findAnnotation(QN_PROVIDE_METHOD);
            if(methods.contains(m)){
                //add annotation if need (select method)
                if(pa == null){
                    modifierList.addAnnotation(QN_PROVIDE_METHOD);
                }
            }else{
                //delete annotation if need (unselect method)
                if(pa != null){
                    pa.delete();
                }
            }
        }
        styleManager.shortenClassReferences(mPsiClass);
    }

    private String generateFields(List<PsiMethod> methods) {
        int base = 1;
        StringBuilder sb = new StringBuilder();
        for(PsiMethod method : methods){
            sb.append("public static final int FLAG_").append(method.getName())
                    .append(" = ").append(base).append(" ;\n");
            base *= 2;
        }
        return sb.toString();
    }

    private PsiClass createJavaFile(String javaFileName, @NonNls String text) {
        PsiJavaFile psiJavaFile = (PsiJavaFile) PsiFileFactory.getInstance(mPsiClass.getProject()).createFileFromText(
                javaFileName + "." + JavaFileType.INSTANCE.getDefaultExtension(),
                JavaFileType.INSTANCE, text);
        PsiClass[] classes = psiJavaFile.getClasses();
        if (classes.length != 1) {
            throw new IncorrectOperationException("Incorrect class '" + text + "'");
        } else {
            PsiClass pc = classes[0];
            if (pc == null) {
                throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null",
                        "com/intellij/psi/impl/PsiJavaParserFacadeImpl", "createJavaFile"));
            } else {
                return pc;
            }
        }
    }
}
