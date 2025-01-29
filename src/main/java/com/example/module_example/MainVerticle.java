package com.example.module_example;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.Router;

public class MainVerticle extends VerticleBase {

	public static void main(String[] args) {
		VertxOptions vertxOptions = new VertxOptions();
		int cores = Runtime.getRuntime().availableProcessors();
		Vertx vertx = Vertx.vertx(vertxOptions);
		DeploymentOptions options = new DeploymentOptions();
		options.setInstances(cores);
		vertx.exceptionHandler(t -> {
			t.printStackTrace();
		});
		vertx.deployVerticle(MainVerticle.class, options)
			.onFailure(Throwable::printStackTrace);
		System.out.println("HTTP server started on port 8888");
	}

	@Override
	public Future<?> start() {
		Router router = Router.router(vertx);

		router.get("/").handler(ctx -> {
			ctx.request().response().putHeader("content-type", "text/plain").end("Hello from Vert.x!");
		});

		return vertx.createHttpServer().requestHandler(router).listen(8888).onSuccess(http -> {
		});
	}
}
