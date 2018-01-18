package com.heaven7.java.injectknife.plugin;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author heaven7
 */
public class GenerateDialog extends DialogWrapper {

    private final CollectionListModel<PsiMethod> methodsCollection;
    private final LabeledComponent<JPanel> methodsComponent;
    private JBList<PsiMethod> methodList;

    protected GenerateDialog(final PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle("Select Methods for Inject-knife Generation");

        methodsCollection = new CollectionListModel<PsiMethod>();
        methodList = new JBList<PsiMethod>(methodsCollection);
        methodList.setCellRenderer(new DefaultPsiElementCellRenderer());
        final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(methodList).disableAddAction();
        final JPanel panel = decorator.createPanel();

        methodsComponent = LabeledComponent.create(panel, "Methods to include in Inject-Knife");

        //update methods show
        setUpMethods(psiClass);
        init();
    }

    private void setUpMethods(PsiClass psiClass) {
        List<PsiMethod> methods = new ArrayList<>();
        for(PsiMethod m : psiClass.getMethods()){
            if(!m.isConstructor()){
                methods.add(m);
            }
        }
        methodsCollection.add(methods);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return methodsComponent;
    }

    @Nullable
    @Override
    protected JComponent createSouthPanel() {
        return super.createSouthPanel();
    }

    public List<PsiMethod> getSelectedMethods() {
        return methodList.getSelectedValuesList();
    }
}