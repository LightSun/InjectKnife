package com.heaven7.java.injectknife.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author heaven7
 */
public class InjectAction extends AnAction {

    private static final String INJECT_PROVIDER = "com.heaven7.java.injectknife.InjectProvider";

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = getEventProject(e);
        if(project == null){
            return;
        }
        PsiClass psiClass = getPsiClassFromContext(e);
        if(psiClass == null){
            Util.logError("psiClass == null");
            return;
        }

        GenerateDialog dlg = new GenerateDialog(psiClass);
        dlg.show();

        if (dlg.isOK()) {
            new InjectServiceGenerator(psiClass).generate(dlg.getSelectedMethods());
        }
    }

    @Override
    public void update(AnActionEvent e) {
        PsiClass psiClass = getPsiClassFromContext(e);
        if(psiClass == null){
            return;
        }
        PsiClass psiClass_pre = JavaPsiFacade.getInstance(psiClass.getProject()).findClass(INJECT_PROVIDER,
                GlobalSearchScope.projectScope(psiClass.getProject()));
        if(psiClass_pre != null && psiClass.isInheritor(psiClass_pre, true)){
            e.getPresentation().setEnabled(true);
        }else{
            e.getPresentation().setEnabled(false);
        }
    }

    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        // psiFile.getViewProvider().getVirtualFile()

        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);

        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }
}
