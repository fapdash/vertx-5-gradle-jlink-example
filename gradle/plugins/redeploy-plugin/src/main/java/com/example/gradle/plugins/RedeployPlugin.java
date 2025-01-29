package com.example.gradle.plugins;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.JavaApplication;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskProvider;

@NonNullApi
public class RedeployPlugin implements Plugin<Project> {
	
    @Override
    public void apply(Project project) {
		project.getPluginManager().apply(ApplicationPlugin.class);

		JavaApplication javaApplication = project.getExtensions().getByType(JavaApplication.class);
		JavaExec runTask = (JavaExec) project.getTasks().getByName("run");
		Task classesTask = project.getTasks().getByName("assemble");

		// we copy the config from the `run` task
		TaskProvider<RedeployTask> taskProvider = project.getTasks().register("vertxRun", RedeployTask.class,
				redeployRun -> {
					// need to depend on 'assemble' task, as Gradle runs modularized apps via the
					// jar, for a classpath based run we could depend on 'classes' and have a
					// quicker restart
					// TODO(FAP): use implicit dependency via defined input instead of dependsOn
					redeployRun.dependsOn(classesTask.getPath());
					redeployRun.setClasspath(runTask.getClasspath());
					redeployRun.setMainClass(javaApplication.getMainClass().get());
					redeployRun.setArguments(runTask.getArgs());
					redeployRun.setMainModule(javaApplication.getMainModule().get());
					// TODO(FAP): add jvmargs etc.
				});
    }

}
