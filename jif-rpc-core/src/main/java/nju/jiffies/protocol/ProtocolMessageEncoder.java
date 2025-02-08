package nju.jiffies.protocol;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.buffer.Buffer;
import nju.jiffies.serializer.Serializer;
import nju.jiffies.serializer.SerializerFactory;

import java.io.IOException;

import static cn.hutool.core.lang.Console.log;
import static nju.jiffies.protocol.ProtocolConstant.CUSTOM_PROTOCOL_OFFSET;

/**
 * 协议消息编码器
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     *
     * @param protocolMessage
     * @return
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        String serializerKey = protocolMessage.getSerializerKey();
        boolean useCustomSerializer = !StrUtil.isEmpty(serializerKey);
        // 依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        if (useCustomSerializer) {
            buffer.appendByte((byte) (CUSTOM_PROTOCOL_OFFSET + serializerKey.getBytes().length));
        } else {
            buffer.appendByte(header.getSerializer());
        }
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        // 获取序列化器
        if (!useCustomSerializer) {
            serializerKey = ProtocolMessageSerializerEnum.getNameByKey(header.getSerializer());
            if (serializerKey == null) {
                throw new RuntimeException("序列化协议不存在");
            }
        }
        // log("使用 {} 序列化器编码", serializerKey);
        Serializer serializer = SerializerFactory.getInstance(serializerKey);
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // 写入 body 长度、自定义序列化器（如果有）和数据
        buffer.appendInt(bodyBytes.length);
        if (useCustomSerializer) {
            buffer.appendBytes(serializerKey.getBytes());
        }
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
