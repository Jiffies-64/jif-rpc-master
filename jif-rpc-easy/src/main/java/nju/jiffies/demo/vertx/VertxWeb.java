package nju.jiffies.demo.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxWeb {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(request -> {
            log.info("{}, {}", request.uri(), request.method());
            request.response().putHeader("context-type", "application/text").end("hello");
        });
        httpServer.listen(8088);
    }
}
