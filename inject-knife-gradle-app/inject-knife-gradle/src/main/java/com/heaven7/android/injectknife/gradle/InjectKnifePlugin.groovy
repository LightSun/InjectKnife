package com.heaven7.android.injectknife.gradle

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project;

/**
 * the plugin of inject-knife
 * Created by heaven7 on 2018/1/28.
 */
public class InjectKnifePlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new PreDexTransform(project))
    }
}
