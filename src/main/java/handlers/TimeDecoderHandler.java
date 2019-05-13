package handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lib.TimeStamp;

import java.util.List;

/**
 * Decodes a series of bytes into a 32-bit integer
 */
public class TimeDecoderHandler extends ByteToMessageDecoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }

        out.add(TimeStamp.fromRFC868(in.readUnsignedInt()));
    }
}
