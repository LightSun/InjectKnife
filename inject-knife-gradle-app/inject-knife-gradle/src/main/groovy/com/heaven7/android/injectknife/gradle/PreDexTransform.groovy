package com.heaven7.android.injectknife.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * @auchor heaven7
 */
class PreDexTransform extends Transform {

    final Project project

    PreDexTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "pre__Dex"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {

        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //transform class impl com.heaven7.java.injectknife.InjectProvider
                // get output
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                InjectImpl.injectDir(project, directoryInput.file.absolutePath)

                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            input.jarInputs.each { JarInput jarInput ->
                String jarPath = jarInput.file.absolutePath;
                String projectName = project.rootProject.name;
                if (jarPath.endsWith("classes.jar") && jarPath.contains("exploded-aar" + "\\" + projectName)) {
                    InjectImpl.injectJar(project,jarPath)
                }
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}
