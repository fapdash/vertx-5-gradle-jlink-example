open module com.example.module_example {
	requires io.vertx.core;
	requires io.vertx.web;
	// Why do we need to require this, should be transitively exported by io,vertx.web?
	requires io.vertx.auth.common;

//	opens com.example.module_example to io.vertx.core;
}
