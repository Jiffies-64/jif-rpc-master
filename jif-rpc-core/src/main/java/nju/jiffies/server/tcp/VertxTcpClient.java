package nju.jiffies.server.tcp;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import nju.jiffies.RpcApplication;
import nju.jiffies.model.RpcRequest;
import nju.jiffies.model.RpcResponse;
import nju.jiffies.model.ServiceMetaInfo;
import nju.jiffies.protocol.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Vertx TCP 请求客户端
 */
public class VertxTcpClient {
    private static final int MAX_POOL_SIZE = 10;
    private static final Map<String, BlockingQueue<NetSocket>> connectionPool = new HashMap<>();
    private static final NetClient netClient;

    static {
        Vertx vertx = Vertx.vertx();
        netClient = vertx.createNetClient();
    }

    /**
     * 发送请求
     *
     * @param rpcRequest
     * @param serviceMetaInfo
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @SuppressWarnings("unchecked")
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        String key = getConnectionKey(serviceMetaInfo);
        NetSocket socket = getConnection(key, serviceMetaInfo);

        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

        // 构造消息
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        String serializer = RpcApplication.getRpcConfig().getSerializer();
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByValue(serializer);
        if (serializerEnum != null) {
            header.setSerializer((byte) serializerEnum.getKey());
        } else {
            protocolMessage.setSerializerKey(serializer);
            header.setSerializer((byte) serializer.length());
        }
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        // 生成全局请求 ID
        header.setRequestId(IdUtil.getSnowflakeNextId());
        protocolMessage.setHeader(header);
        protocolMessage.setBody(rpcRequest);

        // 编码请求
        try {
            Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
            socket.write(encodeBuffer);
        } catch (IOException e) {
            throw new RuntimeException("协议消息编码错误");
        }

        // 接收响应
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                buffer -> {
                    try {
                        ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        responseFuture.complete(rpcResponseProtocolMessage.getBody());
                        releaseConnection(key, socket);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息解码错误");
                    }
                }
        );
        socket.handler(bufferHandlerWrapper);

        return responseFuture.get();
    }

    private static String getConnectionKey(ServiceMetaInfo serviceMetaInfo) {
        return serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
    }

    private static NetSocket getConnection(String key, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        BlockingQueue<NetSocket> queue = connectionPool.computeIfAbsent(key, k -> new LinkedBlockingQueue<>(MAX_POOL_SIZE));
        NetSocket socket = queue.poll();
        if (socket == null) {
            CompletableFuture<NetSocket> future = new CompletableFuture<>();
            netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                    result -> {
                        if (!result.succeeded()) {
                            System.err.println("Failed to connect to TCP server");
                            future.completeExceptionally(result.cause());
                        } else {
                            future.complete(result.result());
                        }
                    });
            socket = future.get();
        }
        return socket;
    }

    private static void releaseConnection(String key, NetSocket socket) {
        BlockingQueue<NetSocket> queue = connectionPool.get(key);
        if (queue != null && queue.size() < MAX_POOL_SIZE) {
            queue.offer(socket);
        } else {
            socket.close();
        }
    }
}