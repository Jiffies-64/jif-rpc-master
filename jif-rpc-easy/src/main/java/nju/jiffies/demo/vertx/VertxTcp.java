package nju.jiffies.demo.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class VertxTcp {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer();
        netServer.connectHandler(netSocket -> {
           netSocket.handler(buffer -> {
               log.warn("接收到的内容为: {}", buffer.toString());
               netSocket.write("Server Received!");
           });
        });
        netServer.listen(8090, result -> {
            if (result.succeeded()) {
                log.info("Server start successfully");
            } else {
                log.info("Something Error when starting server");
            }
        });
    }
}
