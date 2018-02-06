package com.heaven7.android.injectknife.gradle

import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.io.FileUtils

/**
 * inject impl
 * @auchor heaven7
 */
class InjectImpl {

    private static final ClassPool POOL = ClassPool.getDefault()

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
    public static void injectDir(String path) {
        POOL.appendClassPath(path)
        File dir = new File(path)
        if(dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath
                filePath.indexOf(path)
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")
                        ) {
                    String filePath_relative = filePath.substring(filePath.indexOf(path) + path.length() + 1)
                    int end = filePath_relative.length() - 6 // .class = 6
                    String className = filePath_relative.substring(0, end)
                            .replace('\\', '.')
                            .replace('/','.')
                    injectClass(className, path)
                }
            }
        }
    }

    /**
     * 这里需要将jar包先解压，注入代码后再重新生成jar包
     * @path jar包的绝对路径
     */
    public static void injectJar(String path) {
        if (path.endsWith(".jar")) {
            File jarFile = new File(path)


            // jar包解压后的保存路径
            String jarZipDir = jarFile.getParent() +"/"+jarFile.getName().replace('.jar','')

            // 解压jar包, 返回jar包中所有class的完整类名的集合（带.class后缀）
            List classNameList = JarZipUtil.unzipJar(path, jarZipDir)

            // 删除原来的jar包
            jarFile.delete()

            // 注入代码
            POOL.appendClassPath(jarZipDir)
            for(String className : classNameList) {
                if (className.endsWith(".class")
                        && !className.contains('R$')
                        && !className.contains('R.class')
                        && !className.contains("BuildConfig.class")) {
                    className = className.substring(0, className.length()-6)
                    injectClass(className, jarZipDir)
                }
            }

            // 从新打包jar
            JarZipUtil.zipJar(jarZipDir, path)

            // 删除目录
            FileUtils.deleteDirectory(new File(jarZipDir))
        }
    }

    private static void injectClass(String className, String outPath) {
        CtClass c = POOL.getCtClass(className)
        for(CtClass cls : c.getInterfaces()){
            if(!cls.getName().equals("com.heaven7.java.injectknife.InjectProvider")){
                 return;
            }
        }
        if (c.isFrozen()) {
            c.defrost()
        }
        def constructor = c.getConstructors()[0];
        constructor.insertAfter("System.out.println(com.aitsuki.hack.AntilazyLoad.class);")
        c.writeFile(outPath)
    }

}
