package com.heaven7.android.injectknife.gradle

import javassist.*
import javassist.bytecode.CodeAttribute
import javassist.bytecode.LocalVariableAttribute
import javassist.bytecode.MethodInfo

import java.lang.reflect.Method

public class MethodUtil {

    public static ParamInfo[] getAllParamaters(CtMethod cm)
            throws NotFoundException {
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                .getAttribute(LocalVariableAttribute.tag);
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1

        def types = cm.getParameterTypes();
        final int size = types.length
        ParamInfo[] paramNames = new ParamInfo[size]
        for (int i = 0; i < size; i++) {
            def info = new ParamInfo()
            info.name = attr.variableName(i + pos)
            info.type = types[i].getName()
            paramNames[i] = info
        }
        return paramNames
    }

    public static String[] getAllParamaterNames(Method method)
            throws NotFoundException {
        Class<?> clazz = method.getDeclaringClass();
        ClassPool pool = ClassPool.getDefault();
        CtClass clz = pool.get(clazz.getName());
        CtClass[] params = new CtClass[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            params[i] = pool.getCtClass(method.getParameterTypes()[i].getName());
        }
        CtMethod cm = clz.getDeclaredMethod(method.getName(), params);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
                .getAttribute(LocalVariableAttribute.tag);
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        String[] paramNames = new String[cm.getParameterTypes().length];
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }

}
