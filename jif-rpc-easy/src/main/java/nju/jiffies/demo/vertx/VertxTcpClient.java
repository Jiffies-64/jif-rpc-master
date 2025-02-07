package nju.jiffies.demo.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.impl.SocketAddressImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpClient {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        netClient.connect(8090, "127.0.0.1", conn -> {
            if (conn.succeeded()) {
                NetSocket socket = conn.result();
                socket.write("Hello, Server");
                socket.handler(buffer -> {
                    log.warn("Client Receive: {}", buffer.toString());
                });
            } else {
                log.warn("Cannot connect to server");
            }
        });
    }
}
