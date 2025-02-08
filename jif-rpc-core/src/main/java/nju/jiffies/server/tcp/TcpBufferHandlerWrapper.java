package nju.jiffies.server.tcp;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;
import nju.jiffies.protocol.ProtocolConstant;

import static nju.jiffies.protocol.ProtocolConstant.CUSTOM_PROTOCOL_OFFSET;

/**
 * TCP 消息处理器包装
 * 装饰者模式，使用 recordParser 对原有的 buffer 处理能力进行增强
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    /**
     * 解析器，用于解决半包、粘包问题
     */
    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        recordParser = initRecordParser(bufferHandler);
    }

    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }

    /**
     * 初始化解析器
     *
     * @param bufferHandler
     * @return
     */
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造 parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {
            // 初始化
            int stage = 0;
            final int[] size = new int[2];
            // 一次完整的读取（头 + 体）
            Buffer resultBuffer = Buffer.buffer();

            @Override
            // 当 parser 读取到一个完整的消息头后，会触发 handle 方法
            public void handle(Buffer buffer) {
                if (stage == 0) {
                    // 0. 每次循环，首先读取消息头
                    size[0] = buffer.getByte(2);
                    size[1] = buffer.getInt(13);
                    if (size[0] > CUSTOM_PROTOCOL_OFFSET) {
                        // 使用了自定义协议，读取协议名称
                        stage = 1;
                        parser.fixedSizeMode(size[0] - CUSTOM_PROTOCOL_OFFSET);
                        resultBuffer.appendBuffer(buffer);
                    } else {
                        // 没有使用自定义协议，跳转至第2步
                        stage = 2;
                        parser.fixedSizeMode(size[1]);
                        resultBuffer.appendBuffer(buffer);
                    }
                } else if (stage == 1) {
                    // 1.如果有自定义协议，读取协议名称
                    stage = 2;
                    parser.fixedSizeMode(size[1]);
                    resultBuffer.appendBuffer(buffer);
                } else {
                    // 2. 然后读取消息体
                    // 写入体信息到结果
                    resultBuffer.appendBuffer(buffer);
                    // 已拼接为完整 Buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    // 重置一轮
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                    stage = 0;
                    resultBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }
}
