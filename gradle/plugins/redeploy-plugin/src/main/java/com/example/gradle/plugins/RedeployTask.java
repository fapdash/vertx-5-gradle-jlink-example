package com.example.gradle.plugins;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskAction;
import org.gradle.deployment.internal.Deployment;
import org.gradle.deployment.internal.DeploymentHandle;
import org.gradle.deployment.internal.DeploymentRegistry;
import org.gradle.internal.jvm.Jvm;
import org.gradle.process.internal.ExecHandle;
import org.gradle.process.internal.ExecHandleState;
import org.gradle.process.internal.JavaExecHandleBuilder;
import org.gradle.process.internal.JavaExecHandleFactory;
import org.gradle.work.DisableCachingByDefault;

@DisableCachingByDefault(because = "Produces no cacheable output")
class RedeployTask extends DefaultTask {

	List<String> args = new ArrayList<>();
	private static final DeploymentRegistry.ChangeBehavior changeBehavior = DeploymentRegistry.ChangeBehavior.RESTART;

	private FileCollection classpath;
	private String mainClass;
	private String mainModule;

	@Inject
	public RedeployTask() {
	}

    @TaskAction
    void start() {
		JavaExecHandleBuilder builder = buildBuilder();
//		if (getProject().getGradle().getStartParameter().isContinuous()) {
			DeploymentRegistry registry = getDeploymentRegistry();
			JavaApplicationHandle handle = registry.get(getPath(), JavaApplicationHandle.class);
			if (handle == null) {
				registry.start(getPath(), changeBehavior, JavaApplicationHandle.class, builder);
			}
//		} else {
			// TODO(FAP): would need to exec here, not start a handle
//			builder.build().start();
//		}
    }

	private JavaExecHandleBuilder buildBuilder() {
		JavaExecHandleBuilder builder = getExecActionFactory().newJavaExec();
		builder.setExecutable(Jvm.current().getJavaExecutable());
		builder.setClasspath(classpath);
		builder.getMainClass().set(mainClass);
		builder.getMainModule().set(mainModule);
		builder.setArgs(args);
		builder.setErrorOutput(System.err);
		builder.setStandardOutput(System.out);
		builder.setIgnoreExitValue(true);
		return builder;
	}

	@Inject
	protected DeploymentRegistry getDeploymentRegistry() {
		throw new UnsupportedOperationException();
	}

	@Inject
	protected JavaExecHandleFactory getExecActionFactory() {
		throw new UnsupportedOperationException();
	}

	public static class JavaApplicationHandle implements DeploymentHandle {
		private ExecHandle handle;
		private JavaExecHandleBuilder builder;

		@Inject
		public JavaApplicationHandle(JavaExecHandleBuilder builder) {
			this.builder = builder;
        }

        @Override
		public boolean isRunning() {
			return handle != null
					&& (handle.getState() == ExecHandleState.STARTED);
        }

        @Override
		public void start(Deployment deployment) {
			handle = builder.build().start();
        }

        @Override
		public void stop() {
			if (handle != null) {
				handle.abort();
			}
        }
    }

	public void setClasspath(FileCollection classpath) {
		this.classpath = classpath;
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public void setArguments(List<String> args) {
		this.args = args;
	}

	public void setMainModule(String mainModule) {
		this.mainModule = mainModule;

	}

}