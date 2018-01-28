package com.heaven7.android.injectknife.gradle

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.gradle.api.Project

/**
 * @auchor heaven7
 */
class PreDexTransform extends Transform{

    final Project project

    PreDexTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "preDex"
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
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {

        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput->
                //transform class impl com.heaven7.java.injectknife.InjectProvider
                // get output
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                InjectImpl.injectDir(directoryInput.file.absolutePath)
            }
            input.jarInputs.each {JarInput jarInput->

            }
        }
    }
}
