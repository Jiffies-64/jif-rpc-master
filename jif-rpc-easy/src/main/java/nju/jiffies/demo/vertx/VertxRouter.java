package nju.jiffies.demo.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class VertxRouter {
    public static void main(String[] args) {
        // 获取 Vertx 实例
        Vertx vertx = Vertx.vertx();
        // 获取 Http 服务器
        HttpServer httpServer = vertx.createHttpServer();
        // 获取路由实例
        Router router = Router.router(vertx);

        router.route(HttpMethod.GET, "/a").handler(ctx -> {
            log.warn("a");
            ctx.next();
        });

        router.route(HttpMethod.GET, "/a").handler(ctx -> {
            log.warn("a-next");
            ctx.response().putHeader("context-type", "application/text").end("a-next");
        });

        router.route(HttpMethod.GET, "/b/:param").handler(ctx -> {
            String param = ctx.pathParam("param");
            log.warn("b - {}", param);
            ctx.response().putHeader("context-type", "application/text").end("b - " + param);
        });

        httpServer.requestHandler(router);
        httpServer.listen(8089, result -> {
            if (result.succeeded()) {
                log.info("Server start successfully");
            } else {
                log.info("Something Error when starting server");
            }
        });
    }
}
