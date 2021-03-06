package com.heaven7.android.injectknife.gradle

import javassist.ClassClassPath
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
/**
 * inject impl
 * @auchor heaven7
 */
class InjectImpl {

    private static final ClassPool POOL = ClassPool.getDefault()

    static {
        POOL.appendClassPath(new ClassClassPath(Class.forName(
                "com.heaven7.java.injectknife.InjectObserver")))
        POOL.appendClassPath(new ClassClassPath(Class.forName(
                "com.heaven7.java.injectknife.InjectProvider")))
        POOL.appendClassPath(new ClassClassPath(Class.forName(
                "com.heaven7.java.injectknife.internal.ProvideMethod")))
       /* POOL.appendClassPath(new ClassClassPath(Class.forName(
                "com.heaven7.java.injectknife.InjectKnife\$MethodInjector")))
        POOL.appendClassPath(new ClassClassPath(Class.forName(
                "com.heaven7.java.injectknife.InjectParameterSupplier")))*/
    }
    /**
     * add to class path
     * @param libPath
     */
    public static void appendClassPath(String libPath) {
        POOL.appendClassPath(libPath)
    }

    /**
     * 遍历该目录下的所有class，对所有class进行代码注入。
     * 其中以下class是不需要注入代码的：
     * --- 1. R文件相关
     * --- 2. 配置文件相关（BuildConfig）
     * --- 3. Application
     * @param path 目录的路径
     */
    public static void injectDir(Project project,String path) {
        POOL.appendClassPath(path)
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath
                //filePath.indexOf(path)
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")
                ) {
                    String filePath_relative = filePath.substring(filePath.indexOf(path) + path.length() + 1)
                    int end = filePath_relative.length() - 6 // .class = 6
                    String className = filePath_relative.substring(0, end)
                            .replace('\\', '.')
                            .replace('/', '.')
                    injectClass(project, className, path)
                }
            }
        }
    }

    /**
     * 这里需要将jar包先解压，注入代码后再重新生成jar包
     * @path jar包的绝对路径
     */
    public static void injectJar(Project project,String path) {
        if (path.endsWith(".jar")) {
            File jarFile = new File(path)

            // jar包解压后的保存路径
            String jarZipDir = jarFile.getParent() + "/" + jarFile.getName().replace('.jar', '')

            // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
            List classNameList = JarZipUtil.unzipJar(path, jarZipDir)

            // 删除原来的jar包
            jarFile.delete()

            // 注入代码
            POOL.appendClassPath(jarZipDir)
            for (String className : classNameList) {
                if (className.endsWith(".class")
                        && !className.contains('R$')
                        && !className.contains('R.class')
                        && !className.contains("BuildConfig.class")) {
                    className = className.substring(0, className.length() - 6)
                    injectClass(project, className, jarZipDir)
                }
            }

            // 从新打包jar
            JarZipUtil.zipJar(jarZipDir, path)

            // 删除目录
            FileUtils.deleteDirectory(new File(jarZipDir))
        }
    }

    private static void injectClass(Project project, String className, String outPath) {
        CtClass c = POOL.getCtClass(className)
        boolean found = false
        for (CtClass cls : c.getInterfaces()) {
            if (cls.getName().equals("com.heaven7.java.injectknife.InjectProvider")) {
                found = true
                break
            }
        }
        if(!found){
            return
        }
        project.logger.error("found InjectProvider for class '${className}'")
        def method_anno = Class.forName("com.heaven7.java.injectknife.internal.ProvideMethod")
        if (c.isFrozen()) {
            c.defrost()
        }
        //get method anno and inject method.\
        c.getMethods().eachWithIndex { CtMethod ctMethod, int index ->
            if (ctMethod.getAnnotation(method_anno) != null) {
                ParamInfo[] infos = MethodUtil.getAllParamaters(ctMethod)
                if(infos != null && infos.size() > 0) {
                    def sign = ""
                    StringBuilder sb = new StringBuilder()
                    def size = infos.size()
                    for (int i = 0; i < size; i++) {
                        sb.append(infos[i].name)
                        sign += infos[i].type
                        if (i != size - 1) {
                            sb.append(",")
                        }
                    }
                    ctMethod.insertAfter("getInjector().inject(" + String.valueOf(sign.hashCode()) + "," + sb.toString() + ");")
                }else{
                    ctMethod.insertAfter("getInjector().inject(null, null);")
                }
            }
        }
        c.writeFile(outPath)
        //must detach or else next time will insert multi
        c.detach()
    }

}
