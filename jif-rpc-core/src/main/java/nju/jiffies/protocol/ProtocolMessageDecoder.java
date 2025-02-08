package nju.jiffies.protocol;

import io.vertx.core.buffer.Buffer;
import nju.jiffies.model.RpcRequest;
import nju.jiffies.model.RpcResponse;
import nju.jiffies.serializer.Serializer;
import nju.jiffies.serializer.SerializerFactory;

import java.io.IOException;

import static cn.hutool.core.lang.Console.log;
import static nju.jiffies.protocol.ProtocolConstant.CUSTOM_PROTOCOL_OFFSET;
import static nju.jiffies.protocol.ProtocolConstant.MESSAGE_HEADER_LENGTH;

/**
 * 协议消息解码器
 */
public class ProtocolMessageDecoder {

    /**
     * 解码
     *
     * @param buffer
     * @return
     * @throws IOException
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // 分别从指定位置读出 Buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        // 校验魔数
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("消息 magic 非法");
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));
        // 解析消息体
        boolean useCustomSerializer = false;
        String serializerName = ProtocolMessageSerializerEnum.getNameByKey(header.getSerializer());
        int customSerializerNameLen = 0;
        if (serializerName == null) {
            // 可能使用的是自定义序列化器
            useCustomSerializer = true;
            customSerializerNameLen = header.getSerializer() - CUSTOM_PROTOCOL_OFFSET;
            serializerName = buffer.getString(MESSAGE_HEADER_LENGTH, MESSAGE_HEADER_LENGTH + customSerializerNameLen);
        }
        // log("使用 {} 序列化器解码", serializerName);
        Serializer serializer = SerializerFactory.getInstance(serializerName);
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (messageTypeEnum == null) {
            throw new RuntimeException("序列化消息的类型不存在");
        }
        // 解决粘包问题，只读指定长度的数据
        int bodyStartOffset = MESSAGE_HEADER_LENGTH + customSerializerNameLen;
        byte[] bodyBytes = buffer.getBytes(bodyStartOffset, bodyStartOffset + header.getBodyLength());
        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes, RpcRequest.class);
                return new ProtocolMessage<>(header, useCustomSerializer? serializerName: null, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes, RpcResponse.class);
                return new ProtocolMessage<>(header, useCustomSerializer? serializerName: null, response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }

}
