package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lib.TimeStamp;

/**
 * Used by the Time server to encode a TimeStamp into bytes
 */
public class TimeEncoderHandler extends MessageToByteEncoder<TimeStamp> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TimeStamp timeStamp, ByteBuf out) {
        out.writeInt(timeStamp.toInt());
    }
}

/*
 * Keeping this as a reference
 */
//public class TimeEncoderHandler extends ChannelOutboundHandlerAdapter {
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
//        TimeStamp timeStamp = (TimeStamp) msg;
//        ByteBuf buf = ctx.alloc().buffer(4);
//        buf.writeInt(timeStamp.toInt());
//        ctx.write(buf, promise);
//    }
//}
