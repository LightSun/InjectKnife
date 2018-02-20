package com.heaven7.java.injectknife.plugin;

/**
 * @author heaven7
 */
public class Util {

  /*  public static boolean isInjectKnifeMethod(PsiMethod method){
        return !method.isConstructor() && !method.getModifierList()
                .hasModifierProperty(PsiModifier.STATIC);
    }*/

    public static void logError(Object... objs) {
        log(true, objs);
    }
    public static void log(Object... objs) {
        log(false, objs);
    }
    private static void log(boolean error, Object... objs) {
        StringBuilder sb = new StringBuilder();
        if (objs != null) {
            for (Object obj : objs) {
                sb.append(obj != null ? obj.toString() : null);
                sb.append("\r\n");
            }
        }
        if (error) {
            System.err.print(sb.toString());
        } else {
            System.out.print(sb.toString());
        }
    }

    /**
     * get the target class name which will be generated.(exclude package name)
     */
    public static String getTargetClassName(String packageName, String fullName){
        int packageLen = packageName.length() + 1;
        return fullName.substring(packageLen).replace('.', '$');
    }

}
